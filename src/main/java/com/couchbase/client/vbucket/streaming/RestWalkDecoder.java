/**
 * Copyright (C) 2009-2013 Couchbase, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALING
 * IN THE SOFTWARE.
 */

package com.couchbase.client.vbucket.streaming;

import com.couchbase.client.vbucket.config.Config;
import com.couchbase.client.vbucket.config.ConfigFactory;
import com.couchbase.client.vbucket.mapping.*;
import com.couchbase.client.vbucket.ConfigurationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.Map;

/**
 * Subsequently walks the REST API, decodes and analyzes the responses.
 */
class RestWalkDecoder  extends SimpleChannelInboundHandler<HttpObject> {

  /**
   * Always use this pool name by now.
   */
  public static final String DEFAULT_POOL = "default";

  /**
   * Contains information to fill once the walk was successful.
   */
  private RestWalkRequest request;

  /**
   * Holds the current state of the Rest Walk.
   */
  private Steps currentStep;

  /**
   * Contains the body (JSON) aggregated.
   */
  private final StringBuilder contentBuffer;

  /**
   * The Jackson JSON mapper.
   */
  private final ObjectMapper mapper;

  /**
   * The bucket to look for in the bucket list.
   */
  private final String userBucket;

  /**
   * The password of the bucket.
   */
  private final String userPassword;

  /**
   * Create a new Decoder.
   */
  public RestWalkDecoder(String bucket, String password) {
    currentStep = Steps.POOLS;
    contentBuffer = new StringBuilder();
    mapper = new ObjectMapper();
    userBucket = bucket;
    userPassword = password;
  }

  /**
   * Always called when incoming data is loaded.
   *
   * This method aggregates loaded http information (also chunks) and once
   * all information is loaded, dispatches helper methods to handle the actual
   * business logic on top of it.
   *
   * @param ctx the channel context.
   * @param msg the incoming data.
   * @throws Exception
   */
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg)
    throws Exception {
    if (msg instanceof HttpResponse) {
      if (((HttpResponse) msg).getStatus().code() != 200) {
        throw new ConfigurationException("A response while walking the REST "
          + "API returned a non-success state: " + msg.toString());
      }
    } else if (msg instanceof HttpContent) {
      HttpContent content = (HttpContent) msg;
      contentBuffer.append(content.content().toString(CharsetUtil.UTF_8));

      if (msg instanceof LastHttpContent) {
        dispatchStep(ctx);
      }
    }
  }

  /**
   * Depending on the current step in the REST walk, load the next step
   * to parse the information.
   */
  private void dispatchStep(ChannelHandlerContext ctx) {
    switch (currentStep) {
      case POOLS:
        parsePools(ctx);
        break;
      case POOL:
        parsePool(ctx);
        break;
      case BUCKETS:
        parseBuckets(ctx);
        break;
      default:
        throw new IllegalStateException("Unknown state reached during "
          + "decoding: " + currentStep);
    }
  }

  /**
   * Parse the /pools response.
   *
   * @param ctx the context to work with.
   */
  private void parsePools(ChannelHandlerContext ctx) {
    Pools pools = parseJson(contentBuffer.toString(), Pools.class);

    String poolUri = null;
    for (Map<String, String> pool : pools.getPools()) {
      if (pool.get("name").equals(DEFAULT_POOL)) {
        poolUri = pool.get("uri");
      }
    }

    if (poolUri == null) {
      throw new MappingException("Could not determine the Pool URI from the "
        + "pools list.");
    }

    transitionTo(Steps.POOL, poolUri, ctx);
  }

  /**
   * Parse the /pools/{pool} response.
   *
   * @param ctx the context to work with.
   */
  private void parsePool(ChannelHandlerContext ctx) {
    Pool pool = parseJson(contentBuffer.toString(), Pool.class);

    String bucketsUri = pool.getBucketInfo().get("uri");
    if (bucketsUri == null) {
      throw new MappingException("Could not determine the bucket list URI from "
        + "the pool information.");
    }

    transitionTo(Steps.BUCKETS, bucketsUri, ctx);
  }

  /**
   * Parse the /pools/{pool}/buckets response.
   *
   * @param ctx the context to work with.
   */
  private void parseBuckets(ChannelHandlerContext ctx) {
    Buckets buckets = parseJson(contentBuffer.toString(), Buckets.class);

    for (Bucket bucket : buckets.getBuckets()) {
      if (bucket.getName().equals(userBucket)) {
        Config config = ConfigFactory.fromBucket(bucket);
        finishWalking(config, ctx);
        contentBuffer.setLength(0);
        currentStep = Steps.POOLS;
        return;
      }
    }

    throw new ConfigurationException("Could not find bucket " + userBucket
      + " in bucket list.");
  }

  /**
   * Parse the JSON data into the given target type.
   *
   * @param jsonString the string to parse.
   * @param type the type to parse into.
   * @param <T> type container.
   * @return the parsed and instantiated object.
   */
  private <T> T parseJson(String jsonString, Class<T> type) {
    try {
     return mapper.readValue(jsonString, type);
    } catch (Exception ex) {
      throw new MappingException("Could not map the JSON onto the given "
        + "target type ("  + type.getCanonicalName() + "): " + jsonString);
    }
  }

  /**
   * Transition to the next REST walk step and clean up.
   *
   * @param nextStep where to transition into.
   * @param nextUri the next URI to load.
   * @param ctx the context to write into.
   */
  private void transitionTo(Steps nextStep, String nextUri,
    ChannelHandlerContext ctx) {
    String authString = Base64.encode(
      Unpooled.copiedBuffer(userBucket + ":" + userPassword, CharsetUtil.UTF_8)
    ).toString(CharsetUtil.UTF_8);

    HttpRequest request =
      new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, nextUri);
    request.headers().set(HttpHeaders.Names.AUTHORIZATION, "Basic " + authString);
    ctx.writeAndFlush(request);

    currentStep = nextStep;
    contentBuffer.setLength(0);
  }

  /**
   * Finish the REST Walking steps.
   *
   * This method adds the found config to the list of configs found and counts
   * down the latch to signal it is done. Finally, the REST walking codec is
   * removed and the streaming handler is added so that the channel can now
   * work as a streaming connection endpoint.
   *
   * @param config the config to submit.
   * @param ctx the context to work with.
   */
  private void finishWalking(Config config, ChannelHandlerContext ctx) {
    request.getConfigs().add(config);
    request.getLatch().countDown();
  }

  /**
   * Inject the walk request information for later retrieval.
   *
   * @param req the request.
   * @return the {@link RestWalkDecoder} to allow for method chaining.
   */
  public RestWalkDecoder setRestWalkRequest(RestWalkRequest req) {
    request = req;
    return this;
  }

  /**
   * Handle all exceptions.
   *
   * In this state of the parsing phase, all exceptions lead to logging them
   * and then closing the channel.
   *
   * @param ctx the channel context.
   * @param cause the cause of the exception
   * @throws Exception
   */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    throws Exception {
    cause.printStackTrace();
    ctx.channel().close();
  }

  /**
   * Describes the possible steps of the walk in a typesafe manner.
   */
  enum Steps {
    /**
     * Load all pools (/pools).
     */
    POOLS,

    /**
     * Load pool info from /pools/{pool}.
     */
    POOL,

    /**
     * Load all buckets for a pool (/pools/{pool}/buckets).
     */
    BUCKETS
  }


}

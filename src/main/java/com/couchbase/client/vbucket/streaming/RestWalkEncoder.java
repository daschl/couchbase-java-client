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

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.List;

public class RestWalkEncoder extends MessageToMessageEncoder<Object> {

  /**
   * The bucket to look for in the bucket list.
   */
  private final String userBucket;

  /**
   * The password of the bucket.
   */
  private final String userPassword;

  public RestWalkEncoder(String userBucket, String userPassword) {
    this.userBucket = userBucket;
    this.userPassword = userPassword;
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
    if (msg instanceof RestWalkRequest) {
      RestWalkRequest request = (RestWalkRequest) msg;
      forwardToDecoder(ctx, request);
      ctx.writeAndFlush(prepareStepOne());
    } else if (msg instanceof HttpRequest) {
      out.add(msg);
    } else {
      throw new IllegalArgumentException("Could not encode this message type: "
        + msg.getClass().getCanonicalName());
    }
  }

  /**
   * Forwards the received {@link RestWalkRequest} to the codec, who dispatches
   * it to the {@link RestWalkDecoder}.
   *
   * @param ctx the context of the encoder.
   * @param request the request to forward.
   */
  private void forwardToDecoder(ChannelHandlerContext ctx, RestWalkRequest request) {
    RestWalkCodec codec = (RestWalkCodec) ctx.pipeline()
      .get(StreamingInitializer.REST_WALK_CODEC_NAME);
    codec.setRestWalkRequest(request);
  }

  /**
   * Preparing the REST walk by sending out the first request to load the pools.
   *
   * @return the built request, ready to send.
   */
  private HttpRequest prepareStepOne() {
    String authString = Base64.encode(
        Unpooled.copiedBuffer(userBucket + ":" + userPassword, CharsetUtil.UTF_8)
      ).toString(CharsetUtil.UTF_8);

    HttpRequest req =
      new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/pools");
    req.headers().set(HttpHeaders.Names.AUTHORIZATION, "Basic " + authString);
    return req;
  }

}

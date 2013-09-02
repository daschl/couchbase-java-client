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

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;


/**
 * Initializes the pipeline with all needed codecs.
 *
 * Note that the pipeline may change during its lifetime, depending on which
 * functionality is needed. The codecs added in this initializers are in place
 * when the channel is constructed and first used.
 */
public class StreamingInitializer extends ChannelInitializer<SocketChannel> {

  public static final String HTTP_CODEC_NAME = "httpCodec";
  public static final String REST_WALK_CODEC_NAME = "restWalkCodec";
  public static final String STREAMING_CODEC_NAME = "streamingCodec";

  private final String bucket;
  private final String password;

  public StreamingInitializer(String bucket, String password) {
    this.bucket = bucket;
    this.password = password;
  }

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ch.pipeline()
      .addLast(HTTP_CODEC_NAME, new HttpClientCodec())
      .addLast(REST_WALK_CODEC_NAME, new RestWalkCodec(bucket, password));
  }
}


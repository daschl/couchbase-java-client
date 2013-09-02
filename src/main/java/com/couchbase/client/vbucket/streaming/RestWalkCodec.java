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

import io.netty.channel.CombinedChannelDuplexHandler;

/**
 * A codec that is used during the bootstrap phase to walk the Couchbase REST
 * API autonomously.
 */
public class RestWalkCodec
  extends CombinedChannelDuplexHandler<RestWalkDecoder, RestWalkEncoder> {

  private final RestWalkDecoder decoder;
  private final RestWalkEncoder encoder;

  /**
   * Create a new codec.
   */
  public RestWalkCodec(String bucket, String password) {
    decoder = new RestWalkDecoder(bucket, password);
    encoder = new RestWalkEncoder(bucket, password);

    init(decoder, encoder);
  }

  public RestWalkCodec setRestWalkRequest(final RestWalkRequest request) {
    decoder.setRestWalkRequest(request);
    return this;
  }

}

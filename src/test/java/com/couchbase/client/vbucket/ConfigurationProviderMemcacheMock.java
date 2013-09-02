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

package com.couchbase.client.vbucket;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.couchbase.client.vbucket.config.Config;
import com.couchbase.client.vbucket.config.MemcachedConfig;
import net.spy.memcached.TestConfig;

/**
 * Implements a stub configuration provider for testing memcache buckets.
 */
public class ConfigurationProviderMemcacheMock
  implements ConfigurationProvider {

  private Config config;

  public ConfigurationProviderMemcacheMock() {
    config = new MemcachedConfig(Arrays.asList(TestConfig.IPV4_ADDR));
  }

  @Override
  public Config get() {
    return config;
  }

  @Override
  public void update(Config config) {
    this.config = config;
  }

  @Override
  public boolean hasConfig() {
    return true;
  }

  @Override
  public void subscribe(Reconfigurable reconfigurable) { }

  @Override
  public void unsubscribe(Reconfigurable reconfigurable) { }

  @Override
  public void shutdown() { }

  @Override
  public void triggerResubscribe() { }

}

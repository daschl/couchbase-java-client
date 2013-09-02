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

import com.couchbase.client.vbucket.config.Config;

/**
 * Methods that need to be provided for all  {@link ConfigurationProvider}
 * implementations.
 */
public interface ConfigurationProvider {

  /**
   * Returns the current configuration for the connected bucket.
   *
   * @return
   */
  Config get();

  /**
   * Update the current configuration manually.
   *
   * @param config
   */
  void update(Config config);

  /**
   * If there is a {@link Config} currently loaded or not.
   *
   * @return true if there is a configuration, false otherwise.
   */
  boolean hasConfig();

  /**
   * Subscribes for configuration updates.
   *
   * @param reconfigurable reconfigurable that will receive updates
   */
  void subscribe(Reconfigurable reconfigurable);

  /**
   * Unsubscribe from configuration updates.
   *
   * @param reconfigurable the reconfigurable to unsubscribe.
   */
  void unsubscribe(Reconfigurable reconfigurable);

  void shutdown();

  void triggerResubscribe();

}

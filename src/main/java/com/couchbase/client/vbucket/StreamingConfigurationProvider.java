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
import com.couchbase.client.vbucket.config.ConfigType;
import com.couchbase.client.vbucket.quorum.FirstQuorumStrategy;
import com.couchbase.client.vbucket.streaming.RestWalkRequest;
import com.couchbase.client.vbucket.streaming.StreamingInitializer;
import com.couchbase.client.vbucket.quorum.QuorumStrategy;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import net.spy.memcached.compat.SpyObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A {@link ConfigurationProvider} that keeps the configuration up-to-date over
 * one or more streaming connections.
 */
public class StreamingConfigurationProvider extends SpyObject
  implements ConfigurationProvider {

  /**
   * The default streaming port.
   */
  public static final int STREAMING_PORT = 8091;

  /**
   * The entry point for the REST walk.
   */
  public static final String REST_ENTRY = "/pools";

  /**
   * Maximum timeout to wait until a connection is established (in
   * milliseconds).
   */
  public static final int CONNECT_TIMEOUT = 2000;

  /**
   * Maximum time given for every node to walk the REST API and return
   * a valid configuration.
   */
  public static final int CONFIG_FOUND_TIMEOUT = 5000;

  /**
   * The default {@link QuorumStrategy} to use if not otherwise specified.
   */
  public static final QuorumStrategy QUORUM_STRATEGY =
    new FirstQuorumStrategy();

  /**
   * Contains the current configuration of the bucket.
   */
  private final AtomicReference<Config> currentConfig;

  /**
   * The name of the bucket to fetch the configuration for.
   */
  private final String bucketName;

  /**
   * The password of the bucket.
   */
  private final String bucketPassword;

  /**
   * The port where the REST API can be found.
   */
  private final int restPort;

  /**
   * The relative entry point of the REST API.
   */
  private final String restEntry;

  /**
   * Netty {@link Bootstrap} helper to start channels from.
   */
  private final Bootstrap channelBoostrap;

  /**
   * Contains all currently connected channels.
   */
  private ChannelGroup activeChannels;

  /**
   * Which {@link QuorumStrategy} to use on bootstrap.
   */
  private final QuorumStrategy quorumStrategy;

  /**
   * Create a new {@link StreamingConfigurationProvider} with the name of the
   * bucket.
   *
   * If this constructor are used, the default settings are applied. See
   * {@link #STREAMING_PORT}, {@link #REST_ENTRY} and {@link #QUORUM_STRATEGY}
   * for more information. These provide sensible defaults for current
   * Couchbase Server configuration setups.
   *
   * @param bucket the name of the bucket to connect to.
   */
  public StreamingConfigurationProvider(String bucket, String password) {
    this(bucket, password, STREAMING_PORT, REST_ENTRY, QUORUM_STRATEGY);
  }

  /**
   * Create a new {@link StreamingConfigurationProvider}.
   *
   * @param bucket the name of the bucket to connect to.
   * @param port the port of the REST API.
   * @param entry the relative entry point of the REST API.
   * @param strategy the quorum strategy to use.
   */
  public StreamingConfigurationProvider(String bucket, String password,
    int port, String entry, QuorumStrategy strategy) {
    currentConfig = new AtomicReference<Config>();
    bucketName = bucket;
    bucketPassword = password;
    restPort = port;
    restEntry = entry;
    quorumStrategy = strategy;
    activeChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    channelBoostrap = new Bootstrap()
      .group(new NioEventLoopGroup())
      .channel(NioSocketChannel.class)
      .handler(new StreamingInitializer(bucketName, bucketPassword));
  }

  /**
   * Establish connections to the streaming connections.
   *
   * If the bootstrap process was successful, a sound and correct {@link Config}
   * is stored (and returned), which is also based on the configured
   * {@link QuorumStrategy}.
   *
   * @param streamingNodes the list of streaming connections to open.
   */
  public Config boostrap(List<String> streamingNodes) {
    final CountDownLatch connectLatch =
      new CountDownLatch(streamingNodes.size());
    for (final String node : streamingNodes) {
      channelBoostrap.connect(node, restPort).addListener(
        new ChannelFutureListener() {
          @Override
          public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
              activeChannels.add(future.channel());
            } else {
              getLogger().info("Could not establish streaming connection to "
                + node);
            }
            connectLatch.countDown();
          }
      });
    }

    try {
      connectLatch.await(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      throw new ConfigurationException("Interrupted while waiting for "
        + "streaming connections to establish.");
    }

    if (activeChannels.isEmpty()) {
      throw new ConfigurationException("Could not establish a single streaming"
        + "connection from the list of nodes given.");
    }

    int maxBackoffRetries = 5;
    int retryCount = 1;
    List<Config> possibleConfigs;
    do {
      if (retryCount > maxBackoffRetries) {
        throw new ConfigurationException("Cluster is not in a warmed up "
          + "state after " + maxBackoffRetries + " exponential retries.");
      }

      possibleConfigs = walkRestAPI();
      if (possibleConfigs.isEmpty()) {
        throw new ConfigurationException("Not a single possible configuration "
          + "could be loaded.");
      }

      if(isWarmedUp(possibleConfigs)) {
        break;
      }

      int backoffSeconds = new Double(
        retryCount * Math.pow(2, retryCount++)).intValue();
      getLogger().info("Cluster is currently warming up, waiting "
        + backoffSeconds + " seconds for vBuckets to show up.");

      try {
        Thread.sleep(backoffSeconds * 1000);
      } catch (InterruptedException ex) {
        throw new ConfigurationException("Cluster is not in a warmed up "
          + "state.");
      }
    } while (true);

    Config quorumConfig = determineQuorum(possibleConfigs);
    currentConfig.set(quorumConfig);
    return quorumConfig;
  }

  private boolean isWarmedUp(List<Config> possibleConfigs) {
    for (Config config : possibleConfigs) {
      if (config.getConfigType().equals(ConfigType.MEMCACHE)
        || config.getVbucketsCount() > 0) {
        return true;
      }
    }

    return false;
  }

  @Override
  public Config get() {
    return currentConfig.get();
  }

  @Override
  public void update(Config config) {
    currentConfig.set(config);
  }

  @Override
  public boolean hasConfig() {
    return currentConfig.get() != null;
  }

  /**
   * Walk the REST API for the connected nodes and determine a list of possible
   * configurations to use.
   *
   * Note that this method does not end its work even when no config was found,
   * an empty list will be returned in this case. The calling method needs to
   * choose if the result is appropriate or not.
   *
   * @return a list of found configurations.
   */
  List<Config> walkRestAPI() {
    List<Config> foundConfigs = new ArrayList<Config>();
    CountDownLatch walkLatch = new CountDownLatch(activeChannels.size());
    activeChannels.write(new RestWalkRequest(walkLatch, foundConfigs));

    try {
      walkLatch.await(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      throw new ConfigurationException("Interrupted while waiting for "
        + "streaming connections to walk the REST API and retrieve configs.");
    }

    return foundConfigs;
  }

  /**
   * Determine a sound {@link Config} out of a list of possible {@link Config}s.
   *
   * Which configuration is chosen depends on different factors, including the
   * supplied {@link QuorumStrategy}. If there are not enough configs
   * available to satisfy the given strategy, an exception will be raised.
   *
   * @param possibleConfigs the list of possible configurations.
   * @return a quorum-identified configuration.
   */
  Config determineQuorum(List<Config> possibleConfigs) {
    return quorumStrategy.apply(possibleConfigs);
  }

  /**
   * Register a subscriber that will be notified once a new configuration
   * arrives.
   *
   * @param reconfigurable reconfigurable that will receive updates
   */
  @Override
  public void subscribe(Reconfigurable reconfigurable) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * Remove a subscriber from the reconfiguration subscription.
   *
   * @param reconfigurable the reconfigurable to unsubscribe.
   */
  @Override
  public void unsubscribe(Reconfigurable reconfigurable) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void shutdown() {

  }

  @Override
  public void triggerResubscribe() {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}

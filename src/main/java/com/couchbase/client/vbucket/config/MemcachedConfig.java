package com.couchbase.client.vbucket.config;


import com.couchbase.client.vbucket.config.ConfigType;

import java.net.URL;
import java.util.List;

public class MemcachedConfig implements Config {

  private List<String> servers;
  private boolean isStale;

  public MemcachedConfig(List<String> servers) {
    this.servers = servers;
    isStale = false;
  }

  @Override
  public List<String> getServers() {
    return servers;
  }

  @Override
  public int getServersCount() {
    return servers.size();
  }

  @Override
  public String getServer(int serverIndex) {
    return servers.get(serverIndex);
  }

  @Override
  public int getReplicasCount() {
    throw new UnsupportedOperationException("Not supported for memcached "
      + "buckets.");
  }

  @Override
  public int getVbucketsCount() {
    throw new UnsupportedOperationException("Not supported for memcached "
      + "buckets.");
  }

  @Override
  public int getVbucketByKey(String key) {
    throw new UnsupportedOperationException("Not supported for memcached "
      + "buckets.");
  }

  @Override
  public int getMaster(int vbucketIndex) {
    throw new UnsupportedOperationException("Not supported for memcached "
      + "buckets.");
  }

  @Override
  public int getReplica(int vbucketIndex, int replicaIndex) {
    throw new UnsupportedOperationException("Not supported for memcached "
      + "buckets.");
  }

  @Override
  public List<URL> getCouchServers() {
    throw new UnsupportedOperationException("Not supported for memcached "
      + "buckets.");
  }

  @Override
  public ConfigType getConfigType() {
    return ConfigType.MEMCACHE;
  }

  @Override
  public boolean isProbablyStale() {
    return isStale;
  }

  @Override
  public void markAsProbablyStale() {
    isStale = true;
  }

}

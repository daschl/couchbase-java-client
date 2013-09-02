package com.couchbase.client.vbucket.config;

import com.couchbase.client.vbucket.config.ConfigType;
import net.spy.memcached.HashAlgorithm;

import java.net.URL;
import java.util.List;

public class CouchbaseConfig implements Config {

  private final HashAlgorithm hashAlgorithm;
  private final List<String> servers;
  private final List<List<Integer>> vBuckets;
  private final int numReplicas;
  private final List<URL> couchServers;
  private boolean isStale;

  public CouchbaseConfig(List<String> servers, List<List<Integer>> vBuckets,
    int numReplicas, List<URL> couchServers, HashAlgorithm hashAlgorithm) {
    this.servers = servers;
    this.vBuckets = vBuckets;
    this.numReplicas = numReplicas;
    this.couchServers = couchServers;
    this.hashAlgorithm = hashAlgorithm;
    this.isStale = false;
  }

  @Override
  public int getReplicasCount() {
    return numReplicas;
  }

  @Override
  public int getVbucketsCount() {
    return vBuckets.size();
  }

  @Override
  public int getServersCount() {
    return servers.size();
  }

  @Override
  public String getServer(int serverIndex) {
    return servers.get(0);
  }

  @Override
  public int getVbucketByKey(String key) {
    int digest = (int) hashAlgorithm.hash(key);
    int mask = getVbucketsCount() - 1;
    return digest & mask;
  }

  @Override
  public int getMaster(int vbucketIndex) {
    return vBuckets.get(vbucketIndex).get(0);
  }

  @Override
  public int getReplica(int vbucketIndex, int replicaIndex) {
    return vBuckets.get(vbucketIndex).get(replicaIndex);
  }

  @Override
  public List<String> getServers() {
    return servers;
  }

  @Override
  public List<URL> getCouchServers() {
    return couchServers;
  }

  @Override
  public ConfigType getConfigType() {
    return ConfigType.COUCHBASE;
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

package com.couchbase.client.vbucket.config;

import java.net.URL;
import java.util.List;

public interface Config {

  /**
   * Returns the amount of replicas currently in the configuration.
   *
   * @return
   */
  int getReplicasCount();

  /**
   * Returns the number of vbuckets in the configuration.
   *
   * @return
   */
  int getVbucketsCount();

  /**
   * Returns the number of servers in the configuration.
   * @return
   */
  int getServersCount();


  /**
   * Return the hostname for the given server index.
   *
   * @param serverIndex the array index of the server.
   * @return
   */
  String getServer(int serverIndex);

  /**
   * Returns the active vbucket for a given key.
   *
   * @param key the key of the document.
   * @return
   */
  int getVbucketByKey(String key);

  /**
   * Get the master index for the given vbucket.
   *
   * @param vbucketIndex
   * @return
   */
  int getMaster(int vbucketIndex);

  /**
   * Get the replica index for the given vbucket and num replica.
   */
  int getReplica(int vbucketIndex, int replicaIndex);

  /**
   * Returns all servers known.
   *
   * @return
   */
  List<String> getServers();

  /**
   * Returns the URIs of the View servers.
   *
   * @return
   */
  List<URL> getCouchServers();

  /**
   * Returns the current type of configuration.
   *
   * @return
   */
  ConfigType getConfigType();

  /**
   * True if there is a chance that the config is stale.
   *
   * @return
   */
  boolean isProbablyStale();

  /**
   * Mark this config as probably stale.
   */
  void markAsProbablyStale();
}

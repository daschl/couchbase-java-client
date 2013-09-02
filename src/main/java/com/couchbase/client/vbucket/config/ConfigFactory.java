package com.couchbase.client.vbucket.config;

import com.couchbase.client.vbucket.mapping.Bucket;
import net.spy.memcached.HashAlgorithm;
import net.spy.memcached.HashAlgorithmRegistry;

public class ConfigFactory {

  public static Config fromBucket(Bucket bucket) {
    if (bucket.getType().equals("membase")) {
      return prepareCouchbaseBucket(bucket);
    } else if (bucket.getType().equals("memcached")) {
      return prepareMemcachedBucket(bucket);
    } else {
      throw new IllegalStateException("Could not create config from unknown "
        + " bucket type " + bucket.getType());
    }
  }

  private static Config prepareCouchbaseBucket(Bucket bucket) {
    HashAlgorithm hashAlgorithm =
      HashAlgorithmRegistry.lookupHashAlgorithm(bucket.getVBucketAlgorithm());

    return new CouchbaseConfig(bucket.getVBucketServers(),
      bucket.getVBucketList(), bucket.getNumReplicas(),
      bucket.getCouchServers(), hashAlgorithm);
  }

  private static Config prepareMemcachedBucket(Bucket bucket) {
    return new MemcachedConfig(bucket.getNodeServerList());
  }

}

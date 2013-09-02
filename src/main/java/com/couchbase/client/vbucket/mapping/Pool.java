package com.couchbase.client.vbucket.mapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pool {
  private final String name;
  private final Map<String, String> bucketInfo;

  public Pool(
    @JsonProperty("name") String name,
    @JsonProperty("buckets") Map<String, String> buckets) {
    this.name = name;
    this.bucketInfo = buckets;
  }

  public String getName() {
    return name;
  }

  public Map<String, String> getBucketInfo() {
    return bucketInfo;
  }
}

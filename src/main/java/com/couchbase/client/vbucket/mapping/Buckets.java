package com.couchbase.client.vbucket.mapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Buckets {

  private final List<Bucket> buckets;

  @JsonCreator
  public Buckets(List<Bucket> buckets) {
    this.buckets = buckets;
  }

  public List<Bucket> getBuckets() {
    return buckets;
  }
}

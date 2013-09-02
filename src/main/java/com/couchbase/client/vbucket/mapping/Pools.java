package com.couchbase.client.vbucket.mapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Represents a list of pools.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pools {

  private final List<Map<String, String>> pools;

  @JsonCreator
  public Pools(@JsonProperty("pools") List<Map<String, String>> pools) {
    this.pools = pools;
  }

  public List<Map<String, String>> getPools() {
    return pools;
  }

}

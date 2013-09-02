package com.couchbase.client.vbucket.mapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Node {

  private final String couchApiBase;
  private final String clusterMembership;
  private final String status;
  private final String hostname;
  private final String version;
  private final Map<String, Integer> ports;

  @JsonCreator
  public Node(
    @JsonProperty("couchApiBase") String couchApiBase,
    @JsonProperty("clusterMembership") String clusterMembership,
    @JsonProperty("status") String status,
    @JsonProperty("hostname") String hostname,
    @JsonProperty("version") String version,
    @JsonProperty("ports") Map<String, Integer> ports) {
    this.couchApiBase = couchApiBase;
    this.clusterMembership = clusterMembership;
    this.status = status;
    this.hostname = hostname;
    this.version = version;
    this.ports = ports;
  }

  public String getCouchApiBase() {
    return couchApiBase;
  }

  public String getClusterMembership() {
    return clusterMembership;
  }

  public String getStatus() {
    return status;
  }

  public String getHostname() {
    return hostname;
  }

  public String getVersion() {
    return version;
  }

  public Map<String, Integer> getPorts() {
    return ports;
  }
}

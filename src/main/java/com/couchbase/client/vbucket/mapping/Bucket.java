package com.couchbase.client.vbucket.mapping;

import com.couchbase.client.vbucket.ConfigurationException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Bucket {

  private final String name;
  private final String type;
  private final String streamingUri;
  private final List<Node> nodes;
  private final Map<String, Object> vbucketInfo;

  @JsonCreator
  public Bucket(
    @JsonProperty("name") String name,
    @JsonProperty("bucketType") String type,
    @JsonProperty("streamingUri") String streamingUri,
    @JsonProperty("nodes") List<Node> nodes,
    @JsonProperty("vBucketServerMap") Map<String, Object> vbucketInfo) {
    this.name = name;
    this.type = type;
    this.streamingUri = streamingUri;
    this.nodes = nodes;
    this.vbucketInfo = vbucketInfo;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public String getStreamingUri() {
    return streamingUri;
  }

  public List<Node> getNodes() {
    return nodes;
  }

  public Map<String, Object> getVbucketInfo() {
    return vbucketInfo;
  }

  public List<String> getVBucketServers() {
    return (List<String>) vbucketInfo.get("serverList");
  }

  public List<List<Integer>> getVBucketList() {
    return (List<List<Integer>>) vbucketInfo.get("vBucketMap");
  }

  public int getNumReplicas() {
    return (Integer) vbucketInfo.get("numReplicas");
  }

  public List<URL> getCouchServers() {
    List<URL> couchNodes = new ArrayList<URL>();
    for (Node node : nodes) {
      String base = node.getCouchApiBase();
      if (base != null) {
        try {
          couchNodes.add(new URL(base));
        } catch (MalformedURLException e) {
          throw new ConfigurationException("Could not parse the CouchApiBase.");
        }
      }
    }
    return couchNodes;
  }

  public List<String> getNodeServerList() {
    List<String> nodeList = new ArrayList<String>();
    for (Node node : nodes) {
      nodeList.add(node.getHostname() + ":" + node.getPorts().get("direct"));
    }
    return nodeList;
  }

  public String getVBucketAlgorithm() {
    return (String) vbucketInfo.get("hashAlgorithm");
  }

}

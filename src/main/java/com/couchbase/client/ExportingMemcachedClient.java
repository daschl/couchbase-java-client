package com.couchbase.client;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.MemcachedConnection;
import net.spy.memcached.OperationFactory;
import net.spy.memcached.transcoders.TranscodeService;
import net.spy.memcached.transcoders.Transcoder;

import java.io.IOException;

class ExportingMemcachedClient extends MemcachedClient {


  public ExportingMemcachedClient(CouchbaseConnectionFactory factory) throws IOException {
    super(factory, AddrUtil.getAddresses(factory.getVBucketConfig().getServers()));
  }

  public MemcachedConnection getMemcachedConnection() {
    return mconn;
  }

  public long getOperationTimeout() {
    return operationTimeout;
  }

  public OperationFactory getOperationFactory() {
    return opFact;
  }

  public TranscodeService getTranscodeService() {
    return tcService;
  }

  public Transcoder<Object> getTranscoder() {
    return transcoder;
  }



}

package com.couchbase.client;

import net.spy.memcached.*;
import net.spy.memcached.compat.SpyObject;
import net.spy.memcached.internal.BulkFuture;
import net.spy.memcached.internal.GetFuture;
import net.spy.memcached.internal.OperationFuture;
import net.spy.memcached.transcoders.Transcoder;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public abstract class MemcachedClientAdapter extends SpyObject implements MemcachedClientIF {

  abstract MemcachedClient getMemcachedClient();
  
  @Override
  public Collection<SocketAddress> getAvailableServers() {
    return getMemcachedClient().getAvailableServers();
  }

  @Override
  public Collection<SocketAddress> getUnavailableServers() {
    return getMemcachedClient().getUnavailableServers();
  }

  @Override
  public Transcoder<Object> getTranscoder() {
    return getMemcachedClient().getTranscoder();
  }

  @Override
  public NodeLocator getNodeLocator() {
    return getMemcachedClient().getNodeLocator();
  }

  @Override
  public OperationFuture<Boolean> append(long l, String s, Object o) {
    return getMemcachedClient().append(l, s, o);
  }

  @Override
  public OperationFuture<Boolean> append(String s, Object o) {
    return getMemcachedClient().append(s, o);
  }

  @Override
  public <T> OperationFuture<Boolean> append(long l, String s, T t, Transcoder<T> tTranscoder) {
    return getMemcachedClient().append(l, s, t, tTranscoder);
  }

  @Override
  public <T> OperationFuture<Boolean> append(String s, T t, Transcoder<T> tTranscoder) {
    return getMemcachedClient().append(s, t, tTranscoder);
  }

  @Override
  public OperationFuture<Boolean> prepend(long l, String s, Object o) {
    return getMemcachedClient().prepend(l, s, o);
  }

  @Override
  public OperationFuture<Boolean> prepend(String s, Object o) {
    return getMemcachedClient().prepend(s, o);
  }

  @Override
  public <T> OperationFuture<Boolean> prepend(long l, String s, T t, Transcoder<T> tTranscoder) {
    return getMemcachedClient().prepend(l, s, t, tTranscoder);
  }

  @Override
  public <T> OperationFuture<Boolean> prepend(String s, T t, Transcoder<T> tTranscoder) {
    return getMemcachedClient().prepend(s, t, tTranscoder);
  }

  @Override
  public <T> OperationFuture<CASResponse> asyncCAS(String s, long l, T t, Transcoder<T> tTranscoder) {
    return getMemcachedClient().asyncCAS(s, l, t, tTranscoder);
  }

  @Override
  public OperationFuture<CASResponse> asyncCAS(String s, long l, Object o) {
    return getMemcachedClient().asyncCAS(s, l, o);
  }

  @Override
  public <T> CASResponse cas(String s, long l, int i, T t, Transcoder<T> tTranscoder) {
    return getMemcachedClient().cas(s, l, i, t, tTranscoder);
  }

  @Override
  public CASResponse cas(String s, long l, Object o) {
    return getMemcachedClient().cas(s, l, o);
  }

  @Override
  public <T> OperationFuture<Boolean> add(String s, int i, T t, Transcoder<T> tTranscoder) {
    return getMemcachedClient().add(s, i, t, tTranscoder);
  }

  @Override
  public OperationFuture<Boolean> add(String s, int i, Object o) {
    return getMemcachedClient().add(s, i, o);
  }

  @Override
  public <T> OperationFuture<Boolean> set(String s, int i, T t, Transcoder<T> tTranscoder) {
    return getMemcachedClient().set(s, i, t, tTranscoder);
  }

  @Override
  public OperationFuture<Boolean> set(String s, int i, Object o) {
    return getMemcachedClient().set(s, i, o);
  }

  @Override
  public <T> OperationFuture<Boolean> replace(String s, int i, T t, Transcoder<T> tTranscoder) {
    return getMemcachedClient().replace(s, i, t, tTranscoder);
  }

  @Override
  public OperationFuture<Boolean> replace(String s, int i, Object o) {
    return getMemcachedClient().replace(s, i, o);
  }

  @Override
  public <T> GetFuture<T> asyncGet(String s, Transcoder<T> tTranscoder) {
    return getMemcachedClient().asyncGet(s, tTranscoder);
  }

  @Override
  public GetFuture<Object> asyncGet(String s) {
    return getMemcachedClient().asyncGet(s);
  }

  @Override
  public OperationFuture<CASValue<Object>> asyncGetAndTouch(String s, int i) {
    return getMemcachedClient().asyncGetAndTouch(s, i);
  }

  @Override
  public <T> OperationFuture<CASValue<T>> asyncGetAndTouch(String s, int i, Transcoder<T> tTranscoder) {
    return getMemcachedClient().asyncGetAndTouch(s, i, tTranscoder);
  }

  @Override
  public CASValue<Object> getAndTouch(String s, int i) {
    return getMemcachedClient().getAndTouch(s, i);
  }

  @Override
  public <T> CASValue<T> getAndTouch(String s, int i, Transcoder<T> tTranscoder) {
    return getMemcachedClient().getAndTouch(s, i, tTranscoder);
  }

  @Override
  public <T> OperationFuture<CASValue<T>> asyncGets(String s, Transcoder<T> tTranscoder) {
    return getMemcachedClient().asyncGets(s, tTranscoder);
  }

  @Override
  public OperationFuture<CASValue<Object>> asyncGets(String s) {
    return getMemcachedClient().asyncGets(s);
  }

  @Override
  public <T> CASValue<T> gets(String s, Transcoder<T> tTranscoder) {
    return getMemcachedClient().gets(s, tTranscoder);
  }

  @Override
  public CASValue<Object> gets(String s) {
    return getMemcachedClient().gets(s);
  }

  @Override
  public <T> T get(String s, Transcoder<T> tTranscoder) {
    return getMemcachedClient().get(s, tTranscoder);
  }

  @Override
  public Object get(String s) {
    return getMemcachedClient().get(s);
  }

  @Override
  public <T> BulkFuture<Map<String, T>> asyncGetBulk(Iterator<String> iter, Iterator<Transcoder<T>> trans) {
    return getMemcachedClient().asyncGetBulk(iter, trans);
  }

  @Override
  public <T> BulkFuture<Map<String, T>> asyncGetBulk(Collection<String> strings, Iterator<Transcoder<T>> trans) {
    return getMemcachedClient().asyncGetBulk(strings, trans);
  }

  @Override
  public <T> BulkFuture<Map<String, T>> asyncGetBulk(Iterator<String> iter, Transcoder<T> trans) {
    return getMemcachedClient().asyncGetBulk(iter, trans);
  }

  @Override
  public <T> BulkFuture<Map<String, T>> asyncGetBulk(Collection<String> strings, Transcoder<T> trans) {
    return getMemcachedClient().asyncGetBulk(strings, trans);
  }

  @Override
  public BulkFuture<Map<String, Object>> asyncGetBulk(Iterator<String> stringIterator) {
    return getMemcachedClient().asyncGetBulk(stringIterator);
  }

  @Override
  public BulkFuture<Map<String, Object>> asyncGetBulk(Collection<String> strings) {
    return getMemcachedClient().asyncGetBulk(strings);
  }

  @Override
  public <T> BulkFuture<Map<String, T>> asyncGetBulk(Transcoder<T> tTranscoder, String... strings) {
    return getMemcachedClient().asyncGetBulk(tTranscoder, strings);
  }

  @Override
  public BulkFuture<Map<String, Object>> asyncGetBulk(String... strings) {
    return getMemcachedClient().asyncGetBulk(strings);
  }

  @Override
  public <T> Map<String, T> getBulk(Iterator<String> stringIterator, Transcoder<T> tTranscoder) {
    return getMemcachedClient().getBulk(stringIterator, tTranscoder);
  }

  @Override
  public <T> Map<String, T> getBulk(Collection<String> strings, Transcoder<T> tTranscoder) {
    return getMemcachedClient().getBulk(strings, tTranscoder);
  }

  @Override
  public Map<String, Object> getBulk(Iterator<String> stringIterator) {
    return getMemcachedClient().getBulk(stringIterator);
  }

  @Override
  public Map<String, Object> getBulk(Collection<String> strings) {
    return getMemcachedClient().getBulk(strings);
  }

  @Override
  public <T> Map<String, T> getBulk(Transcoder<T> tTranscoder, String... strings) {
    return getMemcachedClient().getBulk(tTranscoder, strings);
  }

  @Override
  public Map<String, Object> getBulk(String... strings) {
    return getMemcachedClient().getBulk(strings);
  }

  @Override
  public <T> OperationFuture<Boolean> touch(String s, int i, Transcoder<T> tTranscoder) {
    return getMemcachedClient().touch(s, i, tTranscoder);
  }

  @Override
  public Future<Boolean> touch(String s, int i) {
    return getMemcachedClient().touch(s, i);
  }

  @Override
  public Map<SocketAddress, String> getVersions() {
    return getMemcachedClient().getVersions();
  }

  @Override
  public Map<SocketAddress, Map<String, String>> getStats() {
    return getMemcachedClient().getStats();
  }

  @Override
  public Map<SocketAddress, Map<String, String>> getStats(String s) {
    return getMemcachedClient().getStats(s);
  }

  @Override
  public long incr(String s, long l) {
    return getMemcachedClient().incr(s, l);
  }

  @Override
  public long incr(String s, int i) {
    return getMemcachedClient().incr(s, i);
  }

  @Override
  public long decr(String s, long l) {
    return getMemcachedClient().decr(s, l);
  }

  @Override
  public long decr(String s, int i) {
    return getMemcachedClient().decr(s, i);
  }

  @Override
  public long incr(String s, long l, long l2, int i) {
    return getMemcachedClient().incr(s, l, l2, i);
  }

  @Override
  public long incr(String s, int i, long l, int i2) {
    return getMemcachedClient().incr(s, i, l, i2);
  }

  @Override
  public long decr(String s, long l, long l2, int i) {
    return getMemcachedClient().decr(s, l, l2, i);
  }

  @Override
  public long decr(String s, int i, long l, int i2) {
    return getMemcachedClient().decr(s, i, l, i2);
  }

  @Override
  public OperationFuture<Long> asyncIncr(String s, long l) {
    return getMemcachedClient().asyncIncr(s, l);
  }

  @Override
  public OperationFuture<Long> asyncIncr(String s, int i) {
    return getMemcachedClient().asyncIncr(s, i);
  }

  @Override
  public OperationFuture<Long> asyncDecr(String s, long l) {
    return getMemcachedClient().asyncDecr(s, l);
  }

  @Override
  public OperationFuture<Long> asyncDecr(String s, int i) {
    return getMemcachedClient().asyncDecr(s, i);
  }

  @Override
  public long incr(String s, long l, long l2) {
    return getMemcachedClient().incr(s, l, l2);
  }

  @Override
  public long incr(String s, int i, long l) {
    return getMemcachedClient().incr(s, i, l);
  }

  @Override
  public long decr(String s, long l, long l2) {
    return getMemcachedClient().decr(s, l, l2);
  }

  @Override
  public long decr(String s, int i, long l) {
    return getMemcachedClient().decr(s, i, l);
  }

  @Override
  public OperationFuture<Boolean> delete(String s) {
    return getMemcachedClient().delete(s);
  }

  @Override
  public OperationFuture<Boolean> delete(String s, long l) {
    return getMemcachedClient().delete(s, l);
  }

  @Override
  public void shutdown() {
    shutdown(-1, TimeUnit.MILLISECONDS);
  }

  @Override
  public boolean waitForQueues(long l, TimeUnit timeUnit) {
    return getMemcachedClient().waitForQueues(l, timeUnit);
  }

  @Override
  public boolean addObserver(ConnectionObserver connectionObserver) {
    return getMemcachedClient().addObserver(connectionObserver);
  }

  @Override
  public boolean removeObserver(ConnectionObserver connectionObserver) {
    return getMemcachedClient().removeObserver(connectionObserver);
  }

  @Override
  public Set<String> listSaslMechanisms() {
    return getMemcachedClient().listSaslMechanisms();
  }

}

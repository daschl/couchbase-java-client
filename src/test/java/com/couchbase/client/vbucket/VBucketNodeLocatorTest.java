/**
 * Copyright (C) 2009-2013 Couchbase, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALING
 * IN THE SOFTWARE.
 */

package com.couchbase.client.vbucket;

import com.couchbase.client.vbucket.config.Config;
import com.couchbase.client.vbucket.config.ConfigFactory;

import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Arrays;

import com.couchbase.client.vbucket.config.CouchbaseConfig;
import junit.framework.TestCase;
import net.spy.memcached.HashAlgorithmRegistry;
import net.spy.memcached.MemcachedNode;
import org.junit.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 * A VBucketNodeLocatorTest.
 */
public class VBucketNodeLocatorTest extends TestCase {

  /**
   * Get primary memcached node for a given key.
   *
   * @pre Create three nodes and set their corresponding socket
   * addresses. Using the default config and the VBucketNodeLocator
   * instance, get primary node.
   * @post Asserts true if node1 is the primary node.
   * Verify the three nodes.
   */
  public void testGetPrimary() throws Exception {
    MemcachedNode node1 = createMock(MemcachedNode.class);
    MemcachedNode node2 = createMock(MemcachedNode.class);
    MemcachedNode node3 = createMock(MemcachedNode.class);
    InetSocketAddress address1 = new InetSocketAddress("127.0.0.1", 11211);
    InetSocketAddress address2 = new InetSocketAddress("127.0.0.1", 11210);
    InetSocketAddress address3 = new InetSocketAddress("127.0.0.1", 11212);

    expect(node1.getSocketAddress()).andReturn(address1);
    expect(node2.getSocketAddress()).andReturn(address2);
    expect(node3.getSocketAddress()).andReturn(address3);

    replay(node1, node2, node3);

    Config config = new CouchbaseConfig(
      Arrays.asList("127.0.0.1:1211", "127.0.0.1:11210", "127.0.0.1:11212"),
      Arrays.asList(
        Arrays.asList(0, 1, 2),
        Arrays.asList(1, 2, 0),
        Arrays.asList(2, 1, -1),
        Arrays.asList(1, 2, 0)
      ),
      2,
      Arrays.asList(new URL("http://10.2.1.67:5984/")),
      HashAlgorithmRegistry.lookupHashAlgorithm("CRC")
    );

    VBucketNodeLocator locator =
        new VBucketNodeLocator(Arrays.asList(node1, node2, node3), config);
    MemcachedNode resultNode = locator.getPrimary("key1");
    assertEquals(node1, resultNode);

    verify(node1, node2, node3);
  }

  /**
   * Get alternative memcached node for a given key.
   *
   * @pre Create three nodes and set their corresponding socket
   * addresses. Using the default config and the VBucketNodeLocator
   * instance, get primary and alternative nodes.
   */
  public void testGetAlternative() throws Exception {
    MemcachedNodeMockImpl node1 = new MemcachedNodeMockImpl();
    MemcachedNodeMockImpl node2 = new MemcachedNodeMockImpl();
    MemcachedNodeMockImpl node3 = new MemcachedNodeMockImpl();
    node1.setSocketAddress(new InetSocketAddress("127.0.0.1", 11211));
    node2.setSocketAddress(new InetSocketAddress("127.0.0.1", 11210));
    node3.setSocketAddress(new InetSocketAddress("127.0.0.1", 11212));
    Config config = new CouchbaseConfig(
      Arrays.asList("127.0.0.1:1211", "127.0.0.1:11210", "127.0.0.1:11212"),
      Arrays.asList(
        Arrays.asList(0, 1, 2),
        Arrays.asList(1, 2, 0),
        Arrays.asList(2, 1, -1),
        Arrays.asList(1, 2, 0)
      ),
      2,
      Arrays.asList(new URL("http://10.2.1.67:5984/")),
      HashAlgorithmRegistry.lookupHashAlgorithm("CRC")
    );
    VBucketNodeLocator locator =
        new VBucketNodeLocator(Arrays.asList((MemcachedNode) node1, node2,
            node3), config);
    MemcachedNode primary = locator.getPrimary("k1");
    MemcachedNode alternative =
        locator.getAlternative("k1", Arrays.asList(primary));
    alternative.getSocketAddress();
  }

  /**
   * Tests that there is no master server for the vbuckets.
   *
   * @pre Create two nodes and set their corresponding socket addresses.
   * Using the no replica config and the VBucketNodeLocator instance,
   * get primary node.
   * @post Succeeds to return that there is no primary
   * node as there are no replicas.
   */
  @Test
  public void testNoMasterServerForVbucket() throws Exception {
    MemcachedNodeMockImpl node1 = new MemcachedNodeMockImpl();
    MemcachedNodeMockImpl node2 = new MemcachedNodeMockImpl();
    node1.setSocketAddress(new InetSocketAddress("127.0.0.1", 11211));
    node2.setSocketAddress(new InetSocketAddress("127.0.0.1", 11210));

    Config config = new CouchbaseConfig(
      Arrays.asList("127.0.0.1:1211", "127.0.0.1:11210"),
      Arrays.asList(
        Arrays.asList(-1),
        Arrays.asList(-1),
        Arrays.asList(0),
        Arrays.asList(0)
      ),
      0,
      Arrays.asList(new URL("http://10.2.1.67:5984/")),
      HashAlgorithmRegistry.lookupHashAlgorithm("CRC")
    );

    VBucketNodeLocator locator = new VBucketNodeLocator(
      Arrays.asList((MemcachedNode) node1, node2),
      config);

    assertNull(locator.getPrimary("key1"));
  }
}

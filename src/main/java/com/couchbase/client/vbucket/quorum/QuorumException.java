package com.couchbase.client.vbucket.quorum;

import com.couchbase.client.vbucket.ConfigurationException;

/**
 * A special {@link ConfigurationException} which signals that the configuration
 * could not be determined because the quorum check failed.
 */
public class QuorumException extends ConfigurationException {

  public QuorumException() {
    super();
  }

  public QuorumException(String message) {
    super(message);
  }

  public QuorumException(String message, Throwable cause) {
    super(message, cause);
  }

  public QuorumException(Throwable cause) {
    super(cause);
  }

}

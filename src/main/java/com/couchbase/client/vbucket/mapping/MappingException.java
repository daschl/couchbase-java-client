package com.couchbase.client.vbucket.mapping;

import com.couchbase.client.vbucket.ConfigurationException;

/**
 * A special {@link com.couchbase.client.vbucket.ConfigurationException} which signals that the configuration
 * could not be determined because the quorum check failed.
 */
public class MappingException extends ConfigurationException {

  public MappingException() {
    super();
  }

  public MappingException(String message) {
    super(message);
  }

  public MappingException(String message, Throwable cause) {
    super(message, cause);
  }

  public MappingException(Throwable cause) {
    super(cause);
  }

}

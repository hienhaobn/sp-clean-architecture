package com.techsolify.aquapure.adapter.common.exception;

import org.springframework.http.HttpStatus;

/** Exception thrown when a requested resource is not found. */
public class ResourceNotFoundException extends BaseException {

  private static final String DEFAULT_MESSAGE = "Resource not found";
  private static final String ERROR_CODE = "RESOURCE_NOT_FOUND";
  private final String resourceName;
  private final String resourceId;

  /** Initialize with default message. */
  public ResourceNotFoundException() {
    super(DEFAULT_MESSAGE, ERROR_CODE, HttpStatus.NOT_FOUND);
    this.resourceName = null;
    this.resourceId = null;
  }

  /**
   * Initialize with custom message.
   *
   * @param message Error message
   */
  public ResourceNotFoundException(String message) {
    super(message, ERROR_CODE, HttpStatus.NOT_FOUND);
    this.resourceName = null;
    this.resourceId = null;
  }

  /**
   * Initialize with resource name and ID.
   *
   * @param resourceName Resource name
   * @param id Resource ID
   */
  public ResourceNotFoundException(String resourceName, Object id) {
    super(
        String.format("%s with id '%s' not found", resourceName, id),
        ERROR_CODE,
        HttpStatus.NOT_FOUND);
    this.resourceName = resourceName;
    this.resourceId = id.toString();
  }

  public ResourceNotFoundException(String resourceName, String resourceId) {
    super(
        String.format("Resource %s with id %s not found", resourceName, resourceId),
        ERROR_CODE,
        HttpStatus.NOT_FOUND);
    this.resourceName = resourceName;
    this.resourceId = resourceId;
  }

  public String getResourceName() {
    return resourceName;
  }

  public String getResourceId() {
    return resourceId;
  }
}

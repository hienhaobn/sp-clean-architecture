package com.techsolify.aquapure.adapter.common.exception;

import org.springframework.http.HttpStatus;

/** Exception thrown when an invalid request is made. */
public class BadRequestException extends BaseException {

  private static final String DEFAULT_MESSAGE = "Invalid request";
  private static final String ERROR_CODE = "BAD_REQUEST";

  /** Initialize with default message. */
  public BadRequestException() {
    super(DEFAULT_MESSAGE, ERROR_CODE, HttpStatus.BAD_REQUEST);
  }

  /**
   * Initialize with custom message.
   *
   * @param message Error message
   */
  public BadRequestException(String message) {
    super(message, ERROR_CODE, HttpStatus.BAD_REQUEST);
  }

  /**
   * Initialize with custom message and error code.
   *
   * @param message Error message
   * @param errorCode Error code
   */
  public BadRequestException(String message, String errorCode) {
    super(message, errorCode, HttpStatus.BAD_REQUEST);
  }
}

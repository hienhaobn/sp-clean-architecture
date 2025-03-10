package com.techsolify.aquapure.adapter.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * BaseException is the base class for all custom exceptions in the application. It helps define a
 * consistent structure for exceptions.
 */
@Getter
public class BaseException extends RuntimeException {

  private final String errorCode;
  private final HttpStatus status;

  /**
   * Initialize a BaseException with message, errorCode and status.
   *
   * @param message Error message
   * @param errorCode Error code
   * @param status HTTP status
   */
  public BaseException(String message, String errorCode, HttpStatus status) {
    super(message);
    this.errorCode = errorCode;
    this.status = status;
  }

  /**
   * Initialize a BaseException with message and status.
   *
   * @param message Error message
   * @param status HTTP status
   */
  public BaseException(String message, HttpStatus status) {
    this(message, status.name(), status);
  }
}

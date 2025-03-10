package com.techsolify.aquapure.adapter.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Generic API Response wrapper for standardizing all API responses. This class uses the Builder
 * pattern for flexible object creation and includes standard response fields like status, message,
 * and data.
 *
 * @param <T> The type of data being returned
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

  private final boolean success;
  private final String message;
  private final String code;
  private final T data;
  private final Integer pageNumber;
  private final Integer pageSize;
  private final Long totalElements;
  private final Integer totalPages;
  private final List<String> errors;
  private final Map<String, String> validationErrors;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private final LocalDateTime timestamp;

  @Builder(access = AccessLevel.PRIVATE)
  private ApiResponse(
      Boolean success,
      String message,
      String code,
      T data,
      Integer pageNumber,
      Integer pageSize,
      Long totalElements,
      Integer totalPages,
      List<String> errors,
      Map<String, String> validationErrors,
      LocalDateTime timestamp) {
    this.success = success != null ? success : true;
    this.message = message;
    this.code = code;
    this.data = data;
    this.pageNumber = pageNumber;
    this.pageSize = pageSize;
    this.totalElements = totalElements;
    this.totalPages = totalPages;
    this.errors = errors;
    this.validationErrors = validationErrors;
    this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
  }

  /**
   * Creates a successful response with data.
   *
   * @param data The data to be included in the response
   * @param <T> The type of data
   * @return A new ApiResponse instance
   */
  public static <T> ApiResponse<T> success(T data) {
    return ApiResponse.<T>builder().success(true).data(data).timestamp(LocalDateTime.now()).build();
  }

  /**
   * Creates a successful response with data and a message.
   *
   * @param data The data to be included in the response
   * @param message The success message
   * @param <T> The type of data
   * @return A new ApiResponse instance
   */
  public static <T> ApiResponse<T> success(T data, String message) {
    return ApiResponse.<T>builder()
        .success(true)
        .data(data)
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Creates a successful response with only a message.
   *
   * @param message The success message
   * @param <T> The type of data
   * @return A new ApiResponse instance
   */
  public static <T> ApiResponse<T> success(String message) {
    return ApiResponse.<T>builder()
        .success(true)
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Creates an error response with a message.
   *
   * @param message The error message
   * @param <T> The type of data
   * @return A new ApiResponse instance
   */
  public static <T> ApiResponse<T> error(String message) {
    return ApiResponse.<T>builder()
        .success(false)
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Creates an error response with a message and error code.
   *
   * @param message The error message
   * @param code The error code
   * @param <T> The type of data
   * @return A new ApiResponse instance
   */
  public static <T> ApiResponse<T> error(String message, String code) {
    return ApiResponse.<T>builder()
        .success(false)
        .message(message)
        .code(code)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Creates an error response with a message and list of errors.
   *
   * @param message The error message
   * @param errors List of detailed error messages
   * @param <T> The type of data
   * @return A new ApiResponse instance
   */
  public static <T> ApiResponse<T> error(String message, List<String> errors) {
    return ApiResponse.<T>builder()
        .success(false)
        .message(message)
        .errors(new ArrayList<>(errors))
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Creates an error response with validation errors.
   *
   * @param message The error message
   * @param validationErrors Map of field names to error messages
   * @param <T> The type of data
   * @return A new ApiResponse instance
   */
  public static <T> ApiResponse<T> validationError(
      String message, Map<String, String> validationErrors) {
    return ApiResponse.<T>builder()
        .success(false)
        .message(message)
        .code("VALIDATION_ERROR")
        .validationErrors(new HashMap<>(validationErrors))
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Creates a paginated response with data and pagination information.
   *
   * @param data The paginated data
   * @param pageNumber Current page number
   * @param pageSize Page size
   * @param totalElements Total number of elements
   * @param totalPages Total number of pages
   * @param <T> The type of data
   * @return A new ApiResponse instance
   */
  public static <T> ApiResponse<T> page(
      T data, Integer pageNumber, Integer pageSize, Long totalElements, Integer totalPages) {
    return ApiResponse.<T>builder()
        .success(true)
        .data(data)
        .pageNumber(pageNumber)
        .pageSize(pageSize)
        .totalElements(totalElements)
        .totalPages(totalPages)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Creates an error response from an HTTP status.
   *
   * @param status HTTP status
   * @param message Error message
   * @param <T> The type of data
   * @return A new ApiResponse instance
   */
  public static <T> ApiResponse<T> fromHttpStatus(HttpStatus status, String message) {
    return ApiResponse.<T>builder()
        .success(status.is2xxSuccessful())
        .message(message)
        .code(status.name())
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * Checks if the response contains any errors.
   *
   * @return true if the response has errors, false otherwise
   */
  public boolean hasErrors() {
    return !success
        || (errors != null && !errors.isEmpty())
        || (validationErrors != null && !validationErrors.isEmpty());
  }

  /**
   * Checks if the response is paginated.
   *
   * @return true if the response contains pagination information, false otherwise
   */
  public boolean isPaginated() {
    return pageNumber != null && pageSize != null && totalElements != null && totalPages != null;
  }

  /**
   * Creates a copy of this response with a different data type.
   *
   * @param newData The new data
   * @param <R> The type of the new data
   * @return A new ApiResponse instance with the new data type
   */
  public <R> ApiResponse<R> withData(R newData) {
    return ApiResponse.<R>builder()
        .success(this.success)
        .message(this.message)
        .code(this.code)
        .data(newData)
        .pageNumber(this.pageNumber)
        .pageSize(this.pageSize)
        .totalElements(this.totalElements)
        .totalPages(this.totalPages)
        .errors(this.errors != null ? new ArrayList<>(this.errors) : null)
        .validationErrors(
            this.validationErrors != null ? new HashMap<>(this.validationErrors) : null)
        .timestamp(this.timestamp)
        .build();
  }
}

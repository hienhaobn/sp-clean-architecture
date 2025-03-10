package com.techsolify.aquapure.adapter.controller;

import com.techsolify.aquapure.adapter.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {

  protected <T> ResponseEntity<ApiResponse<T>> successResponse(T data) {
    return ResponseEntity.ok(ApiResponse.success(data));
  }

  protected <T> ResponseEntity<ApiResponse<T>> successResponse(T data, String message) {
    return ResponseEntity.ok(ApiResponse.success(data, message));
  }

  protected <T> ResponseEntity<ApiResponse<T>> successResponse(String message) {
    return ResponseEntity.ok(ApiResponse.success(message));
  }

  protected <T> ResponseEntity<ApiResponse<T>> createdResponse(T data, String message) {
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, message));
  }

  protected <T> ResponseEntity<ApiResponse<T>> noContentResponse(String message) {
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(message));
  }

  protected <T> ResponseEntity<ApiResponse<T>> errorResponse(String message, HttpStatus status) {
    return ResponseEntity.status(status).body(ApiResponse.error(message));
  }

  protected <T> ResponseEntity<ApiResponse<T>> errorResponse(
      String message, String code, HttpStatus status) {
    return ResponseEntity.status(status).body(ApiResponse.error(message, code));
  }
}

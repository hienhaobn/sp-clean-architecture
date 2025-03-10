package com.techsolify.aquapure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsolify.aquapure.adapter.common.ApiResponse;
import com.techsolify.aquapure.adapter.common.exception.BadRequestException;
import com.techsolify.aquapure.adapter.common.exception.BaseException;
import com.techsolify.aquapure.adapter.common.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

@Component
public class CustomExceptionResolver implements HandlerExceptionResolver {

  private final ObjectMapper objectMapper;

  public CustomExceptionResolver(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public ModelAndView resolveException(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    ApiResponse<Void> apiResponse;
    HttpStatus status;

    if (ex instanceof BaseException) {
      BaseException baseException = (BaseException) ex;
      apiResponse = ApiResponse.error(baseException.getMessage(), baseException.getErrorCode());
      status = baseException.getStatus();
    } else if (ex instanceof ResourceNotFoundException) {
      ResourceNotFoundException notFoundException = (ResourceNotFoundException) ex;
      apiResponse =
          ApiResponse.error(
              String.format(
                  "Resource %s with id %s not found",
                  notFoundException.getResourceName(), notFoundException.getResourceId()),
              "RESOURCE_NOT_FOUND");
      status = HttpStatus.NOT_FOUND;
    } else if (ex instanceof EntityNotFoundException) {
      apiResponse = ApiResponse.error(ex.getMessage(), "ENTITY_NOT_FOUND");
      status = HttpStatus.NOT_FOUND;
    } else if (ex instanceof BadRequestException) {
      BadRequestException badRequestException = (BadRequestException) ex;
      apiResponse = ApiResponse.error(badRequestException.getMessage(), "BAD_REQUEST");
      status = HttpStatus.BAD_REQUEST;
    } else if (ex instanceof MissingServletRequestParameterException) {
      apiResponse =
          ApiResponse.error(
              "Required parameter is missing: " + ex.getMessage(), "MISSING_PARAMETER");
      status = HttpStatus.BAD_REQUEST;
    } else if (ex instanceof MethodArgumentTypeMismatchException) {
      MethodArgumentTypeMismatchException mismatchException =
          (MethodArgumentTypeMismatchException) ex;
      apiResponse =
          ApiResponse.error(
              String.format(
                  "Invalid value '%s' for parameter '%s'. Expected type: %s",
                  mismatchException.getValue(),
                  mismatchException.getName(),
                  mismatchException.getRequiredType().getSimpleName()),
              "INVALID_PARAMETER_TYPE");
      status = HttpStatus.BAD_REQUEST;
    } else if (ex instanceof DuplicateKeyException) {
      apiResponse =
          ApiResponse.error("Resource already exists: " + ex.getMessage(), "DUPLICATE_RESOURCE");
      status = HttpStatus.CONFLICT;
    } else if (ex instanceof DataIntegrityViolationException) {
      apiResponse =
          ApiResponse.error(
              "Data integrity violation: " + ex.getMessage(), "DATA_INTEGRITY_VIOLATION");
      status = HttpStatus.CONFLICT;
    } else {
      apiResponse =
          ApiResponse.error(
              "An unexpected error occurred: " + ex.getMessage(), "INTERNAL_SERVER_ERROR");
      status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    try {
      response.setStatus(status.value());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    } catch (IOException e) {
      // Log error
      e.printStackTrace();
    }

    return new ModelAndView();
  }
}

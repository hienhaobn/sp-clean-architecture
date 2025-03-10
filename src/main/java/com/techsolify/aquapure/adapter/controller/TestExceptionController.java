package com.techsolify.aquapure.adapter.controller;

import com.techsolify.aquapure.adapter.common.ApiResponse;
import com.techsolify.aquapure.adapter.common.exception.BadRequestException;
import com.techsolify.aquapure.adapter.common.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test-exceptions")
@Tag(
    name = "Test Exceptions",
    description = "Endpoints to test various exception handling scenarios")
public class TestExceptionController extends BaseExceptionHandlerController {

  @Operation(
      summary = "Test ResourceNotFoundException",
      description = "Throws ResourceNotFoundException with provided resource name and ID")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Resource not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
      })
  @GetMapping("/resource-not-found")
  public void testResourceNotFound(
      @Parameter(description = "Resource name", example = "Product") @RequestParam
          String resourceName,
      @Parameter(description = "Resource ID", example = "123") @RequestParam String resourceId) {
    throw new ResourceNotFoundException(resourceName, resourceId);
  }

  @Operation(
      summary = "Test BadRequestException",
      description = "Throws BadRequestException with provided message")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
      })
  @GetMapping("/bad-request")
  public void testBadRequest(
      @Parameter(description = "Error message", example = "Invalid input data") @RequestParam
          String message) {
    throw new BadRequestException(message);
  }

  @Operation(
      summary = "Test Missing Parameter",
      description = "Endpoint requiring a parameter to test missing parameter handling")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Missing required parameter",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
      })
  @GetMapping("/missing-parameter")
  public void testMissingParameter(
      @Parameter(description = "Required parameter") @RequestParam(required = true)
          String requiredParam) {
    // This will throw MissingServletRequestParameterException if parameter is not provided
  }

  @Operation(
      summary = "Test Invalid Parameter Type",
      description = "Endpoint requiring a numeric parameter to test type mismatch")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid parameter type",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
      })
  @GetMapping("/invalid-parameter-type")
  public void testInvalidParameterType(
      @Parameter(description = "Numeric parameter", example = "123") @RequestParam
          Long numericParam) {
    // This will throw MethodArgumentTypeMismatchException if non-numeric value is provided
  }
}

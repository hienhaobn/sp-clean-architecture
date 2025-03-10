package com.techsolify.aquapure.adapter.controller;

import com.techsolify.aquapure.adapter.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "API Status", description = "API status and health endpoints")
public class ApiStatusController extends BaseExceptionHandlerController {

  // Constants for response messages
  private static final String API_STATUS_SUCCESS = "API is running successfully";
  private static final String API_HEALTH_SUCCESS = "Service is healthy";

  @Operation(summary = "Get API status", description = "Returns the current status of the API")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "API status retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
      })
  @GetMapping("/status")
  public ResponseEntity<ApiResponse<Map<String, Object>>> getApiStatus() {
    Map<String, Object> status = new HashMap<>();
    status.put("status", "UP");
    status.put("version", "1.0.0");
    status.put("serverTime", System.currentTimeMillis());

    return successResponse(status, API_STATUS_SUCCESS);
  }

  @Operation(
      summary = "Get health status",
      description = "Returns the health status of the service")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Health status retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
      })
  @GetMapping("/health")
  public ResponseEntity<ApiResponse<String>> getHealthStatus() {
    return successResponse("Healthy", API_HEALTH_SUCCESS);
  }
}

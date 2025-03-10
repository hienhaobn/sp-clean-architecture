package com.techsolify.aquapure.aspect;

import com.techsolify.aquapure.adapter.common.ApiResponse;
import com.techsolify.aquapure.adapter.common.exception.BaseException;
import jakarta.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.stream.Collectors;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Aspect
@Component
public class ExceptionHandlingAspect {

  @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
  public void restControllerPointcut() {}

  @Around("restControllerPointcut()")
  public Object handleExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
    try {
      return joinPoint.proceed();
    } catch (BaseException ex) {
      return ResponseEntity.status(ex.getStatus())
          .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode()));
    } catch (EntityNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error(ex.getMessage(), "NOT_FOUND"));
    } catch (DataIntegrityViolationException ex) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(ApiResponse.error("Data integrity violation: " + ex.getMessage(), "CONFLICT"));
    } catch (MethodArgumentNotValidException ex) {
      Map<String, String> validationErrors =
          ex.getBindingResult().getFieldErrors().stream()
              .collect(
                  Collectors.toMap(
                      fieldError -> fieldError.getField(),
                      fieldError ->
                          fieldError.getDefaultMessage() != null
                              ? fieldError.getDefaultMessage()
                              : "Error",
                      (existing, replacement) -> existing));

      return ResponseEntity.badRequest()
          .body(ApiResponse.validationError("Validation failed", validationErrors));
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(
              ApiResponse.error("An error occurred: " + ex.getMessage(), "INTERNAL_SERVER_ERROR"));
    }
  }
}

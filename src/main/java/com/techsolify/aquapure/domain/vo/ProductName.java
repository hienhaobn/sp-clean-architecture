package com.techsolify.aquapure.domain.vo;

import com.techsolify.aquapure.domain.exception.InvalidProductException;
import java.util.Objects;

/**
 * Value Object representing a product name. Enforces business rules for product names: - Must not
 * be null or empty - Must be between 3 and 100 characters - Must not contain only whitespace
 */
public class ProductName {

  private final String value;

  private ProductName(String value) {
    this.value = value;
  }

  public static ProductName of(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new InvalidProductException("Product name cannot be empty");
    }
    if (value.length() < 3) {
      throw new InvalidProductException("Product name must be at least 3 characters long");
    }
    if (value.length() > 100) {
      throw new InvalidProductException("Product name cannot exceed 100 characters");
    }
    return new ProductName(value.trim());
  }

  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ProductName that = (ProductName) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return value;
  }
}

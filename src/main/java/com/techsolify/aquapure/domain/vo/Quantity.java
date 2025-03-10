package com.techsolify.aquapure.domain.vo;

import com.techsolify.aquapure.domain.exception.InvalidProductException;
import java.util.Objects;

public class Quantity {

  private final int value;

  private Quantity(int value) {
    this.value = value;
  }

  public static Quantity of(int value) {
    if (value < 0) {
      throw new InvalidProductException("Quantity cannot be negative");
    }
    return new Quantity(value);
  }

  public int getValue() {
    return value;
  }

  public boolean isInStock() {
    return value > 0;
  }

  public Quantity add(Quantity other) {
    return new Quantity(this.value + other.value);
  }

  public Quantity subtract(Quantity other) {
    int newValue = this.value - other.value;
    if (newValue < 0) {
      throw new InvalidProductException("Cannot subtract to negative quantity");
    }
    return new Quantity(newValue);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Quantity quantity = (Quantity) o;
    return value == quantity.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}

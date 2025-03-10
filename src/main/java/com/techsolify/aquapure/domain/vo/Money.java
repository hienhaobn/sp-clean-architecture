package com.techsolify.aquapure.domain.vo;

import com.techsolify.aquapure.domain.exception.InvalidProductException;
import java.math.BigDecimal;
import java.util.Objects;

public class Money {

  private final BigDecimal amount;

  private Money(BigDecimal amount) {
    this.amount = amount != null ? amount : BigDecimal.ZERO;
  }

  public static Money of(BigDecimal amount) {
    if (amount != null && amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new InvalidProductException("Price cannot be negative");
    }
    return new Money(amount);
  }

  public static Money of(double amount) {
    return of(BigDecimal.valueOf(amount));
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public Money add(Money other) {
    return new Money(this.amount.add(other.amount));
  }

  public Money subtract(Money other) {
    BigDecimal result = this.amount.subtract(other.amount);
    if (result.compareTo(BigDecimal.ZERO) < 0) {
      throw new InvalidProductException("Result cannot be negative");
    }
    return new Money(result);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Money money = (Money) o;
    return amount.compareTo(money.amount) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount);
  }

  @Override
  public String toString() {
    return amount.toString();
  }
}

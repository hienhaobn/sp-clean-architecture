package com.techsolify.aquapure.domain.vo;

import com.techsolify.aquapure.domain.exception.InvalidProductException;
import java.util.Objects;

public class ProductId {
    private final Long value;

    private ProductId(Long value) {
        this.value = value;
    }

    public static ProductId of(Long value) {
        if (value == null || value <= 0) {
            throw new InvalidProductException("Product ID must be greater than 0");
        }
        return new ProductId(value);
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProductId productId = (ProductId) o;
        return Objects.equals(value, productId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
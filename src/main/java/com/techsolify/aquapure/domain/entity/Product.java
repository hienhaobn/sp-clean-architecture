package com.techsolify.aquapure.domain.entity;

import com.techsolify.aquapure.domain.vo.Money;
import com.techsolify.aquapure.domain.vo.ProductId;
import com.techsolify.aquapure.domain.vo.ProductName;
import com.techsolify.aquapure.domain.vo.Quantity;
import java.math.BigDecimal;

/**
 * Product Entity
 * Represents a product in the system with its core business rules.
 * This entity is framework-independent and contains pure domain logic.
 */
public class Product {
    private ProductId id;
    private ProductName name;
    private String description;
    private Money price;
    private Quantity quantity;

    private Product(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.price = builder.price;
        this.quantity = builder.quantity;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ProductId getId() {
        return id;
    }

    public ProductName getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Money getPrice() {
        return price;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public static class Builder {
        private ProductId id;
        private ProductName name;
        private String description;
        private Money price;
        private Quantity quantity;

        public Builder id(Long id) {
            this.id = ProductId.of(id);
            return this;
        }

        public Builder name(String name) {
            this.name = ProductName.of(name);
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder price(Money price) {
            this.price = price;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = Quantity.of(quantity);
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }

    /**
     * Checks if the product is currently in stock
     * 
     * @return true if quantity is greater than 0, false otherwise
     */
    public boolean isInStock() {
        return quantity.isInStock();
    }

    /**
     * Calculates the total value of the product based on price and quantity
     * 
     * @return Money representing the total value
     */
    public Money getTotalPrice() {
        return Money.of(price.getAmount().multiply(BigDecimal.valueOf(quantity.getValue())));
    }
}

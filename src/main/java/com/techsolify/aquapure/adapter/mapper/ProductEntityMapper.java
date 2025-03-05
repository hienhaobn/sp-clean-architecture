package com.techsolify.aquapure.adapter.mapper;

import org.springframework.stereotype.Component;
import com.techsolify.aquapure.adapter.entity.ProductEntity;
import com.techsolify.aquapure.domain.entity.Product;
import com.techsolify.aquapure.domain.vo.Money;
import com.techsolify.aquapure.domain.vo.ProductName;

/**
 * Mapper responsible for converting between domain Product and persistence
 * ProductEntity.
 * Handles all the conversions between value objects and primitive types.
 */
@Component
public class ProductEntityMapper {
    public Product toDomain(ProductEntity entity) {
        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(Money.of(entity.getPrice()))
                .quantity(entity.getQuantity())
                .build();
    }

    public ProductEntity toEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setId(product.getId().getValue());
        entity.setName(product.getName().getValue());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice().getAmount().doubleValue());
        entity.setQuantity(product.getQuantity().getValue());
        return entity;
    }
}
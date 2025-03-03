package com.techsolify.aquapure.adapter.mapper;

import org.springframework.stereotype.Component;

import com.techsolify.aquapure.adapter.entity.ProductEntity;
import com.techsolify.aquapure.domain.entity.Product;

@Component
public class ProductEntityMapper {
    public Product toDomain(ProductEntity entity) {
        Product product = new Product();
        product.setId(entity.getId());
        product.setName(entity.getName());
        product.setDescription(entity.getDescription());
        product.setPrice(entity.getPrice());
        product.setQuantity(entity.getQuantity());
        return product;
    }
    
    public ProductEntity toEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setId(product.getId());
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());
        entity.setQuantity(product.getQuantity());
        return entity;
    }
}
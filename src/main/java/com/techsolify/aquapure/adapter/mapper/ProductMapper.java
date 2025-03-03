package com.techsolify.aquapure.adapter.mapper;

import org.springframework.stereotype.Component;

import com.techsolify.aquapure.adapter.dto.ProductDTO;
import com.techsolify.aquapure.domain.entity.Product;

@Component
public class ProductMapper {
    public ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setInStock(product.isInStock());
        return dto;
    }
    
    public Product toEntity(ProductDTO dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        return product;
    }
}

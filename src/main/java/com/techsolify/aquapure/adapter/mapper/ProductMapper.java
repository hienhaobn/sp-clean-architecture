package com.techsolify.aquapure.adapter.mapper;

import org.springframework.stereotype.Component;
import com.techsolify.aquapure.adapter.dto.ProductDTO;
import com.techsolify.aquapure.domain.entity.Product;
import com.techsolify.aquapure.domain.vo.Money;
import com.techsolify.aquapure.domain.vo.ProductName;

@Component
public class ProductMapper {
    public ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId().getValue());
        dto.setName(product.getName().getValue());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice().getAmount().doubleValue());
        dto.setQuantity(product.getQuantity().getValue());
        dto.setInStock(product.isInStock());
        return dto;
    }

    public Product toEntity(ProductDTO dto) {
        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .price(Money.of(dto.getPrice()))
                .quantity(dto.getQuantity())
                .build();
    }
}

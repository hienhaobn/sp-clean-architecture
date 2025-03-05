package com.techsolify.aquapure.domain.usecase.impl;

import java.util.List;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import com.techsolify.aquapure.domain.entity.Product;
import com.techsolify.aquapure.domain.repository.ProductRepository;
import com.techsolify.aquapure.domain.usecase.ProductUseCase;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductUseCaseImpl implements ProductUseCase {
    private final ProductRepository productRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public Product createProduct(Product product) {
        // Validation is now handled by Value Objects
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        // Check if product exists
        getProductById(id);

        // Create new product with updated fields using builder
        Product updatedProduct = Product.builder()
                .id(id)
                .name(product.getName().getValue())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity().getValue())
                .build();

        return productRepository.save(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        // Check if product exists
        getProductById(id);
        productRepository.deleteById(id);
    }
}

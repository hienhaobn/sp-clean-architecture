package com.techsolify.aquapure.domain.usecase.impl;

import java.util.List;

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
        // Validate product
        if (product.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }

        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        // Check if product exists
        Product existingProduct = getProductById(id);

        // Update fields
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());

        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        // Check if product exists
        getProductById(id);

        productRepository.deleteById(id);
    }

}

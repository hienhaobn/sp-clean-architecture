package com.techsolify.aquapure.domain.repository;

import java.util.List;
import java.util.Optional;

import com.techsolify.aquapure.domain.entity.Product;

public interface ProductRepository {
    List<Product> findAll();
    Optional<Product> findById(Long id);
    Product save(Product product);
    void deleteById(Long id);
}

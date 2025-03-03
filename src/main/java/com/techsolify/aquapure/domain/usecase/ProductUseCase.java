package com.techsolify.aquapure.domain.usecase;

import java.util.List;

import com.techsolify.aquapure.domain.entity.Product;

// UseeCase: Product
// Định nghĩa các method mà client có thể gọi để thực hiện các hoạt động của hệ thống
public interface ProductUseCase {
    List<Product> getAllProducts();

    Product getProductById(Long id);

    Product createProduct(Product product);

    Product updateProduct(Long id, Product product);

    void deleteProduct(Long id);
}

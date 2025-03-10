package com.techsolify.aquapure.domain.usecase.product.query;

import com.techsolify.aquapure.domain.entity.Product;
import com.techsolify.aquapure.domain.exception.ProductNotFoundException;
import com.techsolify.aquapure.domain.repository.ProductRepository;

public class GetProductByIdUseCase {

  private final ProductRepository productRepository;

  public GetProductByIdUseCase(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public Product execute(Long id) {
    return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
  }
}

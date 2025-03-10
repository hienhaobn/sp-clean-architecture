package com.techsolify.aquapure.domain.usecase.product.command;

import com.techsolify.aquapure.domain.entity.Product;
import com.techsolify.aquapure.domain.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateProductUseCase {

  private static final Logger logger = LoggerFactory.getLogger(CreateProductUseCase.class);

  private final ProductRepository productRepository;

  public CreateProductUseCase(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public Product execute(Product product) {
    logger.info("Creating new product: {}", product.getName());
    try {
      Product savedProduct = productRepository.save(product);
      logger.info("Successfully created product with id: {}", savedProduct.getId());
      return savedProduct;
    } catch (Exception e) {
      logger.error("Failed to create product: {}", product.getName(), e);
      throw e;
    }
  }
}

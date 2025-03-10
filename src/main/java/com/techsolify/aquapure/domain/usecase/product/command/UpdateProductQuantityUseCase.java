package com.techsolify.aquapure.domain.usecase.product.command;

import com.techsolify.aquapure.domain.entity.Product;
import com.techsolify.aquapure.domain.exception.ProductNotFoundException;
import com.techsolify.aquapure.domain.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateProductQuantityUseCase {

  private static final Logger logger = LoggerFactory.getLogger(UpdateProductQuantityUseCase.class);

  private final ProductRepository productRepository;

  public UpdateProductQuantityUseCase(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public Product execute(Long productId, int newQuantity) {
    logger.info("Updating quantity for product {}: {}", productId, newQuantity);
    try {
      Product product =
          productRepository
              .findById(productId)
              .orElseThrow(() -> new ProductNotFoundException(productId));

      Product updatedProduct =
          Product.builder()
              .id(product.getId().getValue())
              .name(product.getName().getValue())
              .description(product.getDescription())
              .price(product.getPrice())
              .quantity(newQuantity)
              .build();

      Product savedProduct = productRepository.save(updatedProduct);
      logger.info("Successfully updated quantity for product {}", productId);
      return savedProduct;
    } catch (Exception e) {
      logger.error("Failed to update quantity for product {}", productId, e);
      throw e;
    }
  }
}

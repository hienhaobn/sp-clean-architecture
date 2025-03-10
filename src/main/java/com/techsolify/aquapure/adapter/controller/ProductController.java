package com.techsolify.aquapure.adapter.controller;

import com.techsolify.aquapure.adapter.dto.ProductDTO;
import com.techsolify.aquapure.adapter.mapper.ProductMapper;
import com.techsolify.aquapure.domain.entity.Product;
import com.techsolify.aquapure.domain.usecase.ProductUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Product management API")
public class ProductController {

  private final ProductUseCase productUseCase;

  private final ProductMapper productMapper;

  @Operation(summary = "Get all products", description = "Returns a list of all available products")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the list of products",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProductDTO.class)))
      })
  @GetMapping
  public ResponseEntity<List<ProductDTO>> getAllProducts() {
    List<Product> products = productUseCase.getAllProducts();
    List<ProductDTO> productDTOs =
        products.stream().map(productMapper::toDTO).collect(Collectors.toList());
    return ResponseEntity.ok(productDTOs);
  }

  @Operation(summary = "Get product by ID", description = "Returns a single product by its ID")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Product found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
      })
  @GetMapping("/{id}")
  public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
    Product product = productUseCase.getProductById(id);
    return ResponseEntity.ok(productMapper.toDTO(product));
  }

  @Operation(
      summary = "Create a new product",
      description = "Creates a new product and returns the created product")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Product successfully created",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid product data", content = @Content)
      })
  @PostMapping
  public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
    Product product = productMapper.toEntity(productDTO);
    Product createdProduct = productUseCase.createProduct(product);
    return new ResponseEntity<>(productMapper.toDTO(createdProduct), HttpStatus.CREATED);
  }

  @Operation(
      summary = "Update an existing product",
      description = "Updates a product by its ID and returns the updated product")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Product successfully updated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid product data", content = @Content)
      })
  @PutMapping("/{id}")
  public ResponseEntity<ProductDTO> updateProduct(
      @PathVariable Long id, @RequestBody ProductDTO productDTO) {
    Product product = productMapper.toEntity(productDTO);
    Product updatedProduct = productUseCase.updateProduct(id, product);
    return ResponseEntity.ok(productMapper.toDTO(updatedProduct));
  }

  @Operation(summary = "Delete a product", description = "Deletes a product by its ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Product successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Product not found")
      })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productUseCase.deleteProduct(id);
    return ResponseEntity.noContent().build();
  }
}

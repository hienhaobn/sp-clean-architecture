package com.techsolify.aquapure.adapter.controller;

import com.techsolify.aquapure.adapter.common.ApiResponse;
import com.techsolify.aquapure.adapter.dto.ProductDTO;
import com.techsolify.aquapure.adapter.mapper.ProductMapper;
import com.techsolify.aquapure.domain.entity.Product;
import com.techsolify.aquapure.domain.usecase.ProductUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Product management API")
public class ProductController extends BaseExceptionHandlerController {

  private final ProductUseCase productUseCase;
  private final ProductMapper productMapper;

  // Constants for response messages
  private static final String PRODUCTS_RETRIEVED_SUCCESS = "Products retrieved successfully";
  private static final String PRODUCT_RETRIEVED_SUCCESS = "Product retrieved successfully";
  private static final String PRODUCT_CREATED_SUCCESS = "Product created successfully";
  private static final String PRODUCT_UPDATED_SUCCESS = "Product updated successfully";
  private static final String PRODUCT_DELETED_SUCCESS = "Product deleted successfully";

  @Operation(summary = "Get all products", description = "Returns a list of all available products")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the list of products",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
      })
  @GetMapping
  public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProducts() {
    List<Product> products = productUseCase.getAllProducts();
    List<ProductDTO> productDTOs =
        products.stream().map(productMapper::toDTO).collect(Collectors.toList());
    return successResponse(productDTOs, PRODUCTS_RETRIEVED_SUCCESS);
  }

  @Operation(summary = "Get product by ID", description = "Returns a single product by its ID")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Product found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content)
      })
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable Long id) {
    Product product = productUseCase.getProductById(id);
    return successResponse(productMapper.toDTO(product), PRODUCT_RETRIEVED_SUCCESS);
  }

  @Operation(
      summary = "Create a new product",
      description = "Creates a new product and returns the created product")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Product successfully created",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid product data",
            content = @Content)
      })
  @PostMapping
  public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@RequestBody ProductDTO productDTO) {
    Product createdProduct = productUseCase.createProduct(productMapper.toEntity(productDTO));
    return createdResponse(productMapper.toDTO(createdProduct), PRODUCT_CREATED_SUCCESS);
  }

  @Operation(
      summary = "Update an existing product",
      description = "Updates a product by its ID and returns the updated product")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Product successfully updated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid product data",
            content = @Content)
      })
  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(
      @PathVariable Long id, @RequestBody ProductDTO productDTO) {
    Product updatedProduct = productUseCase.updateProduct(id, productMapper.toEntity(productDTO));
    return successResponse(productMapper.toDTO(updatedProduct), PRODUCT_UPDATED_SUCCESS);
  }

  @Operation(summary = "Delete a product", description = "Deletes a product by its ID")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "Product successfully deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Product not found")
      })
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
    productUseCase.deleteProduct(id);
    return noContentResponse(PRODUCT_DELETED_SUCCESS);
  }
}

package com.techsolify.aquapure.adapter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

  private Long id;

  private String name;

  private String description;

  private double price;

  private int quantity;

  private boolean inStock;
}

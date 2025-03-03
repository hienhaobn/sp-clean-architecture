package com.techsolify.aquapure.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entity: Product
// chứa các entity base của hệ thống và các business rule không phụ thuộc vào bất kỳ framework nào
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;
    private String name;
    private String description;
    private double price;
    private int quantity;

    // business rule: Kiểm tra số lượng sản phẩm trong kho
    public boolean isInStock() {
        return quantity > 0;
    }

    // business rule: Tính tổng giá trị sản phẩm
    public double getTotalPrice() {
        return price * quantity;
    }
}

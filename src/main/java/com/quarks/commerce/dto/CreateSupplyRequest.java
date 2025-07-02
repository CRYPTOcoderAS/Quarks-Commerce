package com.quarks.commerce.dto;

import java.math.BigDecimal;

public class CreateSupplyRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private int quantity;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
} 
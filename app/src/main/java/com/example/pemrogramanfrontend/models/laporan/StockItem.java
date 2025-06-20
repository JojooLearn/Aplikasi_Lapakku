package com.example.pemrogramanfrontend.models.laporan;

public class StockItem {
    private String productName;
    private int quantity;

    public StockItem(String productName, int quantity) {
        this.productName = productName;
        this.quantity = quantity;
    }

    // Getters and setters...

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
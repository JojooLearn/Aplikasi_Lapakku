package com.example.pemrogramanfrontend.models;

public class SaleItem {
    private int id;
    private int sale_id;
    private int product_id;
    private String product_name;
    private int quantity;
    private int price;
    private int subtotal;

    // Getter & Setter

    public int getId() { return id; }
    public int getSaleId() { return sale_id; }
    public int getProductId() { return product_id; }
    public String getProductName() { return product_name; }
    public int getQuantity() { return quantity; }
    public int getPrice() { return price; }
    public int getSubtotal() { return subtotal; }

    public void setId(int id) { this.id = id; }
    public void setSaleId(int sale_id) { this.sale_id = sale_id; }
    public void setProductId(int product_id) { this.product_id = product_id; }
    public void setProductName(String product_name) { this.product_name = product_name; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(int price) { this.price = price; }
    public void setSubtotal(int subtotal) { this.subtotal = subtotal; }
}

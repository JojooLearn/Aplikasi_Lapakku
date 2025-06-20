package com.example.pemrogramanfrontend.models.laporan;

import java.util.Objects;

/**
 * Model data untuk item penjualan (SalesItem).
 */
public class SalesItem {
    private String productName;
    private int quantity;
    private int price;
    private double subtotal;

    /**
     * Konstruktor penuh untuk SalesItem.
     *
     * @param productName Nama produk
     * @param quantity    Jumlah terjual
     * @param price       Harga per item
     * @param subtotal    Total harga (quantity * price)
     */
    public SalesItem(String productName, int quantity, int price, double subtotal) {
        this.productName = productName != null ? productName : "";
        this.quantity = Math.max(quantity, 0);
        this.price = Math.max(price, 0);
        this.subtotal = Math.max(subtotal, 0.0);
    }

    // Getter
    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    public double getSubtotal() {
        return subtotal;
    }

    // Setter
    public void setProductName(String productName) {
        this.productName = productName != null ? productName : "";
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(quantity, 0);
        recalculateSubtotal();
    }

    public void setPrice(int price) {
        this.price = Math.max(price, 0);
        recalculateSubtotal();
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = Math.max(subtotal, 0.0);
    }

    /**
     * Hitung ulang subtotal berdasarkan quantity dan price.
     */
    public void recalculateSubtotal() {
        this.subtotal = this.quantity * this.price;
    }

    @Override
    public String toString() {
        return "SalesItem{" +
                "productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", subtotal=" + subtotal +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SalesItem)) return false;
        SalesItem that = (SalesItem) o;
        return quantity == that.quantity &&
                price == that.price &&
                Double.compare(that.subtotal, subtotal) == 0 &&
                Objects.equals(productName, that.productName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName, quantity, price, subtotal);
    }
}

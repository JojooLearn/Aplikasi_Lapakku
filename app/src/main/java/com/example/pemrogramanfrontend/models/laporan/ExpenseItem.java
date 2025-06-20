package com.example.pemrogramanfrontend.models.laporan;

public class ExpenseItem {
    private int id;
    private String title;
    private String amount;
    private String description;
    private String date;

    public ExpenseItem(int id, String title, String amount, String description, String date) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    // Tambahkan getter jika perlu
    public String getTitle() {
        return title;
    }

    public String getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }
}

package com.example.pemrogramanfrontend.models;

public class Riwayat {
    private String title;
    private String type;
    private String date;

    // Konstruktor lama (jika ada)
    public Riwayat(String title, String type, String date) {
        this.title = title;
        this.type = type;
        this.date = date;
    }

    // Konstruktor baru dengan 5 parameter
    public Riwayat(String title, String type, String date, String amount) {
        this.title = title;
        this.type = type;
        this.date = date;
    }

    // Getter dan Setter untuk semua field
    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

}
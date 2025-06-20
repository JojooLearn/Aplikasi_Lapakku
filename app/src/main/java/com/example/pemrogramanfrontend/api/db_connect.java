package com.example.pemrogramanfrontend.api;

public class db_connect {

    // Ganti IP sesuai server lokal atau hosting kamu

    // 🔐 Auth
    public static final String webserver = "http://192.168.1.9/lapakku/";
    public static final String apilogin = webserver+"api-login.php";
    public static final String apilogout = webserver+"logout.php";
    public static final String apiregister = webserver+"api-register.php";
    public static final String tampilpengguna = webserver+"tampil_pengguna.php";


    // 📦 Products
    public static final String apiProductList   = webserver + "products/index.php";
    public static final String apiProductCreate = webserver + "products/create.php";
    public static final String apiProductUpdate = webserver + "products/update.php";
    public static final String apiProductDelete = webserver + "products/delete.php";

    // 📥 Stock In
    public static final String apiStockList   = webserver + "stock_in/index.php";
    public static final String apiStockCreate = webserver + "stock_in/create.php";
    public static final String apiStockDelete = webserver + "stock_in/delete.php";

    // 🛒 Sales
    public static final String apiSalesCreate = webserver + "sales/create.php";
    public static final String apiSalesList   = webserver + "sales/index.php";
    public static final String apiSalesDetail = webserver + "sales/get_sale_detail.php";
    public static final String apiSalesDelete = webserver + "sales/delete.php";

    // 👥 Customers
    public static final String apiCustomerList   = webserver + "customers/index.php";
    public static final String apiCustomerCreate = webserver + "customers/create.php";
    public static final String apiCustomerUpdate = webserver + "customers/update.php";
    public static final String apiCustomerDelete = webserver + "customers/delete.php";

    // 💸 Expenses
    public static final String apiExpenseList   = webserver + "expenses/index.php";
    public static final String apiExpenseCreate = webserver + "expenses/create.php";
    public static final String apiExpenseDelete = webserver + "expenses/delete.php";

    // 📊 Reports
    public static final String apiReportProfit        = webserver + "reports/profit.php";          // + ?start=YYYY-MM-DD&end=YYYY-MM-DD
    public static final String apiReportStock         = webserver + "reports/stock.php";
    public static final String apiReportSalesSummary  = webserver + "reports/sales_summary.php";  // + ?start=YYYY-MM-DD&end=YYYY-MM-DD
    public static final String apiSummary = webserver + "summary.php";
    public static final String apiActivityLog = webserver + "activities/index.php";
    public static final String apiProfile  = webserver + "profil/get_profile.php";
    public static final String apiUpdatePW = webserver + "profil/update_password.php";
    public static final String apiLaporan = webserver + "reports/daily_report.php?date=";

}

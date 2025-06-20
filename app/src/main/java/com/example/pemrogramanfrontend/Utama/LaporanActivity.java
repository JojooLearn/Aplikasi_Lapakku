package com.example.pemrogramanfrontend.Utama;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.adapters.laporan.CustomersAdapter;
import com.example.pemrogramanfrontend.adapters.laporan.ExpensesAdapter;
import com.example.pemrogramanfrontend.adapters.laporan.SalesAdapter;
import com.example.pemrogramanfrontend.adapters.laporan.StockAdapter;
import com.example.pemrogramanfrontend.api.db_connect;
import com.example.pemrogramanfrontend.models.laporan.CustomerItem;
import com.example.pemrogramanfrontend.models.laporan.ExpenseItem;
import com.example.pemrogramanfrontend.models.laporan.SalesItem;
import com.example.pemrogramanfrontend.models.laporan.StockItem;
import com.example.pemrogramanfrontend.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class LaporanActivity extends AppCompatActivity {
    private MaterialButton btnSelectDate;
    private MaterialTextView tvSelectedDate;
    private RecyclerView rvSales, rvStock, rvExpenses, rvCustomers;
    private MaterialTextView tvTotalSales, tvProfit, tvProductCount, tvCustomerCount, tvTotalExpenses;

    private SalesAdapter salesAdapter;
    private StockAdapter stockAdapter;
    private ExpensesAdapter expensesAdapter;
    private CustomersAdapter customersAdapter;

    private SessionManager session;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);

        initViews();
        setupToolbar();
        setupRecyclerViews();
        setupDatePicker();

        selectedDate = getTodayDate();
        tvSelectedDate.setText("Tanggal: " + formatDisplayDate(selectedDate));

        loadReportData(selectedDate);
    }

    private void initViews() {
        btnSelectDate = findViewById(R.id.btnSelectDate);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        rvSales = findViewById(R.id.rvSales);
        rvStock = findViewById(R.id.rvStock);
        rvExpenses = findViewById(R.id.rvExpenses);
        rvCustomers = findViewById(R.id.rvCustomers);
        tvTotalSales = findViewById(R.id.tvTotalSales);
        tvProfit = findViewById(R.id.tvProfit);
        tvProductCount = findViewById(R.id.tvProductCount);
        tvCustomerCount = findViewById(R.id.tvCustomerCount);
        tvTotalExpenses = findViewById(R.id.tvTotalExpenses);

        session = new SessionManager(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Laporan Ringkasan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerViews() {
        rvSales.setLayoutManager(new LinearLayoutManager(this));
        salesAdapter = new SalesAdapter(this, new ArrayList<>());
        rvSales.setAdapter(salesAdapter);

        rvStock.setLayoutManager(new LinearLayoutManager(this));
        stockAdapter = new StockAdapter(this, new ArrayList<>());
        rvStock.setAdapter(stockAdapter);

        rvExpenses.setLayoutManager(new LinearLayoutManager(this));
        expensesAdapter = new ExpensesAdapter(this, new ArrayList<>());
        rvExpenses.setAdapter(expensesAdapter);

        rvCustomers.setLayoutManager(new LinearLayoutManager(this));
        customersAdapter = new CustomersAdapter(this, new ArrayList<>());
        rvCustomers.setAdapter(customersAdapter);
    }

    private void setupDatePicker() {
        btnSelectDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            long today = MaterialDatePicker.todayInUtcMilliseconds();
            calendar.add(Calendar.YEAR, -1);
            long oneYearAgo = calendar.getTimeInMillis();

            CalendarConstraints constraints = new CalendarConstraints.Builder()
                    .setStart(oneYearAgo)
                    .setEnd(today)
                    .build();

            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Pilih Tanggal")
                    .setSelection(today)
                    .setCalendarConstraints(constraints)
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(selection));
                tvSelectedDate.setText("Tanggal: " + formatDisplayDate(selectedDate));
                loadReportData(selectedDate);
            });

            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        });
    }

    private String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    private void loadReportData(String date) {
        String url = db_connect.apiLaporan + date;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                this::handleResponse,
                this::handleError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + session.getToken());
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void handleResponse(JSONObject response) {
        try {
            if (!response.getBoolean("status")) {
                Toast.makeText(this, "Data kosong atau gagal diambil", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject data = response.getJSONObject("data");

            // 1. Summary
            if (data.has("totals")) {
                JSONObject totals = data.getJSONObject("totals");
                tvTotalSales.setText("Rp" + totals.optString("sales", "0"));
                tvTotalExpenses.setText("Rp" + totals.optString("expenses", "0"));
                tvProfit.setText("Rp" + totals.optString("gross_profit", "0"));
            }

            // 2. Produk
            if (data.has("available_products")) {
                JSONArray productsArray = data.getJSONArray("available_products");
                tvProductCount.setText(productsArray.length() + " Produk");
            }

            // 3. Pelanggan
            if (data.has("new_customers")) {
                JSONArray customersArray = data.getJSONArray("new_customers");
                customersAdapter.updateData(parseCustomers(customersArray));
                tvCustomerCount.setText(customersArray.length() + " Pelanggan");
            }

            // 4. Sales
            if (data.has("sales")) {
                salesAdapter.setSalesList(parseSales(data.getJSONArray("sales")));
            }

            // 5. Stock
            if (data.has("stock_in")) {
                stockAdapter.updateData(parseStock(data.getJSONArray("stock_in")));
            }

            // 6. Expenses
            if (data.has("expenses")) {
                expensesAdapter.updateData(parseExpenses(data.getJSONArray("expenses")));
            }

        } catch (JSONException e) {
            Toast.makeText(this, "Gagal parsing data", Toast.LENGTH_SHORT).show();
            Log.e("LAPORAN_ERROR", "Parsing error", e);
        }
    }

    private List<SalesItem> parseSales(JSONArray array) throws JSONException {
        List<SalesItem> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject o = array.getJSONObject(i);
            list.add(new SalesItem(
                    o.optString("name", "-"),
                    o.optInt("quantity", 0),
                    o.optInt("price", 0),
                    o.optDouble("subtotal", 0)
            ));
        }
        return list;
    }

    private List<StockItem> parseStock(JSONArray array) throws JSONException {
        List<StockItem> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject o = array.getJSONObject(i);
            list.add(new StockItem(
                    o.optString("product_name", "-"),
                    o.optInt("quantity", 0)
            ));
        }
        return list;
    }

    private List<ExpenseItem> parseExpenses(JSONArray array) throws JSONException {
        List<ExpenseItem> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            list.add(new ExpenseItem(
                    obj.optInt("id", 0),
                    obj.optString("title", ""),
                    obj.optString("amount", ""),
                    obj.optString("description", ""),
                    obj.optString("created_at", "")
            ));
        }
        return list;
    }

    private List<CustomerItem> parseCustomers(JSONArray array) throws JSONException {
        List<CustomerItem> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            list.add(new CustomerItem(
                    obj.optString("name", ""),
                    obj.optString("phone", ""),
                    obj.optString("address", "")
            ));
        }
        return list;
    }

    private String formatDisplayDate(String dateStr) {
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            return output.format(input.parse(dateStr));
        } catch (ParseException e) {
            return dateStr;
        }
    }

    private void handleError(VolleyError error) {
        String errorMessage = "Gagal mengambil data";
        if (error.networkResponse != null) {
            try {
                String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                JSONObject json = new JSONObject(body);
                errorMessage = json.optString("message", "Terjadi kesalahan server");
            } catch (Exception e) {
                errorMessage = "Error " + error.networkResponse.statusCode;
            }
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        Log.e("LAPORAN_ERROR", error.toString());
    }

    private void startActivityWithAnimation(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivityWithAnimation(new Intent(this, Dashboard.class));
        finish();
        return true;
    }
}

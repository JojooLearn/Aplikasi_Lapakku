package com.example.pemrogramanfrontend.Utama;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.adapters.RiwayatAdapter;
import com.example.pemrogramanfrontend.api.db_connect;
import com.example.pemrogramanfrontend.auth.login;
import com.example.pemrogramanfrontend.models.Riwayat;
import com.example.pemrogramanfrontend.products.ProductListActivity;
import com.example.pemrogramanfrontend.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RiwayatActivity extends AppCompatActivity {

    private static final String TAG = "RiwayatActivity";
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<Riwayat> riwayatList;
    private RiwayatAdapter adapter;
    private SessionManager session;
    private Context context;

    private String currentFilter = "all"; // all, transfer, payment, topup
    private String currentPeriod = "weekly"; // daily, weekly, monthly, yearly, all
    private Button btnDateFilter; // Tambahkan ini di deklarasi variabel class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);


        context = this;
        session = new SessionManager(this);

        initializeViews();
        setupRecyclerView();
        setupDateFilterButton();
        setupBottomNavigation();
        loadRiwayat();

        // Initialize Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Riwayat");
        }
    }
    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewRiwayat);
        progressBar = findViewById(R.id.progressBarRiwayat);
        btnDateFilter = findViewById(R.id.btnDateFilter); // Tambahkan ini
        updateFilterButtonText(); // Panggil untuk set teks awal
    }

    private void updateFilterButtonText() {
        String buttonText;
        switch (currentPeriod) {
            case "daily":
                buttonText = "Hari Ini";
                break;
            case "weekly":
                buttonText = "Minggu Ini";
                break;
            case "monthly":
                buttonText = "Bulan Ini";
                break;
            case "yearly":
                buttonText = "Tahun Ini";
                break;
            case "all":
                buttonText = "Semua Periode";
                break;
            default:
                buttonText = "Pilih Periode";
        }
        btnDateFilter.setText(buttonText);
    }

    private void setupRecyclerView() {
        riwayatList = new ArrayList<>();
        adapter = new RiwayatAdapter(this, riwayatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupDateFilterButton() {
        Button btnDateFilter = findViewById(R.id.btnDateFilter);
        btnDateFilter.setOnClickListener(v -> showDateFilterDialog());
    }

    private void showDateFilterDialog() {
        String[] periods = {"Hari Ini", "Minggu Ini", "Bulan Ini", "Tahun Ini", "Semua"};

        new AlertDialog.Builder(this)
                .setTitle("Pilih Periode")
                .setItems(periods, (dialog, which) -> {
                    switch (which) {
                        case 0: currentPeriod = "daily"; break;
                        case 1: currentPeriod = "weekly"; break;
                        case 2: currentPeriod = "monthly"; break;
                        case 3: currentPeriod = "yearly"; break;
                        case 4: currentPeriod = "all"; break;
                    }
                    updateFilterButtonText(); // Tambahkan ini
                    loadRiwayat();
                })
                .show();
    }

    private void startActivityWithAnimation(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.button_navigation_menu);
        bottomNav.setSelectedItemId(R.id.Riwayat);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.dashboard) {
                startActivityWithAnimation(new Intent(this, Dashboard.class));
            } else if (id == R.id.Riwayat) {
                // Already in this activity
            } else if (id == R.id.Produk) {
                startActivityWithAnimation(new Intent(this, ProductListActivity.class));
            } else if (id == R.id.profile) {
                startActivityWithAnimation(new Intent(this, ProfileActivity.class));
            }
            return true;
        });
    }

    private void loadRiwayat() {
        progressBar.setVisibility(View.VISIBLE);
        String url = db_connect.apiActivityLog + "?filter=" + currentFilter + "&period=" + currentPeriod;

        Log.d(TAG, "Loading riwayat data with filter: " + currentFilter + " and period: " + currentPeriod);
        Log.d(TAG, "API Endpoint: " + url);
        Log.d(TAG, "Auth Token: " + session.getToken());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                this::handleSuccessResponse,
                this::handleErrorResponse
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + session.getToken());
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000, // 15 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(this).add(request);
    }

    private void handleSuccessResponse(JSONObject response) {
        progressBar.setVisibility(View.GONE);
        try {
            Log.d(TAG, "Success response: " + response.toString());

            if (response.getBoolean("status")) {
                JSONArray data = response.getJSONArray("data");
                riwayatList.clear();

                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    String title = obj.optString("title", "No Title");
                    String type = obj.optString("type", "Unknown Type");
                    String rawDate = obj.optString("date", obj.optString("created_at", ""));
                    String amount = obj.optString("amount", "");
                    String status = obj.optString("status", "");

                    String formattedDate = formatDate(rawDate);
                    riwayatList.add(new Riwayat(title, type, formattedDate));
                }

                adapter.notifyDataSetChanged();

                if (riwayatList.isEmpty()) {
                    Toast.makeText(this, "Tidak ada data riwayat untuk filter ini", Toast.LENGTH_SHORT).show();
                }
            } else {
                String errorMessage = response.optString("message", "Gagal memuat riwayat");
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON parsing error: " + e.getMessage(), e);
            Toast.makeText(this, "Error memproses data", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleErrorResponse(com.android.volley.VolleyError error) {
        progressBar.setVisibility(View.GONE);
        String errorMessage = "Terjadi kesalahan";

        try {
            if (error.networkResponse != null) {
                int statusCode = error.networkResponse.statusCode;
                String errorBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);

                try {
                    JSONObject errorJson = new JSONObject(errorBody);
                    errorMessage = errorJson.optString("message", "Error " + statusCode);
                } catch (JSONException e) {
                    errorMessage = "Error " + statusCode + ": " + errorBody;
                }

                Log.e(TAG, "Server error: " + errorMessage);
            } else if (error.getMessage() != null) {
                errorMessage = error.getMessage();
                Log.e(TAG, "Volley error: " + errorMessage);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling error response", e);
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private String formatDate(String rawDate) {
        if (rawDate == null || rawDate.isEmpty()) {
            return "-";
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(rawDate);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            Log.w(TAG, "Failed to parse date: " + rawDate);
            return rawDate;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString styledTitle = new SpannableString(item.getTitle());
            styledTitle.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.primary)), 0, styledTitle.length(), 0);
            item.setTitle(styledTitle);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void clearSession() {
        getSharedPreferences("user_session", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }
    private void navigateToLogin() {
        startActivity(new Intent(this, login.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void logout() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                db_connect.apilogout,
                response -> {
                    clearSession();
                    navigateToLogin();
                },
                error -> Toast.makeText(this, "Logout gagal", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
                Map<String, String> params = new HashMap<>();
                params.put("token", sp.getString("token", ""));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

}
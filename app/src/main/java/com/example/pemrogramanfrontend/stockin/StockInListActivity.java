package com.example.pemrogramanfrontend.stockin;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.Utama.Dashboard;
import com.example.pemrogramanfrontend.adapters.StockInAdapter;
import com.example.pemrogramanfrontend.api.db_connect;
import com.example.pemrogramanfrontend.auth.login;
import com.example.pemrogramanfrontend.expenses.AddExpenseActivity;
import com.example.pemrogramanfrontend.models.StockIn;
import com.example.pemrogramanfrontend.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockInListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    Button btnAddStock;
    List<StockIn> stockList;
    StockInAdapter adapter;
    SessionManager sessionManager;

    // Tambahan: ActivityResultLauncher
    private final ActivityResultLauncher<Intent> stockInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadStockData(); // refresh data setelah kembali
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stockin_list);

        sessionManager = new SessionManager(this);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerViewStockIn);
        btnAddStock = findViewById(R.id.btnAddStockIn);

        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, login.class));
            finish();
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        stockList = new ArrayList<>();
        adapter = new StockInAdapter(this, stockList);
        recyclerView.setAdapter(adapter);

        loadStockData();

        btnAddStock.setOnClickListener(v -> {
            startActivityWithAnimation(new Intent(this, AddStockInActivity.class));
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Daftar Stok Masuk");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // Rename ke loadStockInList (sesuai permintaan)
    private void loadStockData() {
        progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, db_connect.apiStockList, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (response.getBoolean("status")) {
                            JSONArray data = response.getJSONArray("data");
                            stockList.clear();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                StockIn si = new StockIn();
                                si.setId(obj.getInt("id"));
                                si.setProduct_id(obj.getInt("product_id"));
                                si.setProduct_name(obj.getString("product_name"));
                                si.setQuantity(obj.getInt("quantity"));
                                si.setPrice(obj.getInt("price"));
                                si.setDate(obj.getString("date"));
                                stockList.add(si);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + sessionManager.getToken());
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void startActivityWithAnimation(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivityWithAnimation(new Intent(this, Dashboard.class));
        onBackPressed();
        return true;
    }
}

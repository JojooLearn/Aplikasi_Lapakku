package com.example.pemrogramanfrontend.sales;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.Utama.Dashboard;
import com.example.pemrogramanfrontend.adapters.SaleItemAdapter;
import com.example.pemrogramanfrontend.api.db_connect;
import com.example.pemrogramanfrontend.models.Product;
import com.example.pemrogramanfrontend.models.SaleItem;
import com.example.pemrogramanfrontend.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddSaleActivity extends AppCompatActivity {

    Spinner spinnerCustomer;
    Button btnTambahProduk, btnSimpan;
    RecyclerView recyclerView;
    SaleItemAdapter itemAdapter;
    List<SaleItem> itemList;
    List<Product> productList;
    List<String> customerNames;
    List<Integer> customerIds;

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sale);

        session = new SessionManager(this);

        spinnerCustomer = findViewById(R.id.spinnerCustomer);
        btnTambahProduk = findViewById(R.id.btnTambahProduk);
        btnSimpan = findViewById(R.id.btnSimpan);
        recyclerView = findViewById(R.id.recyclerViewItems);

        productList = new ArrayList<>();
        itemList = new ArrayList<>();
        customerNames = new ArrayList<>();
        customerIds = new ArrayList<>();

        itemAdapter = new SaleItemAdapter(this, itemList, productList, new SaleItemAdapter.OnItemChangeListener() {
            @Override public void onItemChanged() { hitungTotal(); }
            @Override public void onItemRemoved(int position) { hitungTotal(); }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(itemAdapter);

        loadProducts();
        loadCustomers();

        btnTambahProduk.setOnClickListener(v -> {
            itemList.add(new SaleItem()); // produk kosong
            itemAdapter.notifyItemInserted(itemList.size() - 1);
        });

        btnSimpan.setOnClickListener(v -> simpanTransaksi());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Masukan Penjualan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadProducts() {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, db_connect.apiProductList, null,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        productList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            Product p = new Product();
                            p.setId(obj.getInt("id"));
                            p.setName(obj.getString("name"));
                            productList.add(p);
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Gagal parsing produk", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal ambil produk", Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + session.getToken());
                return h;
            }
        };
        Volley.newRequestQueue(this).add(req);
    }

    private void loadCustomers() {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, db_connect.apiCustomerList, null,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        customerNames.clear();
                        customerIds.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            customerNames.add(obj.getString("name"));
                            customerIds.add(obj.getInt("id"));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_spinner_item, customerNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCustomer.setAdapter(adapter);
                    } catch (Exception e) {
                        Toast.makeText(this, "Gagal parsing pelanggan", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal ambil pelanggan", Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + session.getToken());
                return h;
            }
        };
        Volley.newRequestQueue(this).add(req);
    }

    private void hitungTotal() {
        int total = 0;
        for (SaleItem i : itemList) {
            total += i.getQuantity() * i.getPrice();
        }
        ((android.widget.TextView) findViewById(R.id.tvTotal)).setText("Total: Rp" + total);
    }

    private void simpanTransaksi() {
        if (itemList.isEmpty()) {
            Toast.makeText(this, "Tambahkan produk terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        int custIndex = spinnerCustomer.getSelectedItemPosition();
        if (custIndex < 0 || customerIds.isEmpty()) {
            Toast.makeText(this, "Pilih pelanggan", Toast.LENGTH_SHORT).show();
            return;
        }

        int customer_id = customerIds.get(custIndex);
        JSONArray items = new JSONArray();

        for (SaleItem i : itemList) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("product_id", i.getProductId());
                obj.put("quantity", i.getQuantity());
                obj.put("price", i.getPrice());
                items.put(obj);
            } catch (Exception ignored) {}
        }

        JSONObject body = new JSONObject();
        try {
            body.put("customer_id", customer_id);
            body.put("items", items);  // Tidak perlu kirim total, dihitung backend
        } catch (Exception ignored) {}

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, db_connect.apiSalesCreate, body,
                response -> {
                    Log.d("SALE_RESPONSE", response.toString());
                    try {
                        if (response.getBoolean("status")) {
                            Toast.makeText(this, "Transaksi berhasil", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddSaleActivity.this, SaleListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("SALE_PARSE", e.toString());
                        Toast.makeText(this, "Error parsing respon", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Log.e("SALE_ERROR", "Volley Error: " + error.toString());
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorBody = new String(error.networkResponse.data);
                        Log.e("SALE_ERROR_BODY", errorBody);
                    }
                    Toast.makeText(this, "Gagal simpan transaksi", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + session.getToken());
                h.put("Content-Type", "application/json");
                return h;
            }
        };

        Volley.newRequestQueue(this).add(req);
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

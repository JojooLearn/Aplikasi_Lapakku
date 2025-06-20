package com.example.pemrogramanfrontend.products;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.Utama.Dashboard;
import com.example.pemrogramanfrontend.Utama.ProfileActivity;
import com.example.pemrogramanfrontend.Utama.RiwayatActivity;
import com.example.pemrogramanfrontend.adapters.ProductAdapter;
import com.example.pemrogramanfrontend.auth.login;
import com.example.pemrogramanfrontend.models.Product;
import com.example.pemrogramanfrontend.utils.SessionManager;
import com.example.pemrogramanfrontend.api.db_connect;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProductAdapter adapter;
    List<Product> productList;
    ProgressBar progressBar;
    SessionManager sessionManager;
    FloatingActionButton tambahproduct;

    // Tambahan: ActivityResultLauncher
    private final ActivityResultLauncher<Intent> addProductLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadProducts(); // refresh data setelah kembali
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // Inisialisasi komponen
        sessionManager = new SessionManager(this);
        recyclerView = findViewById(R.id.recyclerViewProduct);
        progressBar = findViewById(R.id.progressBar);
        tambahproduct = findViewById(R.id.btnAddProduct);

        // Jika belum login, alihkan ke halaman login
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, login.class));
            finish();
            return;
        }

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Daftar Produk");
        }

        // Setup RecyclerView
        productList = new ArrayList<>();
        adapter = new ProductAdapter(this, productList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Load data produk dari API
        loadProducts();
        setupBottomNavigation();

        // Tombol tambah produk
        tambahproduct.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddProductActivity.class);
            addProductLauncher.launch(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK) {
            loadProducts(); // reload dari server
        }
    }


    // Load Produk dari API
    private void loadProducts() {
        progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, db_connect.apiProductList, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (response.getBoolean("status")) {
                            JSONArray dataArray = response.getJSONArray("data");
                            productList.clear();
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);
                                Product p = new Product();
                                p.setId(obj.getInt("id"));
                                p.setName(obj.getString("name"));
                                p.setCategory(obj.getString("category"));
                                p.setPrice(obj.getInt("price"));
                                p.setCost_price(obj.getInt("cost_price"));
                                p.setStock(obj.getInt("stock"));
                                p.setUnit(obj.getString("unit"));
                                productList.add(p);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "Data produk kosong / gagal", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Gagal koneksi ke server / token mungkin kadaluarsa", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sessionManager.getToken();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    // Inflate menu ke toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    // Aksi ketika item menu toolbar diklik
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            Toast.makeText(this, "Buka Profil", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_settings) {
            Toast.makeText(this, "Buka Pengaturan", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_help) {
            Toast.makeText(this, "Buka Bantuan", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startActivityWithAnimation(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.button_navigation_menu);
        bottomNav.setSelectedItemId(R.id.Produk);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.dashboard) {
                startActivityWithAnimation(new Intent(this, Dashboard.class));
                return true;
            } else if (item.getItemId() == R.id.Produk) {
                    startActivityWithAnimation(new Intent(this, ProductListActivity.class));
                    return true;
            } else if (item.getItemId() == R.id.Riwayat) {
                startActivityWithAnimation(new Intent(this, RiwayatActivity.class));
                return true;
            } else if (item.getItemId() == R.id.profile) {
                startActivityWithAnimation(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    // Logout dari sistem
    private void logout() {
        String token = sessionManager.getToken();

        if (token == null) return;

        StringRequest request = new StringRequest(Request.Method.POST, db_connect.apilogout,
                response -> {
                    sessionManager.logout();
                    Intent intent = new Intent(ProductListActivity.this, login.class);
                    startActivity(intent);
                    finish();
                },
                error -> Toast.makeText(ProductListActivity.this, "Gagal logout: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}

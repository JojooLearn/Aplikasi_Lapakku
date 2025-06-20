package com.example.pemrogramanfrontend.customers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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
import com.example.pemrogramanfrontend.adapters.CustomerAdapter;
import com.example.pemrogramanfrontend.api.db_connect;
import com.example.pemrogramanfrontend.auth.login;
import com.example.pemrogramanfrontend.models.Customer;
import com.example.pemrogramanfrontend.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomerAdapter adapter;
    private List<Customer> customerList;
    private ProgressBar progressBar;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, login.class));
            finish();
            return;
        }

        // Inisialisasi UI
        recyclerView = findViewById(R.id.recyclerViewCustomer);
        progressBar = findViewById(R.id.progressBarCustomer);
        Button btnAddCustomer = findViewById(R.id.btnAddCustomer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Pelanggan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        customerList = new ArrayList<>();
        adapter = new CustomerAdapter(this, customerList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Event klik tombol tambah
        btnAddCustomer.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerListActivity.this, AddCustomerActivity.class);
            startActivityWithAnimation(intent);
        });

        // Ambil data
        loadCustomerData();
    }

    private void loadCustomerData() {
        progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, db_connect.apiCustomerList, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (response.getBoolean("status")) {
                            JSONArray data = response.getJSONArray("data");
                            customerList.clear();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                Customer customer = new Customer();
                                customer.setId(obj.getInt("id"));
                                customer.setName(obj.getString("name"));
                                customer.setPhone(obj.getString("phone"));
                                customer.setAddress(obj.getString("address"));
                                customerList.add(customer);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Gagal memproses data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + session.getToken());
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
        onBackPressed();
        startActivityWithAnimation(new Intent(this, Dashboard.class));
        return true;
    }
}

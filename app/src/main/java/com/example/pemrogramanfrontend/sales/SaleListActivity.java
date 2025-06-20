package com.example.pemrogramanfrontend.sales;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.Utama.Dashboard;
import com.example.pemrogramanfrontend.adapters.SaleAdapter;
import com.example.pemrogramanfrontend.api.db_connect;
import com.example.pemrogramanfrontend.auth.login;
import com.example.pemrogramanfrontend.models.Sale;
import com.example.pemrogramanfrontend.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaleListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SaleAdapter adapter;
    List<Sale> saleList;
    ProgressBar progressBar;
    Button btnAddSale;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_list);

        session = new SessionManager(this);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerViewSales);
        btnAddSale = findViewById(R.id.btnAddSale);

        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, login.class));
            finish();
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        saleList = new ArrayList<>();
        adapter = new SaleAdapter(this, saleList);
        adapter.setOnItemClickListener(sale -> {
            SaleDetailDialogFragment dialog = SaleDetailDialogFragment.newInstance(sale.getId());
            dialog.show(getSupportFragmentManager(), "detailDialog");
        });

        recyclerView.setAdapter(adapter);

        loadSales();

        btnAddSale.setOnClickListener(v -> {
            startActivity(new Intent(this, AddSaleActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Daftar Penjualan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadSales() {
        progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, db_connect.apiSalesList, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        Log.d("SALE_RES", response.toString());

                        if (response.getBoolean("status")) {
                            JSONArray data = response.getJSONArray("data");
                            saleList.clear();

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                Sale s = new Sale();
                                s.setId(obj.getInt("id"));
                                s.setInvoice_number(obj.getString("invoice_number"));
                                s.setCustomer_name(obj.getString("customer_name"));
                                s.setTotal(obj.getInt("total_price"));  // sesuai database
                                s.setDate(obj.getString("date"));

                                saleList.add(s);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "Data kosong / gagal", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("SALE_PARSE", e.toString());
                        Toast.makeText(this, "Gagal parsing JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("SALE_ERROR", error.toString());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        String token = session.getToken();
        if (token == null) return;

        StringRequest request = new StringRequest(Request.Method.POST, db_connect.apilogout,
                response -> {
                    session.logout();
                    startActivity(new Intent(this, login.class));
                    finish();
                },
                error -> Toast.makeText(this, "Gagal logout", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("token", token);
                return p;
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

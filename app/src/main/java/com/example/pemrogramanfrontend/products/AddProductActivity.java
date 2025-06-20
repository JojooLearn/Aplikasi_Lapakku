package com.example.pemrogramanfrontend.products;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.Utama.Dashboard;
import com.example.pemrogramanfrontend.api.db_connect;
import com.example.pemrogramanfrontend.utils.SessionManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    EditText etName, etCategory, etPrice, etCostPrice, etStock, etUnit;
    Button btnSave;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        session = new SessionManager(this);

        etName = findViewById(R.id.etName);
        etCategory = findViewById(R.id.etCategory);
        etPrice = findViewById(R.id.etPrice);
        etCostPrice = findViewById(R.id.etCostPrice);
        etStock = findViewById(R.id.etStock);
        etUnit = findViewById(R.id.etUnit);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> simpanProduk());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tambah Produk");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void simpanProduk() {
        String name = etName.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String costPrice = etCostPrice.getText().toString().trim();
        String stock = etStock.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();

        if (name.isEmpty() || category.isEmpty() || price.isEmpty() || costPrice.isEmpty() || stock.isEmpty() || unit.isEmpty()) {
            Toast.makeText(this, "Isi semua field!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("category", category);
        data.put("price", Integer.parseInt(price));
        data.put("cost_price", Integer.parseInt(costPrice));
        data.put("stock", Integer.parseInt(stock));
        data.put("unit", unit);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, db_connect.apiProductCreate, new JSONObject(data),
                response -> {
                    try {
                        if (response.getBoolean("status")) {
                            Toast.makeText(this, "Produk berhasil disimpan", Toast.LENGTH_SHORT).show();
                            setResult(Activity.RESULT_OK);
                            finish(); // Kembali ke list produk
                        } else {
                            Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Kesalahan saat parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
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
        startActivityWithAnimation(new Intent(this, Dashboard.class));
        onBackPressed();
        return true;
    }
}

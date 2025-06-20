package com.example.pemrogramanfrontend.stockin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddStockInActivity extends AppCompatActivity {

    Spinner spinnerProduct;
    EditText etQuantity, etPrice;
    Button btnSave;
    SessionManager session;

    ArrayList<String> productNames = new ArrayList<>();
    ArrayList<Integer> productIds = new ArrayList<>();
    ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stockin);

        session = new SessionManager(this);

        spinnerProduct = findViewById(R.id.spinnerProduct);
        etQuantity = findViewById(R.id.etQuantity);
        etPrice = findViewById(R.id.etPrice);
        btnSave = findViewById(R.id.btnSave);

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, productNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProduct.setAdapter(spinnerAdapter);

        loadProducts();

        btnSave.setOnClickListener(v -> simpanStockIn());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tambah Stok Masuk");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadProducts() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, db_connect.apiProductList, null,
                response -> {
                    try {
                        if (response.getBoolean("status")) {
                            JSONArray data = response.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                productNames.add(obj.getString("name"));
                                productIds.add(obj.getInt("id"));
                            }
                            spinnerAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Gagal parsing produk", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal koneksi produk", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + session.getToken());
                return h;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void simpanStockIn() {
        int selectedIndex = spinnerProduct.getSelectedItemPosition();
        if (selectedIndex < 0 || productIds.isEmpty()) {
            Toast.makeText(this, "Pilih produk", Toast.LENGTH_SHORT).show();
            return;
        }

        int productId = productIds.get(selectedIndex);
        String quantity = etQuantity.getText().toString().trim();
        String price = etPrice.getText().toString().trim();

        if (quantity.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "Lengkapi data", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("product_id", productId);
        data.put("quantity", Integer.parseInt(quantity));
        data.put("price", Integer.parseInt(price));

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, db_connect.apiStockCreate, new JSONObject(data),
                response -> {
                    try {
                        if (response.getBoolean("status")) {
                            Toast.makeText(this, "Stok masuk berhasil disimpan", Toast.LENGTH_SHORT).show();
                            setResult(Activity.RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Gagal parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal koneksi", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + session.getToken());
                return h;
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

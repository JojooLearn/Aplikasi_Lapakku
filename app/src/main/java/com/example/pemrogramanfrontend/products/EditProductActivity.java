package com.example.pemrogramanfrontend.products;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

public class EditProductActivity extends AppCompatActivity {

    private EditText etName, etCategory, etPrice, etCostPrice, etStock, etUnit;
    private Button btnUpdate;
    private SessionManager session;
    private ProgressDialog progressDialog;

    private int productId;
    private int position; // posisi di adapter, untuk dikirim balik

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        session = new SessionManager(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mengupdate produk...");
        progressDialog.setCancelable(false);

        etName = findViewById(R.id.etName);
        etCategory = findViewById(R.id.etCategory);
        etPrice = findViewById(R.id.etPrice);
        etCostPrice = findViewById(R.id.etCostPrice);
        etStock = findViewById(R.id.etStock);
        etUnit = findViewById(R.id.etUnit);
        btnUpdate = findViewById(R.id.btnUpdate);

        // Ambil data dari intent
        Intent intent = getIntent();
        productId = intent.getIntExtra("id", -1);
        position = intent.getIntExtra("position", -1);

        etName.setText(intent.getStringExtra("name"));
        etCategory.setText(intent.getStringExtra("category"));
        // Ambil price dan cost_price sebagai double, lalu set ke EditText
        double price = intent.getDoubleExtra("price", 0);
        double costPrice = intent.getDoubleExtra("cost_price", 0);
        etPrice.setText(String.valueOf(price));
        etCostPrice.setText(String.valueOf(costPrice));

        etStock.setText(String.valueOf(intent.getIntExtra("stock", 0)));
        etUnit.setText(intent.getStringExtra("unit"));

        btnUpdate.setOnClickListener(v -> updateProduct());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Produk");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void updateProduct() {
        String name = etName.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String costPriceStr = etCostPrice.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();

        if (name.isEmpty() || category.isEmpty() || priceStr.isEmpty() || costPriceStr.isEmpty() || stockStr.isEmpty() || unit.isEmpty()) {
            Toast.makeText(this, "Isi semua field!", Toast.LENGTH_SHORT).show();
            return;
        }

        double price, costPrice;
        int stock;
        try {
            price = Double.parseDouble(priceStr);
            costPrice = Double.parseDouble(costPriceStr);
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Format angka tidak valid!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        Map<String, Object> data = new HashMap<>();
        data.put("id", productId);
        data.put("name", name);
        data.put("category", category);
        data.put("price", price);
        data.put("cost_price", costPrice);
        data.put("stock", stock);
        data.put("unit", unit);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, db_connect.apiProductUpdate, new JSONObject(data),
                response -> {
                    progressDialog.dismiss();
                    try {
                        if (response.getBoolean("status")) {
                            Toast.makeText(this, "Produk berhasil diperbarui", Toast.LENGTH_SHORT).show();
                            Intent resultIntent = new Intent();
                            setResult(RESULT_OK, resultIntent);
                            finish();

//                            // Kirim data hasil update ke activity pemanggil
//                            resultIntent = new Intent();
//                            resultIntent.putExtra("position", position);
//                            resultIntent.putExtra("id", productId);
//                            resultIntent.putExtra("name", name);
//                            resultIntent.putExtra("category", category);
//                            resultIntent.putExtra("price", price);
//                            resultIntent.putExtra("cost_price", costPrice);
//                            resultIntent.putExtra("stock", stock);
//                            resultIntent.putExtra("unit", unit);
//
//                            setResult(RESULT_OK, resultIntent);
//                            finish();
                        } else {
                            Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Gagal memproses respon server", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    error.printStackTrace();
                    Toast.makeText(this, "Koneksi gagal", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + session.getToken());
                headers.put("Content-Type", "application/json");  // opsional, tapi bagus untuk JSON API
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

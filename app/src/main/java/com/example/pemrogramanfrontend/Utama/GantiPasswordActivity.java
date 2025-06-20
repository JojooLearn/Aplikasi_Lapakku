package com.example.pemrogramanfrontend.Utama;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.api.db_connect;
import com.example.pemrogramanfrontend.utils.SessionManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GantiPasswordActivity extends AppCompatActivity {

    EditText etEmail, etPasswordLama, etPasswordBaru;
    Button btnSimpanPassword;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ganti_password);

        session = new SessionManager(this);

        etEmail = findViewById(R.id.etEmail); // pastikan ID ini sesuai layout
        etPasswordLama = findViewById(R.id.etPasswordLama);
        etPasswordBaru = findViewById(R.id.etPasswordBaru);
        btnSimpanPassword = findViewById(R.id.btnSimpanPassword);

        btnSimpanPassword.setOnClickListener(v -> ubahPassword());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Atur Ulang Password");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void ubahPassword() {
        String email = etEmail.getText().toString().trim();
        String lama = etPasswordLama.getText().toString().trim();
        String baru = etPasswordBaru.getText().toString().trim();

        if (email.isEmpty() || lama.isEmpty() || baru.isEmpty()) {
            Toast.makeText(this, "Isi semua kolom!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", email);
            jsonBody.put("lama", lama);
            jsonBody.put("baru", baru);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    db_connect.apiUpdatePW,
                    jsonBody,
                    response -> {
                        try {
                            if (response.getBoolean("status")) {
                                Toast.makeText(this, "Password berhasil diubah", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(this, "Gagal parsing", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Toast.makeText(this, "Gagal koneksi", Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + session.getToken());
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            Volley.newRequestQueue(this).add(request);
        } catch (Exception e) {
            Toast.makeText(this, "Kesalahan saat membuat request", Toast.LENGTH_SHORT).show();
        }
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

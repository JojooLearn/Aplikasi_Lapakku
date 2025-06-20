package com.example.pemrogramanfrontend.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.api.db_connect;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pemrogramanfrontend.Utama.Dashboard;
import com.example.pemrogramanfrontend.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    TextView daftar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etusername);
        etPassword = findViewById(R.id.etpassword);
        btnLogin = findViewById(R.id.btnLogin);
        daftar = findViewById(R.id.etdaftar);

        // Cek apakah user sudah login
        SessionManager sessionManager = new SessionManager(login.this);
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(login.this, Dashboard.class));
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAnimation(new Intent(login.this, register.class));
                finish();
            }
        });
    }

    private void loginUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(login.this, "Isi semua field", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = db_connect.apilogin;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("status").equals("success")) {
                                String token = json.getString("token");

                                SessionManager sessionManager = new SessionManager(login.this);
                                sessionManager.saveSession(token, username);

                                Log.d("SessionDebug", "Token tersimpan: " + sessionManager.getToken());

                                Toast.makeText(login.this, "Login berhasil", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(login.this, Dashboard.class));
                                finish();
                            } else {
                                Toast.makeText(login.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(login.this, "Gagal parsing data", Toast.LENGTH_SHORT).show();
                            Log.e("LoginParseError", "JSONException: ", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(login.this, "Error koneksi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("LoginVolleyError", "Volley error: ", error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(login.this);
        queue.add(request);
    }
    private void startActivityWithAnimation(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}

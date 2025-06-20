package com.example.pemrogramanfrontend.Utama;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.api.db_connect;
import com.example.pemrogramanfrontend.auth.login;
import com.example.pemrogramanfrontend.products.ProductListActivity;
import com.example.pemrogramanfrontend.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    TextView tvNamaLengkap, tvUsername, tvEmail;
    Button btnLogout, btnGantiPassword;
    SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getProfile();
        session = new SessionManager(this);
        tvNamaLengkap = findViewById(R.id.tvNamaLengkap);
        tvUsername = findViewById(R.id.tvUsername);  // <- tambahkan ini
        tvEmail = findViewById(R.id.tvEmail);
        btnLogout = findViewById(R.id.btnLogout);
        btnGantiPassword = findViewById(R.id.btnEditProfile);


        btnLogout.setOnClickListener(v -> logout());

        btnGantiPassword.setOnClickListener(v -> {
            // nanti kita buat activity GantiPasswordActivity
            startActivity(new Intent(this, GantiPasswordActivity.class));
            getProfile();
        });

        // Initialize Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profil");
        }

        setupBottomNavigation();
    }



    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString styledTitle = new SpannableString(item.getTitle());
            styledTitle.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.primary)), 0, styledTitle.length(), 0);
            item.setTitle(styledTitle);
        }
        return true;
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
        StringRequest request = new StringRequest(
                Request.Method.POST,
                db_connect.apilogout,
                response -> {
                    clearSession();
                    navigateToLogin();
                },
                error -> Toast.makeText(this, "Logout gagal", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
                Map<String, String> params = new HashMap<>();
                params.put("token", sp.getString("token", ""));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void getProfile() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, db_connect.apiProfile, null,
                response -> {
                    try {
                        if (response.getBoolean("status")) {
                            JSONObject data = response.getJSONObject("data");
                            tvNamaLengkap.setText(data.getString("nama_lengkap"));
                            tvUsername.setText(data.getString("username"));
                            tvEmail.setText(data.getString("email"));
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Gagal parsing profil", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show()
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

    private void clearSession() {
        getSharedPreferences("user_session", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }
    private void navigateToLogin() {
        startActivity(new Intent(this, login.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
    private void startActivityWithAnimation(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.button_navigation_menu);
        bottomNav.setSelectedItemId(R.id.profile);
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
            }else if (item.getItemId() == R.id.profile) {
                startActivityWithAnimation(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

}
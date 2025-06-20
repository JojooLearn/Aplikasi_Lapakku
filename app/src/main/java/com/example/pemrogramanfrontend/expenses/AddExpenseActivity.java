package com.example.pemrogramanfrontend.expenses;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.Utama.Dashboard;
import com.example.pemrogramanfrontend.api.db_connect;
import com.example.pemrogramanfrontend.utils.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class AddExpenseActivity extends AppCompatActivity {

    EditText etTitle, etAmount, etDescription;
    Button btnSimpan;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        session = new SessionManager(this);

        etTitle = findViewById(R.id.etTitle);
        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        btnSimpan = findViewById(R.id.btnSimpanExpense);

        btnSimpan.setOnClickListener(v -> simpanData());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Masukan Pengeluaran");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void simpanData() {
        String title = etTitle.getText().toString().trim();
        String amount = etAmount.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty() || amount.isEmpty()) {
            Toast.makeText(this, "Judul dan jumlah wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, db_connect.apiExpenseCreate,
                response -> {
                    Toast.makeText(this, "Pengeluaran berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, ExpenseListActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                },
                error -> Toast.makeText(this, "Gagal menyimpan: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> p = new HashMap<>();
                p.put("title", title);
                p.put("amount", amount);
                p.put("description", description);
                return p;
            }

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

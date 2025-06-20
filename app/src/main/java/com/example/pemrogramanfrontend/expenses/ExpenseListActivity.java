package com.example.pemrogramanfrontend.expenses;

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
import com.example.pemrogramanfrontend.adapters.ExpenseAdapter;
import com.example.pemrogramanfrontend.api.db_connect;
import com.example.pemrogramanfrontend.auth.login;
import com.example.pemrogramanfrontend.models.Expense;
import com.example.pemrogramanfrontend.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ExpenseAdapter adapter;
    List<Expense> expenseList;
    ProgressBar progressBar;
    Button btnAdd;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        session = new SessionManager(this);

        // Jika belum login, redirect
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, login.class));
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerViewExpense);
        progressBar = findViewById(R.id.progressBar);
        btnAdd = findViewById(R.id.btnAddExpense);
        expenseList = new ArrayList<>();
        adapter = new ExpenseAdapter(this, expenseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Daftar Pengeluaran");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnAdd.setOnClickListener(v -> {
            startActivityWithAnimation(new Intent(this, AddExpenseActivity.class));
        });

        loadExpenses();
    }

    private void loadExpenses() {
        progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, db_connect.apiExpenseList, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (response.getBoolean("status")) {
                            JSONArray data = response.getJSONArray("data");
                            expenseList.clear();

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                Expense e = new Expense();
                                e.setId(obj.getInt("id"));
                                e.setTitle(obj.getString("title"));
                                e.setAmount(obj.getDouble("amount"));
                                e.setDescription(obj.optString("description"));
                                e.setCreated_at(obj.getString("created_at"));
                                expenseList.add(e);
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "Data kosong / gagal", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Gagal parsing JSON", Toast.LENGTH_SHORT).show();
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
        startActivityWithAnimation(new Intent(this, Dashboard.class));
        onBackPressed();
        return true;
    }
}

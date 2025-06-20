package com.example.pemrogramanfrontend.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.api.db_connect;
import com.example.pemrogramanfrontend.models.Expense;
import com.example.pemrogramanfrontend.utils.SessionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private final Context context;
    private final List<Expense> expenseList;
    private final SessionManager session;

    public ExpenseAdapter(Context context, List<Expense> expenseList) {
        this.context = context;
        this.expenseList = expenseList;
        this.session = new SessionManager(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAmount, tvDate, tvDescription;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnDelete = itemView.findViewById(R.id.btnDeleteExpense);
        }
    }

    @NonNull
    @Override
    public ExpenseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.ViewHolder holder, int position) {
        Expense expense = expenseList.get(position);

        holder.tvTitle.setText(expense.getTitle());
        holder.tvAmount.setText("Rp " + String.format("%,.0f", expense.getAmount()));
        holder.tvDate.setText(expense.getCreated_at());
        holder.tvDescription.setText(expense.getDescription());

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Hapus Pengeluaran")
                    .setMessage("Yakin ingin menghapus pengeluaran ini?")
                    .setPositiveButton("Hapus", (dialog, which) -> {
                        hapusPengeluaran(expense.getId(), position);
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }

    private void hapusPengeluaran(int id, int position) {
        StringRequest request = new StringRequest(Request.Method.POST, db_connect.apiExpenseDelete,
                response -> {
                    Toast.makeText(context, "Pengeluaran berhasil dihapus", Toast.LENGTH_SHORT).show();
                    expenseList.remove(position);
                    notifyItemRemoved(position);
                },
                error -> {
                    Toast.makeText(context, "Gagal menghapus", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> p = new HashMap<>();
                p.put("id", String.valueOf(id));
                return p;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + session.getToken());
                return h;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }
}

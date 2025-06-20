package com.example.pemrogramanfrontend.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.api.db_connect;
import com.example.pemrogramanfrontend.models.StockIn;
import com.example.pemrogramanfrontend.utils.SessionManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockInAdapter extends RecyclerView.Adapter<StockInAdapter.ViewHolder> {

    private final Context context;
    private final List<StockIn> stockList;
    private final SessionManager session;

    public StockInAdapter(Context context, List<StockIn> stockList) {
        this.context = context;
        this.stockList = stockList;
        this.session = new SessionManager(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvQuantity, tvPrice, tvDate;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity    = itemView.findViewById(R.id.tvQuantity);
            tvPrice       = itemView.findViewById(R.id.tvPrice);
            tvDate        = itemView.findViewById(R.id.tvDate);
            btnDelete     = itemView.findViewById(R.id.btnDelete);
        }
    }

    @NonNull
    @Override
    public StockInAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_stockin, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StockInAdapter.ViewHolder holder, int position) {
        StockIn s = stockList.get(position);
        holder.tvProductName.setText(s.getProduct_name());
        holder.tvQuantity.setText("Qty: " + s.getQuantity());
        holder.tvPrice.setText("Harga: Rp" + s.getPrice());
        holder.tvDate.setText("Tanggal: " + s.getDate());

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Konfirmasi Hapus")
                    .setMessage("Yakin ingin menghapus stok ini?")
                    .setPositiveButton("Hapus", (dialog, which) -> deleteStockIn(s.getId(), position))
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }

    private void deleteStockIn(int stockId, int position) {
        // JSON yang dikirim -> { "id": 12 }
        JSONObject body = new JSONObject();
        try { body.put("id", stockId); } catch (Exception ignored) {}

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                db_connect.apiStockDelete, body,
                response -> {
                    try {
                        if (response.getBoolean("status")) {   // status true dari PHP
                            stockList.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Stok berhasil dihapus", Toast.LENGTH_SHORT).show();
                            ((Activity) context).recreate();
                        } else {
                            Toast.makeText(context,
                                    response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Respon tidak valid", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(context, "Gagal menghapus stok", Toast.LENGTH_SHORT).show()) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + session.getToken());
                h.put("Content-Type", "application/json");   // pastikan tipe JSON
                return h;
            }
        };

        Volley.newRequestQueue(context).add(req);
    }


    @Override
    public int getItemCount() {
        return stockList.size();
    }
}

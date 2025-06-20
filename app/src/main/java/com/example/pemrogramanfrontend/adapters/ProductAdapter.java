package com.example.pemrogramanfrontend.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.models.Product;
import com.example.pemrogramanfrontend.products.EditProductActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Tambahkan import
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pemrogramanfrontend.api.db_connect;
import com.example.pemrogramanfrontend.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    SessionManager sessionManager;
    private final Context context;
    private final List<Product> productList;        // data yang tampil sekarang
    private final List<Product> productListFull;    // data lengkap untuk filter

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.productListFull = new ArrayList<>(productList); // copy data awal
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, category, price, stock;
        MaterialButton btnEdit, btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            name     = itemView.findViewById(R.id.textName);
            category = itemView.findViewById(R.id.textCategory);
            price    = itemView.findViewById(R.id.textPrice);
            stock    = itemView.findViewById(R.id.textStock);
            btnEdit  = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.name.setText(product.getName());
        holder.category.setText("Kategori: " + product.getCategory());
        holder.price.setText("Harga: Rp" + product.getPrice());
        holder.stock.setText("Stok: " + product.getStock());

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditProductActivity.class);
            intent.putExtra("id", product.getId());
            intent.putExtra("name", product.getName());
            intent.putExtra("category", product.getCategory());
            intent.putExtra("price", product.getPrice());
            intent.putExtra("cost_price", product.getCost_price());
            intent.putExtra("stock", product.getStock());
            intent.putExtra("unit", product.getUnit());
            intent.putExtra("position", holder.getAdapterPosition());

            if (context instanceof Activity) {
                ((Activity) context).startActivityForResult(intent, 101);  // 101 = requestCode
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Hapus Produk")
                    .setMessage("Apakah Anda yakin ingin menghapus produk ini?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        sessionManager = new SessionManager(context);
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, db_connect.apiProductDelete,
                                response -> {
                                    Log.d("DELETE_RESP", response);
                                    try {
                                        JSONObject jsonResponse = new JSONObject(response);
                                        boolean success = jsonResponse.getBoolean("status"); // sebelumnya "success"
                                        String message = jsonResponse.getString("message");

                                        if (success) {
                                            productList.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, productList.size());
                                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                            ((Activity) context).recreate();
                                        } else {
                                            Toast.makeText(context, "Gagal hapus: " + message, Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        Toast.makeText(context, "Respon tidak valid", Toast.LENGTH_SHORT).show();
                                    }
                                },
                                error -> Toast.makeText(context, "Gagal menghapus produk", Toast.LENGTH_SHORT).show()
                        ) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                params.put("id", String.valueOf(product.getId()));
                                return params;
                            }

                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Authorization", "Bearer " + sessionManager.getToken());
                                return headers;
                            }
                        };

                        Volley.newRequestQueue(context).add(stringRequest);
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

}

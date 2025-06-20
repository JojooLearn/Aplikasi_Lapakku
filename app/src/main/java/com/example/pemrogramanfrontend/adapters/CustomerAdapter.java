package com.example.pemrogramanfrontend.adapters;

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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.api.db_connect;
import com.example.pemrogramanfrontend.models.Customer;
import com.example.pemrogramanfrontend.utils.SessionManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {

    private final Context context;
    private final List<Customer> customerList;
    private final SessionManager session;

    public CustomerAdapter(Context context, List<Customer> customerList) {
        this.context = context;
        this.customerList = customerList;
        this.session = new SessionManager(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvCustomerPhone, tvCustomerAddress;
        ImageButton btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerPhone = itemView.findViewById(R.id.tvCustomerPhone);
            tvCustomerAddress = itemView.findViewById(R.id.tvCustomerAddress);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    @NonNull
    @Override
    public CustomerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_customer, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerAdapter.ViewHolder holder, int position) {
        Customer c = customerList.get(position);
        holder.tvCustomerName.setText(c.getName());
        holder.tvCustomerPhone.setText(c.getPhone());
        holder.tvCustomerAddress.setText(c.getAddress());

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Hapus Data")
                    .setMessage("Yakin ingin menghapus pelanggan ini?")
                    .setPositiveButton("Hapus", (dialog, which) -> deleteCustomer(c.getId(), position))
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }

    private void deleteCustomer(int customerId, int position) {
        StringRequest request = new StringRequest(Request.Method.POST, db_connect.apiCustomerDelete,
                response -> {
                    try {
                        android.util.Log.d("DELETE_CUSTOMER", "Response: " + response);
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("status")) {
                            customerList.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Berhasil dihapus", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Gagal parsing respons", Toast.LENGTH_SHORT).show();
                        android.util.Log.e("DELETE_CUSTOMER", "Parse Error: " + e.getMessage());
                    }
                },
                error -> {
                    Toast.makeText(context, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("id", String.valueOf(customerId));
                return p;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + session.getToken());
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }
    @Override
    public int getItemCount() {
        return customerList.size();
    }
}

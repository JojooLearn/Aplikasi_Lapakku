package com.example.pemrogramanfrontend.adapters.laporan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.models.laporan.CustomerItem;
import java.util.List;

public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.ViewHolder> {
    private Context context;
    private List<CustomerItem> customers;

    // 1. Tambahkan Context di constructor
    public CustomersAdapter(Context context, List<CustomerItem> customers) {
        this.context = context;
        this.customers = customers;
    }

    // 2. Update method onCreateViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_customer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CustomerItem item = customers.get(position);
        holder.tvCustomerName.setText(item.getName());
        holder.tvPhone.setText(item.getPhone());
        holder.tvAddress.setText(item.getAddress());
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public void updateData(List<CustomerItem> newCustomers) {
        this.customers = newCustomers;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvPhone, tvAddress;

        ViewHolder(View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvPhone = itemView.findViewById(R.id.tvCustomerPhone);
            tvAddress = itemView.findViewById(R.id.tvCustomerAddress);
        }
    }
}
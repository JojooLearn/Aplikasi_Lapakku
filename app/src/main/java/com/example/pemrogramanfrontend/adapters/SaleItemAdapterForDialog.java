package com.example.pemrogramanfrontend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.models.SaleItem;

import java.util.List;

public class SaleItemAdapterForDialog extends RecyclerView.Adapter<SaleItemAdapterForDialog.ViewHolder> {

    private final List<SaleItem> itemList;

    public SaleItemAdapterForDialog(List<SaleItem> itemList) {
        this.itemList = itemList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQty, tvPrice, tvSubtotal;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvQty = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
        }
    }

    @NonNull
    @Override
    public SaleItemAdapterForDialog.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sale_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SaleItemAdapterForDialog.ViewHolder holder, int position) {
        SaleItem item = itemList.get(position);

        holder.tvName.setText(item.getProductName());
        holder.tvQty.setText("Qty: " + item.getQuantity());
        holder.tvPrice.setText("Harga: Rp" + item.getPrice());
        holder.tvSubtotal.setText("Subtotal: Rp" + item.getSubtotal());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}

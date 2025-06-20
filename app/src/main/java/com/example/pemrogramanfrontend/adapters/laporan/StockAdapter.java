package com.example.pemrogramanfrontend.adapters.laporan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.models.laporan.StockItem;
import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {
    private Context context;
    private List<StockItem> stockList;

    // 1. Tambahkan Context di constructor
    public StockAdapter(Context context, List<StockItem> stockList) {
        this.context = context;
        this.stockList = stockList;
    }

    // 2. Update method onCreateViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_stockin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StockItem item = stockList.get(position);
        holder.tvProductName.setText(item.getProductName());
        holder.tvQuantity.setText(String.format("%d unit", item.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

    public void updateData(List<StockItem> newStockList) {
        this.stockList = newStockList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvQuantity;

        ViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }
}
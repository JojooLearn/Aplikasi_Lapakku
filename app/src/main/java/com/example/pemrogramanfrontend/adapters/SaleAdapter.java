package com.example.pemrogramanfrontend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.models.Sale;

import java.util.List;

public class SaleAdapter extends RecyclerView.Adapter<SaleAdapter.ViewHolder> {

    private final Context context;
    private final List<Sale> saleList;

    public SaleAdapter(Context context, List<Sale> saleList) {
        this.context = context;
        this.saleList = saleList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInvoice, tvCustomer, tvTotal, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInvoice = itemView.findViewById(R.id.tvInvoice);
            tvCustomer = itemView.findViewById(R.id.tvCustomer);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }

    @NonNull
    @Override
    public SaleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_sale, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SaleAdapter.ViewHolder holder, int position) {
        Sale s = saleList.get(position);

        holder.tvInvoice.setText( s.getInvoice_number());
        holder.tvCustomer.setText("Pelanggan: " + s.getCustomer_name());
        holder.tvTotal.setText("Total: Rp" + s.getTotal());
        holder.tvDate.setText("Tanggal: " + s.getDate());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(saleList.get(position));
        });

    }

    @Override
    public int getItemCount() {
        return saleList.size();
    }
    public interface OnItemClickListener {
        void onItemClick(Sale sale);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}

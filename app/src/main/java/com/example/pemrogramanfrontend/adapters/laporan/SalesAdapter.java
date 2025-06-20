package com.example.pemrogramanfrontend.adapters.laporan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.models.laporan.SalesItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.SalesViewHolder> {

    private final Context context;
    private List<SalesItem> salesList;

    public SalesAdapter(Context context, List<SalesItem> salesList) {
        this.context = context;
        this.salesList = salesList;
    }

    /**
     * Mengganti seluruh data dengan data baru dan menyegarkan tampilan.
     */
    public void setSalesList(List<SalesItem> salesList) {
        this.salesList = salesList;
        notifyDataSetChanged();
    }

    /**
     * Alternatif setSalesList - untuk update data lebih fleksibel.
     */
    public void updateData(List<SalesItem> newData) {
        setSalesList(newData);
    }

    @NonNull
    @Override
    public SalesAdapter.SalesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sale_detail, parent, false);
        return new SalesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalesAdapter.SalesViewHolder holder, int position) {
        SalesItem item = salesList.get(position);

        holder.tvProductName.setText(item.getProductName());
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        // Format angka menjadi Rupiah
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        holder.tvPrice.setText(formatRupiah.format(item.getPrice()));
        holder.tvSubtotal.setText(formatRupiah.format(item.getSubtotal()));
    }

    @Override
    public int getItemCount() {
        return salesList != null ? salesList.size() : 0;
    }

    public class SalesViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvQuantity, tvPrice, tvSubtotal;

        public SalesViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
        }
    }

}

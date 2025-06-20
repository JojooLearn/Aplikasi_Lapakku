package com.example.pemrogramanfrontend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.models.Riwayat;

import java.util.List;

public class RiwayatAdapter extends RecyclerView.Adapter<RiwayatAdapter.ViewHolder> {

    private final Context context;
    private final List<Riwayat> riwayatList;

    public RiwayatAdapter(Context context, List<Riwayat> riwayatList) {
        this.context = context;
        this.riwayatList = riwayatList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvTitle, tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvRiwayatType);
            tvTitle = itemView.findViewById(R.id.tvRiwayatTitle);
            tvDate = itemView.findViewById(R.id.tvRiwayatDate);
        }
    }

    @NonNull
    @Override
    public RiwayatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_riwayat, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RiwayatAdapter.ViewHolder holder, int position) {
        Riwayat riwayat = riwayatList.get(position);

        holder.tvType.setText(riwayat.getType());
        holder.tvTitle.setText(riwayat.getTitle());
        holder.tvDate.setText(riwayat.getDate());
    }

    @Override
    public int getItemCount() {
        return riwayatList.size();
    }
}
package com.example.pemrogramanfrontend.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.models.Product;
import com.example.pemrogramanfrontend.models.SaleItem;

import java.util.List;

public class SaleItemAdapter extends RecyclerView.Adapter<SaleItemAdapter.ViewHolder> {

    private final Context context;
    private final List<SaleItem> itemList;
    private final List<Product> productList;
    private final OnItemChangeListener listener;

    public interface OnItemChangeListener {
        void onItemChanged();
        void onItemRemoved(int position);
    }

    public SaleItemAdapter(Context context, List<SaleItem> itemList, List<Product> productList, OnItemChangeListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.productList = productList;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Spinner spinnerProduk;
        EditText etQty, etHarga;
        ImageButton btnHapus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            spinnerProduk = itemView.findViewById(R.id.spinnerProduk);
            etQty = itemView.findViewById(R.id.etQty);
            etHarga = itemView.findViewById(R.id.etHarga);
            btnHapus = itemView.findViewById(R.id.btnHapus);
        }

        public void bind(int position) {
            SaleItem item = itemList.get(position);

            // Setup spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            for (Product p : productList) {
                adapter.add(p.getName());
            }

            spinnerProduk.setAdapter(adapter);

            // Set default terpilih jika sudah ada
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).getId() == item.getProductId()) {
                    spinnerProduk.setSelection(i);
                    break;
                }
            }

            spinnerProduk.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int i, long l) {
                    item.setProductId(productList.get(i).getId());
                    listener.onItemChanged();
                }

                @Override
                public void onNothingSelected(android.widget.AdapterView<?> adapterView) {
                }
            });

            etQty.setText(String.valueOf(item.getQuantity()));
            etHarga.setText(String.valueOf(item.getPrice()));

            etQty.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    int qty = editable.toString().isEmpty() ? 0 : Integer.parseInt(editable.toString());
                    item.setQuantity(qty);
                    listener.onItemChanged();
                }
            });

            etHarga.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    int harga = editable.toString().isEmpty() ? 0 : Integer.parseInt(editable.toString());
                    item.setPrice(harga);
                    listener.onItemChanged();
                }
            });

            btnHapus.setOnClickListener(v -> {
                itemList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, itemList.size());
                listener.onItemRemoved(position);
            });
        }
    }

    @NonNull
    @Override
    public SaleItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_sale_entry, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SaleItemAdapter.ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}

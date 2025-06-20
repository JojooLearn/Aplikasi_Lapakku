package com.example.pemrogramanfrontend.adapters.laporan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.models.laporan.ExpenseItem;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ViewHolder> {
    private Context context;
    private List<ExpenseItem> expenses;

    // 1. Tambahkan Context di constructor
    public ExpensesAdapter(Context context, List<ExpenseItem> expenses) {
        this.context = context;
        this.expenses = expenses;
    }

    // 2. Update method onCreateViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExpenseItem item = expenses.get(position);
        holder.tvExpenseTitle.setText(item.getTitle());

        try {
            double amount = Double.parseDouble(item.getAmount());
            holder.tvAmount.setText(formatCurrency(amount));
        } catch (NumberFormatException e) {
            holder.tvAmount.setText("Rp 0");
        }
    }



    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public void updateData(List<ExpenseItem> newExpenses) {
        this.expenses = newExpenses;
        notifyDataSetChanged();
    }

    private String formatCurrency(double amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return format.format(amount);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvExpenseTitle, tvAmount;

        ViewHolder(View itemView) {
            super(itemView);
            tvExpenseTitle = itemView.findViewById(R.id.tvTitle);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}
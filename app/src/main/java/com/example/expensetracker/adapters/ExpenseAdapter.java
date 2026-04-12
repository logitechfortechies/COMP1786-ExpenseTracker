package com.example.expensetracker.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensetracker.R;
import com.example.expensetracker.models.Expense;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    public interface ExpenseClickListener {
        void onEditClick(Expense expense);
        void onDeleteClick(Expense expense);
    }

    private List<Expense> expenses;
    private final ExpenseClickListener listener;

    public ExpenseAdapter(List<Expense> expenses, ExpenseClickListener listener) {
        this.expenses = expenses;
        this.listener = listener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Expense e = expenses.get(pos);
        h.tvCode.setText(e.getExpenseCode());
        h.tvDate.setText(e.getDate());
        h.tvAmount.setText(String.format("%s %.2f", e.getCurrency(), e.getAmount()));
        h.tvType.setText(e.getType() + " • " + e.getPaymentMethod());
        h.tvClaimant.setText("by " + e.getClaimant());
        h.tvStatus.setText(e.getPaymentStatus().toUpperCase());

        int color;
        int bg;
        switch (e.getPaymentStatus()) {
            case "Paid":
                color = Color.parseColor("#10B981");
                bg = Color.parseColor("#D1FAE5");
                break;
            case "Reimbursed":
                color = Color.parseColor("#3B82F6");
                bg = Color.parseColor("#DBEAFE");
                break;
            default:
                color = Color.parseColor("#F59E0B");
                bg = Color.parseColor("#FEF3C7");
                break;
        }
        h.tvStatus.setTextColor(color);
        h.tvStatus.setBackgroundColor(bg);

        if (h.tvSync != null) {
            if (e.getSynced() == 1) {
                h.tvSync.setText("✓ Synced");
                h.tvSync.setTextColor(Color.parseColor("#10B981"));
            } else {
                h.tvSync.setText("⬆ Local");
                h.tvSync.setTextColor(Color.parseColor("#64748B"));
            }
        }

        h.btnEdit.setOnClickListener(v -> listener.onEditClick(e));
        h.btnDelete.setOnClickListener(v -> listener.onDeleteClick(e));
    }

    @Override public int getItemCount() { return expenses.size(); }

    public void updateData(List<Expense> data) {
        this.expenses = data; notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvDate, tvAmount, tvType, tvClaimant, tvStatus, tvSync;
        ImageButton btnEdit, btnDelete;
        ViewHolder(View v) {
            super(v);
            tvCode     = v.findViewById(R.id.tvExpenseCode);
            tvDate     = v.findViewById(R.id.tvExpenseDate);
            tvAmount   = v.findViewById(R.id.tvAmount);
            tvType     = v.findViewById(R.id.tvExpenseType);
            tvClaimant = v.findViewById(R.id.tvClaimant);
            tvStatus   = v.findViewById(R.id.tvPayStatus);
            tvSync     = v.findViewById(R.id.tvSyncStatus);
            btnEdit    = v.findViewById(R.id.btnEditExpense);
            btnDelete  = v.findViewById(R.id.btnDeleteExpense);
        }
    }
}
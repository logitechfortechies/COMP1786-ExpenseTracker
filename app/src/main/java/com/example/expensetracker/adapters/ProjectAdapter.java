package com.example.expensetracker.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensetracker.R;
import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.models.Project;
import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

    public interface ProjectClickListener {
        void onProjectClick(Project project);
        void onEditClick(Project project);
        void onDeleteClick(Project project);
    }

    private List<Project> projects;
    private final ProjectClickListener listener;

    public ProjectAdapter(List<Project> projects, ProjectClickListener listener) {
        this.projects = projects;
        this.listener = listener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Project p = projects.get(position);
        DatabaseHelper db = new DatabaseHelper(h.itemView.getContext());
        double totalSpent = db.getProjectTotalExpenses(p.getId());

        h.tvName.setText(p.getProjectName());
        h.tvCode.setText(p.getProjectCode());
        h.tvManager.setText(p.getManager());
        h.tvBudget.setText(String.format("£%.2f", p.getBudget()));
        h.tvStatus.setText(p.getStatus());

        // Progress bar calculation
        int progress = (int) ((totalSpent / p.getBudget()) * 100);
        h.progressBudget.setProgress(Math.min(progress, 100));
        h.tvSpent.setText(String.format("Spent: £%.2f", totalSpent));

        // Colour status text
        int statusColor;
        int statusBg;
        switch (p.getStatus()) {
            case "Active":
                statusColor = Color.parseColor("#10B981");
                statusBg = Color.parseColor("#D1FAE5");
                break;
            case "Completed":
                statusColor = Color.parseColor("#3B82F6");
                statusBg = Color.parseColor("#DBEAFE");
                break;
            default:
                statusColor = Color.parseColor("#F59E0B");
                statusBg = Color.parseColor("#FEF3C7");
                break;
        }
        h.tvStatus.setTextColor(statusColor);
        h.tvStatus.setBackgroundColor(statusBg);

        // Sync indicator
        if (p.getSynced() == 1) {
            h.tvSync.setText("✓ Synced");
            h.tvSync.setTextColor(Color.parseColor("#10B981"));
        } else {
            h.tvSync.setText("⬆ Local");
            h.tvSync.setTextColor(Color.parseColor("#64748B"));
        }

        h.itemView.setOnClickListener(v -> listener.onProjectClick(p));
        h.btnEdit.setOnClickListener(v -> listener.onEditClick(p));
        h.btnDelete.setOnClickListener(v -> listener.onDeleteClick(p));
    }

    @Override public int getItemCount() { return projects.size(); }

    public void updateData(List<Project> newData) {
        this.projects = newData;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCode, tvManager, tvBudget, tvStatus, tvSync, tvSpent;
        ImageButton btnEdit, btnDelete;
        ProgressBar progressBudget;

        ViewHolder(View v) {
            super(v);
            tvName    = v.findViewById(R.id.tvProjectName);
            tvCode    = v.findViewById(R.id.tvProjectCode);
            tvManager = v.findViewById(R.id.tvManager);
            tvBudget  = v.findViewById(R.id.tvBudget);
            tvStatus  = v.findViewById(R.id.tvStatus);
            tvSync    = v.findViewById(R.id.tvSync);
            tvSpent   = v.findViewById(R.id.tvSpent);
            btnEdit   = v.findViewById(R.id.btnEdit);
            btnDelete = v.findViewById(R.id.btnDelete);
            progressBudget = v.findViewById(R.id.progressBudget);
        }
    }
}
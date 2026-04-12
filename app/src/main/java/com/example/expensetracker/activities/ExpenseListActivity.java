package com.example.expensetracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensetracker.R;
import com.example.expensetracker.adapters.ExpenseAdapter;
import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.models.Expense;
import com.example.expensetracker.models.Project;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;

public class ExpenseListActivity extends AppCompatActivity
        implements ExpenseAdapter.ExpenseClickListener {

    private RecyclerView recycler;
    private ExpenseAdapter adapter;
    private DatabaseHelper db;
    private View tvEmpty;
    private int projectId;
    private TextView tvProjectTitle, tvTotalSpent, tvRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        projectId = getIntent().getIntExtra("PROJECT_ID", -1);
        String projectName = getIntent().getStringExtra("PROJECT_NAME");

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        db       = new DatabaseHelper(this);
        recycler = findViewById(R.id.recyclerExpenses);
        tvEmpty  = findViewById(R.id.tvEmpty);
        tvProjectTitle = findViewById(R.id.tvProjectTitle);
        tvTotalSpent   = findViewById(R.id.tvTotalSpent);
        tvRemaining    = findViewById(R.id.tvRemaining);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        ExtendedFloatingActionButton fab = findViewById(R.id.fabAddExpense);
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, AddExpenseActivity.class)
                        .putExtra("PROJECT_ID", projectId)));

        if (projectName != null) tvProjectTitle.setText(projectName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenses();
    }

    private void loadExpenses() {
        List<Expense> expenses = db.getExpensesForProject(projectId);
        Project p = db.getProjectById(projectId);
        double total = db.getProjectTotalExpenses(projectId);

        tvTotalSpent.setText(String.format("Total Spent: £%.2f", total));
        if (p != null) {
            tvRemaining.setText(String.format("Budget: £%.2f", p.getBudget()));
        }

        tvEmpty.setVisibility(expenses.isEmpty() ? View.VISIBLE : View.GONE);
        if (adapter == null) {
            adapter = new ExpenseAdapter(expenses, this);
            recycler.setAdapter(adapter);
        } else {
            adapter.updateData(expenses);
        }
    }

    @Override
    public void onEditClick(Expense e) {
        startActivity(new Intent(this, AddExpenseActivity.class)
                .putExtra("EXPENSE_ID", e.getId())
                .putExtra("PROJECT_ID", projectId));
    }

    @Override
    public void onDeleteClick(Expense e) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Expense")
                .setMessage("Delete expense " + e.getExpenseCode() + "?")
                .setPositiveButton("Delete", (d, w) -> {
                    db.deleteExpense(e.getId());
                    loadExpenses();
                    Snackbar.make(recycler, "Expense deleted",
                            Snackbar.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null).show();
    }
}
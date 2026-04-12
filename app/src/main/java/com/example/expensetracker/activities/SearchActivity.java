package com.example.expensetracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensetracker.R;
import com.example.expensetracker.adapters.ProjectAdapter;
import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.models.Project;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;
import java.util.List;

public class SearchActivity extends AppCompatActivity
        implements ProjectAdapter.ProjectClickListener {

    private TextInputEditText etSearch, etFromDate, etToDate, etManagerFilter;
    private AutoCompleteTextView spStatus;
    private RecyclerView recycler;
    private TextView tvResultCount;
    private View advancedPanel;
    private DatabaseHelper db;
    private ProjectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupToolbar();

        db            = new DatabaseHelper(this);
        etSearch      = findViewById(R.id.etSearch);
        etFromDate    = findViewById(R.id.etFromDate);
        etToDate      = findViewById(R.id.etToDate);
        etManagerFilter = findViewById(R.id.etManagerSearch);
        spStatus      = findViewById(R.id.spStatusSearch);
        recycler      = findViewById(R.id.recyclerSearch);
        tvResultCount = findViewById(R.id.tvResultCount);
        advancedPanel = findViewById(R.id.advancedPanel);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        spStatus.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"", "Active", "Completed", "On Hold"}));

        // Live search on every keystroke
        etSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            public void onTextChanged(CharSequence s, int a, int b, int c) { doSearch(); }
            public void afterTextChanged(Editable s) {}
        });

        // FIX: date pickers use Calendar.getInstance() — not hardcoded 2024
        etFromDate.setFocusable(false);
        etToDate.setFocusable(false);
        etFromDate.setOnClickListener(v -> pickDate(etFromDate));
        etToDate.setOnClickListener(v -> pickDate(etToDate));

        MaterialButton btnToggle = findViewById(R.id.btnToggleAdvanced);
        btnToggle.setOnClickListener(v -> {
            boolean visible = advancedPanel.getVisibility() == View.VISIBLE;
            advancedPanel.setVisibility(visible ? View.GONE : View.VISIBLE);
            btnToggle.setText(visible ? "▼ Advanced Search" : "▲ Hide Filters");
        });

        findViewById(R.id.btnAdvancedSearch).setOnClickListener(v -> doAdvancedSearch());
        findViewById(R.id.btnClearFilters).setOnClickListener(v -> clearFilters());

        doSearch(); // show all on first load
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void doSearch() {
        String kw = etSearch.getText() != null ? etSearch.getText().toString().trim() : "";
        showResults(kw.isEmpty() ? db.getAllProjects() : db.searchProjects(kw));
    }

    private void doAdvancedSearch() {
        String status  = spStatus.getText() != null ? spStatus.getText().toString() : "";
        String manager = etManagerFilter.getText() != null ? etManagerFilter.getText().toString().trim() : "";
        String from    = etFromDate.getText() != null ? etFromDate.getText().toString() : "";
        String to      = etToDate.getText() != null ? etToDate.getText().toString() : "";
        showResults(db.advancedSearch(status, manager, from, to));
    }

    private void clearFilters() {
        spStatus.setText("", false);
        etManagerFilter.setText("");
        etFromDate.setText("");
        etToDate.setText("");
        doSearch();
    }

    private void showResults(List<Project> results) {
        tvResultCount.setText(results.size() + " project(s) found");
        if (adapter == null) {
            adapter = new ProjectAdapter(results, this);
            recycler.setAdapter(adapter);
        } else {
            adapter.updateData(results);
        }
    }

    // FIX: uses current date (not hardcoded 2024)
    private void pickDate(TextInputEditText target) {
        Calendar cal = Calendar.getInstance();
        new android.app.DatePickerDialog(this,
                (v, y, m, d) -> target.setText(
                        String.format("%04d-%02d-%02d", y, m + 1, d)),
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    @Override public void onProjectClick(Project p) {
        startActivity(new Intent(this, ExpenseListActivity.class)
                .putExtra("PROJECT_ID", p.getId())
                .putExtra("PROJECT_NAME", p.getProjectName()));
    }
    @Override public void onEditClick(Project p) {
        startActivity(new Intent(this, AddProjectActivity.class)
                .putExtra("PROJECT_ID", p.getId()));
    }
    @Override public void onDeleteClick(Project p) {}
}
package com.example.expensetracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensetracker.R;
import com.example.expensetracker.adapters.ProjectAdapter;
import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.models.Project;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;

public class ProjectListActivity extends AppCompatActivity implements ProjectAdapter.ProjectClickListener {
    private RecyclerView recyclerView;
    private ProjectAdapter adapter;
    private DatabaseHelper db;
    private View tvEmpty;
    private EditText etSearch;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);

        db = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerProjects);
        tvEmpty = findViewById(R.id.tvEmpty);
        etSearch = findViewById(R.id.etSearch);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ExtendedFloatingActionButton fab = findViewById(R.id.fabAddProject);
        fab.setOnClickListener(v -> startActivity(new Intent(this, AddProjectActivity.class)));

        setupSearch();
        setupBottomNav();
    }

    private void setupBottomNav() {
        bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_projects);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_projects) {
                return true;
            } else if (id == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_sync) {
                startActivity(new Intent(this, UploadActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void performSearch(String query) {
        List<Project> projects = query.isEmpty() ? db.getAllProjects() : db.searchProjects(query);
        updateUI(projects);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProjects();
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_projects);
        }
    }

    private void loadProjects() {
        updateUI(db.getAllProjects());
    }

    private void updateUI(List<Project> projects) {
        tvEmpty.setVisibility(projects.isEmpty() ? View.VISIBLE : View.GONE);
        if (adapter == null) {
            adapter = new ProjectAdapter(projects, this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(projects);
        }
    }

    @Override
    public void onProjectClick(Project p) {
        startActivity(new Intent(this, ExpenseListActivity.class)
                .putExtra("PROJECT_ID", p.getId())
                .putExtra("PROJECT_NAME", p.getProjectName()));
    }

    @Override
    public void onEditClick(Project p) {
        startActivity(new Intent(this, AddProjectActivity.class)
                .putExtra("PROJECT_ID", p.getId()));
    }

    @Override
    public void onDeleteClick(Project p) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Project")
                .setMessage("Delete \"" + p.getProjectName() + "\" and all its expenses?")
                .setPositiveButton("Delete", (d, w) -> {
                    db.deleteProject(p.getId());
                    loadProjects();
                    Snackbar.make(recyclerView, "Deleted", Snackbar.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_project_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_reset_db) {
            new AlertDialog.Builder(this)
                    .setTitle("Reset Database")
                    .setMessage("Delete ALL projects and expenses?")
                    .setPositiveButton("Reset", (d, w) -> {
                        db.resetDatabase();
                        loadProjects();
                        Snackbar.make(recyclerView, "Database cleared", Snackbar.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
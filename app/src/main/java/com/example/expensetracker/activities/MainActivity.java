package com.example.expensetracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetracker.R;
import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.models.Project;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private TextView tvTotalBudget;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        tvTotalBudget = findViewById(R.id.tvMainTotalBudget);

        MaterialCardView cardProjects = findViewById(R.id.cardProjects);
        MaterialCardView cardSearch = findViewById(R.id.cardSearch);
        MaterialCardView cardUpload = findViewById(R.id.cardUpload);
        MaterialCardView cardReset = findViewById(R.id.cardReset);
        MaterialButton btnQuickAdd = findViewById(R.id.btnQuickAdd);

        cardProjects.setOnClickListener(v -> startActivity(new Intent(this, ProjectListActivity.class)));
        cardSearch.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        cardUpload.setOnClickListener(v -> startActivity(new Intent(this, UploadActivity.class)));

        if (cardReset != null) {
            cardReset.setOnClickListener(v -> showResetConfirmation());
        }

        if (btnQuickAdd != null) {
            btnQuickAdd.setOnClickListener(v -> startActivity(new Intent(this, AddProjectActivity.class)));
        }

        setupBottomNav();
    }

    private void setupBottomNav() {
        bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_projects) {
                startActivity(new Intent(this, ProjectListActivity.class));
                return true;
            } else if (id == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            } else if (id == R.id.nav_sync) {
                startActivity(new Intent(this, UploadActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSummary();
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    private void updateSummary() {
        List<Project> projects = db.getAllProjects();
        double totalBudget = 0;
        for (Project p : projects) {
            if ("Active".equals(p.getStatus())) {
                totalBudget += p.getBudget();
            }
        }
        tvTotalBudget.setText(String.format("£%.2f", totalBudget));
    }

    private void showResetConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Reset Database")
                .setMessage("Are you sure you want to delete ALL projects and expenses? This cannot be undone.")
                .setPositiveButton("Reset", (dialog, which) -> {
                    db.resetDatabase();
                    updateSummary();
                    Toast.makeText(this, "Database cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

package com.example.expensetracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.models.Project;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private TextView tvTotalBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        tvTotalBudget = findViewById(R.id.tvMainTotalBudget);

        MaterialCardView cardProjects = findViewById(R.id.cardProjects);
        MaterialCardView cardSearch   = findViewById(R.id.cardSearch);
        MaterialCardView cardUpload   = findViewById(R.id.cardUpload);
        MaterialButton btnQuickAdd    = findViewById(R.id.btnQuickAdd);

        cardProjects.setOnClickListener(v ->
                startActivity(new Intent(this, ProjectListActivity.class)));

        cardSearch.setOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));

        cardUpload.setOnClickListener(v ->
                startActivity(new Intent(this, UploadActivity.class)));

        btnQuickAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddProjectActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSummary();
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
}

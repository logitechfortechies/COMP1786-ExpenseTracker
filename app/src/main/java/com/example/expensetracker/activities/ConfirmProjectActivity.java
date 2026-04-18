package com.example.expensetracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.models.Project;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

public class ConfirmProjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_project);
        setupToolbar();

        Intent in   = getIntent();
        String code    = in.getStringExtra("CODE");
        String name    = in.getStringExtra("NAME");
        String desc    = in.getStringExtra("DESC");
        String start   = in.getStringExtra("START");
        String end     = in.getStringExtra("END");
        String mgr     = in.getStringExtra("MGR");
        String status  = in.getStringExtra("STATUS");
        double budget  = in.getDoubleExtra("BUDGET", 0);
        String special = in.getStringExtra("SPECIAL");
        String client  = in.getStringExtra("CLIENT");
        String addInfo = in.getStringExtra("ADDINFO");
        int editId     = in.getIntExtra("EDIT_ID", -1);

        setText(R.id.tvCode,    "Code: "        + code);
        setText(R.id.tvName,    name);
        setText(R.id.tvDesc,    desc);
        setText(R.id.tvDates,   start + "  →  " + end);
        setText(R.id.tvManager, "Manager: "     + mgr);
        setText(R.id.tvStatus,  status);
        setText(R.id.tvBudget,  "£" + String.format("%.2f", budget));
        setText(R.id.tvSpecial, special  != null && !special.isEmpty()  ? special  : "—");
        setText(R.id.tvClient,  client   != null && !client.isEmpty()   ? client   : "—");
        setText(R.id.tvAddInfo, addInfo  != null && !addInfo.isEmpty()  ? addInfo  : "—");

        MaterialButton btnEdit    = findViewById(R.id.btnEdit);
        MaterialButton btnConfirm = findViewById(R.id.btnConfirm);

        btnEdit.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            DatabaseHelper db = new DatabaseHelper(this);
            Project p = new Project();
            p.setProjectCode(code);
            p.setProjectName(name);
            p.setDescription(desc);
            p.setStartDate(start);
            p.setEndDate(end);
            p.setManager(mgr);
            p.setStatus(status);
            p.setBudget(budget);
            p.setSpecialRequirements(special);
            p.setClientInfo(client);
            p.setAdditionalInfo(addInfo);
            p.setSynced(0);

            if (editId != -1) {
                p.setId(editId);
                db.updateProject(p);
                Snackbar.make(v, "Project updated!", Snackbar.LENGTH_SHORT).show();
            } else {
                db.insertProject(p);
                Snackbar.make(v, "Project saved!", Snackbar.LENGTH_SHORT).show();
            }

            startActivity(new Intent(this, ProjectListActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });
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

    private void setText(int viewId, String text) {
        ((TextView) findViewById(viewId)).setText(text);
    }
}
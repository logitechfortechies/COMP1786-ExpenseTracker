package com.example.expensetracker.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.expensetracker.R;
import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.models.Project;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class AddProjectActivity extends AppCompatActivity {

    private TextInputEditText etCode, etName, etDesc, etStart, etEnd;
    private TextInputEditText etManager, etBudget, etSpecial, etClient, etAddInfo;
    private AutoCompleteTextView spStatus;
    private MaterialButton btnSave;
    private DatabaseHelper db;
    private Project editing = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        db = new DatabaseHelper(this);
        bindViews();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        spStatus.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"Active", "Completed", "On Hold"}));

        etStart.setFocusable(false);
        etEnd.setFocusable(false);
        etStart.setOnClickListener(v -> pickDate(etStart));
        etEnd.setOnClickListener(v -> pickDate(etEnd));

        int projectId = getIntent().getIntExtra("PROJECT_ID", -1);
        if (projectId != -1) {
            editing = db.getProjectById(projectId);
            if (editing != null) fill(editing);
            setTitle("Edit Project");
        } else {
            setTitle("Add New Project");
        }

        btnSave.setOnClickListener(v -> validateAndNext());
    }

    private void bindViews() {
        etCode = findViewById(R.id.etProjectCode);
        etName = findViewById(R.id.etProjectName);
        etDesc = findViewById(R.id.etDescription);
        etStart = findViewById(R.id.etStartDate);
        etEnd = findViewById(R.id.etEndDate);
        etManager = findViewById(R.id.etManager);
        spStatus = findViewById(R.id.spinnerStatus);
        etBudget = findViewById(R.id.etBudget);
        etSpecial = findViewById(R.id.etSpecialReq);
        etClient = findViewById(R.id.etClientInfo);
        etAddInfo = findViewById(R.id.etAdditionalInfo);
        btnSave = findViewById(R.id.btnSave);
    }

    private void pickDate(TextInputEditText target) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this,
                (v, year, month, day) -> target.setText(String.format("%04d-%02d-%02d", year, month + 1, day)),
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void fill(Project p) {
        etCode.setText(p.getProjectCode());
        etName.setText(p.getProjectName());
        etDesc.setText(p.getDescription());
        etStart.setText(p.getStartDate());
        etEnd.setText(p.getEndDate());
        etManager.setText(p.getManager());
        spStatus.setText(p.getStatus(), false);
        etBudget.setText(String.valueOf(p.getBudget()));
        etSpecial.setText(p.getSpecialRequirements());
        etClient.setText(p.getClientInfo());
        etAddInfo.setText(p.getAdditionalInfo());
    }

    private void validateAndNext() {
        if (!req(etCode, "Project Code is required")) return;
        if (!req(etName, "Project Name is required")) return;
        if (!req(etDesc, "Description is required")) return;
        if (!req(etStart, "Start Date is required")) return;
        if (!req(etEnd, "End Date is required")) return;
        if (!req(etManager, "Manager name is required")) return;
        if (!req(etBudget, "Budget is required")) return;
        
        if (TextUtils.isEmpty(spStatus.getText())) {
            spStatus.setError("Please select a status");
            return;
        }

        double budget;
        try {
            budget = Double.parseDouble(val(etBudget));
        } catch (NumberFormatException e) {
            layout(etBudget).setError("Enter a valid number");
            return;
        }

        Intent i = new Intent(this, ConfirmProjectActivity.class);
        i.putExtra("CODE", val(etCode));
        i.putExtra("NAME", val(etName));
        i.putExtra("DESC", val(etDesc));
        i.putExtra("START", val(etStart));
        i.putExtra("END", val(etEnd));
        i.putExtra("MGR", val(etManager));
        i.putExtra("STATUS", spStatus.getText().toString());
        i.putExtra("BUDGET", budget);
        i.putExtra("SPECIAL", val(etSpecial));
        i.putExtra("CLIENT", val(etClient));
        i.putExtra("ADDINFO", val(etAddInfo));
        if (editing != null) i.putExtra("EDIT_ID", editing.getId());
        startActivity(i);
    }

    private boolean req(TextInputEditText f, String msg) {
        if (TextUtils.isEmpty(f.getText())) {
            layout(f).setError(msg);
            return false;
        }
        layout(f).setError(null);
        return true;
    }

    private String val(TextInputEditText f) {
        return f.getText() != null ? f.getText().toString().trim() : "";
    }

    private TextInputLayout layout(TextInputEditText f) {
        return (TextInputLayout) f.getParent().getParent();
    }
}

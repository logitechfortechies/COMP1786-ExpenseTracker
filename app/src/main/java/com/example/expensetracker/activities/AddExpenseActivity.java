package com.example.expensetracker.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.models.Expense;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Calendar;
public class AddExpenseActivity extends AppCompatActivity {

    private static final String[] CURRENCIES   = {"GBP (£)","USD ($)","EUR (€)","JPY (¥)","VND (₫)","Other"};
    private static final String[] TYPES        = {"Travel","Equipment","Materials","Services","Software/Licenses","Labour costs","Utilities","Miscellaneous"};
    private static final String[] PAY_METHODS  = {"Cash","Credit Card","Bank Transfer","Cheque"};
    private static final String[] PAY_STATUSES = {"Paid","Pending","Reimbursed"};

    private TextInputEditText etCode, etDate, etAmount, etClaimant, etDesc, etLocation;
    private AutoCompleteTextView spCurrency, spType, spPayMethod, spPayStatus;
    private MaterialButton btnSave;
    private DatabaseHelper db;
    private Expense editing = null;
    private int projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        db = new DatabaseHelper(this);
        projectId = getIntent().getIntExtra("PROJECT_ID", -1);
        bindViews();
        setupToolbar();
        setupSpinners();

        etDate.setFocusable(false);
        etDate.setOnClickListener(v -> pickDate());

        int expenseId = getIntent().getIntExtra("EXPENSE_ID", -1);
        if (expenseId != -1) {
            editing = db.getExpenseById(expenseId);
            if (editing != null) fill(editing);
            setTitle("Edit Expense");
        } else {
            setTitle("Add Expense");
        }

        btnSave.setOnClickListener(v -> save());
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

    private void bindViews() {
        etCode      = findViewById(R.id.etExpenseCode);
        etDate      = findViewById(R.id.etExpenseDate);
        etAmount    = findViewById(R.id.etAmount);
        spCurrency  = findViewById(R.id.spCurrency);
        spType      = findViewById(R.id.spType);
        spPayMethod = findViewById(R.id.spPaymentMethod);
        etClaimant  = findViewById(R.id.etClaimant);
        spPayStatus = findViewById(R.id.spPaymentStatus);
        etDesc      = findViewById(R.id.etExpenseDesc);
        etLocation  = findViewById(R.id.etLocation);
        btnSave     = findViewById(R.id.btnSaveExpense);
    }

    private void setupSpinners() {
        spCurrency.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, CURRENCIES));
        spType.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, TYPES));
        spPayMethod.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, PAY_METHODS));
        spPayStatus.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, PAY_STATUSES));
    }

    private void pickDate() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this,
                (v, y, m, d) -> etDate.setText(
                        String.format("%04d-%02d-%02d", y, m + 1, d)),
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void fill(Expense e) {
        etCode.setText(e.getExpenseCode());
        etDate.setText(e.getDate());
        etAmount.setText(String.valueOf(e.getAmount()));
        spCurrency.setText(e.getCurrency(), false);
        spType.setText(e.getType(), false);
        spPayMethod.setText(e.getPaymentMethod(), false);
        etClaimant.setText(e.getClaimant());
        spPayStatus.setText(e.getPaymentStatus(), false);
        etDesc.setText(e.getDescription());
        etLocation.setText(e.getLocation());
    }

    private void save() {
        if (!req(etCode,     "Expense Code is required")) return;
        if (!req(etDate,     "Date is required"))         return;
        if (!req(etAmount,   "Amount is required"))       return;
        if (!req(etClaimant, "Claimant is required"))     return;
        if (TextUtils.isEmpty(spCurrency.getText()))  { spCurrency.setError("Select currency");       return; }
        if (TextUtils.isEmpty(spType.getText()))       { spType.setError("Select type");              return; }
        if (TextUtils.isEmpty(spPayMethod.getText()))  { spPayMethod.setError("Select method");       return; }
        if (TextUtils.isEmpty(spPayStatus.getText()))  { spPayStatus.setError("Select status");       return; }

        double amount;
        try { amount = Double.parseDouble(val(etAmount)); }
        catch (NumberFormatException e) {
            layout(etAmount).setError("Enter a valid amount"); return;
        }

        Expense exp = (editing != null) ? editing : new Expense();
        exp.setProjectId(projectId);
        exp.setExpenseCode(val(etCode));
        exp.setDate(val(etDate));
        exp.setAmount(amount);
        exp.setCurrency(spCurrency.getText().toString());
        exp.setType(spType.getText().toString());
        exp.setPaymentMethod(spPayMethod.getText().toString());
        exp.setClaimant(val(etClaimant));
        exp.setPaymentStatus(spPayStatus.getText().toString());
        exp.setDescription(val(etDesc));
        exp.setLocation(val(etLocation));

        if (editing != null) db.updateExpense(exp);
        else db.insertExpense(exp);

        Snackbar.make(btnSave, "Expense saved!", Snackbar.LENGTH_SHORT).show();
        finish();
    }

    private boolean req(TextInputEditText f, String msg) {
        if (TextUtils.isEmpty(f.getText())) { layout(f).setError(msg); return false; }
        layout(f).setError(null); return true;
    }
    private String val(TextInputEditText f) {
        return f.getText() != null ? f.getText().toString().trim() : "";
    }
    private TextInputLayout layout(TextInputEditText f) {
        return (TextInputLayout) f.getParent().getParent();
    }
}
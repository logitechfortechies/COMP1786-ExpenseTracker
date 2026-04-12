package com.example.expensetracker.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.database.DatabaseHelper;
import com.example.expensetracker.models.Expense;
import com.example.expensetracker.models.Project;
import com.example.expensetracker.network.ApiClient;
import com.example.expensetracker.network.ApiService;
import com.example.expensetracker.utils.NetworkUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class UploadActivity extends AppCompatActivity {

    private MaterialButton btnUpload;
    private ProgressBar progressBar;
    private TextView tvStatus;   // FIX: was referenced as tvUploadStatus in old document
    private DatabaseHelper db;
    private ApiService api;
    private int totalToUpload = 0, uploadedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        setTitle("Upload to Cloud");

        db  = new DatabaseHelper(this);
        api = ApiClient.getService();

        btnUpload   = findViewById(R.id.btnUpload);
        progressBar = findViewById(R.id.progressBar);
        // FIX: correct ID matching the layout XML
        tvStatus    = findViewById(R.id.tvStatus);

        updatePendingCount();

        btnUpload.setOnClickListener(v -> {
            if (!NetworkUtils.isConnected(this)) {
                Snackbar.make(v, "No internet connection. Check your network.",
                        Snackbar.LENGTH_LONG).show();
                return;
            }
            startUpload();
        });
    }

    private void updatePendingCount() {
        int pending = db.getUnsyncedProjects().size()
                + db.getUnsyncedExpenses().size();
        tvStatus.setText(pending + " item(s) pending upload");
    }

    private void startUpload() {
        List<Project> projects = db.getUnsyncedProjects();
        List<Expense> expenses = db.getUnsyncedExpenses();
        totalToUpload = projects.size() + expenses.size();
        uploadedCount = 0;

        if (totalToUpload == 0) {
            tvStatus.setText("Everything is already synced!");
            return;
        }

        btnUpload.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(totalToUpload);
        tvStatus.setText("Uploading...");

        for (Project p : projects) uploadProject(p);
        for (Expense e : expenses) uploadExpense(e);
    }

    private void uploadProject(Project p) {
        api.uploadProject(p).enqueue(new Callback<Project>() {
            @Override
            public void onResponse(Call<Project> call, Response<Project> response) {
                if (response.isSuccessful()) db.markProjectSynced(p.getId());
                onItemDone();
            }
            @Override
            public void onFailure(Call<Project> call, Throwable t) {
                tvStatus.setText("Error: " + t.getMessage());
                onItemDone();
            }
        });
    }

    private void uploadExpense(Expense e) {
        api.uploadExpense(e).enqueue(new Callback<Expense>() {
            @Override
            public void onResponse(Call<Expense> call, Response<Expense> response) {
                if (response.isSuccessful()) db.markExpenseSynced(e.getId());
                onItemDone();
            }
            @Override
            public void onFailure(Call<Expense> call, Throwable t) { onItemDone(); }
        });
    }

    private void onItemDone() {
        uploadedCount++;
        progressBar.setProgress(uploadedCount);
        if (uploadedCount >= totalToUpload) {
            tvStatus.setText("✓ Upload complete! " + uploadedCount + " item(s) synced.");
            btnUpload.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            updatePendingCount();
        }
    }
}
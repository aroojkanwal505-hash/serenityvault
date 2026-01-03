package com.example.mortuarymanagementsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PerformAutopsyActivity extends BaseActivity {

    private EditText etCaseId, etFindings;
    private Button btnSubmitAutopsy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perform_autopsy);

        initializeViews();
        setupSubmitButton();
    }

    private void initializeViews() {
        etCaseId = findViewById(R.id.etCaseId);
        etFindings = findViewById(R.id.etFindings);
        btnSubmitAutopsy = findViewById(R.id.btnSubmitAutopsy);
    }

    private void setupSubmitButton() {
        btnSubmitAutopsy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caseId = etCaseId.getText().toString().trim();
                String findings = etFindings.getText().toString().trim();

                if (caseId.isEmpty()) {
                    etCaseId.setError("Case ID is required");
                    return;
                }
                if (findings.isEmpty()) {
                    etFindings.setError("Autopsy findings are required");
                    return;
                }

                submitAutopsyReport(caseId, findings);
            }
        });
    }

    private void submitAutopsyReport(String caseId, String findings) {
        DatabaseHelper dbHelper = new DatabaseHelper(PerformAutopsyActivity.this);

        if (dbHelper.updateCaseStatus(caseId, "Autopsy Completed", "Medical Examination Done")) {
            // In real app, yahan findings bhi database mein save karenge
            Toast.makeText(this, "Autopsy report submitted successfully!\nFindings: " + findings, Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to submit autopsy report", Toast.LENGTH_SHORT).show();
        }
    }
}
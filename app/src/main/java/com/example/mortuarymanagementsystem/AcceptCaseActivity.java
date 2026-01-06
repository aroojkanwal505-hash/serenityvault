package com.example.mortuarymanagementsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AcceptCaseActivity extends BaseActivity {
    private EditText etCaseId;
    private Button btnAcceptCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_case);

        initializeViews();
        setupAcceptButton();
    }

    private void initializeViews() {
        etCaseId = findViewById(R.id.etCaseId);
        btnAcceptCase = findViewById(R.id.btnAcceptCase);
    }

    private void setupAcceptButton() {
        btnAcceptCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caseId = etCaseId.getText().toString().trim();

                if (caseId.isEmpty()) {
                    etCaseId.setError("Case ID is required");
                    return;
                }

                acceptCase(caseId);
            }
        });
    }


    private void acceptCase(String caseId) {
        DatabaseHelper dbHelper = new DatabaseHelper(AcceptCaseActivity.this);

        if (dbHelper.updateCaseStatus(caseId, "Accepted", "Under Mortuary Review")) {
            Toast.makeText(this, "Case accepted successfully!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to accept case", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isCaseExists(DatabaseHelper dbHelper, String caseId) {
        // Simple check - in real app, proper validation karna hoga
        return true; // Temporary
    }
}
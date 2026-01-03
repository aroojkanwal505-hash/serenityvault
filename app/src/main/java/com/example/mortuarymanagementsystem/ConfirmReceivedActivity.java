package com.example.mortuarymanagementsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ConfirmReceivedActivity extends BaseActivity {
    private EditText etCaseId;
    private Button btnConfirmReceived;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_received);

        initializeViews();
        setupConfirmButton();
    }

    private void initializeViews() {
        etCaseId = findViewById(R.id.etCaseId);
        btnConfirmReceived = findViewById(R.id.btnConfirmReceived);
    }

    private void setupConfirmButton() {
        btnConfirmReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caseId = etCaseId.getText().toString().trim();

                if (caseId.isEmpty()) {
                    etCaseId.setError("Case ID is required");
                    return;
                }

                confirmBodyReceived(caseId);
            }
        });
    }

    private void confirmBodyReceived(String caseId) {
        DatabaseHelper dbHelper = new DatabaseHelper(ConfirmReceivedActivity.this);

        if (dbHelper.updateCaseStatus(caseId, "Received", "Stored in Mortuary")) {
            Toast.makeText(this, "Body received confirmed!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to confirm receipt", Toast.LENGTH_SHORT).show();
        }
    }
}
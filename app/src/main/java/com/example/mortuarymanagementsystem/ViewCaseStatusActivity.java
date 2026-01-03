package com.example.mortuarymanagementsystem;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class  ViewCaseStatusActivity extends BaseActivity {

    private EditText etSearchCaseId;
    private Button btnSearch;
    private TextView tvCaseStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_case_status);

        initializeViews();
        setupSearchButton();
    }

    private void initializeViews() {
        etSearchCaseId = findViewById(R.id.etSearchCaseId);
        btnSearch = findViewById(R.id.btnSearch);
        tvCaseStatus = findViewById(R.id.tvCaseStatus);
    }

    private void setupSearchButton() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caseId = etSearchCaseId.getText().toString().trim();

                if (caseId.isEmpty()) {
                    etSearchCaseId.setError("Case ID is required");
                    return;
                }

                searchCaseStatus(caseId);
            }
        });
    }

    private void searchCaseStatus(String caseId) {
        DatabaseHelper dbHelper = new DatabaseHelper(ViewCaseStatusActivity.this);
        Cursor cursor = dbHelper.getCaseById(caseId);

        if (cursor != null && cursor.moveToFirst()) {
            String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS));
            String stage = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CURRENT_STAGE));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION));

            String statusText = "Case ID: " + caseId + "\n"
                    + "Status: " + status + "\n"
                    + "Current Stage: " + stage + "\n"
                    + "Location Found: " + location + "\n"
                    + "\nCase is active and being processed.";

            tvCaseStatus.setText(statusText);
            cursor.close();
        } else {
            tvCaseStatus.setText("Case ID not found in system.");
        }
    }
}
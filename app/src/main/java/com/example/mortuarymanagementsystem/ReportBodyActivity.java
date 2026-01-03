package com.example.mortuarymanagementsystem;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ReportBodyActivity extends BaseActivity {

    private EditText etLocation, etTime, etDescription;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_body);

        initializeViews();
        setupSubmitButton();
    }

    private void initializeViews() {
        etLocation = findViewById(R.id.etLocation);
        etTime = findViewById(R.id.etTime);
        etDescription = findViewById(R.id.etDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void setupSubmitButton() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    submitBodyReport();
                }
            }
        });
    }

    private boolean validateInput() {
        boolean isValid = true;

        if (etLocation.getText().toString().trim().isEmpty()) {
            etLocation.setError("Location is required");
            etLocation.requestFocus();
            isValid = false;
        }

        if (etTime.getText().toString().trim().isEmpty()) {
            etTime.setError("Time is required\nFormat: YYYY-MM-DD HH:MM");
            etTime.requestFocus();
            isValid = false;
        }

        if (etDescription.getText().toString().trim().isEmpty()) {
            etDescription.setError("Description is required");
            etDescription.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    private void submitBodyReport() {
        String location = etLocation.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // Generate unique case ID with timestamp
        String caseId = "CASE_" + System.currentTimeMillis();

        DatabaseHelper dbHelper = new DatabaseHelper(ReportBodyActivity.this);
        if (dbHelper.addNewCase(caseId, location, time, description)) {
            // ✅ NOTIFICATION CREATE KARO MORTUARY STAFF KE LIYE
            boolean notifCreated = dbHelper.createNotification(
                    "🚨 New Case Reported",
                    "New unidentified body reported at " + location + "\nTime: " + time,
                    "Mortuary Staff",
                    "NewCase",
                    caseId
            );

            if (notifCreated) {
                Toast.makeText(this, "Notification sent to Mortuary Staff", Toast.LENGTH_SHORT).show();
            }

            // Show success dialog with case ID
            showSuccessDialog(caseId, location, time);
        } else {
            Toast.makeText(this, "Failed to save case. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSuccessDialog(String caseId, String location, String time) {
        String message = "✅ **CASE REGISTERED SUCCESSFULLY!**\n\n" +
                "**Case ID:** " + caseId + "\n" +
                "**Location:** " + location + "\n" +
                "**Time Reported:** " + time + "\n\n" +
                "📝 **Please note down the Case ID!**\n" +
                "You will need it for:\n" +
                "• Checking case status\n" +
                "• Mortuary staff will use this ID\n" +
                "• Future reference\n\n" +
                "🔔 **Notification sent to Serenity Vault**";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("📋 Case Registered Successfully")
                .setMessage(message)
                .setPositiveButton("Copy Case ID", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Copy case ID to clipboard
                        android.content.ClipboardManager clipboard =
                                (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        android.content.ClipData clip = android.content.ClipData
                                .newPlainText("Case ID", caseId);
                        clipboard.setPrimaryClip(clip);

                        Toast.makeText(ReportBodyActivity.this,
                                "Case ID copied to clipboard!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNeutralButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    @Override
    public void onBackPressed() {
        // Ask for confirmation before exiting
        if (!etLocation.getText().toString().isEmpty() ||
                !etTime.getText().toString().isEmpty() ||
                !etDescription.getText().toString().isEmpty()) {

            new AlertDialog.Builder(this)
                    .setTitle("Discard Report?")
                    .setMessage("You have unsaved changes. Are you sure you want to exit?")
                    .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("Continue Editing", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }
}
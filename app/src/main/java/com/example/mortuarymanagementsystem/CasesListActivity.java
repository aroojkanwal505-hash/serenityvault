package com.example.mortuarymanagementsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CasesListActivity extends BaseActivity {
    private ListView listViewCases;
    private DatabaseHelper dbHelper;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cases_list);

        // Get user role from intent
        userRole = getIntent().getStringExtra("USER_ROLE");
        if (userRole == null) {
            userRole = "Police Officer"; // Default
        }

        dbHelper = new DatabaseHelper(this);

        initializeViews();
        loadCasesList();
        setupListViewClickListener();

        // Show role-specific message
        showWelcomeMessage();
    }

    private void initializeViews() {
        listViewCases = findViewById(R.id.listViewCases);
    }

    private void showWelcomeMessage() {
        String message = "";
        if (userRole.equals("Police Officer")) {
            message = "📋 View all reported cases\n👆 Tap on any case to view details";
        } else if (userRole.equals("Mortuary Staff")) {
            message = "📋 All pending cases\n👆 Tap on 'Reported' cases to Accept/Reject";
        }

        if (!message.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Welcome, " + userRole)
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    private void loadCasesList() {
        Cursor cursor = dbHelper.getAllCasesWithStatus();

        // Check if there are no cases
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No cases found!", Toast.LENGTH_SHORT).show();

            // Create empty adapter with message
            String[] fromColumns = {};
            int[] toViews = {};

            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    this,
                    R.layout.list_item_case,
                    cursor,
                    fromColumns,
                    toViews,
                    0
            );

            listViewCases.setAdapter(adapter);
            return;
        }

        String[] fromColumns = {
                DatabaseHelper.COLUMN_CASE_ID,
                DatabaseHelper.COLUMN_STATUS,
                DatabaseHelper.COLUMN_LOCATION,
                DatabaseHelper.COLUMN_CURRENT_STAGE
        };

        int[] toViews = {
                R.id.tvCaseId,
                R.id.tvStatus,
                R.id.tvLocation,
                R.id.tvStage  // Make sure this TextView exists in list_item_case.xml
        };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.list_item_case,
                cursor,
                fromColumns,
                toViews,
                0
        );

        listViewCases.setAdapter(adapter);
    }

    private void setupListViewClickListener() {
        listViewCases.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String caseId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CASE_ID));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION));
                String stage = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CURRENT_STAGE));

                if (userRole.equals("Police Officer")) {
                    // Police officer - show detailed view
                    showCaseDetailsForPolice(caseId, status, location, stage);
                } else if (userRole.equals("Mortuary Staff")) {
                    // Mortuary staff - take action based on status
                    handleMortuaryAction(caseId, status);
                } else if (userRole.equals("Doctor")) {
                    // Doctor - view case details
                    showCaseDetailsForDoctor(caseId, status, location, stage);
                } else if (userRole.equals("Admin")) {
                    // Admin - full access
                    showCaseDetailsForAdmin(caseId, status, location, stage);
                }
            }
        });

        // Add long click for additional options
        listViewCases.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String caseId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CASE_ID));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS));

                showContextMenu(caseId, status);
                return true;
            }
        });
    }

    private void showCaseDetailsForPolice(String caseId, String status, String location, String stage) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getCaseById(caseId);

        if (cursor != null && cursor.moveToFirst()) {
            String time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIME_FOUND));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));

            String message = "🔍 **Case Details**\n\n" +
                    "**Case ID:** " + caseId + "\n" +
                    "**Status:** " + status + "\n" +
                    "**Current Stage:** " + stage + "\n\n" +
                    "📍 **Location:** " + location + "\n" +
                    "🕒 **Time Reported:** " + time + "\n\n" +
                    "📝 **Description:**\n" + description;

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Case Information")
                    .setMessage(message)
                    .setPositiveButton("Check Status", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Navigate to check status activity
                            Intent intent = new Intent(CasesListActivity.this, ViewCaseStatusActivity.class);
                            intent.putExtra("CASE_ID", caseId);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Close", null)
                    .show();

            cursor.close();
        }
    }

    private void handleMortuaryAction(String caseId, String status) {
        if (status.equals("Reported")) {
            // Open Accept/Reject activity
            Intent intent = new Intent(CasesListActivity.this, MortuaryCaseActionActivity.class);
            intent.putExtra("CASE_ID", caseId);
            startActivity(intent);
        } else if (status.equals("Accepted")) {
            // Already accepted - show confirmation received option
            showAcceptedCaseOptions(caseId);
        } else {
            Toast.makeText(this, "Case is already " + status, Toast.LENGTH_SHORT).show();
        }
    }

    private void showAcceptedCaseOptions(String caseId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Case Already Accepted")
                .setMessage("What would you like to do?")
                .setPositiveButton("Confirm Received", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(CasesListActivity.this, ConfirmReceivedActivity.class);
                        intent.putExtra("CASE_ID", caseId);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("View Details", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showCaseDetails(caseId);
                    }
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void showCaseDetailsForDoctor(String caseId, String status, String location, String stage) {
        String message = "Case ID: " + caseId + "\n" +
                "Status: " + status + "\n" +
                "Stage: " + stage + "\n" +
                "Location: " + location;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Medical Case View")
                .setMessage(message)
                .setPositiveButton("Perform Autopsy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (status.equals("Received") || status.equals("Stored")) {
                            Intent intent = new Intent(CasesListActivity.this, PerformAutopsyActivity.class);
                            intent.putExtra("CASE_ID", caseId);
                            startActivity(intent);
                        } else {
                            Toast.makeText(CasesListActivity.this,
                                    "Case must be received in mortuary first", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Close", null)
                .show();
    }

    private void showCaseDetailsForAdmin(String caseId, String status, String location, String stage) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getCaseById(caseId);

        if (cursor != null && cursor.moveToFirst()) {
            String time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIME_FOUND));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));

            String message = "👑 **Admin View - Case Details**\n\n" +
                    "**Case ID:** " + caseId + "\n" +
                    "**Status:** " + status + "\n" +
                    "**Stage:** " + stage + "\n\n" +
                    "📍 **Location:** " + location + "\n" +
                    "🕒 **Time:** " + time + "\n\n" +
                    "📝 **Description:**\n" + description;

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Full Case Access")
                    .setMessage(message)
                    .setPositiveButton("Update Status", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateCaseStatus(caseId, status);
                        }
                    })
                    .setNegativeButton("Close", null)
                    .show();

            cursor.close();
        }
    }

    private void showCaseDetails(String caseId) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getCaseById(caseId);

        if (cursor != null && cursor.moveToFirst()) {
            String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIME_FOUND));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
            String stage = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CURRENT_STAGE));

            String message = "Case ID: " + caseId + "\n" +
                    "Status: " + status + "\n" +
                    "Stage: " + stage + "\n" +
                    "Location: " + location + "\n" +
                    "Time: " + time + "\n" +
                    "Description: " + description;

            new AlertDialog.Builder(this)
                    .setTitle("Case Details")
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show();

            cursor.close();
        }
    }

    private void showContextMenu(String caseId, String status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Case: " + caseId)
                .setItems(new String[]{"Copy Case ID", "Refresh Status", "View Full History"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0: // Copy Case ID
                                        copyToClipboard(caseId);
                                        break;
                                    case 1: // Refresh Status
                                        loadCasesList();
                                        Toast.makeText(CasesListActivity.this,
                                                "List refreshed!", Toast.LENGTH_SHORT).show();
                                        break;
                                    case 2: // View Full History
                                        showCaseDetails(caseId);
                                        break;
                                }
                            }
                        })
                .show();
    }

    private void updateCaseStatus(String caseId, String currentStatus) {
        // For admin to manually update status
        // You can implement a dialog with status options
        Toast.makeText(this, "Update status feature coming soon", Toast.LENGTH_SHORT).show();
    }

    private void copyToClipboard(String text) {
        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData
                .newPlainText("Case ID", text);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Case ID copied!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh list when returning from other activities
        loadCasesList();
    }
}
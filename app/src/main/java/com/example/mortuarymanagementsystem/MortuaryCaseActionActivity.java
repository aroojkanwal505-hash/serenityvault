package com.example.mortuarymanagementsystem;

// ✅ ADD THESE IMPORT STATEMENTS
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;  // ✅ ADD THIS
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;

import androidx.appcompat.app.AlertDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MortuaryCaseActionActivity extends BaseActivity {

    private TextView tvCaseDetails, tvReservationInfo;
    private Spinner spinnerSlots;
    private Button btnAccept, btnReject;
    private String caseId;
    private DatabaseHelper dbHelper;
    private List<String> availableSlots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mortuary_case_action);

        caseId = getIntent().getStringExtra("CASE_ID");
        if (caseId == null || caseId.isEmpty()) {
            Toast.makeText(this, "Case ID not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);

        initializeViews();
        loadCaseDetails();
        loadAvailableSlots();
        setupButtons();
    }

    private void initializeViews() {
        tvCaseDetails = findViewById(R.id.tvCaseDetails);
        spinnerSlots = findViewById(R.id.spinnerSlots);
        btnAccept = findViewById(R.id.btnAccept);
        btnReject = findViewById(R.id.btnReject);
        tvReservationInfo = findViewById(R.id.tvReservationInfo);
    }

    private void loadCaseDetails() {
        Cursor cursor = dbHelper.getCaseById(caseId);

        if (cursor != null && cursor.moveToFirst()) {
            String location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIME_FOUND));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));

            String details = "📋 **New Case Received**\n\n" +
                    "**Case ID:** " + caseId + "\n\n" +
                    "📍 **Location:** " + location + "\n" +
                    "🕒 **Time Reported:** " + time + "\n\n" +
                    "📝 **Description:**\n" + description;

            tvCaseDetails.setText(details);
            cursor.close();
        }
    }

    private void loadAvailableSlots() {
        Cursor cursor = dbHelper.getAvailableSlots();
        availableSlots.clear();
        availableSlots.add("Select a slot");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String slotNumber = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SLOT_NUMBER));
                availableSlots.add(slotNumber);
            } while (cursor.moveToNext());
            cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, availableSlots);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSlots.setAdapter(adapter);

        if (availableSlots.size() <= 1) {
            tvReservationInfo.setText("❌ No available slots!");
            btnAccept.setEnabled(false);
        }
    }

    private void setupButtons() {
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedSlot = spinnerSlots.getSelectedItem().toString();

                if (selectedSlot.equals("Select a slot")) {
                    Toast.makeText(MortuaryCaseActionActivity.this,
                            "Please select a slot", Toast.LENGTH_SHORT).show();
                    return;
                }

                acceptCaseWithSlot(selectedSlot);
            }
        });

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectCase();
            }
        });
    }

    private void acceptCaseWithSlot(String slotNumber) {
        // Calculate reservation until (2 days from now)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String reservedUntil = sdf.format(calendar.getTime());

        // Reserve slot
        if (dbHelper.reserveSlot(slotNumber, caseId, reservedUntil)) {
            // Update case status
            dbHelper.updateCaseStatus(caseId, "Accepted", "Slot Reserved - Awaiting Delivery");

            // Create notification for Police
            dbHelper.createNotification(
                    "Case Accepted by Serenity Vault",
                    "Case " + caseId + " accepted. Slot " + slotNumber + " reserved until " + reservedUntil,
                    "Police Officer",
                    "CaseAccepted",
                    caseId
            );

            // Clear the "NewCase" notification
            clearNewCaseNotification();

            Toast.makeText(this, "✅ Case Accepted!\nSlot " + slotNumber + " reserved", Toast.LENGTH_LONG).show();

            // Show success message
            String message = "✅ **Case Accepted Successfully!**\n\n" +
                    "**Slot:** " + slotNumber + "\n" +
                    "**Reserved Until:** " + reservedUntil + "\n\n" +
                    "📞 Police officer has been notified to deliver body within 2 days";

            new AlertDialog.Builder(this)
                    .setTitle("Case Accepted")
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .show();
        } else {
            Toast.makeText(this, "Failed to reserve slot", Toast.LENGTH_SHORT).show();
        }
    }

    private void rejectCase() {
        new AlertDialog.Builder(this)
                .setTitle("Reject Case")
                .setMessage("Are you sure you want to reject this case?")
                .setPositiveButton("Reject", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.updateCaseStatus(caseId, "Rejected", "Returned to Police");

                        // Create notification for Police
                        dbHelper.createNotification(
                                "Case Rejected by Serenity Vault",
                                "Case " + caseId + " has been rejected",
                                "Police Officer",
                                "CaseRejected",
                                caseId
                        );

                        // Clear the "NewCase" notification
                        clearNewCaseNotification();

                        Toast.makeText(MortuaryCaseActionActivity.this,
                                "Case rejected", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MortuaryCaseActionActivity.this, CasesListActivity.class);
                        intent.putExtra("USER_ROLE", "Mortuary Staff");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void clearNewCaseNotification() {
        // Find and clear the NewCase notification for this case
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NOTIF_STATUS, "Cleared");

        String whereClause = DatabaseHelper.COLUMN_CASE_ID + " = ? AND " +
                DatabaseHelper.COLUMN_NOTIF_TYPE + " = 'NewCase'";
        String[] whereArgs = {caseId};

        db.update(DatabaseHelper.TABLE_NOTIFICATIONS, values, whereClause, whereArgs);
    }
}
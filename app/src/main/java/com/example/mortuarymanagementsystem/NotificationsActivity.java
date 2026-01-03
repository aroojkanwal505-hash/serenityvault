package com.example.mortuarymanagementsystem;

// ✅ ADD THESE IMPORT STATEMENTS
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;  // ✅ ADD THIS
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;  // ✅ ADD THIS
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class NotificationsActivity extends BaseActivity {

    private ListView listViewNotifications;
    private TextView tvNoNotifications;
    private Button btnClearAll;
    private DatabaseHelper dbHelper;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        userRole = getIntent().getStringExtra("USER_ROLE");
        if (userRole == null) {
            userRole = "Mortuary Staff"; // Default
        }

        dbHelper = new DatabaseHelper(this);

        initializeViews();
        loadNotifications();
    }

    private void initializeViews() {
        listViewNotifications = findViewById(R.id.listViewNotifications);
        tvNoNotifications = findViewById(R.id.tvNoNotifications);
        btnClearAll = findViewById(R.id.btnClearAll);

        btnClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllNotifications();
            }
        });
    }

    private void loadNotifications() {
        Cursor cursor = dbHelper.getNotificationsForRole(userRole);

        if (cursor.getCount() == 0) {
            tvNoNotifications.setVisibility(View.VISIBLE);
            listViewNotifications.setVisibility(View.GONE);
            btnClearAll.setVisibility(View.GONE);
            return;
        }

        tvNoNotifications.setVisibility(View.GONE);
        listViewNotifications.setVisibility(View.VISIBLE);
        btnClearAll.setVisibility(View.VISIBLE);

        String[] fromColumns = {
                DatabaseHelper.COLUMN_NOTIF_TITLE,
                DatabaseHelper.COLUMN_NOTIF_MESSAGE,
                DatabaseHelper.COLUMN_CREATED_AT
        };

        int[] toViews = {
                R.id.tvNotifTitle,
                R.id.tvNotifMessage,
                R.id.tvNotifTime
        };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.list_item_notification,
                cursor,
                fromColumns,
                toViews,
                0
        );

        listViewNotifications.setAdapter(adapter);

        listViewNotifications.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String notifId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIF_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIF_TITLE));
                String message = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIF_MESSAGE));
                String caseId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CASE_ID));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIF_TYPE));

                // Mark as read
                dbHelper.markNotificationAsRead(notifId);

                // Handle notification click based on type
                handleNotificationClick(type, caseId, title, message);
            }
        });

        listViewNotifications.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String notifId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIF_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIF_TITLE));

                showNotificationOptions(notifId, title);
                return true;
            }
        });
    }

    private void handleNotificationClick(String type, String caseId, String title, String message) {
        switch (type) {
            case "NewCase":
                // For Mortuary Staff - Open case action
                if (userRole.equals("Mortuary Staff")) {
                    Intent intent = new Intent(this, MortuaryCaseActionActivity.class);
                    intent.putExtra("CASE_ID", caseId);
                    startActivity(intent);
                }
                break;

            case "CaseAccepted":
                // For Police - Show case accepted info
                showCaseAcceptedInfo(caseId);
                break;

            case "BodyReceived":
                // For Doctor - Open autopsy activity
                if (userRole.equals("Doctor")) {
                    Intent intent = new Intent(this, PerformAutopsyActivity.class);
                    intent.putExtra("CASE_ID", caseId);
                    startActivity(intent);
                }
                break;

            case "DoctorReport":
                // For Police - Show doctor report
                showDoctorReport(caseId);
                break;
        }
    }

    private void showNotificationOptions(final String notifId, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notification: " + title)
                .setItems(new String[]{"Clear Notification", "Mark as Unread", "View Case Details"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        dbHelper.clearNotification(notifId);
                                        Toast.makeText(NotificationsActivity.this,
                                                "Notification cleared", Toast.LENGTH_SHORT).show();
                                        loadNotifications();
                                        break;
                                    case 1:
                                        // You can implement mark as unread if needed
                                        break;
                                    case 2:
                                        // View case details
                                        break;
                                }
                            }
                        })
                .show();
    }

    private void clearAllNotifications() {
        new AlertDialog.Builder(this)
                .setTitle("Clear All Notifications?")
                .setMessage("Are you sure you want to clear all notifications?")
                .setPositiveButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Clear all notifications for this role
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(DatabaseHelper.COLUMN_NOTIF_STATUS, "Cleared");

                        String whereClause = DatabaseHelper.COLUMN_NOTIF_FOR_ROLE + " = ?";
                        String[] whereArgs = {userRole};

                        db.update(DatabaseHelper.TABLE_NOTIFICATIONS, values, whereClause, whereArgs);

                        Toast.makeText(NotificationsActivity.this,
                                "All notifications cleared", Toast.LENGTH_SHORT).show();
                        loadNotifications();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showCaseAcceptedInfo(String caseId) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getCaseById(caseId);

        if (cursor != null && cursor.moveToFirst()) {
            String location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIME_FOUND));

            Cursor slotCursor = dbHelper.getSlotByCaseId(caseId);
            String slotInfo = "Slot not assigned";
            if (slotCursor != null && slotCursor.moveToFirst()) {
                String slotNumber = slotCursor.getString(slotCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SLOT_NUMBER));
                String reservedUntil = slotCursor.getString(slotCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RESERVED_UNTIL));
                slotInfo = "Slot: " + slotNumber + "\nReserved until: " + reservedUntil;
                slotCursor.close();
            }

            String message = "✅ **Case Accepted by Serenity Vault**\n\n" +
                    "**Case ID:** " + caseId + "\n" +
                    "**Location:** " + location + "\n" +
                    "**Reported Time:** " + time + "\n\n" +
                    "**Slot Information:**\n" + slotInfo + "\n\n" +
                    "📦 **Please deliver the body within 2 days**\n" +
                    "⚠️ **Slot will be freed if body not received in 2 days**";

            new AlertDialog.Builder(this)
                    .setTitle("Case Accepted")
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show();

            cursor.close();
        }
    }

    private void showDoctorReport(String caseId) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getDoctorReport(caseId);

        if (cursor != null && cursor.moveToFirst()) {
            String causeOfDeath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAUSE_OF_DEATH));
            String timeOfDeath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIME_OF_DEATH));
            String marks = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MARKS_INJURIES));
            String notes = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DOCTOR_NOTES));

            String message = "🩺 **Medical Examination Report**\n\n" +
                    "**Case ID:** " + caseId + "\n\n" +
                    "**Time of Death:** " + timeOfDeath + "\n" +
                    "**Cause of Death:** " + causeOfDeath + "\n\n" +
                    "**Marks/Injuries:**\n" + marks + "\n\n" +
                    "**Doctor's Notes:**\n" + notes;

            new AlertDialog.Builder(this)
                    .setTitle("Autopsy Report")
                    .setMessage(message)
                    .setPositiveButton("Update Identity", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Open identity update activity
                            Intent intent = new Intent(NotificationsActivity.this, UpdateIdentityActivity.class);
                            intent.putExtra("CASE_ID", caseId);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Close", null)
                    .show();

            cursor.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }
}
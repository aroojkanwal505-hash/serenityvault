package com.example.mortuarymanagementsystem;

// ✅ ADD THESE IMPORTS
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class PoliceDashboardActivity extends BaseActivity {
    private Button btnReportBody, btnUpdateIdentity, btnMarkTransit, btnAuthorizeRelease, btnCheckStatus, btnViewCases;
    private DatabaseHelper dbHelper;  // ✅ ADD THIS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_dashboard);

        dbHelper = new DatabaseHelper(this);  // ✅ INITIALIZE

        initializeViews();
        setupClickListeners();
        checkNotifications();  // ✅ CALL NOTIFICATION CHECK
    }

    private void initializeViews() {
        btnReportBody = findViewById(R.id.btnReportBody);
        btnUpdateIdentity = findViewById(R.id.btnUpdateIdentity);
        btnMarkTransit = findViewById(R.id.btnMarkTransit);
        btnAuthorizeRelease = findViewById(R.id.btnAuthorizeRelease);
        btnCheckStatus = findViewById(R.id.btnCheckStatus);
        btnViewCases = findViewById(R.id.btnViewCases);
    }

    private void setupClickListeners() {
        btnReportBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PoliceDashboardActivity.this, ReportBodyActivity.class);
                startActivity(intent);
            }
        });

        // Check Status button
        btnCheckStatus.setOnClickListener(v -> {
            Intent intent = new Intent(PoliceDashboardActivity.this, ViewCaseStatusActivity.class);
            startActivity(intent);
        });

        btnViewCases.setOnClickListener(v -> {
            Intent intent = new Intent(PoliceDashboardActivity.this, CasesListActivity.class);
            intent.putExtra("USER_ROLE", "Police Officer");
            startActivity(intent);
        });

        // Baaki buttons ke liye temporarily Toast messages
        btnUpdateIdentity.setOnClickListener(v ->
                showToast("Update Identity Feature - Coming Soon"));

        btnMarkTransit.setOnClickListener(v ->
                showToast("Mark Body in Transit - Coming Soon"));

        btnAuthorizeRelease.setOnClickListener(v ->
                showToast("Authorize Release - Coming Soon"));
    }

    // ✅ FIXED NOTIFICATION CHECK METHOD
    private void checkNotifications() {
        int unreadCount = dbHelper.getUnreadNotificationCount("Police Officer");

        ImageView bellIcon = findViewById(R.id.ivNotificationBell);
        if (bellIcon != null) {
            if (unreadCount > 0) {
                // Show red dot
                bellIcon.setImageResource(R.drawable.ic_notification_new);
            } else {
                bellIcon.setImageResource(R.drawable.ic_notification);
            }

            bellIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PoliceDashboardActivity.this, NotificationsActivity.class);
                    intent.putExtra("USER_ROLE", "Police Officer");
                    startActivity(intent);
                }
            });
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh notifications when returning to dashboard
        checkNotifications();
    }
}
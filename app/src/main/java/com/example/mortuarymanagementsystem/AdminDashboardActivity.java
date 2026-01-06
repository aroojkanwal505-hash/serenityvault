package com.example.mortuarymanagementsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends BaseActivity {
    private Button btnManageUsers, btnSystemReports, btnAuditLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnSystemReports = findViewById(R.id.btnSystemReports);
        btnAuditLog = findViewById(R.id.btnAuditLog);
    }

    private void setupClickListeners() {
        btnManageUsers.setOnClickListener(v ->
                showToast("Manage Users - Coming Soon"));

        btnSystemReports.setOnClickListener(v ->
                showToast("System Reports - Coming Soon"));

        btnAuditLog.setOnClickListener(v ->
                showToast("Audit Log - Coming Soon"));
    }

    private void showToast(String message) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
    }
}
package com.example.mortuarymanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MortuaryStaffDashboardActivity extends BaseActivity {

    private Button btnViewCases, btnConfirmReceived, btnUpdateStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mortuary_staff_dashboard);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        btnViewCases = findViewById(R.id.btnViewCases);
        btnConfirmReceived = findViewById(R.id.btnConfirmReceived);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);
    }

    private void setupClickListeners() {
        btnViewCases.setOnClickListener(v -> {
            Intent intent = new Intent(MortuaryStaffDashboardActivity.this, CasesListActivity.class);
            intent.putExtra("USER_ROLE", "Mortuary Staff");
            startActivity(intent);
        });

        // Add this in setupClickListeners():
        btnConfirmReceived.setOnClickListener(v -> {
            Intent intent = new Intent(MortuaryStaffDashboardActivity.this, ConfirmReceivedActivity.class);
            startActivity(intent);
        });

        btnUpdateStatus.setOnClickListener(v ->
                showToast("Update Body Status - Coming Soon"));
    }

    private void showToast(String message) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
    }
}
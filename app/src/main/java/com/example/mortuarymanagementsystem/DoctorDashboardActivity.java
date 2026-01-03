package com.example.mortuarymanagementsystem;

import android.content.Intent;  // Ye line ADD KARO
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class DoctorDashboardActivity extends BaseActivity {

    private Button btnPerformAutopsy, btnViewCases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dashboard);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        btnPerformAutopsy = findViewById(R.id.btnPerformAutopsy);
        btnViewCases = findViewById(R.id.btnViewCases);
    }

    private void setupClickListeners() {
        btnPerformAutopsy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorDashboardActivity.this, PerformAutopsyActivity.class);
                startActivity(intent);
            }
        });

        btnViewCases.setOnClickListener(v ->
                showToast("View Cases - Coming Soon"));
    }

    private void showToast(String message) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
    }
}
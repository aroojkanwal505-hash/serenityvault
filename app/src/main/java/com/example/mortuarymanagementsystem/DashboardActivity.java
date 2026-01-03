package com.example.mortuarymanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends BaseActivity {
    private TextView tvWelcome, tvRoleSpecificMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Get user role from login with null safety
        String userRole = getIntent().getStringExtra("USER_ROLE");
        if (userRole == null) {
            userRole = "User"; // Default value
        }

        tvWelcome = findViewById(R.id.tvWelcome);
        tvRoleSpecificMessage = findViewById(R.id.tvRoleSpecificMessage);

        // Use resource string with placeholder
        String welcomeText = getString(R.string.welcome_user, userRole);
        tvWelcome.setText(welcomeText);

        // Set role-specific message
        setRoleSpecificMessage(userRole);

        // Redirect to role-specific dashboard
        redirectToRoleDashboard(userRole);
    }

    private void setRoleSpecificMessage(String userRole) {
        String message;

        switch (userRole) {
            case "Police Officer":
                message = getString(R.string.police_message);
                break;
            case "Mortuary Staff":
                message = getString(R.string.mortuary_message);
                break;
            case "Doctor":
                message = getString(R.string.doctor_message);
                break;
            case "Admin":
                message = getString(R.string.admin_message);
                break;
            default:
                message = getString(R.string.default_message);
        }

        tvRoleSpecificMessage.setText(message);
    }

    private void redirectToRoleDashboard(String userRole) {
        Class<?> targetActivity;

        switch (userRole) {
            case "Police Officer":
                targetActivity = PoliceDashboardActivity.class;
                break;
            case "Mortuary Staff":
                targetActivity = MortuaryStaffDashboardActivity.class;
                break;
            case "Doctor":
                targetActivity = DoctorDashboardActivity.class;
                break;
            case "Admin":
                targetActivity = AdminDashboardActivity.class;
                break;
            default:
                // Agar koi role match nahi karta, main dashboard pe hi raho
                return;
        }

        Intent intent = new Intent(DashboardActivity.this, targetActivity);
        intent.putExtra("USER_ROLE", userRole);
        startActivity(intent);
        finish();
    }
}
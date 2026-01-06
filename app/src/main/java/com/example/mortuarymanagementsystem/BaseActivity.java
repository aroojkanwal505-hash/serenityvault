package com.example.mortuarymanagementsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme BEFORE super.onCreate
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        // ✅ CRITICAL: Toolbar setup for ActionBar
        setupActionBar();
    }

    private void setupActionBar() {
        // Get the Toolbar if exists in layout
        Toolbar toolbar = findViewById(R.id.toolbar);

        // If Toolbar not found in layout, create one
        if (toolbar == null) {
            toolbar = new Toolbar(this);
            toolbar.setId(R.id.toolbar); // Assign an ID

            // Set title based on activity
            String title = getActivityTitle();
            toolbar.setTitle(title);

            // Set background color based on theme
            if (ThemeManager.getSavedTheme(this) == ThemeManager.THEME_DARK) {
                toolbar.setBackgroundColor(0xFF000000); // Black for dark theme
            } else if (ThemeManager.getSavedTheme(this) == ThemeManager.THEME_CUSTOM_BLUE) {
                toolbar.setBackgroundColor(0xFF2196F3); // Blue for blue theme
            } else {
                toolbar.setBackgroundColor(getResources().getColor(R.color.primary)); // Default
            }

            toolbar.setTitleTextColor(0xFFFFFFFF); // White title

            // Add toolbar to top of layout
            ViewGroup rootView = findViewById(android.R.id.content);
            if (rootView instanceof LinearLayout) {
                // Add toolbar at position 0 (top)
                ((LinearLayout) rootView).addView(toolbar, 0);

                // Set layout params
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                toolbar.setLayoutParams(params);
            }
        }

        // ✅ IMPORTANT: Set Toolbar as ActionBar
        setSupportActionBar(toolbar);

        // Enable back button (except for LoginActivity)
        if (getSupportActionBar() != null && !(this instanceof LoginActivity)) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private String getActivityTitle() {
        // Set appropriate title for each activity
        if (this instanceof DashboardActivity) return "Dashboard";
        if (this instanceof PoliceDashboardActivity) return "Police Dashboard";
        if (this instanceof MortuaryStaffDashboardActivity) return "Mortuary Staff Dashboard";
        if (this instanceof DoctorDashboardActivity) return "Doctor Dashboard";
        if (this instanceof AdminDashboardActivity) return "Admin Dashboard";
        if (this instanceof CasesListActivity) return "All Cases";
        if (this instanceof ReportBodyActivity) return "Report Body";
        if (this instanceof NotificationsActivity) return "Notifications";
        if (this instanceof MortuaryCaseActionActivity) return "Case Action";
        if (this instanceof ConfirmReceivedActivity) return "Confirm Received";
        return "Mortuary Management System";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_light) {
            ThemeManager.saveTheme(this, ThemeManager.THEME_LIGHT);
            recreate();
            return true;
        } else if (id == R.id.menu_dark) {
            ThemeManager.saveTheme(this, ThemeManager.THEME_DARK);
            recreate();
            return true;
        } else if (id == R.id.menu_blue) {
            ThemeManager.saveTheme(this, ThemeManager.THEME_CUSTOM_BLUE);
            recreate();
            return true;
        } else if (id == R.id.menu_webview) {
            Intent intent = new Intent(this, WebViewActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_logout) {
            showLogoutDialog();
            return true;
        } else if (id == android.R.id.home) {
            // Handle back button
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Clear user session/data if any
                        // Navigate to LoginActivity
                        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
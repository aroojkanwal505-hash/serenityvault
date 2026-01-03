package com.example.mortuarymanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends BaseActivity {
    private EditText etUsername, etPassword;
    private Spinner spinnerRole;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupRoleSpinner();
        setupLoginButton();
    }

    private void initializeViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void setupRoleSpinner() {
        String[] roles = {"Select Role", "Police Officer", "Mortuary Staff", "Doctor", "Admin"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
    }

    private void setupLoginButton() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String role = spinnerRole.getSelectedItem().toString();

                if (validateInput(username, password, role)) {
                    performLogin(username, password, role);
                }
            }
        });
    }

    private boolean validateInput(String username, String password, String role) {
        if (username.isEmpty()) {
            etUsername.setError("Username required");
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password required");
            return false;
        }
        if (role.equals("Select Role")) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void performLogin(String username, String password, String role) {
        DatabaseHelper dbHelper = new DatabaseHelper(LoginActivity.this);

        if (dbHelper.authenticateUser(username, password, role)) {
            // Login successful
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            intent.putExtra("USER_ROLE", role);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid credentials or role mismatch", Toast.LENGTH_SHORT).show();
        }
    }
}
package com.example.mortuarymanagementsystem;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateIdentityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_identity);

        // Retrieve the case ID passed from NotificationsActivity
        String caseId = getIntent().getStringExtra("CASE_ID");

        // TODO: Add your logic to update the identity here
    }
}
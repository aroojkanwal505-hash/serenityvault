package com.example.mortuarymanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;


public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before super.onCreate
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        }

        return super.onOptionsItemSelected(item);
    }
}
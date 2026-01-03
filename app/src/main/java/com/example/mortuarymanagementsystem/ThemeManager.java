package com.example.mortuarymanagementsystem;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class ThemeManager {

    private static final String PREF_NAME = "ThemePrefs";
    private static final String KEY_THEME = "selected_theme";

    // Theme constants
    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;
    public static final int THEME_CUSTOM_BLUE = 2;

    // Save selected theme
    public static void saveTheme(Context context, int theme) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(KEY_THEME, theme);
        editor.apply();
    }

    // Get saved theme
    public static int getSavedTheme(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getInt(KEY_THEME, THEME_LIGHT); // Default Light theme
    }

    // Apply theme to activity
    public static void applyTheme(Activity activity) {
        int savedTheme = getSavedTheme(activity);

        switch (savedTheme) {
            case THEME_DARK:
                activity.setTheme(R.style.Theme_Dark);
                break;
            case THEME_CUSTOM_BLUE:
                activity.setTheme(R.style.Theme_CustomBlue);
                break;
            case THEME_LIGHT:
            default:
                activity.setTheme(R.style.Theme_Light);
                break;
        }
    }

    // Get theme name
    public static String getThemeName(Context context) {
        int theme = getSavedTheme(context);

        switch (theme) {
            case THEME_DARK:
                return "Dark Theme";
            case THEME_CUSTOM_BLUE:
                return "Blue Theme";
            case THEME_LIGHT:
            default:
                return "Light Theme";
        }
    }
}
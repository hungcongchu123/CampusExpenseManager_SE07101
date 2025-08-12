package com.example.campusexpensemanager_se07101.budget;

import android.content.Context;
import android.content.SharedPreferences;

public class BudgetSettingsHelper {
    private static final String PREF_NAME = "BudgetSettings";
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String KEY_WARNING_THRESHOLD = "warning_threshold";
    
    private SharedPreferences sharedPreferences;
    
    public BudgetSettingsHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    // Bật/tắt thông báo
    public void setNotificationsEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply();
    }
    
    public boolean areNotificationsEnabled() {
        return sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true); // mặc định bật
    }
    
    // Điều chỉnh ngưỡng cảnh báo (0.5 = 50%, 0.8 = 80%, 1.0 = 100%)
    public void setWarningThreshold(double threshold) {
        sharedPreferences.edit().putFloat(KEY_WARNING_THRESHOLD, (float) threshold).apply();
    }
    
    public double getWarningThreshold() {
        return sharedPreferences.getFloat(KEY_WARNING_THRESHOLD, 0.8f); // mặc định 80%
    }
    
    // Reset về mặc định
    public void resetToDefault() {
        setNotificationsEnabled(true);
        setWarningThreshold(0.8);
    }
}







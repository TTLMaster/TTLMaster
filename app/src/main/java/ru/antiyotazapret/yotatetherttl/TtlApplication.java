package ru.antiyotazapret.yotatetherttl;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author Pavel Savinov (swapii@gmail.com)
 */
public class TtlApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        upgradePreferences();
    }

    private void upgradePreferences() {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        upgradeString(p, "lang", R.string.prefs_misc_language_key);
        upgradeString(p, "method", R.string.prefs_general_reconnectType_key);
        upgradeBoolean(p, "wifi", R.string.prefs_general_wifiHotspotOn_key);
        upgradeBoolean(p, "bootup", R.string.prefs_boot_autoStart_key);
        upgradeBoolean(p, "bootup_toast", R.string.prefs_boot_showToasts_key);
        upgradeBoolean(p, "debugm", R.string.prefs_misc_debugMode_key);
        upgradeStringToInteger(p, "bootup_ttl", R.string.prefs_boot_ttlValue_key, R.integer.prefs_boot_ttlValue_default);
        upgradeStringToInteger(p, "onlaunch_ttl", R.string.prefs_misc_ttlValue_key, R.integer.prefs_misc_ttlValue_default);
    }

    private void upgradeStringToInteger(SharedPreferences preferences, String oldKey, int newKeyId, int defaultValueId) {
        if (preferences.contains(oldKey)) {
            String newKey = getString(newKeyId);
            String existValueString = preferences.getString(oldKey, "");
            int newValue = getResources().getInteger(defaultValueId);
            try {
                newValue = Integer.parseInt(existValueString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            preferences.edit()
                    .putInt(newKey, newValue)
                    .remove(oldKey)
                    .commit();
        }
    }

    private void upgradeBoolean(SharedPreferences preferences, String oldKey, int newKeyId) {
        if (preferences.contains(oldKey)) {
            String key = getString(newKeyId);
            boolean value = preferences.getBoolean(oldKey, false);
            preferences.edit()
                    .putBoolean(key, value)
                    .remove(oldKey)
                    .commit();
        }
    }

    private void upgradeString(SharedPreferences preferences, String oldKey, int newKeyId) {
        if (preferences.contains(oldKey)) {
            String newKey = getString(newKeyId);
            String value = preferences.getString(oldKey, "");
            preferences.edit()
                    .putString(newKey, value)
                    .remove(oldKey)
                    .commit();
        }
    }

}

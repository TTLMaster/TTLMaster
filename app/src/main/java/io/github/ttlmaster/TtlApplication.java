package io.github.ttlmaster;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;

import net.orange_box.storebox.StoreBox;

import java.util.Locale;

/**
 * @author Pavel Savinov (swapii@gmail.com)
 */

public class TtlApplication extends Application {

    private Preferences preferences;
    public final static String TAG = "TTL";

    public static void Logi(String msg) {
        Log.i(TAG, msg);
    }

    public static void Loge(String msg) {
        Log.e(TAG, msg);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        upgradePreferences();
        preferences = StoreBox.create(this, Preferences.class);
        tuneLanguage();
    }

    private void tuneLanguage() {
        //Настройка языка
        String lang = preferences.getSelectedLanguage();
        if (lang.equals("default")) {
            //Автоматическое назначение языка
            lang = getResources().getConfiguration().locale.getCountry();
        }

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, null);
    }

    private void upgradePreferences() {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        upgradeString(p, "lang", R.string.prefs_misc_language_key);
        upgradeString(p, "method", R.string.prefs_general_reconnectType_key);
        upgradeBoolean(p, "wifi", R.string.prefs_general_wifiHotspotOn_key);
        upgradeBoolean(p, "bootup", R.string.prefs_boot_autoStart_key);
        upgradeBoolean(p, "bootup_toast", R.string.prefs_boot_showToasts_key);
        upgradeBoolean(p, "debugm", R.string.prefs_misc_debugMode_key);
        upgradeStringToInteger(p);
        deleteString(p);
    }

    private void upgradeStringToInteger(SharedPreferences preferences) {
        if (preferences.contains("bootup_ttl")) {
            String newKey = getString(R.string.prefs_general_ttlValue_key);
            String existValueString = preferences.getString("bootup_ttl", "");
            int newValue = getResources().getInteger(R.integer.prefs_general_ttlValue_default);
            try {
                newValue = Integer.parseInt(existValueString);
            } catch (NumberFormatException e) {
                TtlApplication.Loge(e.toString());
            }
            preferences.edit()
                    .putInt(newKey, newValue)
                    .remove("bootup_ttl")
                    .apply();
        }
    }

    private void upgradeBoolean(SharedPreferences preferences, String oldKey, int newKeyId) {
        if (preferences.contains(oldKey)) {
            String key = getString(newKeyId);
            boolean value = preferences.getBoolean(oldKey, false);
            preferences.edit()
                    .putBoolean(key, value)
                    .remove(oldKey)
                    .apply();
        }
    }

    private void upgradeString(SharedPreferences preferences, String oldKey, int newKeyId) {
        if (preferences.contains(oldKey)) {
            String newKey = getString(newKeyId);
            String value = preferences.getString(oldKey, "");
            preferences.edit()
                    .putString(newKey, value)
                    .remove(oldKey)
                    .apply();
        }
    }

    private void deleteString(SharedPreferences preferences) {
        if (preferences.contains("onlaunch_ttl")) {
            preferences.edit().remove("onlaunch_ttl").apply();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }
}

package ru.antiyotazapret.yotatetherttl;

import android.app.Application;
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
        upgradeStringToInteger(p, "bootup_ttl", R.string.prefs_general_ttlValue_key, R.integer.prefs_general_ttlValue_default);
        deleteString(p, "onlaunch_ttl");
    }

    private void upgradeStringToInteger(SharedPreferences preferences, String oldKey, int newKeyId, int defaultValueId) {
        if (preferences.contains(oldKey)) {
            String newKey = getString(newKeyId);
            String existValueString = preferences.getString(oldKey, "");
            int newValue = getResources().getInteger(defaultValueId);
            try {
                newValue = Integer.parseInt(existValueString);
            } catch (NumberFormatException e) {
                TtlApplication.Loge(e.toString());
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

    private void deleteString(SharedPreferences preferences, String key) {
        if (preferences.contains(key)) {
            preferences.edit().remove(key).commit();
        }
    }

}

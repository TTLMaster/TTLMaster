package ru.antiyotazapret.yotatetherttl;

import net.orange_box.storebox.annotations.method.DefaultValue;
import net.orange_box.storebox.annotations.method.KeyByString;

/**
 * Created by pavel on 29/08/15.
 */
public interface Preferences {

    @KeyByString("bootup")
    @DefaultValue(R.bool.prefs_bootup_default)
    boolean isBootup();

    @KeyByString("bootup_ttl")
    String bootupTtl();

    @KeyByString("bootup_toast")
    @DefaultValue(R.bool.prefs_bootup_toast_default)
    boolean bootupToast();

    @KeyByString("method")
    @DefaultValue(R.string.prefs_method_airplane)
    String method();

    @KeyByString("lang")
    @DefaultValue(R.string.prefs_lang_default)
    String getLanguage();

    @KeyByString("onlaunch_ttl")
    @DefaultValue(R.string.prefs_onlaunch_ttl_default)
    String getOnLaunchTtl();

    /**
     * Включен ли режим Debug
     *
     * @return {@code true} если включен
     */
    @KeyByString("debugm")
    @DefaultValue(R.bool.prefs_debugm_default)
    boolean getDebugM();

    @KeyByString("wifi")
    @DefaultValue(R.bool.prefs_wifi_default)
    boolean getWiFi();

}

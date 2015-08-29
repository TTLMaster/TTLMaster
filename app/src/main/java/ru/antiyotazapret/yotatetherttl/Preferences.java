package ru.antiyotazapret.yotatetherttl;

import net.orange_box.storebox.annotations.method.DefaultValue;
import net.orange_box.storebox.annotations.method.KeyByString;

/**
 * Настройки приложения.
 *
 * @author Pavel Savinov (swapii@gmail.com)
 */
public interface Preferences {

    /**
     * Применение TTL при загрузке системы.
     *
     * @return {@code true} если авто-применение включено.
     */
    @KeyByString("bootup")
    @DefaultValue(R.bool.prefs_boot_autoStart_default)
    boolean isBootup();

    /**
     * Значение TTL которое надо применить при загрузке системы.
     *
     * @return значение TLL
     */
    @KeyByString("bootup_ttl")
    String bootupTtl();

    /**
     * Отображение процесса применения TTL.
     *
     * @return {@code true} если включено
     */
    @KeyByString("bootup_toast")
    @DefaultValue(R.bool.prefs_boot_showToasts_default)
    boolean bootupToast();

    /**
     * Метод активации TTL после установки.
     *
     * @return одно из значений {@link ru.antiyotazapret.yotatetherttl.R.array#prefs_general_reconnectType_values}
     * @see ru.antiyotazapret.yotatetherttl.R.string#prefs_general_reconnectType_airplane
     * @see ru.antiyotazapret.yotatetherttl.R.string#prefs_general_reconnectType_mobile
     * @see ru.antiyotazapret.yotatetherttl.R.string#prefs_general_reconnectType_off
     */
    @KeyByString("method")
    @DefaultValue(R.string.prefs_general_reconnectType_airplane)
    String method();

    /**
     * Выбранный язык приложения.
     *
     * @return выбранный язык, либо {@code default} если автоматически
     */
    @KeyByString("lang")
    @DefaultValue(R.string.prefs_misc_language_default)
    String getLanguage();

    /**
     * Значение поля TTL на главном экране при старте приложения.
     *
     * @return значение поля TTL
     */
    @KeyByString("onlaunch_ttl")
    @DefaultValue(R.string.prefs_boot_ttlValue_default)
    String getOnLaunchTtl();

    /**
     * Включен ли режим Debug.
     *
     * @return {@code true} если включен
     */
    @KeyByString("debugm")
    @DefaultValue(R.bool.prefs_misc_debugMode_default)
    boolean getDebugM();

    /**
     * Нужно ли включать точку доступа после применения TTL.
     *
     * @return {@code true} если нужно включать
     */
    @KeyByString("wifi")
    @DefaultValue(R.bool.prefs_general_wifiHotspotOn_default)
    boolean getWiFi();

}

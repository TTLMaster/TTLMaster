package ru.antiyotazapret.yotatetherttl;

import net.orange_box.storebox.annotations.method.DefaultValue;
import net.orange_box.storebox.annotations.method.KeyByResource;

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
    @KeyByResource(R.string.prefs_boot_autoStart_key)
    @DefaultValue(R.bool.prefs_boot_autoStart_default)
    boolean autoStartOnBoot();

    /**
     * Значение TTL которое надо применить при загрузке системы.
     *
     * @return значение TLL
     */
    @KeyByResource(R.string.prefs_boot_ttlValue_key)
    @DefaultValue(R.integer.prefs_boot_ttlValue_default)
    int onBootTtlValue();

    /**
     * Отображение процесса применения TTL.
     *
     * @return {@code true} если включено
     */
    @KeyByResource(R.string.prefs_boot_showToasts_key)
    @DefaultValue(R.bool.prefs_boot_showToasts_default)
    boolean showToastsOnBoot();

    /**
     * Метод активации TTL после установки.
     *
     * @return одно из значений {@link ru.antiyotazapret.yotatetherttl.R.array#prefs_general_reconnectType_values}
     * @see ru.antiyotazapret.yotatetherttl.R.string#prefs_general_reconnectType_airplane
     * @see ru.antiyotazapret.yotatetherttl.R.string#prefs_general_reconnectType_mobile
     * @see ru.antiyotazapret.yotatetherttl.R.string#prefs_general_reconnectType_off
     */
    @KeyByResource(R.string.prefs_general_reconnectType_key)
    @DefaultValue(R.string.prefs_general_reconnectType_default)
    String reconnectType();

    /**
     * Выбранный язык приложения.
     *
     * @return выбранный язык, либо {@code default} если автоматически
     */
    @KeyByResource(R.string.prefs_misc_language_key)
    @DefaultValue(R.string.prefs_misc_language_default)
    String getSelectedLanguage();

    /**
     * Значение поля TTL на главном экране при старте приложения.
     *
     * @return значение поля TTL
     */
    @KeyByResource(R.string.prefs_misc_ttlValue_key)
    @DefaultValue(R.integer.prefs_misc_ttlValue_default)
    int getTtlValueForMainScreen();

    /**
     * Включен ли режим Debug.
     *
     * @return {@code true} если включен
     */
    @KeyByResource(R.string.prefs_misc_debugMode_key)
    @DefaultValue(R.bool.prefs_misc_debugMode_default)
    boolean isDebugMode();

    /**
     * Нужно ли включать точку доступа после применения TTL.
     *
     * @return {@code true} если нужно включать
     */
    @KeyByResource(R.string.prefs_general_wifiHotspotOn_key)
    @DefaultValue(R.bool.prefs_general_wifiHotspotOn_default)
    boolean startWifiHotspotOnApplyTtl();


    /**
     * Игорирование  iptables.
     *
     * @return {@code true} если нужно игнорировать
     */
    @KeyByResource(R.string.prefs_general_ignoreIptables_key)
    @DefaultValue(R.bool.prefs_general_ignoreIptables_default)
    boolean ignoreIptables();



}

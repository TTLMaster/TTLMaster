package io.github.ttlmaster;

import net.orange_box.storebox.annotations.method.DefaultValue;
import net.orange_box.storebox.annotations.method.KeyByResource;

import java.util.Set;

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
     * Значение TTL, используемое, если другие методы обхода недоступны
     *
     * @return значение TLL
     */
    @KeyByResource(R.string.prefs_general_ttlValue_key)
    @DefaultValue(R.integer.prefs_general_ttlValue_default)
    int ttlFallbackVaule();

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
     * @return одно из значений {@link io.github.ttlmaster.R.array#prefs_general_reconnectType_values}
     * @see io.github.ttlmaster.R.string#prefs_general_reconnectType_airplane
     * @see io.github.ttlmaster.R.string#prefs_general_reconnectType_mobile
     * @see io.github.ttlmaster.R.string#prefs_general_reconnectType_off
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

    /**
     * URL списка блокировки
     *
     * @return URL
     */
    @KeyByResource(R.string.prefs_restrictions_banurl_key)
    @DefaultValue(R.string.prefs_restrictions_banurl_default)
    String getBanlistURL();

    /**
     * Включён ли стоп-лист
     * @return {@code true} включен
     */
    @KeyByResource(R.string.prefs_restrictions_enabled_key)
    @DefaultValue(R.bool.prefs_restrictions_enabled_default)
    boolean restrictionsEnabled();


    /**
     * База данных блокировок
     *
     */
    @KeyByResource(R.string.prefs_restrictions_banurl_db_key)
    Set<String> getBans();

    @KeyByResource(R.string.prefs_restrictions_banurl_db_key)
    void setBans(Set<String> a);

    @KeyByResource(R.string.prefs_restrictions_banurl_updated_key)
    Long getBansUpdated();

    @KeyByResource(R.string.prefs_restrictions_banurl_updated_key)
    void setBansUpdated(Long a);



}

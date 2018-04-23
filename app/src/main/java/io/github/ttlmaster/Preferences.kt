package io.github.ttlmaster

import net.orange_box.storebox.annotations.method.DefaultValue
import net.orange_box.storebox.annotations.method.KeyByResource

/**
 * Настройки приложения.
 *
 * @author Pavel Savinov (swapii@gmail.com)
 */
interface Preferences {

    /**
     * Выбранный язык приложения.
     *
     * @return выбранный язык, либо `default` если автоматически
     */
    @get:KeyByResource(R.string.prefs_misc_language_key)
    @get:DefaultValue(R.string.prefs_misc_language_default)
    val selectedLanguage: String


    /**
     * Включен ли режим Debug.
     *
     * @return `true` если включен
     */
    @get:KeyByResource(R.string.prefs_misc_debugMode_key)
    @get:DefaultValue(R.bool.prefs_misc_debugMode_default)
    val isDebugMode: Boolean

    /**
     * URL списка блокировки
     *
     * @return URL
     */
    @get:KeyByResource(R.string.prefs_restrictions_banurl_key)
    @get:DefaultValue(R.string.prefs_restrictions_banurl_default)
    val banlistURL: String


    /**
     * База данных блокировок
     *
     */
    @get:KeyByResource(R.string.prefs_restrictions_banurl_db_key)
    @set:KeyByResource(R.string.prefs_restrictions_banurl_db_key)
    var bans: Set<String>

    @get:KeyByResource(R.string.prefs_restrictions_banurl_updated_key)
    @set:KeyByResource(R.string.prefs_restrictions_banurl_updated_key)
    var bansUpdated: Long?

    /**
     * Применение TTL при загрузке системы.
     *
     * @return `true` если авто-применение включено.
     */
    @KeyByResource(R.string.prefs_boot_autoStart_key)
    @DefaultValue(R.bool.prefs_boot_autoStart_default)
    fun autoStartOnBoot(): Boolean

    /**
     * Значение TTL, используемое, если другие методы обхода недоступны
     *
     * @return значение TLL
     */
    @KeyByResource(R.string.prefs_general_ttlValue_key)
    @DefaultValue(R.integer.prefs_general_ttlValue_default)
    fun ttlFallbackVaule(): Int

    /**
     * Отображение процесса применения TTL.
     *
     * @return `true` если включено
     */
    @KeyByResource(R.string.prefs_boot_showToasts_key)
    @DefaultValue(R.bool.prefs_boot_showToasts_default)
    fun showToastsOnBoot(): Boolean

    /**
     * Метод активации TTL после установки.
     *
     * @return одно из значений [io.github.ttlmaster.R.array.prefs_general_reconnectType_values]
     * @see io.github.ttlmaster.R.string.prefs_general_reconnectType_airplane
     *
     * @see io.github.ttlmaster.R.string.prefs_general_reconnectType_mobile
     *
     * @see io.github.ttlmaster.R.string.prefs_general_reconnectType_off
     */
    @KeyByResource(R.string.prefs_general_reconnectType_key)
    @DefaultValue(R.string.prefs_general_reconnectType_default)
    fun reconnectType(): String

    /**
     * Нужно ли включать точку доступа после применения TTL.
     *
     * @return `true` если нужно включать
     */
    @KeyByResource(R.string.prefs_general_wifiHotspotOn_key)
    @DefaultValue(R.bool.prefs_general_wifiHotspotOn_default)
    fun startWifiHotspotOnApplyTtl(): Boolean


    /**
     * Игорирование  iptables.
     *
     * @return `true` если нужно игнорировать
     */
    @KeyByResource(R.string.prefs_general_ignoreIptables_key)
    @DefaultValue(R.bool.prefs_general_ignoreIptables_default)
    fun ignoreIptables(): Boolean

    /**
     * Включён ли стоп-лист
     * @return `true` включен
     */
    @KeyByResource(R.string.prefs_restrictions_enabled_key)
    @DefaultValue(R.bool.prefs_restrictions_enabled_default)
    fun restrictionsEnabled(): Boolean


}

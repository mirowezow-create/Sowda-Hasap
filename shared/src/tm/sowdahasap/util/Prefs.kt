package tm.sowdahasap.util

/** Platforma sazlamalary saklaýan ýer (Android: SharedPreferences, Windows: faýl) */
interface KeyValueStore {
    fun getString(key: String, def: String): String
    fun putString(key: String, value: String)
    fun getLong(key: String, def: Long): Long
    fun putLong(key: String, value: Long)
}

class Prefs(private val kv: KeyValueStore) {
    private fun s(k: String, def: String = "") = kv.getString(k, def)
    private fun putS(k: String, v: String) = kv.putString(k, v)
    private fun l(k: String, def: Long = 0L) = kv.getLong(k, def)
    private fun putL(k: String, v: Long) = kv.putLong(k, v)
    private fun b(k: String, def: Boolean) = kv.getLong(k, if (def) 1L else 0L) == 1L
    private fun putB(k: String, v: Boolean) = kv.putLong(k, if (v) 1L else 0L)

    var licenseKey: String get() = s("license_key"); set(v) = putS("license_key", v)
    var lastSeenTime: Long get() = l("last_seen"); set(v) = putL("last_seen", v)

    var serverUrl: String get() = s("server_url"); set(v) = putS("server_url", v.trim().trimEnd('/'))
    var syncToken: String get() = s("sync_token"); set(v) = putS("sync_token", v.trim())
    var lastPush: Long get() = l("last_push"); set(v) = putL("last_push", v)
    var lastPull: Long get() = l("last_pull"); set(v) = putL("last_pull", v)
    var lastSyncInfo: String get() = s("last_sync_info"); set(v) = putS("last_sync_info", v)
    var autoSync: Boolean get() = b("auto_sync", true); set(v) = putB("auto_sync", v)

    // Windows: bu kompýuter esasy serwer
    var hostServer: Boolean get() = b("host_server", false); set(v) = putB("host_server", v)
    var hostPort: Int get() = l("host_port", 8080L).toInt(); set(v) = putL("host_port", v.toLong())

    var company: String get() = s("company", "Meniň dükanym"); set(v) = putS("company", v)
    var companyInfo: String get() = s("company_info"); set(v) = putS("company_info", v)

    var printerAddress: String get() = s("printer_addr"); set(v) = putS("printer_addr", v)
    var printerName: String get() = s("printer_name"); set(v) = putS("printer_name", v)
    var paperChars: Int get() = l("paper_chars", 32L).toInt(); set(v) = putL("paper_chars", v.toLong())

    var smsTemplate: String
        get() = s("sms_template", "Hormatly {ady}, siziň {firma} öňündäki bergiňiz {bergi} TMT. Sag boluň!")
        set(v) = putS("sms_template", v)
    var smsTranslit: Boolean get() = b("sms_translit", true); set(v) = putB("sms_translit", v)

    var defaultWarehouse: String get() = s("default_wh", "main"); set(v) = putS("default_wh", v)

    // Gijeki baha: günüň minutlarynda (22:00 = 1320)
    var nightStart: Int get() = l("night_start", 22L * 60).toInt(); set(v) = putL("night_start", v.toLong())
    var nightEnd: Int get() = l("night_end", 6L * 60).toInt(); set(v) = putL("night_end", v.toLong())
    var nightEnabled: Boolean get() = b("night_enabled", true); set(v) = putB("night_enabled", v)
}

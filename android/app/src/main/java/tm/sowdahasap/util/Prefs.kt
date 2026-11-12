package tm.sowdahasap.util

import android.content.Context

class Prefs(ctx: Context) {
    private val sp = ctx.getSharedPreferences("sowdahasap", Context.MODE_PRIVATE)

    private fun s(k: String, def: String = "") = sp.getString(k, def) ?: def
    private fun putS(k: String, v: String) = sp.edit().putString(k, v).apply()
    private fun l(k: String) = sp.getLong(k, 0L)
    private fun putL(k: String, v: Long) = sp.edit().putLong(k, v).apply()

    var licenseKey: String get() = s("license_key"); set(v) = putS("license_key", v)
    var lastSeenTime: Long get() = l("last_seen"); set(v) = putL("last_seen", v)

    var serverUrl: String get() = s("server_url"); set(v) = putS("server_url", v.trim().trimEnd('/'))
    var syncToken: String get() = s("sync_token"); set(v) = putS("sync_token", v.trim())
    var lastPush: Long get() = l("last_push"); set(v) = putL("last_push", v)
    var lastPull: Long get() = l("last_pull"); set(v) = putL("last_pull", v)
    var lastSyncInfo: String get() = s("last_sync_info"); set(v) = putS("last_sync_info", v)

    var company: String get() = s("company", "Meniň dükanym"); set(v) = putS("company", v)
    var companyInfo: String get() = s("company_info"); set(v) = putS("company_info", v)

    var printerAddress: String get() = s("printer_addr"); set(v) = putS("printer_addr", v)
    var printerName: String get() = s("printer_name"); set(v) = putS("printer_name", v)
    var paperChars: Int get() = sp.getInt("paper_chars", 32); set(v) = sp.edit().putInt("paper_chars", v).apply()

    var smsTemplate: String
        get() = s("sms_template", "Hormatly {ady}, siziň {firma} öňündäki bergiňiz {bergi} TMT. Sag boluň!")
        set(v) = putS("sms_template", v)
    var smsTranslit: Boolean get() = sp.getBoolean("sms_translit", true); set(v) = sp.edit().putBoolean("sms_translit", v).apply()

    var defaultWarehouse: String get() = s("default_wh", "main"); set(v) = putS("default_wh", v)
}

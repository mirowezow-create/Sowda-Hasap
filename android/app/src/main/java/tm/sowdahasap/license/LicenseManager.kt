package tm.sowdahasap.license

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import tm.sowdahasap.App
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Base64

/**
 * Aýlyk lisenziýa.
 *
 * Açar = "YYYYMMDD-<gol>". Gol ECDSA P-256 bilen "SH1|<enjam kody>|YYYYMMDD" tekstine çekilýär.
 * Programmada diňe AÇYK açar bar — şonuň üçin programmany açyp, açar ýasap bolmaýar.
 * Gizlin açar (private.pem) diňe sizde, tools/keygen.js bilen açar ýasaýarsyňyz.
 */
object LicenseManager {

    // tools/keygen.js init çykaran açyk açary şu ýere goýuň
    private const val PUBLIC_KEY =
        "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEc2seIpeSCzgf2kt+Q8IQOvCC84QBUQGqa7+zWF61PPVQqKZEl5vjTaJctumU+6svKouihtPJhXZ282mDAP7Gwg=="

    private const val WARN_DAYS = 5
    private val YMD = DateTimeFormatter.ofPattern("yyyyMMdd")

    data class Status(
        val valid: Boolean,
        val expiry: LocalDate?,
        val daysLeft: Long,
        val message: String,
    ) {
        val warn: Boolean get() = valid && daysLeft <= WARN_DAYS
    }

    private val publicKey: PublicKey by lazy {
        KeyFactory.getInstance("EC").generatePublic(X509EncodedKeySpec(Base64.getDecoder().decode(PUBLIC_KEY)))
    }

    @SuppressLint("HardwareIds")
    fun deviceCode(ctx: Context): String {
        val aid = Settings.Secure.getString(ctx.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown"
        val h = MessageDigest.getInstance("SHA-256").digest("$aid|${ctx.packageName}".toByteArray())
        return h.take(8).joinToString("") { "%02X".format(it) }.chunked(4).joinToString("-")
    }

    /** Açary barlaýar; dogry bolsa möhlet senesini gaýtarýar */
    fun verify(device: String, rawKey: String): LocalDate? {
        return try {
            val key = rawKey.filterNot { it.isWhitespace() }
            val m = Regex("^(\\d{8})-([A-Za-z0-9_-]+)$").find(key) ?: return null
            val date = m.groupValues[1]
            val sig = Base64.getUrlDecoder().decode(m.groupValues[2])
            if (sig.size != 64) return null
            val v = Signature.getInstance("SHA256withECDSA")
            v.initVerify(publicKey)
            v.update("SH1|$device|$date".toByteArray(Charsets.UTF_8))
            if (v.verify(p1363ToDer(sig))) LocalDate.parse(date, YMD) else null
        } catch (e: Exception) {
            null
        }
    }

    fun status(ctx: Context): Status {
        val prefs = App.prefs
        val key = prefs.licenseKey
        if (key.isBlank()) return Status(false, null, 0, "Lisenziýa açary girizilmedi")
        val expiry = verify(deviceCode(ctx), key) ?: return Status(false, null, 0, "Açar nädogry ýa-da başga enjam üçin")

        // Sagady yzyna süýşürip aldamagyň öňüni almak
        val nowMs = System.currentTimeMillis()
        val last = prefs.lastSeenTime
        if (last > 0 && nowMs < last - 24L * 3600 * 1000) {
            return Status(false, expiry, 0, "Telefonyň senesi nädogry (yzyna süýşürilipdir). Senäni düzediň.")
        }
        if (nowMs > last) prefs.lastSeenTime = nowMs

        val today = LocalDate.now()
        val left = ChronoUnit.DAYS.between(today, expiry)
        return if (left >= 0) Status(true, expiry, left, "Lisenziýa ${expiry} çenli")
        else Status(false, expiry, left, "Lisenziýanyň möhleti ${expiry} gutardy")
    }

    /** Täze açary girizmek. Üstünlik bolsa null, ýogsam ýalňyşlyk teksti */
    fun activate(ctx: Context, key: String): String? {
        val clean = key.filterNot { it.isWhitespace() }
        val exp = verify(deviceCode(ctx), clean) ?: return "Açar nädogry ýa-da başga enjam üçin"
        if (exp.isBefore(LocalDate.now())) return "Bu açaryň möhleti eýýäm gutarypdyr ($exp)"
        App.prefs.licenseKey = clean
        return null
    }

    /** Serwerden şu enjamyň açaryny almak */
    suspend fun fetchOnline(ctx: Context, serverUrl: String): String? = withContext(Dispatchers.IO) {
        try {
            val device = deviceCode(ctx)
            val url = URL("${serverUrl.trimEnd('/')}/api/license?device=" + URLEncoder.encode(device, "UTF-8"))
            val c = url.openConnection() as HttpURLConnection
            c.connectTimeout = 10000
            c.readTimeout = 15000
            val code = c.responseCode
            val body = (if (code in 200..299) c.inputStream else c.errorStream)?.bufferedReader()?.readText() ?: ""
            c.disconnect()
            if (code == 404) return@withContext "Serwerde bu enjam üçin açar ýok. Enjam kodyny satyja ugradyň."
            if (code !in 200..299) return@withContext "Serwer ýalňyşlygy: $code"
            val key = JSONObject(body).optString("key")
            activate(ctx, key)
        } catch (e: Exception) {
            "Serwere birigip bolmady: ${e.message}"
        }
    }

    /** 64 baýtly (r||s) goly Java-nyň garaşýan DER görnüşine öwürýär */
    fun p1363ToDer(sig: ByteArray): ByteArray {
        fun derInt(b: ByteArray): ByteArray {
            var i = 0
            while (i < b.size - 1 && b[i] == 0.toByte()) i++
            var v = b.copyOfRange(i, b.size)
            if ((v[0].toInt() and 0x80) != 0) v = byteArrayOf(0) + v
            return byteArrayOf(0x02, v.size.toByte()) + v
        }
        val body = derInt(sig.copyOfRange(0, 32)) + derInt(sig.copyOfRange(32, 64))
        return byteArrayOf(0x30, body.size.toByte()) + body
    }
}

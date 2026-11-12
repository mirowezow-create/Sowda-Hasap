package tm.sowdahasap.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.abs

fun newId(): String = UUID.randomUUID().toString()
fun now(): Long = System.currentTimeMillis()

/** 125050 -> "1 250.50" */
fun Long.money(): String {
    val a = abs(this)
    val s = String.format(Locale.US, "%,d.%02d", a / 100, a % 100).replace(',', ' ')
    return if (this < 0) "-$s" else s
}

fun Long.moneyTmt(): String = money() + " TMT"

/** Pul giriş meýdanyndaky teksti teňňä öwürýär: "12,5" -> 1250 */
fun parseMoney(s: String): Long? = s.trim().replace(" ", "").replace(',', '.')
    .toBigDecimalOrNull()?.movePointRight(2)?.setScale(0, RoundingMode.HALF_UP)?.toLong()

/** Teňňäni giriş meýdany üçin tekste öwürýär: 1250 -> "12.50" */
fun Long.moneyInput(): String = BigDecimal.valueOf(this).movePointLeft(2).setScale(2).toPlainString()

fun parseQty(s: String): Double? = s.trim().replace(',', '.').toDoubleOrNull()

fun Double.qty(): String =
    if (this % 1.0 == 0.0) this.toLong().toString()
    else String.format(Locale.US, "%.3f", this).trimEnd('0').trimEnd('.')

private val dateFmt = SimpleDateFormat("dd.MM.yyyy", Locale.US)
private val dateTimeFmt = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US)
fun Long.date(): String = dateFmt.format(Date(this))
fun Long.dateTime(): String = dateTimeFmt.format(Date(this))

/** Türkmen harplaryny latyn ASCII-a öwürýär (termal printer we arzan SMS üçin) */
fun translit(s: String): String {
    val map = mapOf(
        'ä' to "a", 'Ä' to "A", 'ç' to "c", 'Ç' to "C", 'ň' to "n", 'Ň' to "N",
        'ö' to "o", 'Ö' to "O", 'ş' to "s", 'Ş' to "S", 'ü' to "u", 'Ü' to "U",
        'ý' to "y", 'Ý' to "Y", 'ž' to "z", 'Ž' to "Z",
    )
    val sb = StringBuilder()
    for (ch in s) {
        val r = map[ch]
        when {
            r != null -> sb.append(r)
            ch.code < 128 -> sb.append(ch)
            else -> sb.append('?')
        }
    }
    return sb.toString()
}

/** Faýly başga programma (Telegram, e-poçta, Excel...) bilen paýlaşmak */
fun shareFile(ctx: Context, file: File, mime: String) {
    val uri = FileProvider.getUriForFile(ctx, ctx.packageName + ".files", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mime
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    ctx.startActivity(Intent.createChooser(intent, "Paýlaş").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}

fun exportDir(ctx: Context): File = File(ctx.cacheDir, "exports").apply { mkdirs() }

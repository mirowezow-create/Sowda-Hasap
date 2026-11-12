package tm.sowdahasap.util

import tm.sowdahasap.App
import java.time.LocalTime
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

/** Häzir gijeki baha wagtymy? (mysal 22:00–06:00) */
fun isNightNow(): Boolean {
    val p = App.prefs
    if (!p.nightEnabled) return false
    val t = LocalTime.now()
    val m = t.hour * 60 + t.minute
    val a = p.nightStart
    val b = p.nightEnd
    return if (a <= b) m in a until b else (m >= a || m < b)
}

fun minutesToHm(m: Int): String = String.format(Locale.US, "%02d:%02d", m / 60, m % 60)

fun parseHm(s: String): Int? {
    val p = s.trim().split(":")
    if (p.size != 2) return null
    val h = p[0].toIntOrNull() ?: return null
    val mi = p[1].toIntOrNull() ?: return null
    if (h !in 0..23 || mi !in 0..59) return null
    return h * 60 + mi
}

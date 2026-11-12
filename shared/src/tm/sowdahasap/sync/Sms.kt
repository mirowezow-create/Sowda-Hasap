package tm.sowdahasap.sync

import tm.sowdahasap.App
import tm.sowdahasap.data.CustomerBalance
import tm.sowdahasap.util.money
import tm.sowdahasap.util.translit

object Sms {
    fun text(c: CustomerBalance, template: String = App.prefs.smsTemplate, tr: Boolean = App.prefs.smsTranslit): String {
        val p = App.prefs
        val t = template
            .replace("{ady}", c.customer.name)
            .replace("{bergi}", c.debt.money())
            .replace("{firma}", p.company)
        return if (tr) translit(t) else t
    }

    /** Telefon belgisini +993 görnüşe getirýär: 865123456 -> +99365123456 */
    fun normalizePhone(raw: String): String {
        val d = raw.filter { it.isDigit() || it == '+' }
        return when {
            d.startsWith("+") -> d
            d.startsWith("993") -> "+$d"
            d.startsWith("8") && d.length == 9 -> "+993" + d.drop(1)
            d.length == 8 -> "+993$d"
            else -> d
        }
    }

}

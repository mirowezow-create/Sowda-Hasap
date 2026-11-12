package tm.sowdahasap.sync

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
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

    /** SMS-i göni ugradýar (SEND_SMS rugsady gerek). Operatoryň tarifi boýunça töleg alynýar. */
    fun send(ctx: Context, phone: String, text: String) {
        val sm: SmsManager = if (Build.VERSION.SDK_INT >= 31) ctx.getSystemService(SmsManager::class.java)
        else @Suppress("DEPRECATION") SmsManager.getDefault()
        val parts = sm.divideMessage(text)
        sm.sendMultipartTextMessage(normalizePhone(phone), null, parts, null, null)
    }

    /** SMS programmasyny açýar (rugsat gerek däl, ulanyjy özi ugradýar) */
    fun openComposer(ctx: Context, phone: String, text: String) {
        val i = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + normalizePhone(phone)))
            .putExtra("sms_body", text).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(i)
    }
}

package tm.sowdahasap.print

import tm.sowdahasap.App
import tm.sowdahasap.data.Customer
import tm.sowdahasap.data.Invoice
import tm.sowdahasap.data.InvoiceLine
import tm.sowdahasap.data.InvoiceStatus
import tm.sowdahasap.data.Repo
import tm.sowdahasap.util.dateTime
import tm.sowdahasap.util.moneyTmt
import tm.sowdahasap.util.money
import tm.sowdahasap.util.qty
import tm.sowdahasap.util.translit
import java.io.ByteArrayOutputStream

class EscPos(private val width: Int) {
    private val out = ByteArrayOutputStream()

    init { out.write(byteArrayOf(0x1B, 0x40)) } // printer reset

    private fun raw(s: String) = out.write(translit(s).toByteArray(Charsets.US_ASCII))

    fun text(s: String) = apply { raw(s); out.write(0x0A) }
    fun center(on: Boolean) = apply { out.write(byteArrayOf(0x1B, 0x61, (if (on) 1 else 0).toByte())) }
    fun bold(on: Boolean) = apply { out.write(byteArrayOf(0x1B, 0x45, (if (on) 1 else 0).toByte())) }
    fun big(on: Boolean) = apply { out.write(byteArrayOf(0x1D, 0x21, (if (on) 0x11 else 0x00).toByte())) }
    fun line() = text("-".repeat(width))
    fun row(left: String, right: String) = apply {
        val l = left.take((width - right.length - 1).coerceAtLeast(1))
        text(l + " ".repeat((width - l.length - right.length).coerceAtLeast(1)) + right)
    }
    fun wrap(s: String) = apply { s.chunked(width).forEach { text(it) } }
    fun feedAndCut() = apply { out.write(byteArrayOf(0x0A, 0x0A, 0x0A, 0x1D, 0x56, 0x42, 0x00)) }
    fun bytes(): ByteArray = out.toByteArray()
}

object Receipts {
    fun invoiceReceipt(inv: Invoice, lines: List<InvoiceLine>, customer: Customer?, debt: Long?): ByteArray {
        val p = App.prefs
        val e = EscPos(p.paperChars)
        e.center(true).bold(true).big(true).text(p.company).big(false).bold(false)
        if (p.companyInfo.isNotBlank()) e.wrap(p.companyInfo)
        e.center(false).line()
        e.row("Faktura: ${inv.number}", "")
        e.text(inv.createdAt.dateTime())
        customer?.let { e.text("Müşderi: ${it.name}") }
        if (inv.status != InvoiceStatus.POSTED) e.text("*** ${InvoiceStatus.title(inv.status).uppercase()} ***")
        e.line()
        lines.forEach {
            e.wrap(it.productName)
            e.row("  ${it.qty.qty()} x ${it.price.money()}", Repo.lineTotal(it).money())
        }
        e.line()
        if (inv.discount > 0) e.row("Arzanladyş", "-" + inv.discount.money())
        e.bold(true).row("JEMI", inv.total.money() + " TMT").bold(false)
        if (debt != null && debt > 0) e.row("Umumy bergi", debt.money() + " TMT")
        e.line().center(true).text("Sag boluň!").center(false)
        return e.feedAndCut().bytes()
    }

    fun testPage(): ByteArray = EscPos(App.prefs.paperChars).center(true).bold(true)
        .text(App.prefs.company).bold(false).text("Printer synagy").text("1234567890".repeat(5).take(App.prefs.paperChars))
        .center(false).feedAndCut().bytes()
}


/** A4 faktura HTML görnüşinde (brauzerde açyp çap etmek ýa-da PDF etmek bolýar) */
object InvoiceHtml {
    private fun esc(s: String) = s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")

    fun build(inv: Invoice, lines: List<InvoiceLine>, customer: Customer?): String {
        val p = App.prefs
        val rows = lines.joinToString("") {
            "<tr><td>${esc(it.productName)}</td><td class=r>${it.qty.qty()}</td>" +
                "<td class=r>${it.price.money()}</td><td class=r>${Repo.lineTotal(it).money()}</td></tr>"
        }
        val status = if (inv.status != InvoiceStatus.POSTED) " (${InvoiceStatus.title(inv.status)})" else ""
        val kind = if (inv.wholesale) "Opt" else "Bölek"
        return """<!DOCTYPE html><html><head><meta charset="utf-8"><title>Faktura ${esc(inv.number)}</title>
<style>body{font-family:Arial,sans-serif;margin:32px;color:#111}h1{margin:0;font-size:22px}
table{width:100%;border-collapse:collapse;margin-top:16px}td,th{border-bottom:1px solid #ccc;padding:6px;text-align:left}
.r{text-align:right}.tot{font-size:18px;font-weight:bold}@media print{button{display:none}}</style></head><body>
<h1>${esc(p.company)}</h1><div>${esc(p.companyInfo)}</div><hr>
<b>FAKTURA № ${esc(inv.number)}</b>$status · $kind satuw<br>${inv.createdAt.dateTime()}<br>
${customer?.let { "Müşderi: ${esc(it.name)} ${esc(it.phone)}" } ?: "Nagt satuw"}
<table><tr><th>Haryt</th><th class=r>Mukdar</th><th class=r>Baha</th><th class=r>Jemi</th></tr>$rows</table>
<p class=r>${if (inv.discount > 0) "Arzanladyş: -${inv.discount.money()}<br>" else ""}<span class=tot>JEMI: ${inv.total.moneyTmt()}</span></p>
<button onclick="window.print()">Çap et</button></body></html>"""
    }
}

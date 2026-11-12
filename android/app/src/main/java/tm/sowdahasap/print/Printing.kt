package tm.sowdahasap.print

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tm.sowdahasap.App
import tm.sowdahasap.data.Customer
import tm.sowdahasap.data.Invoice
import tm.sowdahasap.data.InvoiceLine
import tm.sowdahasap.data.InvoiceStatus
import tm.sowdahasap.data.Repo
import tm.sowdahasap.util.dateTime
import tm.sowdahasap.util.exportDir
import tm.sowdahasap.util.money
import tm.sowdahasap.util.qty
import tm.sowdahasap.util.translit
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID

/** Bluetooth termal printer (ESC/POS, 58 mm = 32 harp, 80 mm = 48 harp) */
object BtPrinter {
    private val SPP: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    @SuppressLint("MissingPermission")
    fun pairedDevices(ctx: Context): List<Pair<String, String>> {
        val adapter = ctx.getSystemService(BluetoothManager::class.java)?.adapter ?: return emptyList()
        if (!adapter.isEnabled) return emptyList()
        return adapter.bondedDevices.map { (it.name ?: it.address) to it.address }
    }

    @SuppressLint("MissingPermission")
    suspend fun send(ctx: Context, address: String, data: ByteArray) = withContext(Dispatchers.IO) {
        val adapter = ctx.getSystemService(BluetoothManager::class.java)?.adapter
            ?: error("Bu telefonda Bluetooth ýok")
        if (!adapter.isEnabled) error("Bluetooth-y açyň")
        require(address.isNotBlank()) { "Sazlamalarda printer saýlaň" }
        adapter.cancelDiscovery()
        val socket = adapter.getRemoteDevice(address).createRfcommSocketToServiceRecord(SPP)
        socket.use {
            it.connect()
            it.outputStream.write(data)
            it.outputStream.flush()
            Thread.sleep(400)
        }
    }
}

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

/** A4 PDF faktura (paýlaşmak üçin) */
object PdfInvoice {
    fun create(ctx: Context, inv: Invoice, lines: List<InvoiceLine>, customer: Customer?): File {
        val doc = PdfDocument()
        val w = 595; val h = 842; val margin = 40f
        val normal = Paint().apply { textSize = 11f; isAntiAlias = true }
        val bold = Paint(normal).apply { typeface = Typeface.DEFAULT_BOLD }
        val title = Paint(bold).apply { textSize = 18f }
        val right = Paint(normal).apply { textAlign = Paint.Align.RIGHT }
        val rightBold = Paint(bold).apply { textAlign = Paint.Align.RIGHT }
        val cols = floatArrayOf(margin, 330f, 410f, w - margin) // ady | mukdar | baha | jemi

        var pageNo = 0
        var page = doc.startPage(PdfDocument.PageInfo.Builder(w, h, ++pageNo).create())
        var y = margin + 10

        fun header() {
            val c = page.canvas
            c.drawText(App.prefs.company, margin, y, title); y += 18
            if (App.prefs.companyInfo.isNotBlank()) { c.drawText(App.prefs.companyInfo, margin, y, normal); y += 16 }
            y += 8
            c.drawText("FAKTURA № ${inv.number}", margin, y, bold)
            c.drawText(inv.createdAt.dateTime(), w - margin, y, right); y += 16
            customer?.let { c.drawText("Müşderi: ${it.name}  ${it.phone}", margin, y, normal); y += 16 }
            if (inv.status != InvoiceStatus.POSTED) { c.drawText("(${InvoiceStatus.title(inv.status)})", margin, y, normal); y += 16 }
            y += 8
            c.drawText("Haryt", cols[0], y, bold)
            c.drawText("Mukdar", cols[2] - 10, y, rightBold)
            c.drawText("Baha", cols[2] + 70, y, rightBold)
            c.drawText("Jemi", cols[3], y, rightBold)
            y += 6; c.drawLine(margin, y, w - margin, y, normal); y += 16
        }
        header()
        for (l in lines) {
            if (y > h - 100) {
                doc.finishPage(page)
                page = doc.startPage(PdfDocument.PageInfo.Builder(w, h, ++pageNo).create())
                y = margin + 10
                header()
            }
            val c = page.canvas
            c.drawText(l.productName.take(48), cols[0], y, normal)
            c.drawText(l.qty.qty(), cols[2] - 10, y, right)
            c.drawText(l.price.money(), cols[2] + 70, y, right)
            c.drawText(Repo.lineTotal(l).money(), cols[3], y, right)
            y += 16
        }
        val c = page.canvas
        c.drawLine(margin, y - 8, w - margin, y - 8, normal); y += 8
        if (inv.discount > 0) { c.drawText("Arzanladyş: -${inv.discount.money()}", w - margin, y, right); y += 16 }
        c.drawText("JEMI: ${inv.total.money()} TMT", w - margin, y, rightBold)
        doc.finishPage(page)

        val f = File(exportDir(ctx), "faktura_${inv.number}.pdf")
        f.outputStream().use { doc.writeTo(it) }
        doc.close()
        return f
    }
}

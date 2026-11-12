@file:OptIn(ExperimentalMaterial3Api::class)

package tm.sowdahasap.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import tm.sowdahasap.App
import tm.sowdahasap.Role
import tm.sowdahasap.Session
import tm.sowdahasap.data.Customer
import tm.sowdahasap.data.Invoice
import tm.sowdahasap.data.InvoiceKind
import tm.sowdahasap.data.InvoiceLine
import tm.sowdahasap.data.InvoiceStatus
import tm.sowdahasap.data.PayType
import tm.sowdahasap.data.Product
import tm.sowdahasap.data.ProductUnit
import tm.sowdahasap.data.Repo
import tm.sowdahasap.platform.NeedPerm
import tm.sowdahasap.platform.Platform
import tm.sowdahasap.platform.rememberScanner
import tm.sowdahasap.platform.rememberWithPermission
import tm.sowdahasap.print.InvoiceHtml
import tm.sowdahasap.print.Receipts
import tm.sowdahasap.util.dateTime
import tm.sowdahasap.util.isNightNow
import tm.sowdahasap.util.money
import tm.sowdahasap.util.moneyInput
import tm.sowdahasap.util.newId
import tm.sowdahasap.util.now
import tm.sowdahasap.util.parseMoney
import tm.sowdahasap.util.parseQty
import tm.sowdahasap.util.qty
import kotlin.math.roundToLong

private enum class Pick { KIND, PAY, CASHBOOK, WAREHOUSE, TO_WAREHOUSE, CUSTOMER }

@Composable
fun InvoiceAdatyScreen(idArg: String, onTitle: (String) -> Unit) {
    val scope = rememberCoroutineScope()
    val show = toast()
    val warehouses by App.dao.warehouses().collectAsState(initial = emptyList())
    val customers by App.dao.customers().collectAsState(initial = emptyList())
    val cashbooks by App.dao.cashbooks().collectAsState(initial = emptyList())

    var inv by remember { mutableStateOf<Invoice?>(null) }
    val lines = remember { mutableStateListOf<InvoiceLine>() }
    val products = remember { mutableStateMapOf<String, Product>() }
    val units = remember { mutableStateMapOf<String, List<ProductUnit>>() }
    var selected by remember { mutableStateOf(-1) }
    var barcode by remember { mutableStateOf("") }
    var nameQuery by remember { mutableStateOf("") }
    var given by remember { mutableStateOf("") }
    var pctText by remember { mutableStateOf("") }
    var picker by remember { mutableStateOf<String?>(null) }   // haryt gözlegi
    var pick by remember { mutableStateOf<Pick?>(null) }
    var confirm by remember { mutableStateOf<Pair<String, () -> Unit>?>(null) }
    var customerDebt by remember { mutableStateOf<Long?>(null) }
    val barcodeFocus = remember { FocusRequester() }

    fun blank(prev: Invoice?): Invoice {
        val t = now()
        return Invoice(newId(), Repo.newInvoiceNumber(), null, prev?.warehouseId ?: App.prefs.defaultWarehouse,
            userId = Session.userId, createdAt = t, updatedAt = t,
            kind = prev?.kind ?: InvoiceKind.SALE, payType = PayType.CASH,
            cashbookId = prev?.cashbookId ?: Repo.MAIN_CASHBOOK, wholesale = prev?.wholesale ?: false)
    }

    fun startNew() {
        inv = blank(inv); lines.clear(); selected = -1; given = ""; pctText = ""; customerDebt = null
        onTitle("Faktura - Adaty")
    }

    LaunchedEffect(idArg) {
        if (idArg == "new") inv = blank(null) else {
            val i = App.dao.invoice(idArg)
            inv = i
            lines.clear(); lines.addAll(App.dao.linesOf(idArg))
            pctText = i?.discountPct?.takeIf { it != 0.0 }?.qty() ?: ""
            i?.let { onTitle("Faktura ${it.number}") }
        }
    }

    val cur = inv
    if (cur == null) { Centered { Text("...") }; return }
    val editable = cur.status == InvoiceStatus.DRAFT
    val subtotal = lines.sumOf { Repo.lineTotal(it) }
    val pct = parseQty(pctText) ?: 0.0
    val discount = (subtotal * pct / 100.0).roundToLong()
    val total = (subtotal - discount).coerceAtLeast(0)
    val givenMoney = parseMoney(given) ?: 0L
    val change = givenMoney - total
    var customerState by remember { mutableStateOf<Customer?>(null) }
    val customer = customers.firstOrNull { it.id == cur.customerId } ?: customerState?.takeIf { it.id == cur.customerId }

    LaunchedEffect(cur.customerId, cur.status) {
        customerState = cur.customerId?.let { App.dao.customer(it) }
        customerDebt = cur.customerId?.let { id -> App.dao.debtOf(id).first() }
    }
    LaunchedEffect(Unit) { runCatching { barcodeFocus.requestFocus() } }

    suspend fun productOf(id: String): Product? = products[id] ?: App.dao.product(id)?.also { products[id] = it }
    suspend fun unitsOfP(id: String): List<ProductUnit> = units[id] ?: App.dao.unitsOf(id).also { units[id] = it }

    // Açylan fakturanyň harytlaryny we ölçeglerini ýükle
    LaunchedEffect(lines.size) { lines.toList().forEach { productOf(it.productId); unitsOfP(it.productId) } }

    /** Esasy ölçegiň bahasy (görnüşe, opt/bölege, gijä görä) */
    fun basePrice(p: Product, i: Invoice): Long =
        if (InvoiceKind.usesBuyPrice(i.kind)) p.buyPrice else Repo.priceFor(p, i.wholesale)

    fun unitPrice(p: Product, u: ProductUnit?, i: Invoice): Long = when {
        u == null -> basePrice(p, i)
        u.price > 0 && !InvoiceKind.usesBuyPrice(i.kind) && !i.wholesale -> u.price
        else -> (basePrice(p, i) * u.factor).roundToLong()
    }

    suspend fun addProduct(p: Product, u: ProductUnit?) {
        var i = inv ?: return
        if (i.status != InvoiceStatus.DRAFT) { startNew(); i = inv ?: return }
        products[p.id] = p
        unitsOfP(p.id)
        val unitName = u?.unitName ?: p.unit
        val idx = lines.indexOfFirst { it.productId == p.id && it.unitName.ifBlank { p.unit } == unitName }
        if (idx >= 0) {
            lines[idx] = lines[idx].copy(qty = lines[idx].qty + 1)
            selected = idx
        } else {
            val f = u?.factor ?: 1.0
            lines.add(InvoiceLine(newId(), i.id, p.id, p.name, 1.0, unitPrice(p, u, i), (p.buyPrice * f).roundToLong(), now(),
                unitName = unitName, factor = f))
            selected = lines.size - 1
        }
    }

    suspend fun onBarcode(code: String) {
        val r = Repo.findByCode(code)
        if (r != null) addProduct(r.first, r.second) else { show("Tapylmady: $code"); picker = code }
    }

    suspend fun reprice(i: Invoice) {
        for (k in lines.indices) {
            val l = lines[k]
            val p = productOf(l.productId) ?: continue
            val u = unitsOfP(p.id).firstOrNull { it.unitName == l.unitName && l.factor != 1.0 }
            lines[k] = l.copy(price = unitPrice(p, u, i))
        }
    }

    fun setInv(newInv: Invoice, repriceToo: Boolean = false) {
        inv = newInv
        if (repriceToo) scope.launch { reprice(newInv) }
    }

    val scan = rememberScanner { code -> scope.launch { onBarcode(code) } }

    val print = rememberWithPermission(NeedPerm.BLUETOOTH) {
        scope.launch {
            try {
                Platform.print(App.prefs.printerAddress, Receipts.invoiceReceipt(cur, lines.toList(), customer, customerDebt))
                show("Çap edildi")
            } catch (e: Exception) { show("Printer: ${e.message}") }
        }
    }

    fun withCurrent(): Invoice = cur.copy(discount = discount, discountPct = pct)

    fun doPost() {
        val paid = when (cur.payType) {
            PayType.CASH -> total
            PayType.PARTIAL -> givenMoney.coerceIn(0, total)
            else -> 0L
        }
        scope.launch {
            val posted = Repo.post(withCurrent(), lines.toList(), if (cur.kind == InvoiceKind.TRANSFER) 0L else paid)
            inv = posted
            onTitle("Faktura ${posted.number}")
            show("Tassyklandy: ${posted.number} · ${posted.total.money()} TMT")
        }
    }

    fun tryPost() {
        when {
            lines.isEmpty() -> { show("Haryt goşuň"); return }
            cur.kind == InvoiceKind.TRANSFER && (cur.toWarehouseId.isBlank() || cur.toWarehouseId == cur.warehouseId) ->
                { show("Haýsy ambara geçirilýär? Başga ambar saýlaň"); return }
            cur.kind != InvoiceKind.TRANSFER && cur.payType != PayType.CASH && cur.customerId == null ->
                { show("Bölek töleg ýa-da ýazgy üçin müşderi saýlaň"); return }
            lines.any { it.qty <= 0 } -> { show("Mukdary 0 bolan setir bar"); return }
        }
        scope.launch {
            val warnings = mutableListOf<String>()
            // Ammarda ýeterlikmi?
            if (InvoiceKind.stockSign(cur.kind) < 0) {
                lines.groupBy { it.productId }.forEach { (pid, ls) ->
                    val need = ls.sumOf { it.qty * it.factor }
                    val have = App.dao.stockOf(pid, cur.warehouseId)
                    if (have < need) warnings += "${ls.first().productName}: ambarda ${have.qty()}, gerek ${need.qty()}"
                }
            }
            // Bergi çägi
            val c = customer
            if (c != null && c.creditLimit > 0 && cur.kind == InvoiceKind.SALE) {
                val paid = when (cur.payType) { PayType.CASH -> total; PayType.PARTIAL -> givenMoney.coerceIn(0, total); else -> 0L }
                val newDebt = (customerDebt ?: 0L) + total - paid
                if (newDebt > c.creditLimit) warnings += "Müşderiniň bergisi ${newDebt.money()} bolar (çägi ${c.creditLimit.money()})"
            }
            if (warnings.isEmpty()) doPost()
            else confirm = (warnings.joinToString("\n") + "\n\nDowam etmelimi?") to { doPost() }
        }
    }

    // ===================== EKRAN =====================
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wide = maxWidth > 900.dp
        Row(Modifier.fillMaxSize()) {
            // ---------- Çep: esasy bölek ----------
            Column(Modifier.weight(1f).fillMaxHeight().padding(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(Modifier.weight(1f).horizontalScroll(rememberScrollState())) {
                        ToolButton("➕", "Täze", { Tabs.open("invoice/new") })
                        ToolButton("🖨", "Çek", print, enabled = lines.isNotEmpty())
                        ToolButton("📄", "A4", {
                            val html = InvoiceHtml.build(withCurrent().copy(total = total), lines.toList(), customer)
                            Platform.shareFile("faktura_${cur.number}.html", "text/html", html.toByteArray(Charsets.UTF_8))
                        }, enabled = lines.isNotEmpty())
                        if (editable) {
                            ToolButton("💾", "Garalama", {
                                scope.launch { inv = Repo.saveDraft(withCurrent(), lines.toList()); show("Garalama saklandy") }
                            }, enabled = lines.isNotEmpty())
                            ToolButton("✅", "Tassykla", { tryPost() }, enabled = lines.isNotEmpty())
                        } else {
                            if (cur.status == InvoiceStatus.POSTED && Session.role == Role.ADMIN)
                                ToolButton("⛔", "Ýatyr", {
                                    confirm = "Faktura ýatyrylar: ambar we kassa hereketleri yzyna alnar." to {
                                        scope.launch { inv = Repo.cancel(cur); show("Ýatyryldy") }
                                    }
                                })
                        }
                        if (scan != null) ToolButton("📷", "Kamera", scan)
                    }
                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(start = 8.dp)) {
                        BigTotal("${total.money()} TMT")
                        if (!editable) Text(InvoiceStatus.title(cur.status), color = if (cur.status == InvoiceStatus.POSTED)
                            Color(0xFF1B7F3A) else Pal.RedX, fontWeight = FontWeight.Bold)
                        else if (isNightNow() && !cur.wholesale && cur.kind == InvoiceKind.SALE) Text("🌙 Gijeki baha", color = Pal.Muted)
                    }
                }
                Spacer(Modifier.height(4.dp))
                // Ştrihkod / Ady / Müşderi / Alynan pul
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Field(barcode, { barcode = it }, "Ştrihkod", modifier = Modifier.weight(1f).focusRequester(barcodeFocus),
                        onDone = {
                            val c = barcode.trim(); barcode = ""
                            if (c.isNotEmpty()) scope.launch { onBarcode(c) }
                            runCatching { barcodeFocus.requestFocus() }
                        })
                    Field(nameQuery, { nameQuery = it }, "Ady (Enter — gözle)", modifier = Modifier.weight(1f),
                        onDone = { picker = nameQuery.trim(); nameQuery = "" })
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Field(given, { given = it }, "Alynan pul (TMT)", modifier = Modifier.weight(1f), number = true)
                    Text("Gaýtargy:  ${if (givenMoney > 0 && change > 0) change.money() else "0"} TMT",
                        fontSize = 18.sp, modifier = Modifier.weight(1f).padding(start = 8.dp))
                }
                Spacer(Modifier.height(6.dp))
                if (!wide) Box(Modifier.fillMaxWidth()) {
                    var open by remember { mutableStateOf(false) }
                    Column {
                        Text((if (open) "▾ " else "▸ ") + "${InvoiceKind.title(cur.kind)} · ${PayType.title(cur.payType)} · " +
                            (customer?.name ?: "Müşderisiz"), color = Pal.Link, modifier = Modifier.clickable { open = !open }.padding(4.dp))
                        if (open) WhitePanel(Modifier.fillMaxWidth()) {
                            SidePanel(cur, editable, customer, customerDebt, pctText, { pctText = it }, warehouses, cashbooks,
                                onPick = { pick = it }, setInv = ::setInv)
                        }
                    }
                }
                // ---------- Tablisa ----------
                LinesTable(lines, products, units, editable, selected, onSelect = { selected = it }, wide = wide,
                    onChange = { idx, l -> lines[idx] = l },
                    onRemove = { idx -> lines.removeAt(idx); selected = -1 },
                    unitPriceFor = { p, u -> unitPrice(p, u, cur) },
                    modifier = Modifier.weight(1f))
            }
            // ---------- Sag panel ----------
            if (wide) WhitePanel(Modifier.width(310.dp).fillMaxHeight().padding(4.dp)) {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    SidePanel(cur, editable, customer, customerDebt, pctText, { pctText = it }, warehouses, cashbooks,
                        onPick = { pick = it }, setInv = ::setInv)
                }
            }
        }
    }

    // ===================== Penjireler =====================
    picker?.let { q ->
        ProductPicker(cur.warehouseId, initial = q, onPick = { p -> picker = null; scope.launch { addProduct(p, null) } },
            onDismiss = { picker = null })
    }
    when (pick) {
        Pick.KIND -> ChoiceDialog("Görnüşi", InvoiceKind.all, { InvoiceKind.title(it) },
            onPick = { k -> pick = null; setInv(cur.copy(kind = k), repriceToo = true) }, onDismiss = { pick = null })
        Pick.PAY -> ChoiceDialog("Tölegiň görnüşi", PayType.all, { PayType.title(it) },
            onPick = { t -> pick = null; setInv(cur.copy(payType = t)) }, onDismiss = { pick = null })
        Pick.CASHBOOK -> ChoiceDialog("Depder", cashbooks, { it.name },
            onPick = { c -> pick = null; setInv(cur.copy(cashbookId = c.id)) }, onDismiss = { pick = null })
        Pick.WAREHOUSE -> ChoiceDialog("Ambar", warehouses, { it.name },
            onPick = { w -> pick = null; setInv(cur.copy(warehouseId = w.id)) }, onDismiss = { pick = null })
        Pick.TO_WAREHOUSE -> ChoiceDialog("Haýsy ambara", warehouses.filter { it.id != cur.warehouseId }, { it.name },
            onPick = { w -> pick = null; setInv(cur.copy(toWarehouseId = w.id)) }, onDismiss = { pick = null })
        Pick.CUSTOMER -> SearchDialog("Müşderi", customers,
            listOf(Col("Ady", 3f) to { c: Customer -> c.name }, Col("Kody", 1.2f) to { c -> c.code }, Col("Telefon", 1.6f) to { c -> c.phone }),
            onPick = { c -> pick = null; setInv(cur.copy(customerId = c.id)) }, onDismiss = { pick = null })
        null -> {}
    }
    confirm?.let { (text, action) ->
        AlertDialog(onDismissRequest = { confirm = null }, title = { Text("Üns beriň") }, text = { Text(text) },
            confirmButton = { TextButton(onClick = { confirm = null; action() }) { Text("Hawa") } },
            dismissButton = { TextButton(onClick = { confirm = null }) { Text("Ýok") } })
    }
}

@Composable
private fun SidePanel(
    cur: Invoice, editable: Boolean, customer: Customer?, customerDebt: Long?,
    pctText: String, setPct: (String) -> Unit,
    warehouses: List<tm.sowdahasap.data.Warehouse>, cashbooks: List<tm.sowdahasap.data.Cashbook>,
    onPick: (Pick) -> Unit, setInv: (Invoice, Boolean) -> Unit,
) {
    val e: ((Pick) -> (() -> Unit)?) = { p -> if (editable) ({ onPick(p) }) else null }
    Column {
        Box(Modifier.fillMaxWidth().background(Pal.Header).padding(6.dp), contentAlignment = Alignment.Center) {
            Text(if (editable) "Goşulmadyk hereket" else InvoiceStatus.title(cur.status), color = Color.White, fontWeight = FontWeight.Bold)
        }
        Text("Kody", fontSize = 11.sp, color = Pal.Muted, modifier = Modifier.padding(top = 4.dp))
        Text(cur.number, fontSize = 15.sp, modifier = Modifier.fillMaxWidth().background(Pal.FieldBg).padding(6.dp))
        PanelLink("Görnüşi", InvoiceKind.title(cur.kind), e(Pick.KIND))
        if (cur.kind != InvoiceKind.TRANSFER) {
            PanelLink("Tölegiň görnüşi", PayType.title(cur.payType), e(Pick.PAY), band = Pal.GreenBand)
            PanelLink("Depder", cashbooks.firstOrNull { it.id == cur.cashbookId }?.name ?: "Nagt depder", e(Pick.CASHBOOK))
        }
        Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Senesi", fontSize = 13.sp, modifier = Modifier.width(70.dp))
            Text(cur.createdAt.dateTime(), fontSize = 13.sp)
        }
        if (cur.kind != InvoiceKind.TRANSFER) Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Skidka (-/+ %)", fontSize = 13.sp, modifier = Modifier.width(110.dp))
            if (editable) Field(pctText, setPct, "%", modifier = Modifier.weight(1f), number = true)
            else Text(cur.discountPct.qty() + " %  (" + cur.discount.money() + ")")
        }
        PanelLink("Ambar", warehouses.firstOrNull { it.id == cur.warehouseId }?.name ?: "?", e(Pick.WAREHOUSE))
        if (cur.kind == InvoiceKind.TRANSFER)
            PanelLink("Haýsy ambara", warehouses.firstOrNull { it.id == cur.toWarehouseId }?.name ?: "????", e(Pick.TO_WAREHOUSE))
        else {
            PanelLink("Müşderi", customer?.let { "[${it.code.ifBlank { "-" }}] ${it.name}" } ?: "Hiç kim (nagt)", e(Pick.CUSTOMER),
                onClear = if (editable && customer != null) ({ setInv(cur.copy(customerId = null), false) }) else null)
            if (customer != null && customerDebt != null)
                Text("Bergisi: ${customerDebt.money()} TMT", color = if (customerDebt > 0) Pal.RedX else Color(0xFF1B7F3A),
                    fontSize = 13.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            if (cur.kind == InvoiceKind.SALE) Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = cur.wholesale, enabled = editable, onCheckedChange = { setInv(cur.copy(wholesale = it), true) })
                Text("Opt bahasy bilen", fontSize = 13.sp)
            }
        }
        if (editable) Field(cur.note, { setInv(cur.copy(note = it), false) }, "Bellik")
        else if (cur.note.isNotBlank()) Text("Bellik: ${cur.note}")
        PanelLink("Ulanyjy", Session.user?.name ?: "", null)
        if (!editable && cur.kind != InvoiceKind.TRANSFER) Text("Tölenen: ${cur.paid.money()} TMT", fontSize = 13.sp)
    }
}

@Composable
private fun LinesTable(
    lines: List<InvoiceLine>,
    products: Map<String, Product>,
    units: Map<String, List<ProductUnit>>,
    editable: Boolean,
    selected: Int,
    onSelect: (Int) -> Unit,
    wide: Boolean,
    onChange: (Int, InvoiceLine) -> Unit,
    onRemove: (Int) -> Unit,
    unitPriceFor: (Product, ProductUnit?) -> Long,
    modifier: Modifier,
) {
    val cols = if (wide) listOf(Col("No:", 0.5f), Col("Haryt", 3f), Col("Sany", 1f, TextAlign.End), Col("Biriniň bahasy", 1.3f, TextAlign.End),
        Col("Jemi baha", 1.3f, TextAlign.End), Col("Ölçeg", 1f, TextAlign.Center), Col("Birindäki sany", 1.5f))
    else listOf(Col("Haryt", 2.2f), Col("Sany", 1f, TextAlign.End), Col("Baha", 1.2f, TextAlign.End), Col("Ölçeg", 1f, TextAlign.Center))
    Column(modifier.fillMaxWidth().background(Color(0xEEFFFFFF)).border(1.dp, Color(0x33000000))) {
        TableHeader(cols, lead = if (editable) 34 else 0)
        LazyColumn(Modifier.fillMaxSize()) {
            itemsIndexed(lines, key = { _, l -> l.id }) { idx, l ->
                val p = products[l.productId]
                TableRow(idx, selected = idx == selected, onClick = { onSelect(idx) }) {
                    if (editable) Box(Modifier.width(34.dp)) { RedX({ onRemove(idx) }) }
                    if (wide) Cell("${idx + 1}", 0.5f)
                    Cell(l.productName, if (wide) 3f else 2.2f)
                    NumCell(l.qty.qty(), 1f, editable) { v -> parseQty(v)?.let { onChange(idx, l.copy(qty = it)) } }
                    NumCell(l.price.moneyInput(), if (wide) 1.3f else 1.2f, editable) { v -> parseMoney(v)?.let { onChange(idx, l.copy(price = it)) } }
                    if (wide) Cell(Repo.lineTotal(l).money(), 1.3f, TextAlign.End, bold = true)
                    UnitCell(l, p, units[l.productId].orEmpty(), editable, 1f) { u ->
                        if (p != null) {
                            val f = u?.factor ?: 1.0
                            onChange(idx, l.copy(unitName = u?.unitName ?: p.unit, factor = f, price = unitPriceFor(p, u),
                                cost = (p.buyPrice * f).roundToLong()))
                        }
                    }
                    if (wide) Cell("1 ${l.unitName.ifBlank { p?.unit ?: "" }} = ${l.factor.qty()} ${p?.unit ?: ""}", 1.5f, color = Pal.Muted)
                }
            }
        }
    }
}

/** Tablisada üýtgedilýän san öýjügi */
@Composable
private fun RowScope.NumCell(value: String, weight: Float, editable: Boolean, onValue: (String) -> Unit) {
    if (!editable) { Cell(value, weight, TextAlign.End); return }
    var text by remember(value) { mutableStateOf(value) }
    BasicTextField(
        value = text,
        onValueChange = { text = it; onValue(it) },
        singleLine = true,
        textStyle = TextStyle(fontSize = 14.sp, textAlign = TextAlign.End, color = Pal.Text),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.weight(weight).padding(horizontal = 4.dp).background(Color(0x99FFFFFF)).padding(3.dp),
    )
}

@Composable
private fun RowScope.UnitCell(l: InvoiceLine, p: Product?, extra: List<ProductUnit>, editable: Boolean, weight: Float,
                              onPick: (ProductUnit?) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box(Modifier.weight(weight), contentAlignment = Alignment.Center) {
        val name = l.unitName.ifBlank { p?.unit ?: "" }
        Text(name + if (editable && extra.isNotEmpty()) " ▾" else "", fontSize = 14.sp, color = if (editable) Pal.Link else Pal.Text,
            modifier = if (editable && extra.isNotEmpty()) Modifier.clickable { open = true } else Modifier)
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            DropdownMenuItem(text = { Text(p?.unit ?: "esasy") }, onClick = { open = false; onPick(null) })
            extra.forEach { u ->
                DropdownMenuItem(text = { Text("${u.unitName} (= ${u.factor.qty()} ${p?.unit ?: ""})") }, onClick = { open = false; onPick(u) })
            }
        }
    }
}

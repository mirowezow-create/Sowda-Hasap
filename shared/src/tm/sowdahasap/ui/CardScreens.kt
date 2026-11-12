@file:OptIn(ExperimentalMaterial3Api::class)

package tm.sowdahasap.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import tm.sowdahasap.App
import tm.sowdahasap.Perm
import tm.sowdahasap.Role
import tm.sowdahasap.Session
import tm.sowdahasap.data.Barcode
import tm.sowdahasap.data.Customer
import tm.sowdahasap.data.CustomerBalance
import tm.sowdahasap.data.InvoiceKind
import tm.sowdahasap.data.ItemState
import tm.sowdahasap.data.MoveType
import tm.sowdahasap.data.Product
import tm.sowdahasap.data.ProductStock
import tm.sowdahasap.data.ProductUnit
import tm.sowdahasap.data.Repo
import tm.sowdahasap.data.UnitDef
import tm.sowdahasap.data.Warehouse
import tm.sowdahasap.platform.NeedPerm
import tm.sowdahasap.platform.Platform
import tm.sowdahasap.platform.rememberScanner
import tm.sowdahasap.platform.rememberWithPermission
import tm.sowdahasap.sync.Sms
import tm.sowdahasap.util.date
import tm.sowdahasap.util.money
import tm.sowdahasap.util.moneyInput
import tm.sowdahasap.util.newId
import tm.sowdahasap.util.now
import tm.sowdahasap.util.parseMoney
import tm.sowdahasap.util.parseQty
import tm.sowdahasap.util.qty

private const val ANY_STATE = "ANY"

private fun stateTitle(s: String) = if (s == ANY_STATE) "Statusyna garamazdan" else ItemState.title(s)
private fun statesOf(s: String) = if (s == ANY_STATE) ItemState.all else listOf(s)
private fun bandColor(s: String) = when (s) {
    ItemState.ACTIVE -> Pal.GreenBand; ItemState.HIDDEN -> Color(0xFFFFC857); ItemState.ERROR -> Color(0xFFFF8A80)
    else -> Color(0xFFB0BEC5)
}

/** "Haýsylar görünsin?" saýlawy */
@Composable
private fun StateFilter(state: String, onState: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box(Modifier.background(Pal.Header).padding(horizontal = 10.dp, vertical = 6.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Haýsylar görünsin?", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(stateTitle(state), color = Color.White, fontSize = 13.sp,
                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                modifier = Modifier.padding(top = 2.dp).clickable { open = true })
        }
    }
    if (open) ChoiceDialog("Haýsylar görünsin?", ItemState.all + ANY_STATE, ::stateTitle, { onState(it); open = false }, { open = false })
}

// ======================= HARYTLAR (sanaw) =======================

@Composable
fun ProductsListScreen() {
    var q by remember { mutableStateOf("") }
    var state by remember { mutableStateOf(ItemState.ACTIVE) }
    val flow = remember(q, state) { App.dao.productsWithStockIn(q, null, statesOf(state)) }
    val list by flow.collectAsState(initial = emptyList())
    val canEdit = Session.can(Perm.PRODUCTS_EDIT)
    var scanned by remember { mutableStateOf<String?>(null) }
    val scan = rememberScanner { scanned = it }
    LaunchedEffect(scanned) { scanned?.let { q = it; scanned = null } }

    Column(Modifier.fillMaxSize().padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (canEdit) ToolButton("➕", "Täze haryt", { Tabs.open("product/new") })
            if (scan != null) ToolButton("📷", "Barkod", scan)
            Field(q, { q = it }, "Gözleg (ady, kody, ştrihkod)", modifier = Modifier.weight(1f))
            StateFilter(state) { state = it }
        }
        Column(Modifier.weight(1f).fillMaxWidth().background(Color(0xEEFFFFFF)).border(1.dp, Color(0x33000000))) {
            TableHeader(listOf(Col("Kody", 1.1f), Col("Ady", 3f), Col("Ölçeg", 0.8f), Col("Satuw", 1f, TextAlign.End),
                Col("Opt", 1f, TextAlign.End), Col("Galyndy", 1.1f, TextAlign.End)))
            LazyColumn(Modifier.fillMaxSize()) {
                itemsIndexed(list, key = { _, ps -> ps.product.id }) { i, ps ->
                    val p = ps.product
                    val low = p.minStock > 0 && ps.stock <= p.minStock
                    TableRow(i, onClick = if (canEdit) ({ Tabs.open("product/${p.id}", p.name) }) else null) {
                        Cell(p.code, 1.1f, color = Pal.Muted)
                        Cell(p.name + if (p.state != ItemState.ACTIVE) "  (${ItemState.one(p.state)})" else "", 3f)
                        Cell(p.unit, 0.8f)
                        Cell(p.sellPrice.money(), 1f, TextAlign.End)
                        Cell(if (p.wholesalePrice > 0) p.wholesalePrice.money() else "", 1f, TextAlign.End, color = Pal.Muted)
                        Cell(ps.stock.qty() + if (low) " ⚠" else "", 1.1f, TextAlign.End, bold = true,
                            color = if (low || ps.stock < 0) Pal.RedX else Pal.Text)
                    }
                }
            }
        }
        MutedText("Tapyldy: ${list.size}")
    }
}

// ======================= HARYT KARTOÇKASY =======================

@Composable
fun ProductCardScreen(idArg: String, onTitle: (String) -> Unit, onClose: () -> Unit) {
    val scope = rememberCoroutineScope()
    val show = toast()
    val unitDefs by App.dao.units().collectAsState(initial = emptyList())
    var product by remember { mutableStateOf<Product?>(null) }
    val isNew = product == null
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("san") }
    var sell by remember { mutableStateOf("") }
    var buy by remember { mutableStateOf("") }
    var wholesale by remember { mutableStateOf("") }
    var night by remember { mutableStateOf("") }
    var minStock by remember { mutableStateOf("") }
    var startQty by remember { mutableStateOf("") }
    val extraUnits = remember { mutableStateListOf<ProductUnit>() }
    val codes = remember { mutableStateListOf<Barcode>() }
    var newCode by remember { mutableStateOf("") }
    var newCodeUnit by remember { mutableStateOf("") }
    var newUnitName by remember { mutableStateOf("") }
    var newUnitFactor by remember { mutableStateOf("") }
    var pick by remember { mutableStateOf(0) } // 1 esasy ölçeg, 2 goşmaça ölçeg, 3 ştrihkod ölçegi, 4 ýagdaý
    var stock by remember { mutableStateOf<Double?>(null) }
    val showCost = Session.can(Perm.VIEW_COST)
    val scan = rememberScanner { newCode = it }

    LaunchedEffect(idArg) {
        if (idArg != "new") {
            val p = App.dao.product(idArg) ?: return@LaunchedEffect
            product = p; code = p.code; name = p.name; unit = p.unit
            sell = p.sellPrice.moneyInput(); buy = p.buyPrice.moneyInput()
            wholesale = if (p.wholesalePrice > 0) p.wholesalePrice.moneyInput() else ""
            night = if (p.nightPrice > 0) p.nightPrice.moneyInput() else ""
            minStock = if (p.minStock > 0) p.minStock.qty() else ""
            extraUnits.clear(); extraUnits.addAll(App.dao.unitsOf(p.id))
            codes.clear()
            if (p.barcode.isNotBlank()) codes.add(Barcode(newId(), p.id, p.barcode, "", now()))
            codes.addAll(App.dao.barcodesOf(p.id))
            stock = App.dao.totalStock(p.id)
        } else {
            code = Repo.nextProductCode()
        }
    }

    fun addCode() {
        val c = newCode.trim()
        if (c.isEmpty()) return
        if (codes.any { it.code == c }) { show("Bu ştrihkod eýýäm bar"); return }
        scope.launch {
            val other = App.dao.productByBarcode(c)
            if (other != null && other.id != product?.id) { show("Bu ştrihkod başga harytda: ${other.name}"); return@launch }
            codes.add(Barcode(newId(), product?.id ?: "", c, newCodeUnit, now()))
            newCode = ""
        }
    }

    fun save() {
        val sp = parseMoney(sell)
        when {
            name.isBlank() -> { show("Harydyň adyny ýazyň"); return }
            sp == null -> { show("Satuw bahasy nädogry"); return }
        }
        scope.launch {
            val base = product ?: Product(newId(), "", updatedAt = now())
            val p = base.copy(
                name = name.trim(), code = code.trim(), unit = unit,
                sellPrice = sp!!, buyPrice = if (showCost) parseMoney(buy) ?: 0L else base.buyPrice,
                wholesalePrice = parseMoney(wholesale) ?: 0L, nightPrice = parseMoney(night) ?: 0L,
                minStock = parseQty(minStock) ?: 0.0,
            )
            Repo.saveProductFull(p, extraUnits.toList(), codes.toList())
            val sq = parseQty(startQty)
            if (product == null && sq != null && sq != 0.0) Repo.addMove(p.id, App.prefs.defaultWarehouse, sq, MoveType.IN, "Başlangyç")
            product = p
            onTitle(p.name)
            show("Ýatda saklandy")
        }
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wide = maxWidth > 900.dp
        Column(Modifier.fillMaxSize().padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ToolButton("💾", "Sakla", { save() })
                ToolButton("➕", "Täze", { Tabs.open("product/new") })
                if (!isNew) Text("  Galyndy: ${stock?.qty() ?: "…"} $unit", fontSize = 16.sp, color = Pal.TabActive)
            }
            val col1: @Composable ColumnScope.() -> Unit = {
                product?.let { p -> StateBand(ItemState.one(p.state), bandColor(p.state)) { pick = 4 } }
                    ?: StateBand("Täze haryt", Pal.GreenBand) {}
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Field(code, { code = it }, "Kody", modifier = Modifier.weight(1f))
                    TextButton(onClick = { scope.launch { code = Repo.nextProductCode() } }) { Text("TÄZE KOD") }
                }
                Field(name, { name = it }, "Harydyň ady *")
                PanelLink("Esasy ölçeg", unit, { pick = 1 })
                SmallTitle("Goşmaça ölçegler")
                MutedText("Bir goşmaça ölçegde näçe esasy ölçeg bar? (mysal: pacga = 20 $unit)")
                extraUnits.toList().forEachIndexed { i, u ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 3.dp)) {
                        RedX({ extraUnits.removeAt(i) })
                        Text("  ${u.unitName}", color = Pal.Link, modifier = Modifier.weight(1f))
                        Text("= ${u.factor.qty()} $unit", modifier = Modifier.weight(1f))
                        Field(if (u.price > 0) u.price.moneyInput() else "", { v -> extraUnits[i] = u.copy(price = parseMoney(v) ?: 0L) },
                            "Bahasy", modifier = Modifier.width(110.dp), number = true)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    LinkText(newUnitName.ifBlank { "ölçeg saýla" }, { pick = 2 }, Modifier.weight(1f))
                    Field(newUnitFactor, { newUnitFactor = it }, "Näçe $unit", modifier = Modifier.weight(1f), number = true)
                    TextButton(onClick = {
                        val f = parseQty(newUnitFactor)
                        when {
                            newUnitName.isBlank() -> show("Ölçeg saýlaň")
                            newUnitName == unit -> show("Esasy ölçeg bilen birmeňzeş")
                            f == null || f <= 0 -> show("Sany nädogry")
                            extraUnits.any { it.unitName == newUnitName } -> show("Bu ölçeg eýýäm bar")
                            else -> { extraUnits.add(ProductUnit(newId(), product?.id ?: "", newUnitName, f, 0, now())); newUnitName = ""; newUnitFactor = "" }
                        }
                    }) { Text("➕") }
                }
            }
            val col2: @Composable ColumnScope.() -> Unit = {
                Field(sell, { sell = it }, "Esasy satyş bahasy (TMT) *", number = true)
                if (showCost) Field(buy, { buy = it }, "Esasy alyş bahasy (TMT)", number = true)
                Field(wholesale, { wholesale = it }, "Opt bahasy (boş = satyş bahasy)", number = true)
                Field(night, { night = it }, "🌙 Gijeki baha (boş = adaty)", number = true)
                Field(minStock, { minStock = it }, "Ambarda azyndan näçe haryt bolsun?", number = true)
                if (isNew) Field(startQty, { startQty = it }, "Başlangyç mukdar (esasy ambara)", number = true)
                if (showCost) {
                    val s = parseMoney(sell) ?: 0; val b = parseMoney(buy) ?: 0
                    if (b > 0) MutedText("Göterim: ${(((s - b) * 100.0) / b).let { String.format(java.util.Locale.US, "%.1f", it) }} %")
                }
            }
            val col3: @Composable ColumnScope.() -> Unit = {
                SmallTitle("Ştrihkodlary")
                Column(Modifier.fillMaxWidth().background(Color(0xEEFFFFFF)).padding(6.dp).heightIn(min = 80.dp)) {
                    codes.toList().forEachIndexed { i, c ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 3.dp)) {
                            RedX({ codes.removeAt(i) })
                            Text("  ${c.code}", modifier = Modifier.weight(1f))
                            Text(c.unitName.ifBlank { unit }, color = Pal.Muted, fontSize = 13.sp)
                        }
                    }
                    if (codes.isEmpty()) MutedText("Ştrihkod ýok")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Field(newCode, { newCode = it }, "Täze ştrihkod", modifier = Modifier.weight(1f), onDone = { addCode() })
                    if (scan != null) IconText("📷", scan)
                    TextButton(onClick = { addCode() }) { Text("➕") }
                }
                if (extraUnits.isNotEmpty()) Row(verticalAlignment = Alignment.CenterVertically) {
                    MutedText("Ştrihkodyň ölçegi: ")
                    LinkText(newCodeUnit.ifBlank { unit }, { pick = 3 })
                }
            }
            if (wide) Row(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Column(Modifier.weight(1f), content = col1)
                Column(Modifier.weight(1f), content = col2)
                Column(Modifier.weight(1f), content = col3)
            } else Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                col1(); col2(); col3()
            }
        }
    }

    when (pick) {
        1 -> ChoiceDialog("Esasy ölçeg", unitDefs.map { it.name }, { it }, { unit = it; pick = 0 }, { pick = 0 })
        2 -> ChoiceDialog("Goşmaça ölçeg", unitDefs.map { it.name }.filter { it != unit }, { it }, { newUnitName = it; pick = 0 }, { pick = 0 })
        3 -> ChoiceDialog("Ştrihkodyň ölçegi", listOf(unit) + extraUnits.map { it.unitName }, { it },
            { newCodeUnit = if (it == unit) "" else it; pick = 0 }, { pick = 0 })
        4 -> ChoiceDialog("Ýagdaýy", ItemState.all, { ItemState.one(it) }, { st ->
            pick = 0
            val p = product ?: return@ChoiceDialog
            scope.launch {
                val err = Repo.setProductState(p, st)
                if (err != null) show(err) else { product = p.copy(state = st); show("Ýagdaýy: ${ItemState.one(st)}") }
            }
        }, { pick = 0 })
    }
}

// ======================= MÜŞDERILER (sanaw) =======================

@Composable
fun CustomersListScreen() {
    var q by remember { mutableStateOf("") }
    var state by remember { mutableStateOf(ItemState.ACTIVE) }
    var onlyDebt by remember { mutableStateOf(false) }
    val flow = remember(q, state) { App.dao.customersWithDebtIn(q, statesOf(state)) }
    val all by flow.collectAsState(initial = emptyList())
    val list = if (onlyDebt) all.filter { it.debt != 0L } else all
    Column(Modifier.fillMaxSize().padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ToolButton("➕", "Täze", { Tabs.open("customer/new") })
            ToolButton(if (onlyDebt) "☑" else "☐", "Diňe bergili", { onlyDebt = !onlyDebt })
            Field(q, { q = it }, "Gözleg (ady, kody, telefon)", modifier = Modifier.weight(1f))
            StateFilter(state) { state = it }
        }
        Column(Modifier.weight(1f).fillMaxWidth().background(Color(0xEEFFFFFF)).border(1.dp, Color(0x33000000))) {
            TableHeader(listOf(Col("Kody", 1f), Col("Ady", 3f), Col("Telefon", 1.5f), Col("Algy / Bergi", 1.4f, TextAlign.End)))
            LazyColumn(Modifier.fillMaxSize()) {
                itemsIndexed(list, key = { _, c -> c.customer.id }) { i, cb ->
                    val c = cb.customer
                    TableRow(i, onClick = { Tabs.open("customer/${c.id}", c.name) }) {
                        Cell(c.code, 1f, color = Pal.Muted)
                        Cell(c.name + if (c.state != ItemState.ACTIVE) "  (${ItemState.one(c.state)})" else "", 3f)
                        Cell(c.phone, 1.5f)
                        Cell(cb.debt.money(), 1.4f, TextAlign.End, bold = true, color = debtColor(cb.debt))
                    }
                }
            }
        }
        val toUs = list.filter { it.debt > 0 }.sumOf { it.debt }
        val fromUs = list.filter { it.debt < 0 }.sumOf { -it.debt }
        Text("Bize bergili: ${toUs.money()} TMT     Biz bergili: ${fromUs.money()} TMT", fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(4.dp))
    }
}

private fun debtColor(d: Long) = when { d > 0 -> Pal.RedX; d < 0 -> Color(0xFF1565C0); else -> Color(0xFF1B7F3A) }

// ======================= MÜŞDERI KARTOÇKASY =======================

private class Ledger(val time: Long, val title: String, val plus: Long, val minus: Long, val invoiceId: String?)

@Composable
fun CustomerCardScreen(idArg: String, onTitle: (String) -> Unit) {
    val scope = rememberCoroutineScope()
    val show = toast()
    var customer by remember { mutableStateOf<Customer?>(null) }
    val id = customer?.id ?: idArg.takeIf { it != "new" }
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var phone2 by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var limit by remember { mutableStateOf("") }
    var days by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var payDialog by remember { mutableStateOf(0) } // +1 töleg al, -1 pul ber
    var pickState by remember { mutableStateOf(false) }

    LaunchedEffect(idArg) {
        if (idArg != "new") App.dao.customer(idArg)?.let { c ->
            customer = c; code = c.code; name = c.name; phone = c.phone; phone2 = c.phone2; address = c.address
            limit = if (c.creditLimit > 0) c.creditLimit.moneyInput() else ""; days = if (c.payDays > 0) c.payDays.toString() else ""
            note = c.note
        } else code = Repo.nextCustomerCode()
    }
    val debt by remember(id) { if (id == null) kotlinx.coroutines.flow.flowOf(0L) else App.dao.debtOf(id) }.collectAsState(initial = 0L)
    val invoices by remember(id) { if (id == null) kotlinx.coroutines.flow.flowOf(emptyList()) else App.dao.postedInvoicesOf(id) }
        .collectAsState(initial = emptyList())
    val payments by remember(id) { if (id == null) kotlinx.coroutines.flow.flowOf(emptyList()) else App.dao.paymentsOf(id) }
        .collectAsState(initial = emptyList())
    val ledger = (invoices.map { inv ->
        val signed = when (inv.kind) { InvoiceKind.PURCHASE, InvoiceKind.RETURN_IN -> -inv.total; else -> inv.total }
        Ledger(inv.createdAt, "${InvoiceKind.title(inv.kind)} ${inv.number}", if (signed > 0) signed else 0, if (signed < 0) -signed else 0, inv.id)
    } + payments.map { p ->
        Ledger(p.createdAt, if (p.amount >= 0) "Töleg aldyk ${p.note}" else "Pul berdik ${p.note}",
            if (p.amount < 0) -p.amount else 0, if (p.amount > 0) p.amount else 0, null)
    }).sortedBy { it.time }

    val sendSms = rememberWithPermission(NeedPerm.SMS) {
        val c = customer ?: return@rememberWithPermission
        try { Platform.sendSms(Sms.normalizePhone(c.phone), Sms.text(CustomerBalance(c, debt))); show("SMS ugradyldy") }
        catch (e: Exception) { show("SMS: ${e.message}") }
    }

    fun save() {
        if (name.isBlank()) { show("Adyny ýazyň"); return }
        scope.launch {
            val base = customer ?: Customer(newId(), "", updatedAt = now())
            val c = base.copy(code = code.trim(), name = name.trim(), phone = phone.trim(), phone2 = phone2.trim(),
                address = address.trim(), creditLimit = parseMoney(limit) ?: 0L, payDays = days.toIntOrNull() ?: 0, note = note.trim())
            Repo.saveCustomer(c)
            customer = c
            onTitle(c.name)
            show("Ýatda saklandy")
        }
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wide = maxWidth > 900.dp
        val left: @Composable ColumnScope.() -> Unit = {
            customer?.let { c -> StateBand(ItemState.one(c.state), bandColor(c.state)) { pickState = true } }
                ?: StateBand("Täze müşderi", Pal.GreenBand) {}
            Row(verticalAlignment = Alignment.CenterVertically) {
                Field(code, { code = it }, "Kody", modifier = Modifier.weight(1f))
                TextButton(onClick = { scope.launch { code = Repo.nextCustomerCode() } }) { Text("TÄZE KOD") }
            }
            Field(name, { name = it }, "Ady *")
            Field(phone, { phone = it }, "Tel.", phone = true)
            Field(phone2, { phone2 = it }, "Jübi tel.", phone = true)
            Field(address, { address = it }, "Umumy adresi")
            Field(limit, { limit = it }, "Iň köp näçe bergisi bolup biler? (TMT, boş = çäksiz)", number = true)
            Field(days, { days = it }, "Tölegiň wagty näçe günde geçýär?", number = true)
            Field(note, { note = it }, "Bellik")
        }
        val right: @Composable ColumnScope.() -> Unit = {
            WhitePanel(Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(when { debt > 0 -> "Bize bergili"; debt < 0 -> "Biz bergili"; else -> "Hasaplaşyk deň" }, color = Pal.Muted)
                    Text("${kotlin.math.abs(debt).money()} TMT", fontSize = 28.sp, color = debtColor(debt))
                    if (customer != null && customer!!.creditLimit > 0) MutedText("Bergi çägi: ${customer!!.creditLimit.money()} TMT")
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (customer != null && Session.can(Perm.PAYMENTS)) {
                            ToolButton("💵", "Töleg al", { payDialog = 1 })
                            ToolButton("💸", "Pul ber", { payDialog = -1 })
                        }
                        if (customer != null) ToolButton("🧾", "Faktura", { Tabs.open("invoice/new") })
                        if (Platform.supportsSms && phone.isNotBlank()) {
                            ToolButton("📞", "Jaň", { Platform.dial(Sms.normalizePhone(phone)) })
                            if (debt > 0 && Session.can(Perm.SMS)) ToolButton("✉️", "SMS", sendSms)
                        }
                    }
                }
            }
            SmallTitle("Bir müşderiniň hereketleri")
            Column(Modifier.fillMaxWidth().background(Color(0xEEFFFFFF)).border(1.dp, Color(0x33000000))) {
                TableHeader(listOf(Col("Senesi", 1.1f), Col("Resminama", 2.4f), Col("Bergi +", 1f, TextAlign.End),
                    Col("Töleg −", 1f, TextAlign.End), Col("Galyndy", 1.1f, TextAlign.End)))
                var run = 0L
                val rows = ledger.map { l -> run += l.plus - l.minus; l to run }.reversed()
                rows.forEachIndexed { i, (l, bal) ->
                    TableRow(i, onClick = l.invoiceId?.let { iid -> { Tabs.open("invoice/$iid") } }) {
                        Cell(l.time.date(), 1.1f)
                        Cell(l.title, 2.4f)
                        Cell(if (l.plus > 0) l.plus.money() else "", 1f, TextAlign.End, color = Pal.RedX)
                        Cell(if (l.minus > 0) l.minus.money() else "", 1f, TextAlign.End, color = Color(0xFF1B7F3A))
                        Cell(bal.money(), 1.1f, TextAlign.End, bold = true)
                    }
                }
                if (rows.isEmpty()) Text("Hereket ýok", Modifier.padding(8.dp), color = Pal.Muted)
            }
        }
        Column(Modifier.fillMaxSize().padding(8.dp)) {
            Row { ToolButton("💾", "Sakla", { save() }); ToolButton("➕", "Täze", { Tabs.open("customer/new") }) }
            if (wide) Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), content = left)
                Column(Modifier.weight(1.4f).verticalScroll(rememberScrollState()), content = right)
            } else Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) { left(); right() }
        }
    }

    if (payDialog != 0) {
        val sign = payDialog
        var amount by remember { mutableStateOf("") }
        var pnote by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { payDialog = 0 },
            title = { Text(if (sign > 0) "Töleg al (müşderi pul berdi)" else "Pul ber (müşderä pul berdik)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Häzirki: ${debt.money()} TMT")
                    Field(amount, { amount = it }, "Möçber (TMT)", number = true)
                    Field(pnote, { pnote = it }, "Bellik (nagt, kart...)")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val a = parseMoney(amount)
                    val cid = customer?.id
                    if (a == null || a <= 0 || cid == null) show("Möçber nädogry") else {
                        payDialog = 0
                        scope.launch { Repo.addPayment(cid, a * sign, pnote.trim()); show("Ýazyldy") }
                    }
                }) { Text("Ýaz") }
            },
            dismissButton = { TextButton(onClick = { payDialog = 0 }) { Text("Goýbolsun") } },
        )
    }
    if (pickState) ChoiceDialog("Ýagdaýy", ItemState.all, { ItemState.one(it) }, { st ->
        pickState = false
        val c = customer ?: return@ChoiceDialog
        scope.launch {
            val err = Repo.setCustomerState(c, st)
            if (err != null) show(err) else { customer = c.copy(state = st); show("Ýagdaýy: ${ItemState.one(st)}") }
        }
    }, { pickState = false })
}

// ======================= ÖLÇEGLER =======================

@Composable
fun UnitsScreen() {
    val scope = rememberCoroutineScope()
    val units by App.dao.units().collectAsState(initial = emptyList())
    var name by remember { mutableStateOf("") }
    var noteT by remember { mutableStateOf("") }
    var edit by remember { mutableStateOf<UnitDef?>(null) }
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Field(name, { name = it }, "Ölçegiň ady", modifier = Modifier.weight(1f))
            Field(noteT, { noteT = it }, "Bellik", modifier = Modifier.weight(1f))
            ToolButton("➕", "Goş", {
                val n = name.trim()
                if (n.isNotEmpty() && units.none { it.name.equals(n, true) }) scope.launch {
                    App.dao.upsertUnit(UnitDef(newId(), n, noteT.trim(), now())); name = ""; noteT = ""
                }
            })
        }
        Column(Modifier.weight(1f).fillMaxWidth().background(Color(0xEEFFFFFF))) {
            TableHeader(listOf(Col("Ady", 2f), Col("Bellik", 3f)))
            LazyColumn {
                itemsIndexed(units, key = { _, u -> u.id }) { i, u ->
                    TableRow(i, onClick = { edit = u }) { Cell(u.name, 2f); Cell(u.note, 3f, color = Pal.Muted) }
                }
            }
        }
        MutedText("Ölçegiň adyny üýtgetseňiz, köne harytlarda öňki ady galýar.")
    }
    edit?.let { u -> RenameDialog("Ölçeg", u.name, onDismiss = { edit = null }) { n ->
        edit = null; scope.launch { App.dao.upsertUnit(u.copy(name = n, updatedAt = now())) }
    } }
}

// ======================= AMBARLAR =======================

@Composable
fun WarehousesScreen() {
    val scope = rememberCoroutineScope()
    val list by App.dao.warehouses().collectAsState(initial = emptyList())
    var name by remember { mutableStateOf("") }
    var edit by remember { mutableStateOf<Warehouse?>(null) }
    var def by remember { mutableStateOf(App.prefs.defaultWarehouse) }
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Field(name, { name = it }, "Täze ambaryň ady", modifier = Modifier.weight(1f))
            ToolButton("➕", "Goş", {
                val n = name.trim()
                if (n.isNotEmpty()) scope.launch { App.dao.upsertWarehouse(Warehouse(newId(), n, now())); name = "" }
            })
        }
        Column(Modifier.weight(1f).fillMaxWidth().background(Color(0xEEFFFFFF))) {
            TableHeader(listOf(Col("Ady", 3f), Col("Şu kompýuterde esasy", 1.5f)))
            LazyColumn {
                itemsIndexed(list, key = { _, w -> w.id }) { i, w ->
                    TableRow(i, selected = w.id == def, onClick = { edit = w }) {
                        Cell(w.name, 3f)
                        Box(Modifier.weight(1.5f)) {
                            LinkText(if (w.id == def) "✔ esasy" else "esasy et", { def = w.id; App.prefs.defaultWarehouse = w.id })
                        }
                    }
                }
            }
        }
    }
    edit?.let { w -> RenameDialog("Ambar", w.name, onDismiss = { edit = null }) { n ->
        edit = null; scope.launch { App.dao.upsertWarehouse(w.copy(name = n, updatedAt = now())) }
    } }
}

@file:OptIn(ExperimentalMaterial3Api::class)

package tm.sowdahasap.ui

import android.Manifest
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import tm.sowdahasap.App
import tm.sowdahasap.Session
import tm.sowdahasap.data.Customer
import tm.sowdahasap.data.Invoice
import tm.sowdahasap.data.InvoiceLine
import tm.sowdahasap.data.InvoiceStatus
import tm.sowdahasap.data.Product
import tm.sowdahasap.data.Repo
import tm.sowdahasap.print.BtPrinter
import tm.sowdahasap.print.PdfInvoice
import tm.sowdahasap.print.Receipts
import tm.sowdahasap.util.dateTime
import tm.sowdahasap.util.money
import tm.sowdahasap.util.moneyInput
import tm.sowdahasap.util.newId
import tm.sowdahasap.util.now
import tm.sowdahasap.util.parseMoney
import tm.sowdahasap.util.parseQty
import tm.sowdahasap.util.qty
import tm.sowdahasap.util.shareFile

val btPerms: Array<String> =
    if (Build.VERSION.SDK_INT >= 31) arrayOf(Manifest.permission.BLUETOOTH_CONNECT) else emptyArray()

// ======================= FAKTURALAR SANAWY =======================

@Composable
fun InvoicesScreen(onBack: () -> Unit, open: (String) -> Unit) {
    var status by remember { mutableStateOf<String?>(null) }
    val flow = remember(status) { App.dao.invoices(status) }
    val list by flow.collectAsState(initial = emptyList())

    Screen("Fakturalar", onBack, fab = {
        FloatingActionButton(onClick = { open("new") }) { Icon(Icons.Default.Add, "Täze satuw") }
    }) { pad ->
        Column(Modifier.padding(pad).padding(horizontal = 12.dp)) {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(selected = status == null, onClick = { status = null }, label = { Text("Ählisi") })
                listOf(InvoiceStatus.DRAFT, InvoiceStatus.POSTED, InvoiceStatus.CANCELLED).forEach { s ->
                    FilterChip(selected = status == s, onClick = { status = s }, label = { Text(InvoiceStatus.title(s)) })
                }
            }
            LazyColumn(Modifier.fillMaxSize()) {
                items(list, key = { it.invoice.id }) { r ->
                    val i = r.invoice
                    ListItem(
                        headlineContent = { Text("${i.number} · ${r.customerName ?: "Nagt satuw"}") },
                        supportingContent = { Text("${i.createdAt.dateTime()} · ${InvoiceStatus.title(i.status)}") },
                        trailingContent = {
                            Text(i.total.money(), style = MaterialTheme.typography.titleMedium,
                                color = when (i.status) {
                                    InvoiceStatus.CANCELLED -> MaterialTheme.colorScheme.outline
                                    InvoiceStatus.DRAFT -> MaterialTheme.colorScheme.secondary
                                    else -> MaterialTheme.colorScheme.onSurface
                                })
                        },
                        modifier = Modifier.clickable { open(i.id) },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

// ======================= FAKTURA =======================

@Composable
fun InvoiceEditScreen(idArg: String, onBack: () -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val show = toast()
    val warehouses by App.dao.warehouses().collectAsState(initial = emptyList())
    val customers by App.dao.customers().collectAsState(initial = emptyList())

    var inv by remember { mutableStateOf<Invoice?>(null) }
    val lines = remember { mutableStateListOf<InvoiceLine>() }
    var discountText by remember { mutableStateOf("") }
    var picking by remember { mutableStateOf(false) }
    var customerPick by remember { mutableStateOf(false) }
    var editLine by remember { mutableStateOf<InvoiceLine?>(null) }
    var postDialog by remember { mutableStateOf(false) }
    var confirmCancel by remember { mutableStateOf(false) }
    var stockWarn by remember { mutableStateOf<String?>(null) }
    var scanned by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(idArg) {
        if (idArg == "new") {
            val t = now()
            inv = Invoice(newId(), Repo.newInvoiceNumber(), null, App.prefs.defaultWarehouse,
                userId = Session.userId, createdAt = t, updatedAt = t)
        } else {
            inv = App.dao.invoice(idArg)
            lines.clear(); lines.addAll(App.dao.linesOf(idArg))
            discountText = inv?.discount?.takeIf { it > 0 }?.moneyInput() ?: ""
        }
    }

    val current = inv
    if (current == null) { Centered { Text("...") }; return }
    val editable = current.status == InvoiceStatus.DRAFT
    val discount = parseMoney(discountText) ?: 0L
    val total = Repo.calcTotal(lines, discount)
    val customer = customers.firstOrNull { it.id == current.customerId }

    fun addProduct(p: Product) {
        val idx = lines.indexOfFirst { it.productId == p.id }
        if (idx >= 0) lines[idx] = lines[idx].copy(qty = lines[idx].qty + 1)
        else lines.add(InvoiceLine(newId(), current.id, p.id, p.name, 1.0, p.sellPrice, p.buyPrice, now()))
    }

    val scan = rememberScanner { scanned = it }
    LaunchedEffect(scanned) {
        val code = scanned ?: return@LaunchedEffect
        val p = App.dao.productByBarcode(code)
        if (p != null) { addProduct(p); show("+1 ${p.name}") } else show("Barkod tapylmady: $code")
        scanned = null
    }

    val print = rememberWithPermissions(btPerms) {
        scope.launch {
            try {
                val c = current.customerId?.let { App.dao.customer(it) }
                val debt = current.customerId?.let { id ->
                    App.dao.debtors().firstOrNull { it.customer.id == id }?.debt
                }
                BtPrinter.send(ctx, App.prefs.printerAddress, Receipts.invoiceReceipt(current, lines.toList(), c, debt))
                show("Çap edildi")
            } catch (e: Exception) { show("Printer: ${e.message}") }
        }
    }

    suspend fun checkStock(): String? {
        val short = mutableListOf<String>()
        lines.groupBy { it.productId }.forEach { (pid, ls) ->
            val need = ls.sumOf { it.qty }
            val have = App.dao.stockOf(pid, current.warehouseId)
            if (have < need) short += "${ls.first().productName}: bar ${have.qty()}, gerek ${need.qty()}"
        }
        return if (short.isEmpty()) null else short.joinToString("\n")
    }

    Screen(
        "Faktura ${current.number}", onBack,
        actions = {
            if (!editable) {
                TextButton(onClick = print) { Text("🖨", style = MaterialTheme.typography.titleLarge) }
                TextButton(onClick = {
                    val f = PdfInvoice.create(ctx, current, lines.toList(), customer)
                    shareFile(ctx, f, "application/pdf")
                }) { Text("PDF", color = MaterialTheme.colorScheme.onPrimary) }
            }
        },
    ) { pad ->
        Column(Modifier.padding(pad).padding(12.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(InvoiceStatus.title(current.status) + " · " + current.createdAt.dateTime(),
                style = MaterialTheme.typography.bodySmall)
            OutlinedButton(enabled = editable, onClick = { customerPick = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Müşderi: " + (customer?.name ?: "Nagt satuw (müşderisiz)"))
            }
            if (editable) Select("Ammar", warehouses, warehouses.firstOrNull { it.id == current.warehouseId },
                { it.name }, { inv = current.copy(warehouseId = it.id) })

            if (editable) Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { picking = true }) { Icon(Icons.Default.Add, null); Text(" Haryt") }
                OutlinedButton(onClick = scan) { Icon(Icons.Default.Search, null); Text(" Barkod") }
            }

            LazyColumn(Modifier.weight(1f)) {
                items(lines, key = { it.id }) { l ->
                    ListItem(
                        headlineContent = { Text(l.productName) },
                        supportingContent = { Text("${l.qty.qty()} × ${l.price.money()}") },
                        trailingContent = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(Repo.lineTotal(l).money(), style = MaterialTheme.typography.titleMedium)
                                if (editable) IconButton(onClick = { lines.remove(l) }) { Icon(Icons.Default.Close, "Aýyr") }
                            }
                        },
                        modifier = Modifier.clickable(enabled = editable) { editLine = l },
                    )
                    HorizontalDivider()
                }
            }

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    if (editable) Field(discountText, { discountText = it }, "Arzanladyş (TMT)", number = true)
                    else if (current.discount > 0) InfoRow("Arzanladyş", "-" + current.discount.money())
                    InfoRow("JEMI", total.money() + " TMT")
                }
            }

            if (editable) Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {
                    scope.launch {
                        inv = Repo.saveDraft(current.copy(discount = discount), lines.toList())
                        show("Garalama saklandy")
                    }
                }) { Text("Garalama") }
                Button(modifier = Modifier.weight(1f), enabled = lines.isNotEmpty(), onClick = {
                    scope.launch { stockWarn = checkStock(); if (stockWarn == null) postDialog = true }
                }) { Text("Tassykla") }
            } else if (current.status == InvoiceStatus.POSTED && Session.role == tm.sowdahasap.Role.ADMIN) {
                OutlinedButton(onClick = { confirmCancel = true }, modifier = Modifier.fillMaxWidth()) { Text("Fakturany ýatyr") }
            }
        }
    }

    if (picking) ProductPicker(current.warehouseId, onPick = { addProduct(it); picking = false }, onDismiss = { picking = false })

    if (customerPick) CustomerPicker(customers, onPick = { inv = current.copy(customerId = it?.id); customerPick = false },
        onDismiss = { customerPick = false })

    editLine?.let { l ->
        LineDialog(l, onSave = { nl ->
            val i = lines.indexOfFirst { it.id == l.id }
            if (i >= 0) lines[i] = nl
            editLine = null
        }, onDismiss = { editLine = null })
    }

    stockWarn?.let { w ->
        ConfirmDialog("Ammarda ýeterlik ýok", "$w\n\nDowam etmelimi?", onConfirm = { postDialog = true },
            onDismiss = { stockWarn = null })
    }

    if (postDialog) {
        var paidText by remember { mutableStateOf(if (current.customerId == null) total.moneyInput() else "") }
        AlertDialog(
            onDismissRequest = { postDialog = false },
            title = { Text("Tassyklamak: ${total.money()} TMT") },
            text = {
                Column {
                    if (current.customerId != null) {
                        Text("Müşderi şu wagt näçe töledi? Galany bergi bolup ýazylar.")
                        Field(paidText, { paidText = it }, "Tölenen (TMT)", number = true)
                        Row {
                            TextButton(onClick = { paidText = total.moneyInput() }) { Text("Doly") }
                            TextButton(onClick = { paidText = "0" }) { Text("Bergä") }
                        }
                    } else Text("Nagt satuw — doly tölendi hasaplanýar.")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    postDialog = false
                    val paid = (parseMoney(paidText) ?: 0L).coerceIn(0L, total)
                    scope.launch {
                        inv = Repo.post(current.copy(discount = discount), lines.toList(), paid)
                        show("Tassyklandy")
                    }
                }) { Text("Tassykla") }
            },
            dismissButton = { TextButton(onClick = { postDialog = false }) { Text("Goýbolsun") } },
        )
    }

    if (confirmCancel) ConfirmDialog("Ýatyrmak", "Faktura ýatyrylar, harytlar ammara gaýdyp geler, tölegler pozular.",
        onConfirm = { scope.launch { inv = Repo.cancel(current); show("Ýatyryldy") } },
        onDismiss = { confirmCancel = false })
}

@Composable
private fun LineDialog(l: InvoiceLine, onSave: (InvoiceLine) -> Unit, onDismiss: () -> Unit) {
    var q by remember { mutableStateOf(l.qty.qty()) }
    var p by remember { mutableStateOf(l.price.moneyInput()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(l.productName) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Field(q, { q = it }, "Mukdar", number = true)
                Field(p, { p = it }, "Baha (TMT)", number = true)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val qv = parseQty(q); val pv = parseMoney(p)
                if (qv != null && qv > 0 && pv != null && pv >= 0) onSave(l.copy(qty = qv, price = pv))
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Goýbolsun") } },
    )
}

@Composable
fun CustomerPicker(customers: List<Customer>, onPick: (Customer?) -> Unit, onDismiss: () -> Unit) {
    var q by remember { mutableStateOf("") }
    val filtered = customers.filter { q.isBlank() || it.name.contains(q, true) || it.phone.contains(q) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Müşderi") },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Ýap") } },
        text = {
            Column {
                Field(q, { q = it }, "Gözleg")
                TextButton(onClick = { onPick(null) }) { Text("— Nagt satuw (müşderisiz)") }
                LazyColumn(Modifier.width(320.dp).height(360.dp)) {
                    items(filtered, key = { it.id }) { c ->
                        ListItem(headlineContent = { Text(c.name) }, supportingContent = { Text(c.phone) },
                            modifier = Modifier.clickable { onPick(c) })
                    }
                }
            }
        },
    )
}

@file:OptIn(ExperimentalMaterial3Api::class)

package tm.sowdahasap.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import tm.sowdahasap.App
import tm.sowdahasap.Perm
import tm.sowdahasap.Session
import tm.sowdahasap.data.MoveType
import tm.sowdahasap.data.Product
import tm.sowdahasap.data.Repo
import tm.sowdahasap.data.Warehouse
import tm.sowdahasap.util.dateTime
import tm.sowdahasap.util.money
import tm.sowdahasap.util.moneyInput
import tm.sowdahasap.util.newId
import tm.sowdahasap.util.now
import tm.sowdahasap.util.parseMoney
import tm.sowdahasap.util.parseQty
import tm.sowdahasap.util.qty

// ======================= HARYTLAR =======================

@Composable
fun ProductsScreen(onBack: () -> Unit, open: (String) -> Unit) {
    var q by remember { mutableStateOf("") }
    val show = toast()
    val warehouses by App.dao.warehouses().collectAsState(initial = emptyList())
    var wh by remember { mutableStateOf<Warehouse?>(null) }
    val flow = remember(q, wh) { App.dao.productsWithStock(q, wh?.id) }
    val list by flow.collectAsState(initial = emptyList())
    var scanned by remember { mutableStateOf<String?>(null) }
    val scan = rememberScanner { scanned = it }
    val canEdit = Session.can(Perm.PRODUCTS_EDIT)

    LaunchedEffect(scanned) {
        val code = scanned ?: return@LaunchedEffect
        val p = App.dao.productByBarcode(code)
        when {
            p != null && canEdit -> open(p.id)
            p != null -> q = code
            canEdit -> { show("Täze haryt: $code"); open("new:$code") }
            else -> show("Tapylmady: $code")
        }
        scanned = null
    }

    Screen("Harytlar", onBack,
        fab = { if (canEdit) FloatingActionButton(onClick = { open("new") }) { Icon(Icons.Default.Add, "Täze") } },
    ) { pad ->
        Column(Modifier.padding(pad).padding(horizontal = 12.dp)) {
            Field(q, { q = it }, "Gözleg", trailing = { IconButton(onClick = scan) { Icon(Icons.Default.Search, "Barkod") } })
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(selected = wh == null, onClick = { wh = null }, label = { Text("Ähli ammar") })
                warehouses.forEach { w -> FilterChip(selected = wh?.id == w.id, onClick = { wh = w }, label = { Text(w.name) }) }
            }
            Text("${list.size} haryt", style = MaterialTheme.typography.bodySmall)
            LazyColumn(Modifier.fillMaxSize()) {
                items(list, key = { it.product.id }) { ps ->
                    val p = ps.product
                    val low = p.minStock > 0 && ps.stock <= p.minStock
                    ListItem(
                        headlineContent = { Text(p.name) },
                        supportingContent = {
                            Text(buildString {
                                append("${p.sellPrice.money()} TMT")
                                if (p.barcode.isNotBlank()) append(" · ${p.barcode}")
                            })
                        },
                        trailingContent = {
                            Text("${ps.stock.qty()} ${p.unit}" + if (low) " ⚠️" else "",
                                color = if (low) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                        },
                        modifier = Modifier.clickable(enabled = canEdit) { open(p.id) },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun ProductEditScreen(idArg: String, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val show = toast()
    val isNew = idArg.startsWith("new")
    var product by remember { mutableStateOf<Product?>(null) }
    var name by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf(if (idArg.startsWith("new:")) idArg.removePrefix("new:") else "") }
    var unit by remember { mutableStateOf("sany") }
    var buy by remember { mutableStateOf("") }
    var sell by remember { mutableStateOf("") }
    var minStock by remember { mutableStateOf("") }
    var startQty by remember { mutableStateOf("") }
    var confirmDelete by remember { mutableStateOf(false) }
    val scan = rememberScanner { barcode = it }
    val showCost = Session.can(Perm.VIEW_COST)

    LaunchedEffect(idArg) {
        if (!isNew) App.dao.product(idArg)?.let {
            product = it; name = it.name; barcode = it.barcode; unit = it.unit
            buy = it.buyPrice.moneyInput(); sell = it.sellPrice.moneyInput()
            minStock = if (it.minStock > 0) it.minStock.qty() else ""
        }
    }

    Screen(if (isNew) "Täze haryt" else "Haryt", onBack, actions = {
        if (!isNew) IconButton(onClick = { confirmDelete = true }) { Icon(Icons.Default.Delete, "Poz") }
    }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Field(name, { name = it }, "Ady *")
            Field(barcode, { barcode = it }, "Barkod", trailing = { IconButton(onClick = scan) { Icon(Icons.Default.Search, "Okat") } })
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("sany", "kg", "litr", "metr", "gap", "guty").forEach { u ->
                    FilterChip(selected = unit == u, onClick = { unit = u }, label = { Text(u) })
                }
            }
            if (showCost) Field(buy, { buy = it }, "Alyş bahasy (TMT)", number = true)
            Field(sell, { sell = it }, "Satuw bahasy (TMT) *", number = true)
            Field(minStock, { minStock = it }, "Iň az galyndy (duýduryş üçin)", number = true)
            if (isNew) Field(startQty, { startQty = it }, "Başlangyç mukdar (esasy ammara)", number = true)
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                val sp = parseMoney(sell)
                when {
                    name.isBlank() -> show("Ady giriziň")
                    sp == null -> show("Satuw bahasy nädogry")
                    else -> scope.launch {
                        val b = barcode.trim()
                        val dup = if (b.isNotEmpty()) App.dao.productByBarcode(b) else null
                        if (dup != null && dup.id != product?.id) {
                            show("Bu barkod eýýäm bar: ${dup.name}")
                            return@launch
                        }
                        val p = (product ?: Product(newId(), "", updatedAt = now())).copy(
                            name = name.trim(), barcode = b, unit = unit,
                            buyPrice = if (showCost) parseMoney(buy) ?: 0L else product?.buyPrice ?: 0L,
                            sellPrice = sp, minStock = parseQty(minStock) ?: 0.0,
                        )
                        Repo.saveProduct(p)
                        val sq = parseQty(startQty)
                        if (isNew && sq != null && sq != 0.0) Repo.addMove(p.id, App.prefs.defaultWarehouse, sq, MoveType.IN, "Başlangyç")
                        show("Ýatda saklandy")
                        onBack()
                    }
                }
            }) { Text("Ýatda sakla") }
        }
    }
    if (confirmDelete) ConfirmDialog("Pozmak", "Bu harydy pozmalymy?", onConfirm = {
        scope.launch { product?.let { Repo.deleteProduct(it) }; onBack() }
    }, onDismiss = { confirmDelete = false })
}

// ======================= AMMAR =======================

@Composable
fun StockScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val show = toast()
    val warehouses by App.dao.warehouses().collectAsState(initial = emptyList())
    val moves by App.dao.recentMoves().collectAsState(initial = emptyList())
    var type by remember { mutableStateOf(MoveType.IN) }
    var wh by remember { mutableStateOf<Warehouse?>(null) }
    var toWh by remember { mutableStateOf<Warehouse?>(null) }
    var product by remember { mutableStateOf<Product?>(null) }
    var qtyText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var picking by remember { mutableStateOf(false) }
    var current by remember { mutableStateOf<Double?>(null) }

    LaunchedEffect(warehouses) { if (wh == null) wh = warehouses.firstOrNull { it.id == App.prefs.defaultWarehouse } ?: warehouses.firstOrNull() }
    LaunchedEffect(product, wh, moves.size) {
        val p = product; val w = wh
        current = if (p != null && w != null) App.dao.stockOf(p.id, w.id) else null
    }

    Screen("Ammar hereketi", onBack) { pad ->
        Column(Modifier.padding(pad).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(MoveType.IN, MoveType.OUT, MoveType.TRANSFER, MoveType.ADJUST).forEach { t ->
                    FilterChip(selected = type == t, onClick = { type = t }, label = { Text(MoveType.title(t)) })
                }
            }
            Select(if (type == MoveType.TRANSFER) "Nireden" else "Ammar", warehouses, wh, { it.name }, { wh = it })
            if (type == MoveType.TRANSFER) Select("Nirä", warehouses.filter { it.id != wh?.id }, toWh, { it.name }, { toWh = it })
            OutlinedButton(onClick = { picking = true }, modifier = Modifier.fillMaxWidth()) {
                Text(product?.let { "${it.name}" + (current?.let { c -> " (bar: ${c.qty()})" } ?: "") } ?: "Haryt saýla / barkod okat")
            }
            Field(qtyText, { qtyText = it },
                if (type == MoveType.ADJUST) "Hakyky sanalan mukdar" else "Mukdar", number = true)
            Field(note, { note = it }, "Bellik (üpjünçi, sebäp...)")
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                val p = product; val w = wh; val qv = parseQty(qtyText)
                when {
                    p == null -> show("Haryt saýlaň")
                    w == null -> show("Ammar saýlaň")
                    qv == null || (qv <= 0 && type != MoveType.ADJUST) || qv < 0 -> show("Mukdar nädogry")
                    type == MoveType.TRANSFER && toWh == null -> show("Haýsy ammara?")
                    else -> scope.launch {
                        when (type) {
                            MoveType.IN -> Repo.addMove(p.id, w.id, qv, MoveType.IN, note)
                            MoveType.OUT -> Repo.addMove(p.id, w.id, -qv, MoveType.OUT, note)
                            MoveType.TRANSFER -> Repo.transfer(p.id, w.id, toWh!!.id, qv, note)
                            MoveType.ADJUST -> Repo.adjustTo(p.id, w.id, qv, note)
                        }
                        show("Ýazyldy"); qtyText = ""; note = ""
                    }
                }
            }) { Text("Ýaz") }
            HorizontalDivider()
            Text("Soňky hereketler", style = MaterialTheme.typography.titleSmall)
            LazyColumn(Modifier.fillMaxSize()) {
                items(moves, key = { it.move.id }) { r ->
                    ListItem(
                        headlineContent = { Text(r.productName) },
                        supportingContent = { Text("${MoveType.title(r.move.type)} · ${r.warehouseName} · ${r.move.createdAt.dateTime()} ${r.move.note}") },
                        trailingContent = {
                            Text((if (r.move.qty > 0) "+" else "") + r.move.qty.qty(),
                                color = if (r.move.qty < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                        },
                    )
                }
            }
        }
    }
    if (picking) ProductPicker(wh?.id, onPick = { product = it; picking = false }, onDismiss = { picking = false })
}

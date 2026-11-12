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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import tm.sowdahasap.platform.rememberScanner
import tm.sowdahasap.util.dateTime
import tm.sowdahasap.util.money
import tm.sowdahasap.util.moneyInput
import tm.sowdahasap.util.newId
import tm.sowdahasap.util.now
import tm.sowdahasap.util.parseMoney
import tm.sowdahasap.util.parseQty
import tm.sowdahasap.util.qty

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

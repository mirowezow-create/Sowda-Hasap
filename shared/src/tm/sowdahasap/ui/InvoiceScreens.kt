@file:OptIn(ExperimentalMaterial3Api::class)

package tm.sowdahasap.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import tm.sowdahasap.App
import tm.sowdahasap.data.Customer
import tm.sowdahasap.data.InvoiceKind
import tm.sowdahasap.data.InvoiceStatus
import tm.sowdahasap.util.dateTime
import tm.sowdahasap.util.money

@Composable
fun InvoicesScreen(onBack: () -> Unit, open: (String) -> Unit) {
    var status by remember { mutableStateOf<String?>(null) }
    var kind by remember { mutableStateOf<String?>(null) }
    var q by remember { mutableStateOf("") }
    val flow = remember(status) { App.dao.invoices(status) }
    val all by flow.collectAsState(initial = emptyList())
    val list = all.filter { r ->
        (kind == null || r.invoice.kind == kind) &&
            (q.isBlank() || r.invoice.number.contains(q, true) || (r.customerName ?: "").contains(q, true))
    }
    Column(Modifier.fillMaxSize().padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ToolButton("➕", "Täze", { Tabs.open("invoice/new") })
            Field(q, { q = it }, "Gözleg (kody, müşderi)", modifier = Modifier.weight(1f).padding(start = 8.dp))
        }
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            FilterChip(selected = status == null, onClick = { status = null }, label = { Text("Ählisi") })
            listOf(InvoiceStatus.DRAFT, InvoiceStatus.POSTED, InvoiceStatus.CANCELLED).forEach { s ->
                FilterChip(selected = status == s, onClick = { status = s }, label = { Text(InvoiceStatus.title(s)) })
            }
            Text("  |  ")
            FilterChip(selected = kind == null, onClick = { kind = null }, label = { Text("Ähli görnüş") })
            InvoiceKind.all.forEach { k -> FilterChip(selected = kind == k, onClick = { kind = k }, label = { Text(InvoiceKind.title(k)) }) }
        }
        Column(Modifier.weight(1f).fillMaxWidth().background(Color(0xEEFFFFFF)).border(1.dp, Color(0x33000000))) {
            TableHeader(listOf(Col("Kody", 1.4f), Col("Senesi", 1.3f), Col("Görnüşi", 1.4f), Col("Müşderi", 1.8f),
                Col("Ýagdaýy", 1.1f), Col("Jemi", 1.1f, TextAlign.End)))
            LazyColumn(Modifier.fillMaxSize()) {
                itemsIndexed(list, key = { _, r -> r.invoice.id }) { i, r ->
                    val inv = r.invoice
                    TableRow(i, onClick = { open(inv.id) }) {
                        Cell(inv.number, 1.4f)
                        Cell(inv.createdAt.dateTime(), 1.3f)
                        Cell(InvoiceKind.title(inv.kind) + if (inv.wholesale) " · opt" else "", 1.4f)
                        Cell(r.customerName ?: "—", 1.8f)
                        Cell(InvoiceStatus.title(inv.status), 1.1f, color = when (inv.status) {
                            InvoiceStatus.CANCELLED -> Pal.RedX; InvoiceStatus.DRAFT -> Color(0xFFB26A00); else -> Color(0xFF1B7F3A)
                        })
                        Cell(inv.total.money(), 1.1f, TextAlign.End, bold = true)
                    }
                }
            }
        }
        MutedText("Jemi: ${list.size} faktura · ${list.filter { it.invoice.status == InvoiceStatus.POSTED }.sumOf { it.invoice.total }.money()} TMT")
    }
}

@Composable
fun CustomerPicker(customers: List<Customer>, onPick: (Customer?) -> Unit, onDismiss: () -> Unit) {
    SearchDialog("Müşderi", listOf<Customer?>(null) + customers,
        listOf(Col("Ady", 3f) to { c: Customer? -> c?.name ?: "— Hiç kim (nagt)" }, Col("Kody", 1f) to { c -> c?.code ?: "" },
            Col("Telefon", 1.5f) to { c -> c?.phone ?: "" }),
        onPick = onPick, onDismiss = onDismiss)
}

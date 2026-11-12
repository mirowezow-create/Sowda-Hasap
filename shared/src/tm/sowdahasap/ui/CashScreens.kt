@file:OptIn(ExperimentalMaterial3Api::class)

package tm.sowdahasap.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import tm.sowdahasap.App
import tm.sowdahasap.Role
import tm.sowdahasap.Session
import tm.sowdahasap.data.Cashbook
import tm.sowdahasap.data.Customer
import tm.sowdahasap.data.Repo
import tm.sowdahasap.util.dateTime
import tm.sowdahasap.util.money
import tm.sowdahasap.util.newId
import tm.sowdahasap.util.now
import tm.sowdahasap.util.parseMoney
import java.time.LocalDate
import java.time.ZoneId

private enum class CashPeriod(val title: String) { TODAY("Şu gün"), WEEK("7 gün"), MONTH("Şu aý"), ALL("Ählisi") }

private fun cashRange(p: CashPeriod): Pair<Long, Long> {
    val z = ZoneId.systemDefault()
    val today = LocalDate.now()
    val from = when (p) {
        CashPeriod.TODAY -> today
        CashPeriod.WEEK -> today.minusDays(6)
        CashPeriod.MONTH -> today.withDayOfMonth(1)
        CashPeriod.ALL -> LocalDate.of(2000, 1, 1)
    }
    return from.atStartOfDay(z).toInstant().toEpochMilli() to Long.MAX_VALUE
}

private enum class CashOp(val title: String) { IN("Girdeji (pul girdi)"), OUT("Çykdajy (pul çykdy)"), MOVE("Depderiň arasy") }

@Composable
fun CashScreen() {
    val scope = rememberCoroutineScope()
    val show = toast()
    val cashbooks by App.dao.cashbooks().collectAsState(initial = emptyList())
    val balances by App.dao.cashBalances().collectAsState(initial = emptyList())
    val customers by App.dao.customers().collectAsState(initial = emptyList())
    var cb by remember { mutableStateOf<Cashbook?>(null) }
    var period by remember { mutableStateOf(CashPeriod.TODAY) }
    val (from, to) = cashRange(period)
    val rowsFlow = remember(cb, period) { App.dao.cashRows(cb?.id, from, to) }
    val rows by rowsFlow.collectAsState(initial = emptyList())
    var op by remember { mutableStateOf<CashOp?>(null) }
    var pickCb by remember { mutableStateOf(false) }
    var cancelId by remember { mutableStateOf<tm.sowdahasap.data.Payment?>(null) }

    val totalIn = rows.filter { it.payment.amount > 0 }.sumOf { it.payment.amount }
    val totalOut = rows.filter { it.payment.amount < 0 }.sumOf { -it.payment.amount }

    Column(Modifier.fillMaxSize().padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.weight(1f).horizontalScroll(rememberScrollState())) {
                ToolButton("➕", "Girdeji", { op = CashOp.IN })
                ToolButton("➖", "Çykdajy", { op = CashOp.OUT })
                ToolButton("🔁", "Depder arasy", { op = CashOp.MOVE }, enabled = cashbooks.size > 1)
            }
            Column(horizontalAlignment = Alignment.End) {
                val bal = if (cb == null) balances.sumOf { it.balance } else balances.firstOrNull { it.cashbookId == cb?.id }?.balance ?: 0
                BigTotal("${bal.money()} TMT")
                MutedText("Kassadaky pul" + (cb?.let { " (${it.name})" } ?: " (ähli depder)"))
            }
        }
        Row(Modifier.horizontalScroll(rememberScrollState()), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            LinkText("Depder: " + (cb?.name ?: "Ählisi"), { pickCb = true })
            CashPeriod.entries.forEach { p -> FilterChip(selected = period == p, onClick = { period = p }, label = { Text(p.title) }) }
            Text("Girdi: ${totalIn.money()}   Çykdy: ${totalOut.money()}", fontSize = 14.sp)
        }
        Column(Modifier.weight(1f).fillMaxWidth().background(Color(0xEEFFFFFF)).border(1.dp, Color(0x33000000))) {
            TableHeader(listOf(Col("Senesi", 1.4f), Col("Depder", 1.2f), Col("Müşderi", 1.6f), Col("Bellik", 2f),
                Col("Girdi", 1f, TextAlign.End), Col("Çykdy", 1f, TextAlign.End)))
            LazyColumn(Modifier.fillMaxSize()) {
                itemsIndexed(rows, key = { _, r -> r.payment.id }) { i, r ->
                    val p = r.payment
                    TableRow(i, onClick = if (Session.role == Role.ADMIN && p.invoiceId == null) ({ cancelId = p }) else null) {
                        Cell(p.createdAt.dateTime(), 1.4f)
                        Cell(r.cashbookName ?: "", 1.2f)
                        Cell(r.customerName ?: "", 1.6f)
                        Cell(p.note, 2f, color = Pal.Muted)
                        Cell(if (p.amount > 0) p.amount.money() else "", 1f, TextAlign.End, color = Color(0xFF1B7F3A))
                        Cell(if (p.amount < 0) (-p.amount).money() else "", 1f, TextAlign.End, color = Pal.RedX)
                    }
                }
            }
        }
    }

    if (pickCb) SearchDialog("Depder", listOf<Cashbook?>(null) + cashbooks,
        listOf(Col("Ady", 1f) to { c: Cashbook? -> c?.name ?: "Ählisi" }),
        onPick = { cb = it; pickCb = false }, onDismiss = { pickCb = false })

    op?.let { o -> CashOpDialog(o, cashbooks, customers, cb, onDismiss = { op = null }) { book, toBook, cust, amount, note ->
        op = null
        scope.launch {
            when (o) {
                CashOp.IN -> Repo.addPayment(cust?.id ?: "", amount, note.ifBlank { "Girdeji" }, book.id)
                CashOp.OUT -> Repo.addPayment(cust?.id ?: "", -amount, note.ifBlank { "Çykdajy" }, book.id)
                CashOp.MOVE -> {
                    Repo.addPayment("", -amount, "→ ${toBook?.name}: $note", book.id)
                    Repo.addPayment("", amount, "← ${book.name}: $note", toBook!!.id)
                }
            }
            show("Ýazyldy")
        }
    } }

    cancelId?.let { p ->
        ConfirmDialog("Ýalňyş ýazgy", "${p.amount.money()} TMT ýazgyny ýalňyş hökmünde ýatyrmalymy?",
            onConfirm = { scope.launch { Repo.cancelPayment(p); show("Ýatyryldy") } }, onDismiss = { cancelId = null })
    }
}

@Composable
private fun CashOpDialog(
    op: CashOp, cashbooks: List<Cashbook>, customers: List<Customer>, defaultBook: Cashbook?,
    onDismiss: () -> Unit, onSave: (Cashbook, Cashbook?, Customer?, Long, String) -> Unit,
) {
    var book by remember { mutableStateOf(defaultBook ?: cashbooks.firstOrNull()) }
    var toBook by remember { mutableStateOf(cashbooks.firstOrNull { it.id != book?.id }) }
    var cust by remember { mutableStateOf<Customer?>(null) }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var pick by remember { mutableStateOf(0) } // 1 depder, 2 nirä, 3 müşderi
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(op.title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                PanelLink(if (op == CashOp.MOVE) "Haýsy depderden" else "Depder", book?.name ?: "?", { pick = 1 })
                if (op == CashOp.MOVE) PanelLink("Haýsy depdere", toBook?.name ?: "?", { pick = 2 })
                else PanelLink("Müşderi (hökman däl)", cust?.name ?: "Hiç kim", { pick = 3 },
                    onClear = if (cust != null) ({ cust = null }) else null)
                if (op != CashOp.MOVE && cust != null) MutedText(if (op == CashOp.IN) "Müşderiniň bergisi azalar" else "Müşderä pul berildi (bergisi artar)")
                Field(amount, { amount = it }, "Möçber (TMT)", number = true)
                Field(note, { note = it }, "Bellik")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val a = parseMoney(amount)
                val b = book
                when {
                    a == null || a <= 0 -> Msgs.err("Möçber nädogry")
                    b == null -> Msgs.err("Depder saýlaň")
                    op == CashOp.MOVE && (toBook == null || toBook?.id == b.id) -> Msgs.err("Başga depder saýlaň")
                    else -> onSave(b, toBook, cust, a, note.trim())
                }
            }) { Text("Ýaz") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Goýbolsun") } },
    )
    when (pick) {
        1 -> ChoiceDialog("Depder", cashbooks, { it.name }, { book = it; pick = 0 }, { pick = 0 })
        2 -> ChoiceDialog("Depder", cashbooks.filter { it.id != book?.id }, { it.name }, { toBook = it; pick = 0 }, { pick = 0 })
        3 -> SearchDialog("Müşderi", customers, listOf(Col("Ady", 3f) to { c: Customer -> c.name }, Col("Kody", 1f) to { c -> c.code },
            Col("Telefon", 1.5f) to { c -> c.phone }), { cust = it; pick = 0 }, { pick = 0 })
    }
}

private object Msgs { fun err(t: String) = tm.sowdahasap.Msg.show(t) }

// ======================= DEPDERLER =======================

@Composable
fun CashbooksScreen() {
    val scope = rememberCoroutineScope()
    val cashbooks by App.dao.cashbooks().collectAsState(initial = emptyList())
    val balances by App.dao.cashBalances().collectAsState(initial = emptyList())
    var name by remember { mutableStateOf("") }
    var edit by remember { mutableStateOf<Cashbook?>(null) }
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        SmallTitle("Depderler (kassa, bank, kart we ş.m.)")
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Field(name, { name = it }, "Täze depderiň ady", modifier = Modifier.weight(1f))
            ToolButton("➕", "Goş", {
                val n = name.trim()
                if (n.isNotEmpty()) scope.launch { App.dao.upsertCashbook(Cashbook(newId(), n, now())); name = "" }
            })
        }
        Box(Modifier.weight(1f).fillMaxWidth().background(Color(0xEEFFFFFF))) {
            Column {
                TableHeader(listOf(Col("Ady", 3f), Col("Galyndy", 1.5f, TextAlign.End)))
                LazyColumn {
                    itemsIndexed(cashbooks, key = { _, c -> c.id }) { i, c ->
                        TableRow(i, onClick = { edit = c }) {
                            Cell(c.name, 3f)
                            Cell((balances.firstOrNull { it.cashbookId == c.id }?.balance ?: 0L).money(), 1.5f, TextAlign.End, bold = true)
                        }
                    }
                }
            }
        }
    }
    edit?.let { c -> RenameDialog("Depderiň ady", c.name, onDismiss = { edit = null }) { n ->
        edit = null; scope.launch { App.dao.upsertCashbook(c.copy(name = n, updatedAt = now())) }
    } }
}

@Composable
fun RenameDialog(title: String, initial: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var t by remember { mutableStateOf(initial) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text(title) },
        text = { Field(t, { t = it }, "Ady") },
        confirmButton = { TextButton(onClick = { if (t.isNotBlank()) onSave(t.trim()) }) { Text("Sakla") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Goýbolsun") } })
}

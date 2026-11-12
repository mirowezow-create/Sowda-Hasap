@file:OptIn(ExperimentalMaterial3Api::class)

package tm.sowdahasap.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import tm.sowdahasap.App
import tm.sowdahasap.Perm
import tm.sowdahasap.Session
import tm.sowdahasap.data.Customer
import tm.sowdahasap.data.CustomerBalance
import tm.sowdahasap.data.Repo
import tm.sowdahasap.sync.Sms
import tm.sowdahasap.platform.NeedPerm
import tm.sowdahasap.platform.Platform
import tm.sowdahasap.platform.rememberWithPermission
import tm.sowdahasap.util.date
import tm.sowdahasap.util.money
import tm.sowdahasap.util.newId
import tm.sowdahasap.util.now
import tm.sowdahasap.util.parseMoney

// ======================= MÜŞDERILER =======================

@Composable
fun CustomersScreen(onBack: () -> Unit, open: (String) -> Unit) {
    var q by remember { mutableStateOf("") }
    val flow = remember(q) { App.dao.customersWithDebt(q) }
    val list by flow.collectAsState(initial = emptyList())
    val totalDebt = list.sumOf { it.debt.coerceAtLeast(0) }

    Screen("Müşderiler", onBack, fab = {
        FloatingActionButton(onClick = { open("new") }) { Text("➕") }
    }) { pad ->
        Column(Modifier.padding(pad).padding(horizontal = 12.dp)) {
            Field(q, { q = it }, "Gözleg (ady ýa-da telefon)")
            Text("Jemi bergi: ${totalDebt.money()} TMT", style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(vertical = 6.dp))
            LazyColumn(Modifier.fillMaxSize()) {
                items(list, key = { it.customer.id }) { cb ->
                    ListItem(
                        headlineContent = { Text(cb.customer.name) },
                        supportingContent = { Text(cb.customer.phone) },
                        trailingContent = {
                            Text(cb.debt.money(), style = MaterialTheme.typography.titleMedium,
                                color = if (cb.debt > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                        },
                        modifier = Modifier.clickable { open(cb.customer.id) },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

private sealed class Entry(val time: Long) {
    class Inv(val number: String, val id: String, val amount: Long, t: Long) : Entry(t)
    class Pay(val p: tm.sowdahasap.data.Payment) : Entry(p.createdAt)
}

@Composable
fun CustomerDetailScreen(idArg: String, onBack: () -> Unit, openInvoice: (String) -> Unit) {
    val scope = rememberCoroutineScope()
    val show = toast()
    val isNew = idArg == "new"
    var customer by remember { mutableStateOf<Customer?>(null) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var payDialog by remember { mutableStateOf(false) }
    var delPayment by remember { mutableStateOf<tm.sowdahasap.data.Payment?>(null) }

    LaunchedEffect(idArg) {
        if (!isNew) App.dao.customer(idArg)?.let { customer = it; name = it.name; phone = it.phone; note = it.note }
    }
    val debt by remember(idArg) { if (isNew) flowOf(0L) else App.dao.debtOf(idArg) }.collectAsState(initial = 0L)
    val invoices by remember(idArg) { App.dao.postedInvoicesOf(idArg) }.collectAsState(initial = emptyList())
    val payments by remember(idArg) { App.dao.paymentsOf(idArg) }.collectAsState(initial = emptyList())
    val ledger = (invoices.map { Entry.Inv(it.number, it.id, it.total, it.createdAt) } + payments.map { Entry.Pay(it) })
        .sortedByDescending { it.time }

    val sendSms = rememberWithPermission(NeedPerm.SMS) {
        val c = customer ?: return@rememberWithPermission
        try {
            Platform.sendSms(Sms.normalizePhone(c.phone), Sms.text(CustomerBalance(c, debt)))
            show("SMS ugradyldy")
        } catch (e: Exception) { show("SMS ýalňyşlygy: ${e.message}") }
    }

    Screen(if (isNew) "Täze müşderi" else name, onBack) { pad ->
        Column(Modifier.padding(pad).padding(12.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Field(name, { name = it }, "Ady *")
            Field(phone, { phone = it }, "Telefon (+993...)", phone = true)
            Field(note, { note = it }, "Bellik")
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                if (name.isBlank()) { show("Ady giriziň") } else scope.launch {
                    val c = (customer ?: Customer(newId(), "", updatedAt = now()))
                        .copy(name = name.trim(), phone = phone.trim(), note = note.trim())
                    Repo.saveCustomer(c)
                    show("Ýatda saklandy")
                    if (isNew) onBack() else customer = c
                }
            }) { Text("Ýatda sakla") }

            if (!isNew) {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        InfoRow("Bergi", debt.money() + " TMT")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (Session.can(Perm.PAYMENTS)) Button(onClick = { payDialog = true }) { Text("Töleg al") }
                            if (phone.isNotBlank()) {
                                if (Platform.supportsSms) OutlinedButton(onClick = { Platform.dial(Sms.normalizePhone(phone)) }) { Text("📞") }
                                if (Platform.supportsSms && Session.can(Perm.SMS) && debt > 0) OutlinedButton(onClick = sendSms) { Text("SMS") }
                            }
                        }
                    }
                }
                Text("Hereketler", style = MaterialTheme.typography.titleSmall)
                LazyColumn(Modifier.fillMaxSize()) {
                    items(ledger) { e ->
                        when (e) {
                            is Entry.Inv -> ListItem(
                                headlineContent = { Text("Faktura ${e.number}") },
                                supportingContent = { Text(e.time.date()) },
                                trailingContent = { Text("+" + e.amount.money(), color = MaterialTheme.colorScheme.error) },
                                modifier = Modifier.clickable { openInvoice(e.id) },
                            )
                            is Entry.Pay -> ListItem(
                                headlineContent = { Text("Töleg " + e.p.note) },
                                supportingContent = { Text(e.time.date()) },
                                trailingContent = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("-" + e.p.amount.money(), color = MaterialTheme.colorScheme.primary)
                                        if (Session.role == tm.sowdahasap.Role.ADMIN && e.p.invoiceId == null)
                                            IconText("🗑", { delPayment = e.p })
                                    }
                                },
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    if (payDialog) {
        var amount by remember { mutableStateOf("") }
        var pnote by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { payDialog = false },
            title = { Text("Töleg al") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Bergi: ${debt.money()} TMT")
                    Field(amount, { amount = it }, "Möçber (TMT)", number = true)
                    Field(pnote, { pnote = it }, "Bellik (nagt, kart...)")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val a = parseMoney(amount)
                    if (a == null || a <= 0) show("Möçber nädogry") else {
                        payDialog = false
                        scope.launch { Repo.addPayment(idArg, a, pnote.trim()); show("Töleg ýazyldy") }
                    }
                }) { Text("Ýaz") }
            },
            dismissButton = { TextButton(onClick = { payDialog = false }) { Text("Goýbolsun") } },
        )
    }

    delPayment?.let { p ->
        ConfirmDialog("Tölegi pozmak", "${p.amount.money()} TMT tölegi pozmalymy?",
            onConfirm = { scope.launch { Repo.deletePayment(p) } }, onDismiss = { delPayment = null })
    }
}

// ======================= BERGI SMS =======================

@Composable
fun DebtSmsScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val show = toast()
    val debtors = remember { mutableStateListOf<CustomerBalance>() }
    val selected = remember { mutableStateListOf<String>() }
    var template by remember { mutableStateOf(App.prefs.smsTemplate) }
    var translit by remember { mutableStateOf(App.prefs.smsTranslit) }
    var minDebt by remember { mutableStateOf("") }
    var sending by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0) }
    var confirm by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        debtors.clear()
        debtors.addAll(App.dao.debtors().filter { it.customer.phone.isNotBlank() })
        selected.clear(); selected.addAll(debtors.map { it.customer.id })
    }
    val min = parseMoney(minDebt) ?: 0L
    val visible = debtors.filter { it.debt >= min }
    val targets = visible.filter { it.customer.id in selected }

    LaunchedEffect(template, translit) { App.prefs.smsTemplate = template; App.prefs.smsTranslit = translit }

    val startSending = rememberWithPermission(NeedPerm.SMS) { confirm = true }

    Screen("Bergi SMS", onBack) { pad ->
        Column(Modifier.padding(pad).padding(12.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Field(template, { template = it }, "SMS nusgasy", singleLine = false)
            Text("{ady} — müşderiniň ady, {bergi} — bergisi, {firma} — siziň adyňyz", style = MaterialTheme.typography.bodySmall)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = translit, onCheckedChange = { translit = it })
                Text("  ä,ň,ş... harplary a,n,s ýaly ýaz (SMS arzan we uzyn bolar)", style = MaterialTheme.typography.bodySmall)
            }
            Field(minDebt, { minDebt = it }, "Iň az bergi (TMT) — mundan köp bergililere", number = true)
            debtors.firstOrNull()?.let {
                Text("Mysal: " + Sms.text(it, template, translit), style = MaterialTheme.typography.bodySmall)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Saýlanan: ${targets.size} / ${visible.size}", Modifier.weight(1f))
                TextButton(onClick = { selected.clear(); selected.addAll(visible.map { it.customer.id }) }) { Text("Ählisi") }
                TextButton(onClick = { selected.clear() }) { Text("Hiç") }
            }
            if (sending) LinearProgressIndicator(progress = { progress / targets.size.coerceAtLeast(1).toFloat() }, modifier = Modifier.fillMaxWidth())
            LazyColumn(Modifier.weight(1f)) {
                items(visible, key = { it.customer.id }) { d ->
                    val on = d.customer.id in selected
                    ListItem(
                        leadingContent = { Checkbox(checked = on, onCheckedChange = { if (it) selected.add(d.customer.id) else selected.remove(d.customer.id) }) },
                        headlineContent = { Text(d.customer.name) },
                        supportingContent = { Text(d.customer.phone) },
                        trailingContent = { Text(d.debt.money(), color = MaterialTheme.colorScheme.error) },
                    )
                }
            }
            Button(enabled = !sending && targets.isNotEmpty(), modifier = Modifier.fillMaxWidth(), onClick = startSending) {
                Text("${targets.size} müşderä SMS ugrat")
            }
        }
    }

    if (confirm) ConfirmDialog("SMS ugratmak", "${targets.size} SMS ugradylar. Operatoryňyzyň tarifi boýunça töleg alnar. Dowam etmelimi?",
        onConfirm = {
            sending = true; progress = 0
            val list = targets.toList()
            scope.launch {
                var ok = 0
                for (d in list) {
                    try { Platform.sendSms(Sms.normalizePhone(d.customer.phone), Sms.text(d, template, translit)); ok++ } catch (e: Exception) { }
                    progress++
                    delay(1500) // operator spam diýip petiklemez ýaly
                }
                sending = false
                show("Ugradyldy: $ok / ${list.size}")
            }
        }, onDismiss = { confirm = false })
}

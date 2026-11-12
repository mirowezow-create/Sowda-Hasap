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

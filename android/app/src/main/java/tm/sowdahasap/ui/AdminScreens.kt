@file:OptIn(ExperimentalMaterial3Api::class)

package tm.sowdahasap.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import tm.sowdahasap.Perm
import tm.sowdahasap.Role
import tm.sowdahasap.Session
import tm.sowdahasap.data.DaySum
import tm.sowdahasap.data.ProductStock
import tm.sowdahasap.data.Repo
import tm.sowdahasap.data.TopProduct
import tm.sowdahasap.data.User
import tm.sowdahasap.data.Warehouse
import tm.sowdahasap.license.LicenseManager
import tm.sowdahasap.print.BtPrinter
import tm.sowdahasap.print.Receipts
import tm.sowdahasap.sync.SyncClient
import tm.sowdahasap.util.exportDir
import tm.sowdahasap.util.money
import tm.sowdahasap.util.newId
import tm.sowdahasap.util.now
import tm.sowdahasap.util.qty
import tm.sowdahasap.util.shareFile
import java.io.File
import java.time.LocalDate
import java.time.ZoneId

// ======================= HASABATLAR =======================

private enum class Period(val title: String) { TODAY("Şu gün"), WEEK("7 gün"), MONTH("Şu aý"), LAST_MONTH("Geçen aý"), YEAR("Şu ýyl") }

private fun range(p: Period): Pair<Long, Long> {
    val z = ZoneId.systemDefault()
    val today = LocalDate.now()
    val (from, toExcl) = when (p) {
        Period.TODAY -> today to today.plusDays(1)
        Period.WEEK -> today.minusDays(6) to today.plusDays(1)
        Period.MONTH -> today.withDayOfMonth(1) to today.plusDays(1)
        Period.LAST_MONTH -> today.withDayOfMonth(1).minusMonths(1) to today.withDayOfMonth(1)
        Period.YEAR -> today.withDayOfYear(1) to today.plusDays(1)
    }
    return from.atStartOfDay(z).toInstant().toEpochMilli() to toExcl.atStartOfDay(z).toInstant().toEpochMilli() - 1
}

private data class Report(
    val sales: Long, val count: Int, val profit: Long, val discounts: Long, val payments: Long, val debt: Long,
    val days: List<DaySum>, val top: List<TopProduct>, val low: List<ProductStock>,
)

@Composable
fun ReportsScreen(onBack: () -> Unit) {
    val ctx = LocalContext.current
    var period by remember { mutableStateOf(Period.MONTH) }
    var r by remember { mutableStateOf<Report?>(null) }

    LaunchedEffect(period) {
        val (f, t) = range(period)
        val d = App.dao
        r = Report(d.salesTotal(f, t), d.invoiceCount(f, t), d.grossProfit(f, t) - d.discountTotal(f, t),
            d.discountTotal(f, t), d.paymentsTotal(f, t), d.totalDebt(), d.salesByDay(f, t), d.topProducts(f, t), d.lowStock(null))
    }

    Screen("Hasabatlar", onBack, actions = {
        IconButton(onClick = { r?.let { shareFile(ctx, reportCsv(ctx, period.title, it), "text/csv") } }) {
            Icon(Icons.Default.Share, "CSV")
        }
    }) { pad ->
        Column(Modifier.padding(pad).padding(12.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Period.entries.forEach { p -> FilterChip(selected = period == p, onClick = { period = p }, label = { Text(p.title) }) }
            }
            val rep = r
            if (rep == null) Text("...") else {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        InfoRow("Satuw", rep.sales.money() + " TMT")
                        InfoRow("Faktura sany", rep.count.toString())
                        if (rep.count > 0) InfoRow("Ortaça çek", (rep.sales / rep.count).money())
                        if (Session.can(Perm.VIEW_COST)) InfoRow("Girdeji (arassa)", rep.profit.money() + " TMT")
                        InfoRow("Alnan tölegler", rep.payments.money())
                        InfoRow("Müşderileriň umumy bergisi", rep.debt.money())
                    }
                }
                Text("Günler boýunça satuw", style = MaterialTheme.typography.titleSmall)
                if (rep.days.isEmpty()) Text("Satuw ýok")
                else BarChart(rep.days.map { it.day.takeLast(2) }, rep.days.map { it.total })

                Text("Iň köp satylan harytlar", style = MaterialTheme.typography.titleSmall)
                rep.top.forEachIndexed { i, t ->
                    InfoRow("${i + 1}. ${t.productName} (${t.qty.qty()})", t.total.money())
                }
                if (rep.low.isNotEmpty()) {
                    Text("⚠️ Az galan harytlar", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.error)
                    rep.low.forEach { InfoRow(it.product.name, "${it.stock.qty()} / ${it.product.minStock.qty()} ${it.product.unit}") }
                }
            }
        }
    }
}

private fun reportCsv(ctx: android.content.Context, title: String, r: Report): File {
    // Excel türkmen harplaryny dogry açar ýaly BOM goşulýar
    val sb = StringBuilder("﻿")
    sb.append("Hasabat;$title\n")
    sb.append("Satuw;${r.sales.money()}\nFaktura sany;${r.count}\nAlnan tölegler;${r.payments.money()}\nUmumy bergi;${r.debt.money()}\n\n")
    sb.append("Gün;Satuw\n"); r.days.forEach { sb.append("${it.day};${it.total.money()}\n") }
    sb.append("\nHaryt;Mukdar;Jemi\n"); r.top.forEach { sb.append("${it.productName.replace(';', ',')};${it.qty.qty()};${it.total.money()}\n") }
    val f = File(exportDir(ctx), "hasabat_${System.currentTimeMillis()}.csv")
    f.writeText(sb.toString())
    return f
}

// ======================= ULANYJYLAR =======================

@Composable
fun UsersScreen(onBack: () -> Unit) {
    val users by App.dao.users().collectAsState(initial = emptyList())
    var edit by remember { mutableStateOf<User?>(null) }
    var creating by remember { mutableStateOf(false) }

    Screen("Ulanyjylar", onBack, fab = {
        FloatingActionButton(onClick = { creating = true }) { Icon(Icons.Default.Add, "Täze") }
    }) { pad ->
        LazyColumn(Modifier.padding(pad).fillMaxSize()) {
            items(users, key = { it.id }) { u ->
                ListItem(
                    headlineContent = { Text(u.name + if (!u.active) " (petiklenen)" else "") },
                    supportingContent = { Text("${u.login} · ${runCatching { Role.valueOf(u.role).title }.getOrDefault(u.role)}") },
                    modifier = Modifier.clickable { edit = u },
                )
                HorizontalDivider()
            }
        }
    }
    if (creating || edit != null) UserDialog(edit, onDismiss = { creating = false; edit = null })
}

@Composable
private fun UserDialog(u: User?, onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    val show = toast()
    var name by remember { mutableStateOf(u?.name ?: "") }
    var login by remember { mutableStateOf(u?.login ?: "") }
    var pass by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(u?.let { runCatching { Role.valueOf(it.role) }.getOrNull() } ?: Role.SELLER) }
    var active by remember { mutableStateOf(u?.active ?: true) }
    val isSelf = u?.id == Session.userId

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (u == null) "Täze ulanyjy" else "Ulanyjy") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Field(name, { name = it }, "Ady")
                Field(login, { login = it }, "Login")
                Field(pass, { pass = it }, if (u == null) "Parol" else "Täze parol (boş goýsaň üýtgemez)", password = true)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Role.entries.forEach { r ->
                        FilterChip(selected = role == r, enabled = !isSelf, onClick = { role = r }, label = { Text(r.title) })
                    }
                }
                Text(when (role) {
                    Role.ADMIN -> "Ähli zat: hasabatlar, sazlamalar, ulanyjylar"
                    Role.SELLER -> "Satuw, müşderiler, tölegler, SMS"
                    Role.STOREKEEPER -> "Harytlar, ammar hereketleri"
                }, style = MaterialTheme.typography.bodySmall)
                if (!isSelf) Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = active, onCheckedChange = { active = it }); Text("  Işjeň")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                when {
                    name.isBlank() || login.isBlank() -> show("Ady we logini dolduryň")
                    u == null && pass.length < 4 -> show("Parol azyndan 4 simwol")
                    u != null && pass.isNotEmpty() && pass.length < 4 -> show("Parol azyndan 4 simwol")
                    else -> scope.launch {
                        val other = App.dao.userByLogin(login.trim().lowercase())
                        if (other != null && other.id != u?.id) { show("Bu login eýýäm bar"); return@launch }
                        val saved = Repo.saveUser(u, name, login, pass, role, active)
                        if (isSelf) Session.user = saved
                        show("Ýatda saklandy"); onDismiss()
                    }
                }
            }) { Text("Ýatda sakla") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Goýbolsun") } },
    )
}

// ======================= SAZLAMALAR =======================

@Composable
fun SettingsScreen(onBack: () -> Unit, openLicense: () -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val show = toast()
    val p = App.prefs
    var company by remember { mutableStateOf(p.company) }
    var info by remember { mutableStateOf(p.companyInfo) }
    var server by remember { mutableStateOf(p.serverUrl) }
    var token by remember { mutableStateOf(p.syncToken) }
    var syncInfo by remember { mutableStateOf(p.lastSyncInfo) }
    var printerName by remember { mutableStateOf(p.printerName) }
    var chars by remember { mutableStateOf(p.paperChars) }
    var devices by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var newWh by remember { mutableStateOf("") }
    val warehouses by App.dao.warehouses().collectAsState(initial = emptyList())
    var defWh by remember { mutableStateOf(p.defaultWarehouse) }
    val license = remember { LicenseManager.status(ctx) }

    val loadPrinters = rememberWithPermissions(btPerms) {
        devices = BtPrinter.pairedDevices(ctx)
        if (devices.isEmpty()) show("Jübütlenen Bluetooth enjam ýok. Ilki telefonyň Bluetooth sazlamalarynda printeri jübütläň.")
    }
    val testPrint = rememberWithPermissions(btPerms) {
        scope.launch {
            try { BtPrinter.send(ctx, p.printerAddress, Receipts.testPage()); show("Iberildi") }
            catch (e: Exception) { show("Printer: ${e.message}") }
        }
    }

    Screen("Sazlamalar", onBack) { pad ->
        Column(Modifier.padding(pad).padding(12.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {

            Section("Firma")
            Field(company, { company = it }, "Firmanyň ady")
            Field(info, { info = it }, "Salgy, telefon (çekde görkezilýär)")
            Button(onClick = { p.company = company; p.companyInfo = info; show("Saklandy") }) { Text("Sakla") }

            Section("Ammarlar")
            warehouses.forEach { w ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(selected = defWh == w.id, onClick = { defWh = w.id; p.defaultWarehouse = w.id },
                        label = { Text(w.name + if (defWh == w.id) " (esasy)" else "") })
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Field(newWh, { newWh = it }, "Täze ammaryň ady", modifier = Modifier.weight(1f))
                Button(enabled = newWh.isNotBlank(), onClick = {
                    scope.launch { App.dao.upsertWarehouse(Warehouse(newId(), newWh.trim(), now())); newWh = "" }
                }) { Text("Goş") }
            }

            Section("Serwer (sinhronlaşdyrma)")
            Field(server, { server = it }, "Salgy (http://IP:8080 ýa-da https://...)")
            Field(token, { token = it }, "Token", password = true)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    p.serverUrl = server; p.syncToken = token
                    scope.launch {
                        try { syncInfo = SyncClient.sync(); show(syncInfo) } catch (e: Exception) { show("Ýalňyşlyk: ${e.message}") }
                    }
                }) { Text("Sakla we sinhronlaşdyr") }
            }
            if (syncInfo.isNotBlank()) Text("Soňky: $syncInfo", style = MaterialTheme.typography.bodySmall)

            Section("Çek printeri (Bluetooth)")
            Text("Saýlanan: " + printerName.ifBlank { "ýok" })
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = loadPrinters) { Text("Printerleri gözle") }
                OutlinedButton(enabled = p.printerAddress.isNotBlank(), onClick = testPrint) { Text("Synag çap") }
            }
            devices.forEach { (n, addr) ->
                TextButton(onClick = { p.printerAddress = addr; p.printerName = n; printerName = n; devices = emptyList() }) {
                    Text("🖨 $n ($addr)")
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(selected = chars == 32, onClick = { chars = 32; p.paperChars = 32 }, label = { Text("58 mm") })
                FilterChip(selected = chars == 48, onClick = { chars = 48; p.paperChars = 48 }, label = { Text("80 mm") })
            }

            Section("Lisenziýa")
            Text(license.message)
            Text("Enjam kody: ${LicenseManager.deviceCode(ctx)}", style = MaterialTheme.typography.bodySmall)
            OutlinedButton(onClick = openLicense) { Text("Täze açar girizmek") }
        }
    }
}

@Composable
private fun Section(t: String) {
    HorizontalDivider(Modifier.padding(top = 8.dp))
    Text(t, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
}

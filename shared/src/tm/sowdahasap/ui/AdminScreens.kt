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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import tm.sowdahasap.print.Receipts
import tm.sowdahasap.platform.NeedPerm
import tm.sowdahasap.platform.Platform
import tm.sowdahasap.platform.rememberFileOpener
import tm.sowdahasap.platform.rememberFileSaver
import tm.sowdahasap.platform.rememberWithPermission
import tm.sowdahasap.util.minutesToHm
import tm.sowdahasap.util.parseHm
import tm.sowdahasap.util.dateTime
import tm.sowdahasap.sync.SyncClient
import tm.sowdahasap.util.money
import tm.sowdahasap.util.newId
import tm.sowdahasap.util.now
import tm.sowdahasap.util.qty
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
    var period by remember { mutableStateOf(Period.MONTH) }
    var r by remember { mutableStateOf<Report?>(null) }

    LaunchedEffect(period) {
        val (f, t) = range(period)
        val d = App.dao
        r = Report(d.salesTotal(f, t), d.invoiceCount(f, t), d.grossProfit(f, t) - d.discountTotal(f, t),
            d.discountTotal(f, t), d.paymentsTotal(f, t), d.totalDebt(), d.salesByDay(f, t), d.topProducts(f, t), d.lowStock(null))
    }

    Screen("Hasabatlar", onBack, actions = {
        IconText("📤", { r?.let { Platform.shareFile("hasabat_${System.currentTimeMillis()}.csv", "text/csv", reportCsv(period.title, it)) } })
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

private fun reportCsv(title: String, r: Report): ByteArray {
    // Excel türkmen harplaryny dogry açar ýaly BOM goşulýar
    val sb = StringBuilder("﻿")
    sb.append("Hasabat;$title\n")
    sb.append("Satuw;${r.sales.money()}\nFaktura sany;${r.count}\nAlnan tölegler;${r.payments.money()}\nUmumy bergi;${r.debt.money()}\n\n")
    sb.append("Gün;Satuw\n"); r.days.forEach { sb.append("${it.day};${it.total.money()}\n") }
    sb.append("\nHaryt;Mukdar;Jemi\n"); r.top.forEach { sb.append("${it.productName.replace(';', ',')};${it.qty.qty()};${it.total.money()}\n") }
    return sb.toString().toByteArray(Charsets.UTF_8)
}

// ======================= ULANYJYLAR =======================

@Composable
fun UsersScreen(onBack: () -> Unit) {
    val users by App.dao.users().collectAsState(initial = emptyList())
    var edit by remember { mutableStateOf<User?>(null) }
    var creating by remember { mutableStateOf(false) }

    Screen("Ulanyjylar", onBack, fab = {
        FloatingActionButton(onClick = { creating = true }) { Text("➕") }
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
    val scope = rememberCoroutineScope()
    val show = toast()
    val p = App.prefs
    var company by remember { mutableStateOf(p.company) }
    var info by remember { mutableStateOf(p.companyInfo) }
    var server by remember { mutableStateOf(p.serverUrl) }
    var token by remember { mutableStateOf(p.syncToken) }
    var autoSync by remember { mutableStateOf(p.autoSync) }
    var syncInfo by remember { mutableStateOf(p.lastSyncInfo) }
    var printerName by remember { mutableStateOf(p.printerName) }
    var chars by remember { mutableStateOf(p.paperChars) }
    var devices by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var newWh by remember { mutableStateOf("") }
    val warehouses by App.dao.warehouses().collectAsState(initial = emptyList())
    var defWh by remember { mutableStateOf(p.defaultWarehouse) }
    var nightOn by remember { mutableStateOf(p.nightEnabled) }
    var nightFrom by remember { mutableStateOf(minutesToHm(p.nightStart)) }
    var nightTo by remember { mutableStateOf(minutesToHm(p.nightEnd)) }
    var hostOn by remember { mutableStateOf(Platform.serverSupported && Platform.serverRunning()) }
    var hostPort by remember { mutableStateOf(p.hostPort.toString()) }
    val license = remember { LicenseManager.status() }

    val loadPrinters = rememberWithPermission(NeedPerm.BLUETOOTH) {
        devices = Platform.printers()
        if (devices.isEmpty()) show(if (Platform.isDesktop) "Windows-da gurnalan printer ýok"
        else "Jübütlenen Bluetooth printer ýok. Ilki telefonyň Bluetooth sazlamalarynda printeri jübütläň.")
    }
    val testPrint = rememberWithPermission(NeedPerm.BLUETOOTH) {
        scope.launch {
            try { Platform.print(p.printerAddress, Receipts.testPage()); show("Iberildi") }
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

            Section("🌙 Gijeki baha")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = nightOn, onCheckedChange = { nightOn = it; p.nightEnabled = it })
                Text("  Gijeki baha işjeň")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Field(nightFrom, { nightFrom = it }, "Başlaýar (22:00)", modifier = Modifier.weight(1f))
                Field(nightTo, { nightTo = it }, "Gutarýar (06:00)", modifier = Modifier.weight(1f))
                Button(onClick = {
                    val a = parseHm(nightFrom); val b = parseHm(nightTo)
                    if (a == null || b == null) show("Wagt nädogry, mysal: 22:00") else {
                        p.nightStart = a; p.nightEnd = b; show("Saklandy")
                    }
                }) { Text("Sakla") }
            }
            Text("Harydyň kartasynda gijeki baha ýazylan bolsa, şu aralykda bölek satuwda şol baha goýulýar.",
                style = MaterialTheme.typography.bodySmall)

            Section("Ammarlar")
            warehouses.forEach { w ->
                FilterChip(selected = defWh == w.id, onClick = { defWh = w.id; p.defaultWarehouse = w.id },
                    label = { Text(w.name + if (defWh == w.id) " (esasy)" else "") })
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Field(newWh, { newWh = it }, "Täze ammaryň ady", modifier = Modifier.weight(1f))
                Button(enabled = newWh.isNotBlank(), onClick = {
                    scope.launch { App.dao.upsertWarehouse(Warehouse(newId(), newWh.trim(), now())); newWh = "" }
                }) { Text("Goş") }
            }

            if (Platform.serverSupported) {
                Section("🖥 Bu kompýuter esasy serwer")
                Text("Açsaňyz, beýleki kompýuterler we telefonlar Wi-Fi arkaly şu kompýutere birigip maglumat alyşar.",
                    style = MaterialTheme.typography.bodySmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Field(hostPort, { hostPort = it }, "Port", modifier = Modifier.weight(1f), number = true)
                    Field(token, { token = it }, "Token (gizlin söz)", modifier = Modifier.weight(2f))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = hostOn, onCheckedChange = { on ->
                        val port = hostPort.toIntOrNull() ?: 8080
                        if (on) {
                            if (token.length < 4) { show("Token azyndan 4 simwol bolmaly"); return@Switch }
                            p.syncToken = token; p.hostPort = port
                            val err = Platform.startServer(port, token)
                            if (err != null) { show("Serwer açylmady: $err"); return@Switch }
                            p.hostServer = true
                            // Bu kompýuteriň özi hem öz serwerine birigýär
                            p.serverUrl = "http://127.0.0.1:$port"; server = p.serverUrl
                            hostOn = true
                            show("Serwer açyldy")
                        } else {
                            Platform.stopServer(); p.hostServer = false; hostOn = false
                        }
                    })
                    Text(if (hostOn) "  Işleýär" else "  Ýapyk")
                }
                if (hostOn) {
                    Text("Beýleki enjamlarda şu salgyny ýazyň:", style = MaterialTheme.typography.bodySmall)
                    Platform.localAddresses().forEach { ip ->
                        Text("http://$ip:${p.hostPort}", style = MaterialTheme.typography.titleMedium)
                    }
                    Text("Windows «Firewall» sorasa, «Rugsat ber» (Allow access) basyň.", style = MaterialTheme.typography.bodySmall)
                }
            }

            Section("🔄 Sinhronlaşdyrma")
            Text("Esasy kompýuteriň salgysy (Wi-Fi) ýa-da internet serweri", style = MaterialTheme.typography.bodySmall)
            Field(server, { server = it }, "Salgy (http://192.168.1.10:8080)")
            Field(token, { token = it }, "Token", password = true)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = autoSync, onCheckedChange = { autoSync = it; p.autoSync = it })
                Text("  Awtomatik (her 2 minutda)")
            }
            Button(onClick = {
                p.serverUrl = server; p.syncToken = token
                scope.launch {
                    try { syncInfo = SyncClient.sync(); show(syncInfo) } catch (e: Exception) { show("Ýalňyşlyk: ${e.message}") }
                }
            }) { Text("Sakla we sinhronlaşdyr") }
            if (syncInfo.isNotBlank()) Text("Soňky: $syncInfo", style = MaterialTheme.typography.bodySmall)

            Section(if (Platform.isDesktop) "🖨 Çek printeri" else "🖨 Çek printeri (Bluetooth)")
            Text("Saýlanan: " + printerName.ifBlank { "ýok" })
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = loadPrinters) { Text("Printerleri gözle") }
                OutlinedButton(enabled = p.printerAddress.isNotBlank(), onClick = testPrint) { Text("Synag çap") }
            }
            devices.forEach { (n, addr) ->
                TextButton(onClick = { p.printerAddress = addr; p.printerName = n; printerName = n; devices = emptyList() }) {
                    Text("🖨 $n")
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(selected = chars == 32, onClick = { chars = 32; p.paperChars = 32 }, label = { Text("58 mm") })
                FilterChip(selected = chars == 48, onClick = { chars = 48; p.paperChars = 48 }, label = { Text("80 mm") })
            }

            Section("🔑 Lisenziýa")
            Text(license.message)
            Text("Enjam kody: ${LicenseManager.deviceCode()}", style = MaterialTheme.typography.bodySmall)
            OutlinedButton(onClick = openLicense) { Text("Täze açar girizmek") }
        }
    }
}

// ======================= ÄTIÝAÇLYK =======================

@Composable
fun BackupScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val show = toast()
    var busy by remember { mutableStateOf(false) }
    var pending by remember { mutableStateOf<ByteArray?>(null) }

    val saver = rememberFileSaver()
    val opener = rememberFileOpener { bytes -> pending = bytes }

    Screen("💾 Ätiýaçlyk", onBack) { pad ->
        Column(Modifier.padding(pad).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ätiýaçlyk nusga almak", style = MaterialTheme.typography.titleMedium)
                    Text("Ähli harytlar, ammar, fakturalar, müşderiler, tölegler we ulanyjylar bir faýla ýazylýar. " +
                        "Faýly fleşka, Telegram ýa-da başga ýere saklaň.", style = MaterialTheme.typography.bodySmall)
                    Button(enabled = !busy, onClick = {
                        busy = true
                        scope.launch {
                            try {
                                val bytes = Repo.backupBytes()
                                val name = "sowdahasap_" + java.text.SimpleDateFormat("yyyy-MM-dd_HHmm", java.util.Locale.US)
                                    .format(java.util.Date()) + ".json"
                                saver(name, "application/json", bytes)
                            } catch (e: Exception) { show("Ýalňyşlyk: ${e.message}") }
                            busy = false
                        }
                    }) { Text("💾 Ätiýaçlyk faýlyny döret") }
                }
            }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ätiýaçlykdan dikeltmek", style = MaterialTheme.typography.titleMedium)
                    Text("⚠️ Häzirki ähli maglumatlar pozular we faýldaky maglumatlar bilen çalşylar.",
                        color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    OutlinedButton(enabled = !busy, onClick = opener) { Text("📂 Faýly saýla we dikelt") }
                }
            }
        }
    }

    pending?.let { bytes ->
        ConfirmDialog("Dikeltmek", "Häzirki maglumatlar pozulyp, faýldakylar ýazylar. Dowam etmelimi?",
            onConfirm = {
                busy = true
                scope.launch {
                    try {
                        val n = Repo.restore(bytes)
                        show("Dikeldildi: $n ýazgy. Täzeden giriň.")
                        Session.user = null
                    } catch (e: Exception) { show("Faýl okalmady: ${e.message}") }
                    busy = false
                }
            },
            onDismiss = { pending = null })
    }
}

@Composable
private fun Section(t: String) {
    HorizontalDivider(Modifier.padding(top = 8.dp))
    Text(t, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
}

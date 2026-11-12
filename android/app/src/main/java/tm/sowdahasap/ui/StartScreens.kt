@file:OptIn(ExperimentalMaterial3Api::class)

package tm.sowdahasap.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import tm.sowdahasap.App
import tm.sowdahasap.Perm
import tm.sowdahasap.Role
import tm.sowdahasap.Session
import tm.sowdahasap.data.Repo
import tm.sowdahasap.license.LicenseManager
import tm.sowdahasap.sync.SyncClient

// ======================= LISENZIÝA =======================

@Composable
fun LicenseScreen(status: LicenseManager.Status, onBack: (() -> Unit)? = null, onChanged: () -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val show = toast()
    val device = remember { LicenseManager.deviceCode(ctx) }
    var key by remember { mutableStateOf("") }
    var server by remember { mutableStateOf(App.prefs.serverUrl) }
    var busy by remember { mutableStateOf(false) }
    val scan = rememberScanner { key = it }

    Screen("Lisenziýa", onBack = onBack) { pad ->
        Column(
            Modifier.padding(pad).padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Card(colors = CardDefaults.cardColors(
                containerColor = if (status.valid) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
            )) {
                Text(status.message, Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
            }
            Text("Enjamyňyzyň kody:")
            Text(
                device, fontFamily = FontFamily.Monospace, fontSize = 24.sp,
                modifier = Modifier.fillMaxWidth().clickable {
                    val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.setPrimaryClip(ClipData.newPlainText("device", device))
                    show("Göçürildi")
                },
                textAlign = TextAlign.Center,
            )
            Text("Bu kody satyja ugradyň (basyp göçürip bilersiňiz). Ol size aýlyk açar berer.",
                style = MaterialTheme.typography.bodySmall)

            Field(key, { key = it }, "Lisenziýa açary", singleLine = false)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    val err = LicenseManager.activate(ctx, key)
                    if (err == null) { show("Lisenziýa işjeňleşdirildi"); onChanged() } else show(err)
                }, enabled = key.isNotBlank()) { Text("Işjeňleşdir") }
                OutlinedButton(onClick = scan) { Text("QR-dan okat") }
            }

            Spacer(Modifier.height(8.dp))
            Text("Ýa-da serwerden awtomatik al:", style = MaterialTheme.typography.titleSmall)
            Field(server, { server = it }, "Serwer salgysy (http://...)")
            Button(enabled = !busy && server.startsWith("http"), onClick = {
                busy = true
                App.prefs.serverUrl = server
                scope.launch {
                    val err = LicenseManager.fetchOnline(ctx, server)
                    busy = false
                    if (err == null) { show("Lisenziýa serwerden alyndy"); onChanged() } else show(err)
                }
            }) { Text(if (busy) "Garaşyň..." else "Onlaýn al") }
        }
    }
}

// ======================= GIRIŞ =======================

@Composable
fun LoginScreen() {
    val scope = rememberCoroutineScope()
    val show = toast()
    var firstRun by remember { mutableStateOf<Boolean?>(null) }
    var name by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var pass2 by remember { mutableStateOf("") }
    var serverDialog by remember { mutableStateOf(false) }
    var lastTry by remember { mutableStateOf(0L) }
    var reload by remember { mutableStateOf(0) }

    LaunchedEffect(reload) { firstRun = App.dao.userCount() == 0 }

    Screen(App.prefs.company) { pad ->
        Column(
            Modifier.padding(pad).padding(24.dp).fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (firstRun) {
                null -> Text("...")
                true -> {
                    Text("Ilkinji gezek: administrator dörediň", style = MaterialTheme.typography.titleMedium)
                    Field(name, { name = it }, "Adyňyz")
                    Field(login, { login = it }, "Login")
                    Field(pass, { pass = it }, "Parol", password = true)
                    Field(pass2, { pass2 = it }, "Paroly gaýtalaň", password = true)
                    Button(modifier = Modifier.fillMaxWidth(), onClick = {
                        when {
                            name.isBlank() || login.isBlank() -> show("Ady we logini dolduryň")
                            pass.length < 4 -> show("Parol azyndan 4 simwol bolmaly")
                            pass != pass2 -> show("Parollar gabat gelmeýär")
                            else -> scope.launch {
                                Session.user = Repo.saveUser(null, name, login, pass, Role.ADMIN, true)
                            }
                        }
                    }) { Text("Döret we gir") }
                    TextButton(onClick = { serverDialog = true }) { Text("Ýa-da serwerdäki ulanyjylary ýükle") }
                }
                false -> {
                    Text("Ulgama giriň", style = MaterialTheme.typography.titleLarge)
                    Field(login, { login = it }, "Login")
                    Field(pass, { pass = it }, "Parol", password = true)
                    Button(modifier = Modifier.fillMaxWidth(), onClick = {
                        val t = System.currentTimeMillis()
                        if (t - lastTry < 3000) {
                            show("Biraz garaşyň")
                        } else {
                            lastTry = t
                            scope.launch {
                                val u = Repo.login(login, pass)
                                if (u == null) show("Login ýa-da parol nädogry") else Session.user = u
                            }
                        }
                    }) { Text("Gir") }
                    TextButton(onClick = { serverDialog = true }) { Text("Serwer sazlamalary / sinhronlaşdyr") }
                }
            }
        }
    }

    if (serverDialog) ServerDialog(onDismiss = { serverDialog = false; reload++ })
}

@Composable
fun ServerDialog(onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    val show = toast()
    var url by remember { mutableStateOf(App.prefs.serverUrl) }
    var token by remember { mutableStateOf(App.prefs.syncToken) }
    var busy by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Serwer") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Field(url, { url = it }, "Salgy (mysal: http://192.168.1.10:8080)")
                Field(token, { token = it }, "Token", password = true)
                Text(App.prefs.lastSyncInfo, style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            TextButton(enabled = !busy, onClick = {
                App.prefs.serverUrl = url; App.prefs.syncToken = token
                busy = true
                scope.launch {
                    try { show(SyncClient.sync()) } catch (e: Exception) { show("Ýalňyşlyk: ${e.message}") }
                    busy = false
                }
            }) { Text(if (busy) "Garaşyň..." else "Sinhronlaşdyr") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Ýap") } },
    )
}

// ======================= BAŞ EKRAN =======================

private data class Tile(val title: String, val emoji: String, val route: String, val perm: Perm?)

private val tiles = listOf(
    Tile("Satuw / Faktura", "🧾", "invoices", Perm.SALES),
    Tile("Harytlar", "📦", "products", null),
    Tile("Ammar", "🏬", "stock", Perm.STOCK),
    Tile("Müşderiler", "👥", "customers", Perm.CUSTOMERS),
    Tile("Bergi SMS", "✉️", "sms", Perm.SMS),
    Tile("Hasabatlar", "📊", "reports", Perm.REPORTS),
    Tile("Ulanyjylar", "🔐", "users", Perm.USERS),
    Tile("Sazlamalar", "⚙️", "settings", Perm.SETTINGS),
)

@Composable
fun HomeScreen(license: LicenseManager.Status, go: (String) -> Unit) {
    val scope = rememberCoroutineScope()
    val show = toast()
    var syncing by remember { mutableStateOf(false) }
    val user = Session.user

    Screen(
        App.prefs.company,
        actions = {
            if (Session.can(Perm.SYNC)) IconButton(enabled = !syncing, onClick = {
                syncing = true
                scope.launch {
                    try { show(SyncClient.sync()) } catch (e: Exception) { show("Sinhron ýalňyşlygy: ${e.message}") }
                    syncing = false
                }
            }) { Icon(Icons.Default.Refresh, "Sinhronlaşdyr") }
            IconButton(onClick = { Session.user = null }) { Icon(Icons.AutoMirrored.Filled.ExitToApp, "Çyk") }
        },
    ) { pad ->
        Column(Modifier.padding(pad).padding(12.dp)) {
            Text("${user?.name} · ${Session.role?.title}", style = MaterialTheme.typography.bodyMedium)
            if (license.warn) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { go("license") },
                ) {
                    Text("⚠️ Lisenziýanyň gutarmagyna ${license.daysLeft} gün galdy. Täze açar giriziň.",
                        Modifier.padding(12.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(tiles.filter { it.perm == null || Session.can(it.perm) }) { t ->
                    Card(Modifier.fillMaxWidth().height(110.dp).clickable { go(t.route) }) {
                        Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(t.emoji, fontSize = 32.sp)
                            Text(t.title, style = MaterialTheme.typography.titleSmall, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}

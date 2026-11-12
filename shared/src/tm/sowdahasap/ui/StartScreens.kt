@file:OptIn(ExperimentalMaterial3Api::class)

package tm.sowdahasap.ui

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
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
import tm.sowdahasap.platform.Platform
import tm.sowdahasap.platform.rememberScanner

// ======================= LISENZIÝA =======================

@Composable
fun LicenseScreen(status: LicenseManager.Status, onBack: (() -> Unit)? = null, onChanged: () -> Unit) {
    val scope = rememberCoroutineScope()
    val show = toast()
    val clipboard = LocalClipboardManager.current
    val device = remember { LicenseManager.deviceCode() }
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
                    clipboard.setText(AnnotatedString(device))
                    show("Göçürildi")
                },
                textAlign = TextAlign.Center,
            )
            Text("Bu kody satyja ugradyň (basyp göçürip bilersiňiz). Ol size aýlyk açar berer.",
                style = MaterialTheme.typography.bodySmall)

            Field(key, { key = it }, "Lisenziýa açary", singleLine = false)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    val err = LicenseManager.activate(key)
                    if (err == null) { show("Lisenziýa işjeňleşdirildi"); onChanged() } else show(err)
                }, enabled = key.isNotBlank()) { Text("Işjeňleşdir") }
                if (scan != null) OutlinedButton(onClick = scan) { Text("QR-dan okat") }
                OutlinedButton(onClick = { clipboard.getText()?.text?.let { key = it.trim() } }) { Text("Goý") }
            }

            Spacer(Modifier.height(8.dp))
            Text("Ýa-da serwerden awtomatik al:", style = MaterialTheme.typography.titleSmall)
            Field(server, { server = it }, "Serwer salgysy (http://...)")
            Button(enabled = !busy && server.startsWith("http"), onClick = {
                busy = true
                App.prefs.serverUrl = server
                scope.launch {
                    val err = LicenseManager.fetchOnline(server)
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


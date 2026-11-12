@file:OptIn(ExperimentalMaterial3Api::class)

package tm.sowdahasap.keygen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

/** Söwda Hasap lisenziýa açar generatory (internetsiz) */
@Composable
fun KeygenApp(dataDir: File) {
    val store = remember { KeygenStore(dataDir) }
    var sealed by remember { mutableStateOf(store.loadKey()) }
    var unlocked by remember { mutableStateOf<Unlocked?>(null) }
    val snack = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val show: (String) -> Unit = { m -> scope.launch { snack.showSnackbar(m) } }

    androidx.compose.material3.MaterialTheme(colorScheme = lightColorScheme(primary = Color(0xFF4A148C))) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("🔑 Açar generatory") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    actions = {
                        if (unlocked != null) TextButton(onClick = { unlocked = null }) {
                            Text("🔒 Gulpla", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    },
                )
            },
            snackbarHost = { SnackbarHost(snack) },
        ) { pad ->
            Box(Modifier.padding(pad).fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                Box(Modifier.widthIn(max = 720.dp).fillMaxWidth()) {
                    val s = sealed
                    val u = unlocked
                    when {
                        s == null -> SetupScreen(store, show) { newSealed, newUnlocked -> sealed = newSealed; unlocked = newUnlocked }
                        u == null -> UnlockScreen(s, show) { unlocked = it }
                        else -> MainScreen(store, s, u, show)
                    }
                }
            }
        }
    }
}

@Composable
private fun PasswordField(value: String, onChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value, onChange, label = { Text(label) }, singleLine = true, modifier = Modifier.fillMaxWidth(),
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    )
}

@Composable
private fun SetupScreen(store: KeygenStore, show: (String) -> Unit, onReady: (SealedKey, Unlocked) -> Unit) {
    val scope = rememberCoroutineScope()
    var p1 by remember { mutableStateOf("") }
    var p2 by remember { mutableStateOf("") }
    var importText by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current

    Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Ilkinji gezek: generatory sazlaň", style = MaterialTheme.typography.titleLarge)
        Text("Gizlin açar şu enjamda parol bilen şifrlenip saklanýar. Paroly ýatdan çykarsaňyz, açary dikeldip bolmaz!",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
        PasswordField(p1, { p1 = it }, "Parol (azyndan 8 simwol)")
        PasswordField(p2, { p2 = it }, "Paroly gaýtalaň")

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("1. Täze açar jübütini döretmek", style = MaterialTheme.typography.titleMedium)
                Text("Diňe BIR GEZEK, ilkinji enjamda ediň. Soňra açyk açary Söwda Hasap programmasyna goýmaly.",
                    style = MaterialTheme.typography.bodySmall)
                Button(enabled = !busy, onClick = {
                    when {
                        p1.length < 8 -> show("Parol azyndan 8 simwol bolmaly")
                        p1 != p2 -> show("Parollar gabat gelmeýär")
                        else -> {
                            busy = true
                            scope.launch {
                                val (s, u) = withContext(Dispatchers.Default) { KeyCrypto.newKey(p1) }
                                store.saveKey(s)
                                busy = false
                                onReady(s, u)
                            }
                        }
                    }
                }) { Text(if (busy) "Garaşyň..." else "Täze açar döret") }
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("2. Ýa-da başga enjamdan geçirmek", style = MaterialTheme.typography.titleMedium)
                Text("Beýleki generatoryň «Sazlama → Açary eksport et» tekstini goýuň. Parol şol enjamdaky parol.",
                    style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(importText, { importText = it }, label = { Text("SHKEY1.... tekst") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { clipboard.getText()?.text?.let { importText = it.trim() } }) { Text("Goý") }
                    Button(enabled = !busy, onClick = {
                        val s = KeyCrypto.importText(importText)
                        if (s == null) { show("Tekst nädogry"); return@Button }
                        busy = true
                        scope.launch {
                            val u = withContext(Dispatchers.Default) { KeyCrypto.unseal(s, p1) }
                            busy = false
                            if (u == null) show("Parol nädogry") else { store.saveKey(s); onReady(s, u) }
                        }
                    }) { Text("Import et") }
                }
            }
        }
    }
}

@Composable
private fun UnlockScreen(s: SealedKey, show: (String) -> Unit, onUnlock: (Unlocked) -> Unit) {
    val scope = rememberCoroutineScope()
    var pw by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Paroly giriziň", style = MaterialTheme.typography.titleLarge)
        PasswordField(pw, { pw = it }, "Parol")
        Button(enabled = !busy && pw.isNotEmpty(), modifier = Modifier.fillMaxWidth(), onClick = {
            busy = true
            scope.launch {
                val u = withContext(Dispatchers.Default) { KeyCrypto.unseal(s, pw) }
                if (u == null) { delay(1500); show("Parol nädogry") } else onUnlock(u)
                busy = false
            }
        }) { Text(if (busy) "Barlanýar..." else "Aç") }
    }
}

@Composable
private fun MainScreen(store: KeygenStore, sealed: SealedKey, k: Unlocked, show: (String) -> Unit) {
    var tab by remember { mutableStateOf(0) }
    var history by remember { mutableStateOf(store.history()) }
    Column(Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tab) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Açar ber") })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Taryh (${history.size})") })
            Tab(selected = tab == 2, onClick = { tab = 2 }, text = { Text("Sazlama") })
        }
        when (tab) {
            0 -> IssueTab(k, show) { i -> store.addHistory(i); history = store.history() }
            1 -> HistoryTab(history, show)
            else -> SettingsTab(sealed, k, show)
        }
    }
}

@Composable
private fun IssueTab(k: Unlocked, show: (String) -> Unit, onIssued: (Issued) -> Unit) {
    val clipboard = LocalClipboardManager.current
    var device by remember { mutableStateOf("") }
    var months by remember { mutableStateOf(1) }
    var customDate by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<Issued?>(null) }

    Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(device, { device = it }, label = { Text("Enjam kody (XXXX-XXXX-XXXX-XXXX)") },
            singleLine = true, modifier = Modifier.fillMaxWidth(),
            trailingIcon = { TextButton(onClick = { clipboard.getText()?.text?.let { device = it.trim() } }) { Text("Goý") } })
        Text("Möhlet:")
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(1, 2, 3, 6, 12).forEach { m ->
                FilterChip(selected = months == m && customDate.isBlank(), onClick = { months = m; customDate = "" },
                    label = { Text("$m aý") })
            }
        }
        OutlinedTextField(customDate, { customDate = it }, label = { Text("Ýa-da anyk sene (2026-12-31)") },
            singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(note, { note = it }, label = { Text("Bellik (müşderiniň ady, dükan)") },
            singleLine = true, modifier = Modifier.fillMaxWidth())
        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            val dev = KeyCrypto.normDevice(device)
            val expiry = if (customDate.isNotBlank()) runCatching { LocalDate.parse(customDate.trim()) }.getOrNull()
            else LocalDate.now().plusMonths(months.toLong())
            when {
                dev == null -> show("Enjam kody 16 simwol bolmaly (0-9, A-F)")
                expiry == null -> show("Sene nädogry, mysal: 2026-12-31")
                expiry.isBefore(LocalDate.now()) -> show("Sene geçipdir")
                else -> {
                    val key = KeyCrypto.makeLicense(k, dev, expiry)
                    val i = Issued(dev, expiry.toString(), note.trim(), key, System.currentTimeMillis())
                    onIssued(i)
                    result = i
                    clipboard.setText(AnnotatedString(key))
                    show("Açar döredildi we göçürildi")
                }
            }
        }) { Text("🔑 Açar döret") }

        result?.let { r ->
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${r.device} · ${r.expiry} çenli", style = MaterialTheme.typography.titleSmall)
                    Text(r.key, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                    Button(onClick = { clipboard.setText(AnnotatedString(r.key)); show("Göçürildi") }) { Text("📋 Göçür") }
                }
            }
        }
    }
}

@Composable
private fun HistoryTab(history: List<Issued>, show: (String) -> Unit) {
    val clipboard = LocalClipboardManager.current
    var q by remember { mutableStateOf("") }
    val fmt = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US) }
    val list = history.filter { q.isBlank() || it.note.contains(q, true) || it.device.contains(q, true) }
    Column(Modifier.padding(horizontal = 12.dp)) {
        OutlinedTextField(q, { q = it }, label = { Text("Gözleg (ady ýa-da enjam kody)") }, singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
        LazyColumn(Modifier.fillMaxSize()) {
            items(list) { i ->
                ListItem(
                    headlineContent = { Text(i.note.ifBlank { i.device }) },
                    supportingContent = { Text("${i.device} · ${i.expiry} çenli · berildi ${fmt.format(Date(i.created))}") },
                    trailingContent = { Text("📋") },
                    modifier = Modifier.clickable { clipboard.setText(AnnotatedString(i.key)); show("Açar göçürildi") },
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun SettingsTab(sealed: SealedKey, k: Unlocked, show: (String) -> Unit) {
    val clipboard = LocalClipboardManager.current
    var showExport by remember { mutableStateOf(false) }
    Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Açyk açar", style = MaterialTheme.typography.titleMedium)
                Text("Söwda Hasap programmasyna şu açar goýulmaly (LicenseManager.kt → PUBLIC_KEY). Bu gizlin däl.",
                    style = MaterialTheme.typography.bodySmall)
                Text(k.pubB64, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                Button(onClick = { clipboard.setText(AnnotatedString(k.pubB64)); show("Göçürildi") }) { Text("📋 Göçür") }
            }
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Açary başga enjama geçirmek", style = MaterialTheme.typography.titleMedium)
                Text("Tekst parolyňyz bilen şifrlenen. Beýleki enjamda «Import et» bilen şol parol bilen açyň. " +
                    "Ony diňe özüňiz saklaň!", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                if (!showExport) OutlinedButton(onClick = { showExport = true }) { Text("Açary eksport et") }
                else {
                    val text = KeyCrypto.exportText(sealed)
                    Text(text, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                    Button(onClick = { clipboard.setText(AnnotatedString(text)); show("Göçürildi") }) { Text("📋 Göçür") }
                }
            }
        }
    }
}

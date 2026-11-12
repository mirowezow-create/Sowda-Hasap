package tm.sowdahasap.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.launch
import tm.sowdahasap.App
import tm.sowdahasap.Msg
import tm.sowdahasap.Perm
import tm.sowdahasap.Session
import tm.sowdahasap.license.LicenseManager
import tm.sowdahasap.platform.Platform
import tm.sowdahasap.platform.PlatformBackHandler
import tm.sowdahasap.sync.SyncClient

/** Açyk sahypalar (tab) */
object Tabs {
    class Tab(val id: Long, val route: String, title: String) {
        var title by mutableStateOf(title)
    }

    val list = mutableStateListOf<Tab>()
    var current by mutableStateOf<Long?>(null)
    private var seq = 0L

    fun titleOf(route: String): String = when (route.substringBefore('/')) {
        "invoice" -> if (route.endsWith("/new")) "Faktura - Adaty" else "Faktura"
        "invoices" -> "Fakturalar"
        "cash" -> "Kassa - Adaty"
        "cashbooks" -> "Depderler"
        "products" -> "Harytlar"
        "product" -> "Haryt"
        "customers" -> "Müşderiler"
        "customer" -> "Müşderi"
        "units" -> "Ölçegler"
        "warehouses" -> "Ambarlar"
        "users" -> "Ulanyjylar"
        "reports" -> "Hasabatlar"
        "stock" -> "Ambar hereketi"
        "sms" -> "Bergi SMS"
        "backup" -> "Ätiýaçlyk"
        "settings" -> "Sazlaýyşlar"
        "license" -> "Aýlyk açar"
        else -> route
    }

    /** Sahypa açmak. Şol bir sahypa açyk bolsa, oňa geçýär (täze faktura hemişe täze sahypa). */
    fun open(route: String, title: String = titleOf(route)) {
        val existing = if (route.endsWith("/new")) null else list.firstOrNull { it.route == route }
        if (existing != null) { current = existing.id; return }
        val t = Tab(++seq, route, title)
        list.add(t)
        current = t.id
    }

    fun close(id: Long) {
        val i = list.indexOfFirst { it.id == id }
        if (i < 0) return
        list.removeAt(i)
        if (current == id) current = list.getOrNull((i - 1).coerceAtLeast(0))?.id ?: list.lastOrNull()?.id
    }

    fun closeAll() { list.clear(); current = null }
}

private val months = listOf("Ýanwar", "Fewral", "Mart", "Aprel", "Maý", "Iýun", "Iýul", "Awgust", "Sentýabr", "Oktýabr", "Noýabr", "Dekabr")

@Composable
fun Shell(license: LicenseManager.Status, onLicense: () -> Unit) {
    var menuOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var syncing by remember { mutableStateOf(false) }
    val currentTab = Tabs.list.firstOrNull { it.id == Tabs.current }

    PlatformBackHandler(enabled = currentTab != null) { currentTab?.let { Tabs.close(it.id) } }

    Column(Modifier.fillMaxSize()) {
        // ---- Gök başlyk ----
        Row(Modifier.fillMaxWidth().background(Pal.TitleBlue).padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically) {
            val exp = license.expiry
            val keyText = if (exp != null) "Aýyň açary : ${months[exp.monthValue - 1]} ${exp.year}" else "Açar ýok"
            Text("$keyText | ${App.prefs.company} | ${Session.user?.name ?: ""}", color = Color.White, fontSize = 15.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            if (license.warn) Text("⚠ ${license.daysLeft} gün", color = Color(0xFFFFE082), fontSize = 13.sp,
                modifier = Modifier.clickable { Tabs.open("license") }.padding(horizontal = 8.dp))
            if (Session.can(Perm.SYNC) && SyncClient.configured) Text(if (syncing) "⏳" else "🔄", fontSize = 16.sp,
                modifier = Modifier.clickable(enabled = !syncing) {
                    syncing = true
                    scope.launch {
                        try { Msg.show(SyncClient.sync()) } catch (e: Exception) { Msg.show("Sinhron: ${e.message}") }
                        syncing = false
                    }
                }.padding(horizontal = 8.dp))
        }
        // ---- Sahypalar (tab) ----
        Row(Modifier.fillMaxWidth().background(Color(0xFFE3F0F6)).horizontalScroll(rememberScrollState()).padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Tabs.list.forEach { t ->
                val active = t.id == Tabs.current
                Row(Modifier.clickable { Tabs.current = t.id }.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(t.title, color = if (active) Pal.TabActive else Pal.TabInactive, fontSize = 15.sp,
                        fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal)
                    Text(" ✕", color = Pal.TabInactive, fontSize = 12.sp, modifier = Modifier.clickable { Tabs.close(t.id) })
                }
            }
            if (Tabs.list.isEmpty()) Text("α menýudan sahypa açyň", color = Pal.TabInactive, fontSize = 14.sp,
                modifier = Modifier.padding(8.dp))
        }
        // ---- Sahypalaryň içi ----
        PatternBackground(Modifier.weight(1f).fillMaxWidth()) {
            if (Tabs.list.isEmpty()) StartTiles()
            // Ähli açyk sahypalar ýatda saklanýar, diňe saýlanany görünýär
            Tabs.list.forEach { t ->
                key(t.id) {
                    Box(if (t.id == Tabs.current) Modifier.fillMaxSize() else Modifier.size(0.dp)) {
                        TabContent(t, license, onLicense)
                    }
                }
            }
        }
        // ---- Aşaky zolak ----
        Box {
            Row(Modifier.fillMaxWidth().background(Pal.Status).padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(34.dp).background(Color.White, RoundedCornerShape(4.dp))
                    .border(2.dp, Color(0xFF2E7D32), RoundedCornerShape(4.dp)).clickable { menuOpen = !menuOpen },
                    contentAlignment = Alignment.Center) {
                    Text("α", fontSize = 22.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                }
                Text("  Soňky waka : " + (Msg.text ?: App.prefs.lastSyncInfo), color = Color.White, fontSize = 12.sp,
                    maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                Text("Söwda Hasap v1.2", color = Color.White, fontSize = 11.sp)
            }
            if (menuOpen) AlphaMenu(onClose = { menuOpen = false })
        }
    }
}

@Composable
private fun TabContent(t: Tabs.Tab, license: LicenseManager.Status, onLicense: () -> Unit) {
    val close = { Tabs.close(t.id) }
    val route = t.route
    val arg = route.substringAfter('/', "")
    when (route.substringBefore('/')) {
        "invoice" -> InvoiceAdatyScreen(arg.ifEmpty { "new" }, onTitle = { t.title = it })
        "invoices" -> InvoicesScreen(close, open = { id -> Tabs.open("invoice/$id") })
        "cash" -> CashScreen()
        "cashbooks" -> CashbooksScreen()
        "products" -> ProductsListScreen()
        "product" -> ProductCardScreen(arg.ifEmpty { "new" }, onTitle = { t.title = it }, onClose = close)
        "customers" -> CustomersListScreen()
        "customer" -> CustomerCardScreen(arg.ifEmpty { "new" }, onTitle = { t.title = it })
        "units" -> UnitsScreen()
        "warehouses" -> WarehousesScreen()
        "users" -> UsersScreen(close)
        "reports" -> ReportsScreen(close)
        "stock" -> StockScreen(close)
        "sms" -> DebtSmsScreen(close)
        "backup" -> BackupScreen(close)
        "settings" -> SettingsScreen(close, openLicense = { Tabs.open("license") })
        "license" -> LicenseScreen(license, onBack = close) { onLicense(); close() }
        else -> Text("?")
    }
}

// ---------------- α menýu ----------------

private class MenuItem(val icon: String, val title: String, val route: String? = null, val perm: Perm? = null,
                       val action: (() -> Unit)? = null, val children: List<MenuItem> = emptyList())

private fun menu(): List<MenuItem> = listOf(
    MenuItem("🛒", "Faktura", children = listOf(
        MenuItem("🧾", "Faktura - Adaty", "invoice/new", Perm.SALES),
        MenuItem("📋", "Fakturalar", "invoices", Perm.SALES),
        MenuItem("🏬", "Ambar hereketi", "stock", Perm.STOCK),
    )),
    MenuItem("💲", "Kassa", children = listOf(
        MenuItem("💵", "Kassa - Adaty", "cash", Perm.PAYMENTS),
        MenuItem("📒", "Depderler", "cashbooks", Perm.PAYMENTS),
    )),
    MenuItem("🗃", "Kartoçkalar", children = listOf(
        MenuItem("📦", "Harytlar", "products"),
        MenuItem("👥", "Müşderiler", "customers", Perm.CUSTOMERS),
        MenuItem("📏", "Ölçegler", "units", Perm.PRODUCTS_EDIT),
        MenuItem("🏬", "Ambarlar", "warehouses", Perm.SETTINGS),
        MenuItem("📒", "Depderler", "cashbooks", Perm.SETTINGS),
        MenuItem("🔐", "Ulanyjylar", "users", Perm.USERS),
    )),
    MenuItem("📊", "Hasabatlar", children = listOfNotNull(
        MenuItem("📈", "Hasabatlar", "reports", Perm.REPORTS),
        MenuItem("👥", "Müşderileriň bergileri", "customers", Perm.CUSTOMERS),
        if (Platform.supportsSms) MenuItem("✉️", "Bergi SMS", "sms", Perm.SMS) else null,
    )),
    MenuItem("⚙", "Sazlaýyşlar", "settings", Perm.SETTINGS),
    MenuItem("🗄", "Başga", children = listOf(
        MenuItem("💾", "Ätiýaçlyk kopiýa", "backup", Perm.BACKUP),
        MenuItem("🔑", "Ýyllyk / Aýlyk açar", "license"),
    )),
    MenuItem("👤", "Başga ulanyja geç", action = { Tabs.closeAll(); Session.user = null }),
    MenuItem("🚪", "Çykyş", action = { Tabs.closeAll(); Session.user = null; Platform.exitApp() }),
)

private fun allowed(m: MenuItem): Boolean = m.perm == null || Session.can(m.perm)

@Composable
private fun AlphaMenu(onClose: () -> Unit) {
    var openCat by remember { mutableStateOf<MenuItem?>(null) }
    val items = menu().mapNotNull { m ->
        if (m.children.isEmpty()) m.takeIf { allowed(it) }
        else MenuItem(m.icon, m.title, children = m.children.filter { allowed(it) }).takeIf { it.children.isNotEmpty() }
    }
    fun run(m: MenuItem) {
        when {
            m.action != null -> { onClose(); m.action.invoke() }
            m.route != null -> { onClose(); Tabs.open(m.route) }
            else -> openCat = m
        }
    }
    Popup(alignment = Alignment.BottomStart, offset = IntOffset(0, -46), onDismissRequest = onClose,
        properties = PopupProperties(focusable = true)) {
        Row(verticalAlignment = Alignment.Bottom) {
            Surface(shadowElevation = 10.dp, color = Color(0xFFF7F7F7)) {
                Column(Modifier.width(250.dp).verticalScroll(rememberScrollState())) {
                    items.forEach { m ->
                        MenuRow(m.icon, m.title, hasChildren = m.children.isNotEmpty(), selected = openCat?.title == m.title) { run(m) }
                    }
                }
            }
            openCat?.let { cat ->
                Surface(shadowElevation = 10.dp, color = Color(0xFFF7F7F7)) {
                    Column(Modifier.widthIn(min = 230.dp, max = 320.dp)) {
                        cat.children.forEach { c -> MenuRow(c.icon, c.title, false, false) { run(c) } }
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuRow(icon: String, title: String, hasChildren: Boolean, selected: Boolean, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().background(if (selected) Color(0xFFDDE6EE) else Color.Transparent).clickable(onClick = onClick)
        .padding(horizontal = 14.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(icon, fontSize = 22.sp, modifier = Modifier.width(38.dp))
        Text(title, fontSize = 16.sp, color = Pal.Text, modifier = Modifier.weight(1f))
        if (hasChildren) Text("▸", color = Pal.Muted)
    }
    HorizontalDivider(color = Color(0x14000000))
}

/** Hiç sahypa açyk däl wagty: çalt düwmeler */
@Composable
private fun StartTiles() {
    val tiles = listOfNotNull(
        Triple("🧾", "Faktura - Adaty", "invoice/new").takeIf { Session.can(Perm.SALES) },
        Triple("💵", "Kassa - Adaty", "cash").takeIf { Session.can(Perm.PAYMENTS) },
        Triple("📦", "Harytlar", "products"),
        Triple("👥", "Müşderiler", "customers").takeIf { Session.can(Perm.CUSTOMERS) },
        Triple("📋", "Fakturalar", "invoices").takeIf { Session.can(Perm.SALES) },
        Triple("📊", "Hasabatlar", "reports").takeIf { Session.can(Perm.REPORTS) },
    )
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("${Session.user?.name} · ${Session.role?.title}", color = Pal.TabActive, fontSize = 18.sp)
        Spacer(Modifier.height(16.dp))
        tiles.chunked(3).forEach { rowTiles ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(bottom = 12.dp)) {
                rowTiles.forEach { (icon, title, route) ->
                    Column(Modifier.size(width = 150.dp, height = 110.dp).background(Color(0xEEFFFFFF), RoundedCornerShape(6.dp))
                        .border(1.dp, Color(0x22000000), RoundedCornerShape(6.dp)).clickable { Tabs.open(route) },
                        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(icon, fontSize = 34.sp)
                        Text(title, fontSize = 14.sp, color = Pal.Text)
                    }
                }
            }
        }
    }
}

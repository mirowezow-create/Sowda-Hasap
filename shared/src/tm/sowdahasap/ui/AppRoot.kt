package tm.sowdahasap.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import tm.sowdahasap.App
import tm.sowdahasap.Msg
import tm.sowdahasap.Session
import tm.sowdahasap.data.Repo
import tm.sowdahasap.license.LicenseManager
import tm.sowdahasap.platform.Platform
import tm.sowdahasap.platform.PlatformBackHandler
import tm.sowdahasap.sync.SyncClient

/** Ýönekeý nawigasiýa: ekranlaryň sanawy */
class Nav {
    val stack = mutableStateListOf("home")
    val current: String get() = stack.last()
    fun go(route: String) { stack.add(route) }
    fun back() { if (stack.size > 1) stack.removeAt(stack.size - 1) }
}

/** Programmanyň kök ekrany — Android we Windows şuny çagyrýar */
@Composable
fun AppRoot() {
    AppTheme {
        var license by remember { mutableStateOf(LicenseManager.status()) }
        val snack = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            Repo.ensureDefaults()
            // Windows: esasy serwer bolsa, açylanda işe girizilýär
            if (Platform.serverSupported && App.prefs.hostServer) {
                Platform.startServer(App.prefs.hostPort, App.prefs.syncToken)
            }
        }
        LaunchedEffect(Msg.seq) {
            Msg.text?.let { snack.showSnackbar(it) }
        }
        // Awtomatik sinhronlaşdyrma (her 2 minutda)
        LaunchedEffect(Session.user != null) {
            while (Session.user != null) {
                if (App.prefs.autoSync && SyncClient.configured) runCatching { SyncClient.sync() }
                delay(120_000)
            }
        }
        // Lisenziýany wagtal-wagtal barla (programma açyk galsa hem)
        LaunchedEffect(Unit) {
            while (true) { delay(600_000); license = LicenseManager.status() }
        }

        Surface(Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxSize()) {
                when {
                    !license.valid -> LicenseScreen(license) { license = LicenseManager.status() }
                    Session.user == null -> LoginScreen()
                    else -> MainNav(license, onLicense = { license = LicenseManager.status() })
                }
                SnackbarHost(snack, Modifier.align(Alignment.BottomCenter).padding(16.dp))
            }
        }
    }
}

@Composable
private fun MainNav(license: LicenseManager.Status, onLicense: () -> Unit) {
    val nav = remember { Nav() }
    PlatformBackHandler(enabled = nav.stack.size > 1) { nav.back() }
    val back: () -> Unit = { nav.back() }
    val r = nav.current
    val arg = r.substringAfter('/', "")
    key(nav.stack.size, r) { when (r.substringBefore('/')) {
        "home" -> HomeScreen(license, go = { nav.go(it) })
        "products" -> ProductsScreen(back, open = { nav.go("product/$it") })
        "product" -> ProductEditScreen(arg.ifEmpty { "new" }, back)
        "stock" -> StockScreen(back)
        "invoices" -> InvoicesScreen(back, open = { nav.go("invoice/$it") })
        "invoice" -> InvoiceEditScreen(arg.ifEmpty { "new" }, back)
        "customers" -> CustomersScreen(back, open = { nav.go("customer/$it") })
        "customer" -> CustomerDetailScreen(arg.ifEmpty { "new" }, back, openInvoice = { nav.go("invoice/$it") })
        "reports" -> ReportsScreen(back)
        "sms" -> DebtSmsScreen(back)
        "users" -> UsersScreen(back)
        "backup" -> BackupScreen(back)
        "settings" -> SettingsScreen(back, openLicense = { nav.go("license") })
        "license" -> LicenseScreen(license, onBack = back) { onLicense(); back() }
        else -> HomeScreen(license, go = { nav.go(it) })
    } }
}

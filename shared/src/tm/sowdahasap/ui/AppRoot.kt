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
                    !license.valid -> PatternBackground { LicenseScreen(license) { license = LicenseManager.status() } }
                    Session.user == null -> PatternBackground { LoginScreen() }
                    else -> Shell(license, onLicense = { license = LicenseManager.status() })
                }
                SnackbarHost(snack, Modifier.align(Alignment.BottomCenter).padding(bottom = 56.dp))
            }
        }
    }
}

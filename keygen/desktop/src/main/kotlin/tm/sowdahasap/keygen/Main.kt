package tm.sowdahasap.keygen

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.io.File

fun main() {
    val base = System.getenv("APPDATA")?.let { File(it) } ?: File(System.getProperty("user.home"), ".config")
    val dir = File(base, "SowdaHasapKeygen")
    application {
        Window(onCloseRequest = ::exitApplication, title = "Söwda Hasap — Açar generatory",
            state = rememberWindowState(width = 760.dp, height = 720.dp)) {
            KeygenApp(dir)
        }
    }
}

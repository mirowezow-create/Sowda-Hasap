package tm.sowdahasap

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import tm.sowdahasap.data.AppDatabase
import tm.sowdahasap.data.AppDatabase_Impl
import tm.sowdahasap.data.MIGRATION_1_2
import tm.sowdahasap.platform.FileKeyValueStore
import tm.sowdahasap.platform.LanServer
import tm.sowdahasap.platform.Platform
import tm.sowdahasap.ui.AppRoot
import tm.sowdahasap.util.Prefs
import java.io.File

fun main() {
    val dir = Platform.dataDir()
    val db = Room.databaseBuilder<AppDatabase>(
        name = File(dir, "sowdahasap.db").absolutePath,
        factory = { AppDatabase_Impl() },
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .addMigrations(MIGRATION_1_2)
        .build()
    App.init(db, Prefs(FileKeyValueStore(File(dir, "settings.properties"))))

    application {
        Window(
            onCloseRequest = { LanServer.stop(); exitApplication() },
            title = "Söwda Hasap",
            state = rememberWindowState(width = 1100.dp, height = 760.dp),
        ) {
            AppRoot()
        }
    }
}

package tm.sowdahasap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import tm.sowdahasap.data.AppDao
import tm.sowdahasap.data.AppDatabase
import tm.sowdahasap.data.User
import tm.sowdahasap.util.Prefs

/**
 * Programmanyň umumy ýagdaýy. Android-da we Windows-da platforma başlanda init() çagyrýar.
 *
 * Platforma (android/ we desktop/ modullary) tm.sowdahasap.platform bukjasynda şulary bermeli:
 *   object Platform { name, isDesktop, deviceCode(), printers(), print(), supportsSms, sendSms(),
 *                     dial(), openFile(), exportDir(), serverSupported, startServer(), stopServer(),
 *                     serverRunning(), localAddresses(), invoiceDocument() }
 *   @Composable rememberScanner(), rememberWithPermission(), PlatformBackHandler(),
 *               rememberFileSaver(), rememberFileOpener()
 */
object App {
    lateinit var db: AppDatabase
        private set
    lateinit var prefs: Prefs
        private set

    val dao: AppDao get() = db.dao()

    fun init(database: AppDatabase, preferences: Prefs) {
        db = database
        prefs = preferences
    }
}

enum class Role(val title: String) { ADMIN("Admin"), SELLER("Satyjy"), STOREKEEPER("Ammarçy") }

enum class Perm { PRODUCTS_EDIT, VIEW_COST, STOCK, SALES, CUSTOMERS, PAYMENTS, REPORTS, SMS, USERS, SETTINGS, SYNC, BACKUP }

val rolePerms: Map<Role, Set<Perm>> = mapOf(
    Role.ADMIN to Perm.entries.toSet(),
    Role.SELLER to setOf(Perm.SALES, Perm.CUSTOMERS, Perm.PAYMENTS, Perm.SMS, Perm.SYNC),
    Role.STOREKEEPER to setOf(Perm.PRODUCTS_EDIT, Perm.VIEW_COST, Perm.STOCK, Perm.SYNC),
)

object Session {
    var user by mutableStateOf<User?>(null)

    val role: Role? get() = user?.let { u -> runCatching { Role.valueOf(u.role) }.getOrNull() }

    fun can(p: Perm): Boolean = role?.let { rolePerms[it]?.contains(p) } ?: false

    val userId: String get() = user?.id ?: ""
}

/** Ekranyň aşagynda gysga habar görkezmek (Android-da we Windows-da birmeňzeş) */
object Msg {
    var text by mutableStateOf<String?>(null)
    var seq by mutableStateOf(0)
    fun show(t: String) { text = t; seq++ }
}

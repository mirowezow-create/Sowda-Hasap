package tm.sowdahasap

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.room.Room
import tm.sowdahasap.data.AppDao
import tm.sowdahasap.data.AppDatabase
import tm.sowdahasap.data.User
import tm.sowdahasap.util.Prefs

class App : Application() {
    lateinit var db: AppDatabase
        private set
    lateinit var prefs: Prefs
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        db = Room.databaseBuilder(this, AppDatabase::class.java, "sowdahasap.db").build()
        prefs = Prefs(this)
    }

    companion object {
        lateinit var instance: App
            private set
        val dao: AppDao get() = instance.db.dao()
        val prefs: Prefs get() = instance.prefs
    }
}

enum class Role(val title: String) { ADMIN("Admin"), SELLER("Satyjy"), STOREKEEPER("Ammarçy") }

enum class Perm { PRODUCTS_EDIT, VIEW_COST, STOCK, SALES, CUSTOMERS, PAYMENTS, REPORTS, SMS, USERS, SETTINGS, SYNC }

val rolePerms: Map<Role, Set<Perm>> = mapOf(
    Role.ADMIN to Perm.entries.toSet(),
    Role.SELLER to setOf(Perm.SALES, Perm.CUSTOMERS, Perm.PAYMENTS, Perm.SMS, Perm.SYNC),
    Role.STOREKEEPER to setOf(Perm.PRODUCTS_EDIT, Perm.VIEW_COST, Perm.STOCK, Perm.SYNC),
)

object Session {
    var user by mutableStateOf<User?>(null)

    val role: Role? get() = user?.let { runCatching { Role.valueOf(it.role) }.getOrNull() }

    fun can(p: Perm): Boolean = role?.let { rolePerms[it]?.contains(p) } ?: false

    val userId: String get() = user?.id ?: ""
}

package tm.sowdahasap

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import tm.sowdahasap.data.AppDatabase
import tm.sowdahasap.data.MIGRATION_1_2
import tm.sowdahasap.platform.Platform
import tm.sowdahasap.ui.AppRoot
import tm.sowdahasap.util.KeyValueStore
import tm.sowdahasap.util.Prefs

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Platform.context = this
        val db = Room.databaseBuilder<AppDatabase>(this, getDatabasePath("sowdahasap.db").absolutePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .addMigrations(MIGRATION_1_2)
            .build()
        val sp = getSharedPreferences("sowdahasap", Context.MODE_PRIVATE)
        val kv = object : KeyValueStore {
            override fun getString(key: String, def: String) = sp.getString(key, def) ?: def
            override fun putString(key: String, value: String) = sp.edit().putString(key, value).apply()
            override fun getLong(key: String, def: Long): Long = try {
                sp.getLong(key, def)
            } catch (e: ClassCastException) {
                // 1.0 wersiýada käbir sazlamalar Int/Boolean bolup saklanypdy
                when (val v = sp.all[key]) {
                    is Int -> v.toLong()
                    is Boolean -> if (v) 1L else 0L
                    else -> def
                }
            }
            override fun putLong(key: String, value: Long) = sp.edit().putLong(key, value).apply()
        }
        App.init(db, Prefs(kv))
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Platform.activity = this
        setContent { AppRoot() }
    }
}

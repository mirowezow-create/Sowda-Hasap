package tm.sowdahasap.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

@Database(
    entities = [Warehouse::class, Product::class, StockMove::class, Customer::class,
        Invoice::class, InvoiceLine::class, Payment::class, User::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): AppDao
}

/** 1.0 -> 1.1: opt bahasy, gijeki baha, opt faktura */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE products ADD COLUMN wholesalePrice INTEGER NOT NULL DEFAULT 0")
        connection.execSQL("ALTER TABLE products ADD COLUMN nightPrice INTEGER NOT NULL DEFAULT 0")
        connection.execSQL("ALTER TABLE invoices ADD COLUMN wholesale INTEGER NOT NULL DEFAULT 0")
    }
}

package tm.sowdahasap.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

@Database(
    entities = [Warehouse::class, Product::class, StockMove::class, Customer::class,
        Invoice::class, InvoiceLine::class, Payment::class, User::class,
        UnitDef::class, ProductUnit::class, Barcode::class, Cashbook::class],
    version = 3,
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

/** 1.1 -> 1.2: ölçegler, ştrihkodlar, depderler, faktura görnüşleri */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(connection: SQLiteConnection) {
        val sql = listOf(
            "ALTER TABLE products ADD COLUMN code TEXT NOT NULL DEFAULT ''",
            "ALTER TABLE customers ADD COLUMN code TEXT NOT NULL DEFAULT ''",
            "ALTER TABLE customers ADD COLUMN phone2 TEXT NOT NULL DEFAULT ''",
            "ALTER TABLE customers ADD COLUMN address TEXT NOT NULL DEFAULT ''",
            "ALTER TABLE customers ADD COLUMN creditLimit INTEGER NOT NULL DEFAULT 0",
            "ALTER TABLE customers ADD COLUMN payDays INTEGER NOT NULL DEFAULT 0",
            "ALTER TABLE customers ADD COLUMN state TEXT NOT NULL DEFAULT 'ACTIVE'",
            "ALTER TABLE products ADD COLUMN state TEXT NOT NULL DEFAULT 'ACTIVE'",
            // Öň pozulan haryt/müşderiler "Öçürilen" bolup gaýdyp gelýär (bergileri dogry galsyn)
            "UPDATE products SET state = 'ARCHIVED', deleted = 0 WHERE deleted = 1",
            "UPDATE customers SET state = 'ARCHIVED', deleted = 0 WHERE deleted = 1",
            "ALTER TABLE invoices ADD COLUMN kind TEXT NOT NULL DEFAULT 'SALE'",
            "ALTER TABLE invoices ADD COLUMN payType TEXT NOT NULL DEFAULT 'CASH'",
            "ALTER TABLE invoices ADD COLUMN cashbookId TEXT NOT NULL DEFAULT 'main'",
            "ALTER TABLE invoices ADD COLUMN toWarehouseId TEXT NOT NULL DEFAULT ''",
            "ALTER TABLE invoices ADD COLUMN paid INTEGER NOT NULL DEFAULT 0",
            "ALTER TABLE invoices ADD COLUMN discountPct REAL NOT NULL DEFAULT 0",
            "ALTER TABLE invoice_lines ADD COLUMN unitName TEXT NOT NULL DEFAULT ''",
            "ALTER TABLE invoice_lines ADD COLUMN factor REAL NOT NULL DEFAULT 1.0",
            "ALTER TABLE payments ADD COLUMN cashbookId TEXT NOT NULL DEFAULT 'main'",
            "CREATE TABLE IF NOT EXISTS `units` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `note` TEXT NOT NULL, " +
                "`updatedAt` INTEGER NOT NULL, `deleted` INTEGER NOT NULL, PRIMARY KEY(`id`))",
            "CREATE TABLE IF NOT EXISTS `product_units` (`id` TEXT NOT NULL, `productId` TEXT NOT NULL, `unitName` TEXT NOT NULL, " +
                "`factor` REAL NOT NULL, `price` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `deleted` INTEGER NOT NULL, PRIMARY KEY(`id`))",
            "CREATE INDEX IF NOT EXISTS `index_product_units_productId` ON `product_units` (`productId`)",
            "CREATE TABLE IF NOT EXISTS `barcodes` (`id` TEXT NOT NULL, `productId` TEXT NOT NULL, `code` TEXT NOT NULL, " +
                "`unitName` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, `deleted` INTEGER NOT NULL, PRIMARY KEY(`id`))",
            "CREATE INDEX IF NOT EXISTS `index_barcodes_code` ON `barcodes` (`code`)",
            "CREATE INDEX IF NOT EXISTS `index_barcodes_productId` ON `barcodes` (`productId`)",
            "CREATE TABLE IF NOT EXISTS `cashbooks` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, " +
                "`deleted` INTEGER NOT NULL, PRIMARY KEY(`id`))",
            // Köne wersiýadaky "sany" ölçegini "san" et (täze ölçeg sanawy bilen gabat gelsin)
            "UPDATE products SET unit = 'san' WHERE unit = 'sany'",
        )
        sql.forEach { connection.execSQL(it) }
    }
}

val ALL_MIGRATIONS = arrayOf(MIGRATION_1_2, MIGRATION_2_3)

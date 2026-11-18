package tm.sowdahasap.`data`

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _appDao: Lazy<AppDao> = lazy {
    AppDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(3,
        "032233cc779f3331ed8018e8c9a8f592", "95027383049127280d49c590f58c02a9") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `warehouses` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, `deleted` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `products` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `barcode` TEXT NOT NULL, `unit` TEXT NOT NULL, `buyPrice` INTEGER NOT NULL, `sellPrice` INTEGER NOT NULL, `minStock` REAL NOT NULL, `updatedAt` INTEGER NOT NULL, `deleted` INTEGER NOT NULL, `wholesalePrice` INTEGER NOT NULL DEFAULT 0, `nightPrice` INTEGER NOT NULL DEFAULT 0, `code` TEXT NOT NULL DEFAULT '', `state` TEXT NOT NULL DEFAULT 'ACTIVE', PRIMARY KEY(`id`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_products_barcode` ON `products` (`barcode`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `stock_moves` (`id` TEXT NOT NULL, `productId` TEXT NOT NULL, `warehouseId` TEXT NOT NULL, `qty` REAL NOT NULL, `type` TEXT NOT NULL, `invoiceId` TEXT, `note` TEXT NOT NULL, `userId` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `deleted` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_stock_moves_productId` ON `stock_moves` (`productId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_stock_moves_warehouseId` ON `stock_moves` (`warehouseId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_stock_moves_invoiceId` ON `stock_moves` (`invoiceId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `customers` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `phone` TEXT NOT NULL, `note` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, `deleted` INTEGER NOT NULL, `code` TEXT NOT NULL DEFAULT '', `phone2` TEXT NOT NULL DEFAULT '', `address` TEXT NOT NULL DEFAULT '', `creditLimit` INTEGER NOT NULL DEFAULT 0, `payDays` INTEGER NOT NULL DEFAULT 0, `state` TEXT NOT NULL DEFAULT 'ACTIVE', PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `invoices` (`id` TEXT NOT NULL, `number` TEXT NOT NULL, `customerId` TEXT, `warehouseId` TEXT NOT NULL, `status` TEXT NOT NULL, `total` INTEGER NOT NULL, `discount` INTEGER NOT NULL, `note` TEXT NOT NULL, `userId` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `deleted` INTEGER NOT NULL, `wholesale` INTEGER NOT NULL DEFAULT 0, `kind` TEXT NOT NULL DEFAULT 'SALE', `payType` TEXT NOT NULL DEFAULT 'CASH', `cashbookId` TEXT NOT NULL DEFAULT 'main', `toWarehouseId` TEXT NOT NULL DEFAULT '', `paid` INTEGER NOT NULL DEFAULT 0, `discountPct` REAL NOT NULL DEFAULT 0, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_invoices_customerId` ON `invoices` (`customerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_invoices_createdAt` ON `invoices` (`createdAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `invoice_lines` (`id` TEXT NOT NULL, `invoiceId` TEXT NOT NULL, `productId` TEXT NOT NULL, `productName` TEXT NOT NULL, `qty` REAL NOT NULL, `price` INTEGER NOT NULL, `cost` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `deleted` INTEGER NOT NULL, `unitName` TEXT NOT NULL DEFAULT '', `factor` REAL NOT NULL DEFAULT 1.0, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_invoice_lines_invoiceId` ON `invoice_lines` (`invoiceId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_invoice_lines_productId` ON `invoice_lines` (`productId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `payments` (`id` TEXT NOT NULL, `customerId` TEXT NOT NULL, `amount` INTEGER NOT NULL, `invoiceId` TEXT, `note` TEXT NOT NULL, `userId` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `deleted` INTEGER NOT NULL, `cashbookId` TEXT NOT NULL DEFAULT 'main', PRIMARY KEY(`id`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_payments_customerId` ON `payments` (`customerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_payments_invoiceId` ON `payments` (`invoiceId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `users` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `login` TEXT NOT NULL, `passHash` TEXT NOT NULL, `salt` TEXT NOT NULL, `role` TEXT NOT NULL, `active` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `deleted` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_users_login` ON `users` (`login`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `units` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `note` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, `deleted` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `product_units` (`id` TEXT NOT NULL, `productId` TEXT NOT NULL, `unitName` TEXT NOT NULL, `factor` REAL NOT NULL, `price` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `deleted` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_product_units_productId` ON `product_units` (`productId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `barcodes` (`id` TEXT NOT NULL, `productId` TEXT NOT NULL, `code` TEXT NOT NULL, `unitName` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, `deleted` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_barcodes_code` ON `barcodes` (`code`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_barcodes_productId` ON `barcodes` (`productId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `cashbooks` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, `deleted` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '032233cc779f3331ed8018e8c9a8f592')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `warehouses`")
        connection.execSQL("DROP TABLE IF EXISTS `products`")
        connection.execSQL("DROP TABLE IF EXISTS `stock_moves`")
        connection.execSQL("DROP TABLE IF EXISTS `customers`")
        connection.execSQL("DROP TABLE IF EXISTS `invoices`")
        connection.execSQL("DROP TABLE IF EXISTS `invoice_lines`")
        connection.execSQL("DROP TABLE IF EXISTS `payments`")
        connection.execSQL("DROP TABLE IF EXISTS `users`")
        connection.execSQL("DROP TABLE IF EXISTS `units`")
        connection.execSQL("DROP TABLE IF EXISTS `product_units`")
        connection.execSQL("DROP TABLE IF EXISTS `barcodes`")
        connection.execSQL("DROP TABLE IF EXISTS `cashbooks`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsWarehouses: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsWarehouses.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWarehouses.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWarehouses.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWarehouses.put("deleted", TableInfo.Column("deleted", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysWarehouses: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesWarehouses: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoWarehouses: TableInfo = TableInfo("warehouses", _columnsWarehouses,
            _foreignKeysWarehouses, _indicesWarehouses)
        val _existingWarehouses: TableInfo = read(connection, "warehouses")
        if (!_infoWarehouses.equals(_existingWarehouses)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |warehouses(tm.sowdahasap.data.Warehouse).
              | Expected:
              |""".trimMargin() + _infoWarehouses + """
              |
              | Found:
              |""".trimMargin() + _existingWarehouses)
        }
        val _columnsProducts: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsProducts.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("barcode", TableInfo.Column("barcode", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("unit", TableInfo.Column("unit", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("buyPrice", TableInfo.Column("buyPrice", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("sellPrice", TableInfo.Column("sellPrice", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("minStock", TableInfo.Column("minStock", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("deleted", TableInfo.Column("deleted", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("wholesalePrice", TableInfo.Column("wholesalePrice", "INTEGER", true,
            0, "0", TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("nightPrice", TableInfo.Column("nightPrice", "INTEGER", true, 0, "0",
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("code", TableInfo.Column("code", "TEXT", true, 0, "''",
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("state", TableInfo.Column("state", "TEXT", true, 0, "'ACTIVE'",
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysProducts: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesProducts: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesProducts.add(TableInfo.Index("index_products_barcode", false, listOf("barcode"),
            listOf("ASC")))
        val _infoProducts: TableInfo = TableInfo("products", _columnsProducts, _foreignKeysProducts,
            _indicesProducts)
        val _existingProducts: TableInfo = read(connection, "products")
        if (!_infoProducts.equals(_existingProducts)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |products(tm.sowdahasap.data.Product).
              | Expected:
              |""".trimMargin() + _infoProducts + """
              |
              | Found:
              |""".trimMargin() + _existingProducts)
        }
        val _columnsStockMoves: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsStockMoves.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsStockMoves.put("productId", TableInfo.Column("productId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsStockMoves.put("warehouseId", TableInfo.Column("warehouseId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsStockMoves.put("qty", TableInfo.Column("qty", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsStockMoves.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsStockMoves.put("invoiceId", TableInfo.Column("invoiceId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsStockMoves.put("note", TableInfo.Column("note", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsStockMoves.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsStockMoves.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsStockMoves.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsStockMoves.put("deleted", TableInfo.Column("deleted", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysStockMoves: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesStockMoves: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesStockMoves.add(TableInfo.Index("index_stock_moves_productId", false,
            listOf("productId"), listOf("ASC")))
        _indicesStockMoves.add(TableInfo.Index("index_stock_moves_warehouseId", false,
            listOf("warehouseId"), listOf("ASC")))
        _indicesStockMoves.add(TableInfo.Index("index_stock_moves_invoiceId", false,
            listOf("invoiceId"), listOf("ASC")))
        val _infoStockMoves: TableInfo = TableInfo("stock_moves", _columnsStockMoves,
            _foreignKeysStockMoves, _indicesStockMoves)
        val _existingStockMoves: TableInfo = read(connection, "stock_moves")
        if (!_infoStockMoves.equals(_existingStockMoves)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |stock_moves(tm.sowdahasap.data.StockMove).
              | Expected:
              |""".trimMargin() + _infoStockMoves + """
              |
              | Found:
              |""".trimMargin() + _existingStockMoves)
        }
        val _columnsCustomers: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCustomers.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCustomers.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCustomers.put("phone", TableInfo.Column("phone", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCustomers.put("note", TableInfo.Column("note", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCustomers.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCustomers.put("deleted", TableInfo.Column("deleted", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCustomers.put("code", TableInfo.Column("code", "TEXT", true, 0, "''",
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCustomers.put("phone2", TableInfo.Column("phone2", "TEXT", true, 0, "''",
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCustomers.put("address", TableInfo.Column("address", "TEXT", true, 0, "''",
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCustomers.put("creditLimit", TableInfo.Column("creditLimit", "INTEGER", true, 0,
            "0", TableInfo.CREATED_FROM_ENTITY))
        _columnsCustomers.put("payDays", TableInfo.Column("payDays", "INTEGER", true, 0, "0",
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCustomers.put("state", TableInfo.Column("state", "TEXT", true, 0, "'ACTIVE'",
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCustomers: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCustomers: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoCustomers: TableInfo = TableInfo("customers", _columnsCustomers,
            _foreignKeysCustomers, _indicesCustomers)
        val _existingCustomers: TableInfo = read(connection, "customers")
        if (!_infoCustomers.equals(_existingCustomers)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |customers(tm.sowdahasap.data.Customer).
              | Expected:
              |""".trimMargin() + _infoCustomers + """
              |
              | Found:
              |""".trimMargin() + _existingCustomers)
        }
        val _columnsInvoices: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsInvoices.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoices.put("number", TableInfo.Column("number", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoices.put("customerId", TableInfo.Column("customerId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoices.put("warehouseId", TableInfo.Column("warehouseId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoices.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoices.put("total", TableInfo.Column("total", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoices.put("discount", TableInfo.Column("discount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoices.put("note", TableInfo.Column("note", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoices.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoices.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoices.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoices.put("deleted", TableInfo.Column("deleted", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoices.put("wholesale", TableInfo.Column("wholesale", "INTEGER", true, 0, "0",
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoices.put("kind", TableInfo.Column("kind", "TEXT", true, 0, "'SALE'",
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoices.put("payType", TableInfo.Column("payType", "TEXT", true, 0, "'CASH'",
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoices.put("cashbookId", TableInfo.Column("cashbookId", "TEXT", true, 0, "'main'",
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoices.put("toWarehouseId", TableInfo.Column("toWarehouseId", "TEXT", true, 0,
            "''", TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoices.put("paid", TableInfo.Column("paid", "INTEGER", true, 0, "0",
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoices.put("discountPct", TableInfo.Column("discountPct", "REAL", true, 0, "0",
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysInvoices: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesInvoices: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesInvoices.add(TableInfo.Index("index_invoices_customerId", false,
            listOf("customerId"), listOf("ASC")))
        _indicesInvoices.add(TableInfo.Index("index_invoices_createdAt", false, listOf("createdAt"),
            listOf("ASC")))
        val _infoInvoices: TableInfo = TableInfo("invoices", _columnsInvoices, _foreignKeysInvoices,
            _indicesInvoices)
        val _existingInvoices: TableInfo = read(connection, "invoices")
        if (!_infoInvoices.equals(_existingInvoices)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |invoices(tm.sowdahasap.data.Invoice).
              | Expected:
              |""".trimMargin() + _infoInvoices + """
              |
              | Found:
              |""".trimMargin() + _existingInvoices)
        }
        val _columnsInvoiceLines: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsInvoiceLines.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoiceLines.put("invoiceId", TableInfo.Column("invoiceId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoiceLines.put("productId", TableInfo.Column("productId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoiceLines.put("productName", TableInfo.Column("productName", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoiceLines.put("qty", TableInfo.Column("qty", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoiceLines.put("price", TableInfo.Column("price", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoiceLines.put("cost", TableInfo.Column("cost", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoiceLines.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoiceLines.put("deleted", TableInfo.Column("deleted", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoiceLines.put("unitName", TableInfo.Column("unitName", "TEXT", true, 0, "''",
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoiceLines.put("factor", TableInfo.Column("factor", "REAL", true, 0, "1.0",
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysInvoiceLines: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesInvoiceLines: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesInvoiceLines.add(TableInfo.Index("index_invoice_lines_invoiceId", false,
            listOf("invoiceId"), listOf("ASC")))
        _indicesInvoiceLines.add(TableInfo.Index("index_invoice_lines_productId", false,
            listOf("productId"), listOf("ASC")))
        val _infoInvoiceLines: TableInfo = TableInfo("invoice_lines", _columnsInvoiceLines,
            _foreignKeysInvoiceLines, _indicesInvoiceLines)
        val _existingInvoiceLines: TableInfo = read(connection, "invoice_lines")
        if (!_infoInvoiceLines.equals(_existingInvoiceLines)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |invoice_lines(tm.sowdahasap.data.InvoiceLine).
              | Expected:
              |""".trimMargin() + _infoInvoiceLines + """
              |
              | Found:
              |""".trimMargin() + _existingInvoiceLines)
        }
        val _columnsPayments: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPayments.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("customerId", TableInfo.Column("customerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("amount", TableInfo.Column("amount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("invoiceId", TableInfo.Column("invoiceId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("note", TableInfo.Column("note", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("deleted", TableInfo.Column("deleted", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("cashbookId", TableInfo.Column("cashbookId", "TEXT", true, 0, "'main'",
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPayments: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesPayments: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesPayments.add(TableInfo.Index("index_payments_customerId", false,
            listOf("customerId"), listOf("ASC")))
        _indicesPayments.add(TableInfo.Index("index_payments_invoiceId", false, listOf("invoiceId"),
            listOf("ASC")))
        val _infoPayments: TableInfo = TableInfo("payments", _columnsPayments, _foreignKeysPayments,
            _indicesPayments)
        val _existingPayments: TableInfo = read(connection, "payments")
        if (!_infoPayments.equals(_existingPayments)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |payments(tm.sowdahasap.data.Payment).
              | Expected:
              |""".trimMargin() + _infoPayments + """
              |
              | Found:
              |""".trimMargin() + _existingPayments)
        }
        val _columnsUsers: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsUsers.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("login", TableInfo.Column("login", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("passHash", TableInfo.Column("passHash", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("salt", TableInfo.Column("salt", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("role", TableInfo.Column("role", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("active", TableInfo.Column("active", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("deleted", TableInfo.Column("deleted", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysUsers: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesUsers: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesUsers.add(TableInfo.Index("index_users_login", false, listOf("login"),
            listOf("ASC")))
        val _infoUsers: TableInfo = TableInfo("users", _columnsUsers, _foreignKeysUsers,
            _indicesUsers)
        val _existingUsers: TableInfo = read(connection, "users")
        if (!_infoUsers.equals(_existingUsers)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |users(tm.sowdahasap.data.User).
              | Expected:
              |""".trimMargin() + _infoUsers + """
              |
              | Found:
              |""".trimMargin() + _existingUsers)
        }
        val _columnsUnits: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsUnits.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUnits.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUnits.put("note", TableInfo.Column("note", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUnits.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUnits.put("deleted", TableInfo.Column("deleted", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysUnits: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesUnits: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoUnits: TableInfo = TableInfo("units", _columnsUnits, _foreignKeysUnits,
            _indicesUnits)
        val _existingUnits: TableInfo = read(connection, "units")
        if (!_infoUnits.equals(_existingUnits)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |units(tm.sowdahasap.data.UnitDef).
              | Expected:
              |""".trimMargin() + _infoUnits + """
              |
              | Found:
              |""".trimMargin() + _existingUnits)
        }
        val _columnsProductUnits: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsProductUnits.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProductUnits.put("productId", TableInfo.Column("productId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProductUnits.put("unitName", TableInfo.Column("unitName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProductUnits.put("factor", TableInfo.Column("factor", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProductUnits.put("price", TableInfo.Column("price", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProductUnits.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProductUnits.put("deleted", TableInfo.Column("deleted", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysProductUnits: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesProductUnits: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesProductUnits.add(TableInfo.Index("index_product_units_productId", false,
            listOf("productId"), listOf("ASC")))
        val _infoProductUnits: TableInfo = TableInfo("product_units", _columnsProductUnits,
            _foreignKeysProductUnits, _indicesProductUnits)
        val _existingProductUnits: TableInfo = read(connection, "product_units")
        if (!_infoProductUnits.equals(_existingProductUnits)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |product_units(tm.sowdahasap.data.ProductUnit).
              | Expected:
              |""".trimMargin() + _infoProductUnits + """
              |
              | Found:
              |""".trimMargin() + _existingProductUnits)
        }
        val _columnsBarcodes: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBarcodes.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBarcodes.put("productId", TableInfo.Column("productId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBarcodes.put("code", TableInfo.Column("code", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBarcodes.put("unitName", TableInfo.Column("unitName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBarcodes.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBarcodes.put("deleted", TableInfo.Column("deleted", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBarcodes: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBarcodes: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesBarcodes.add(TableInfo.Index("index_barcodes_code", false, listOf("code"),
            listOf("ASC")))
        _indicesBarcodes.add(TableInfo.Index("index_barcodes_productId", false, listOf("productId"),
            listOf("ASC")))
        val _infoBarcodes: TableInfo = TableInfo("barcodes", _columnsBarcodes, _foreignKeysBarcodes,
            _indicesBarcodes)
        val _existingBarcodes: TableInfo = read(connection, "barcodes")
        if (!_infoBarcodes.equals(_existingBarcodes)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |barcodes(tm.sowdahasap.data.Barcode).
              | Expected:
              |""".trimMargin() + _infoBarcodes + """
              |
              | Found:
              |""".trimMargin() + _existingBarcodes)
        }
        val _columnsCashbooks: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCashbooks.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCashbooks.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCashbooks.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCashbooks.put("deleted", TableInfo.Column("deleted", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCashbooks: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCashbooks: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoCashbooks: TableInfo = TableInfo("cashbooks", _columnsCashbooks,
            _foreignKeysCashbooks, _indicesCashbooks)
        val _existingCashbooks: TableInfo = read(connection, "cashbooks")
        if (!_infoCashbooks.equals(_existingCashbooks)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |cashbooks(tm.sowdahasap.data.Cashbook).
              | Expected:
              |""".trimMargin() + _infoCashbooks + """
              |
              | Found:
              |""".trimMargin() + _existingCashbooks)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "warehouses", "products",
        "stock_moves", "customers", "invoices", "invoice_lines", "payments", "users", "units",
        "product_units", "barcodes", "cashbooks")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(AppDao::class, AppDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun dao(): AppDao = _appDao.value
}

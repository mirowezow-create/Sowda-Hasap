package tm.sowdahasap.`data`

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlin.text.StringBuilder
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDao_Impl(
  __db: RoomDatabase,
) : AppDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfWarehouse: EntityUpsertAdapter<Warehouse>

  private val __upsertAdapterOfBarcode: EntityUpsertAdapter<Barcode>

  private val __upsertAdapterOfProductUnit: EntityUpsertAdapter<ProductUnit>

  private val __upsertAdapterOfUnitDef: EntityUpsertAdapter<UnitDef>

  private val __upsertAdapterOfCashbook: EntityUpsertAdapter<Cashbook>

  private val __upsertAdapterOfProduct: EntityUpsertAdapter<Product>

  private val __upsertAdapterOfStockMove: EntityUpsertAdapter<StockMove>

  private val __upsertAdapterOfCustomer: EntityUpsertAdapter<Customer>

  private val __upsertAdapterOfInvoice: EntityUpsertAdapter<Invoice>

  private val __upsertAdapterOfInvoiceLine: EntityUpsertAdapter<InvoiceLine>

  private val __upsertAdapterOfPayment: EntityUpsertAdapter<Payment>

  private val __upsertAdapterOfUser: EntityUpsertAdapter<User>
  init {
    this.__db = __db
    this.__upsertAdapterOfWarehouse = EntityUpsertAdapter<Warehouse>(object :
        EntityInsertAdapter<Warehouse>() {
      protected override fun createQuery(): String =
          "INSERT INTO `warehouses` (`id`,`name`,`updatedAt`,`deleted`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Warehouse) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindLong(3, entity.updatedAt)
        val _tmp: Int = if (entity.deleted) 1 else 0
        statement.bindLong(4, _tmp.toLong())
      }
    }, object : EntityDeleteOrUpdateAdapter<Warehouse>() {
      protected override fun createQuery(): String =
          "UPDATE `warehouses` SET `id` = ?,`name` = ?,`updatedAt` = ?,`deleted` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Warehouse) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindLong(3, entity.updatedAt)
        val _tmp: Int = if (entity.deleted) 1 else 0
        statement.bindLong(4, _tmp.toLong())
        statement.bindText(5, entity.id)
      }
    })
    this.__upsertAdapterOfBarcode = EntityUpsertAdapter<Barcode>(object :
        EntityInsertAdapter<Barcode>() {
      protected override fun createQuery(): String =
          "INSERT INTO `barcodes` (`id`,`productId`,`code`,`unitName`,`updatedAt`,`deleted`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Barcode) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.productId)
        statement.bindText(3, entity.code)
        statement.bindText(4, entity.unitName)
        statement.bindLong(5, entity.updatedAt)
        val _tmp: Int = if (entity.deleted) 1 else 0
        statement.bindLong(6, _tmp.toLong())
      }
    }, object : EntityDeleteOrUpdateAdapter<Barcode>() {
      protected override fun createQuery(): String =
          "UPDATE `barcodes` SET `id` = ?,`productId` = ?,`code` = ?,`unitName` = ?,`updatedAt` = ?,`deleted` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Barcode) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.productId)
        statement.bindText(3, entity.code)
        statement.bindText(4, entity.unitName)
        statement.bindLong(5, entity.updatedAt)
        val _tmp: Int = if (entity.deleted) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        statement.bindText(7, entity.id)
      }
    })
    this.__upsertAdapterOfProductUnit = EntityUpsertAdapter<ProductUnit>(object :
        EntityInsertAdapter<ProductUnit>() {
      protected override fun createQuery(): String =
          "INSERT INTO `product_units` (`id`,`productId`,`unitName`,`factor`,`price`,`updatedAt`,`deleted`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ProductUnit) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.productId)
        statement.bindText(3, entity.unitName)
        statement.bindDouble(4, entity.factor)
        statement.bindLong(5, entity.price)
        statement.bindLong(6, entity.updatedAt)
        val _tmp: Int = if (entity.deleted) 1 else 0
        statement.bindLong(7, _tmp.toLong())
      }
    }, object : EntityDeleteOrUpdateAdapter<ProductUnit>() {
      protected override fun createQuery(): String =
          "UPDATE `product_units` SET `id` = ?,`productId` = ?,`unitName` = ?,`factor` = ?,`price` = ?,`updatedAt` = ?,`deleted` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ProductUnit) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.productId)
        statement.bindText(3, entity.unitName)
        statement.bindDouble(4, entity.factor)
        statement.bindLong(5, entity.price)
        statement.bindLong(6, entity.updatedAt)
        val _tmp: Int = if (entity.deleted) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        statement.bindText(8, entity.id)
      }
    })
    this.__upsertAdapterOfUnitDef = EntityUpsertAdapter<UnitDef>(object :
        EntityInsertAdapter<UnitDef>() {
      protected override fun createQuery(): String =
          "INSERT INTO `units` (`id`,`name`,`note`,`updatedAt`,`deleted`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: UnitDef) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.note)
        statement.bindLong(4, entity.updatedAt)
        val _tmp: Int = if (entity.deleted) 1 else 0
        statement.bindLong(5, _tmp.toLong())
      }
    }, object : EntityDeleteOrUpdateAdapter<UnitDef>() {
      protected override fun createQuery(): String =
          "UPDATE `units` SET `id` = ?,`name` = ?,`note` = ?,`updatedAt` = ?,`deleted` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: UnitDef) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.note)
        statement.bindLong(4, entity.updatedAt)
        val _tmp: Int = if (entity.deleted) 1 else 0
        statement.bindLong(5, _tmp.toLong())
        statement.bindText(6, entity.id)
      }
    })
    this.__upsertAdapterOfCashbook = EntityUpsertAdapter<Cashbook>(object :
        EntityInsertAdapter<Cashbook>() {
      protected override fun createQuery(): String =
          "INSERT INTO `cashbooks` (`id`,`name`,`updatedAt`,`deleted`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Cashbook) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindLong(3, entity.updatedAt)
        val _tmp: Int = if (entity.deleted) 1 else 0
        statement.bindLong(4, _tmp.toLong())
      }
    }, object : EntityDeleteOrUpdateAdapter<Cashbook>() {
      protected override fun createQuery(): String =
          "UPDATE `cashbooks` SET `id` = ?,`name` = ?,`updatedAt` = ?,`deleted` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Cashbook) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindLong(3, entity.updatedAt)
        val _tmp: Int = if (entity.deleted) 1 else 0
        statement.bindLong(4, _tmp.toLong())
        statement.bindText(5, entity.id)
      }
    })
    this.__upsertAdapterOfProduct = EntityUpsertAdapter<Product>(object :
        EntityInsertAdapter<Product>() {
      protected override fun createQuery(): String =
          "INSERT INTO `products` (`id`,`name`,`barcode`,`unit`,`buyPrice`,`sellPrice`,`minStock`,`updatedAt`,`deleted`,`wholesalePrice`,`nightPrice`,`code`,`state`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Product) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.barcode)
        statement.bindText(4, entity.unit)
        statement.bindLong(5, entity.buyPrice)
        statement.bindLong(6, entity.sellPrice)
        statement.bindDouble(7, entity.minStock)
        statement.bindLong(8, entity.updatedAt)
        val _tmp: Int = if (entity.deleted) 1 else 0
        statement.bindLong(9, _tmp.toLong())
        statement.bindLong(10, entity.wholesalePrice)
        statement.bindLong(11, entity.nightPrice)
        statement.bindText(12, entity.code)
        statement.bindText(13, entity.state)
      }
    }, object : EntityDeleteOrUpdateAdapter<Product>() {
      protected override fun createQuery(): String =
          "UPDATE `products` SET `id` = ?,`name` = ?,`barcode` = ?,`unit` = ?,`buyPrice` = ?,`sellPrice` = ?,`minStock` = ?,`updatedAt` = ?,`deleted` = ?,`wholesalePrice` = ?,`nightPrice` = ?,`code` = ?,`state` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Product) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.barcode)
        statement.bindText(4, entity.unit)
        statement.bindLong(5, entity.buyPrice)
        statement.bindLong(6, entity.sellPrice)
        statement.bindDouble(7, entity.minStock)
        statement.bindLong(8, entity.updatedAt)
        val _tmp: Int = if (entity.deleted) 1 else 0
        statement.bindLong(9, _tmp.toLong())
        statement.bindLong(10, entity.wholesalePrice)
        statement.bindLong(11, entity.nightPrice)
        statement.bindText(12, entity.code)
        statement.bindText(13, entity.state)
        statement.bindText(14, entity.id)
      }
    })
    this.__upsertAdapterOfStockMove = EntityUpsertAdapter<StockMove>(object :
        EntityInsertAdapter<StockMove>() {
      protected override fun createQuery(): String =
          "INSERT INTO `stock_moves` (`id`,`productId`,`warehouseId`,`qty`,`type`,`invoiceId`,`note`,`userId`,`createdAt`,`updatedAt`,`deleted`) VALUES (?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: StockMove) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.productId)
        statement.bindText(3, entity.warehouseId)
        statement.bindDouble(4, entity.qty)
        statement.bindText(5, entity.type)
        val _tmpInvoiceId: String? = entity.invoiceId
        if (_tmpInvoiceId == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpInvoiceId)
        }
        statement.bindText(7, entity.note)
        statement.bindText(8, entity.userId)
        statement.bindLong(9, entity.createdAt)
        statement.bindLong(10, entity.updatedAt)
        val _tmp: Int = if (entity.deleted) 1 else 0
        statement.bindLong(11, _tmp.toLong())
      }
    }, object : EntityDeleteOrUpdateAdapter<StockMove>() {
      protected override fun createQuery(): String =
          "UPDATE `stock_moves` SET `id` = ?,`productId` = ?,`warehouseId` = ?,`qty` = ?,`type` = ?,`invoiceId` = ?,`note` = ?,`userId` = ?,`createdAt` = ?,`updatedAt` = ?,`deleted` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: StockMove) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.productId)
        statement.bindText(3, entity.warehouseId)
        statement.bindDouble(4, entity.qty)
        statement.bindText(5, entity.type)
        val _tmpInvoiceId: String? = entity.invoiceId
        if (_tmpInvoiceId == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpInvoiceId)
        }
        statement.bindText(7, entity.note)
        statement.bindText(8, entity.userId)
        statement.bindLong(9, entity.createdAt)
        statement.bindLong(10, entity.updatedAt)
        val _tmp: Int = if (entity.deleted) 1 else 0
        statement.bindLong(11, _tmp.toLong())
        statement.bindText(12, entity.id)
      }
    })
    this.__upsertAdapterOfCustomer = EntityUpsertAdapter<Customer>(object :
        EntityInsertAdapter<Customer>() {
      protected override fun createQuery(): String =
          "INSERT INTO `customers` (`id`,`name`,`phone`,`note`,`updatedAt`,`deleted`,`code`,`phone2`,`address`,`creditLimit`,`payDays`,`state`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Customer) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.phone)
        statement.bindText(4, entity.note)
        statement.bindLong(5, entity.updatedAt)
        val _tmp: Int = if (entity.deleted) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        statement.bindText(7, entity.code)
        statement.bindText(8, entity.phone2)
        statement.bindText(9, entity.address)
        statement.bindLong(10, entity.creditLimit)
        statement.bindLong(11, entity.payDays.toLong())
        statement.bindText(12, entity.state)
      }
    }, object : EntityDeleteOrUpdateAdapter<Customer>() {
      protected override fun createQuery(): String =
          "UPDATE `customers` SET `id` = ?,`name` = ?,`phone` = ?,`note` = ?,`updatedAt` = ?,`deleted` = ?,`code` = ?,`phone2` = ?,`address` = ?,`creditLimit` = ?,`payDays` = ?,`state` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Customer) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.phone)
        statement.bindText(4, entity.note)
        statement.bindLong(5, entity.updatedAt)
        val _tmp: Int = if (entity.deleted) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        statement.bindText(7, entity.code)
        statement.bindText(8, entity.phone2)
        statement.bindText(9, entity.address)
        statement.bindLong(10, entity.creditLimit)
        statement.bindLong(11, entity.payDays.toLong())
        statement.bindText(12, entity.state)
        statement.bindText(13, entity.id)
      }
    })
    this.__upsertAdapterOfInvoice = EntityUpsertAdapter<Invoice>(object :
        EntityInsertAdapter<Invoice>() {
      protected override fun createQuery(): String =
          "INSERT INTO `invoices` (`id`,`number`,`customerId`,`warehouseId`,`status`,`total`,`discount`,`note`,`userId`,`createdAt`,`updatedAt`,`deleted`,`wholesale`,`kind`,`payType`,`cashbookId`,`toWarehouseId`,`paid`,`discountPct`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Invoice) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.number)
        val _tmpCustomerId: String? = entity.customerId
        if (_tmpCustomerId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpCustomerId)
        }
        statement.bindText(4, entity.warehouseId)
        statement.bindText(5, entity.status)
        statement.bindLong(6, entity.total)
        statement.bindLong(7, entity.discount)
        statement.bindText(8, entity.note)
        statement.bindText(9, entity.userId)
        statement.bindLong(10, entity.createdAt)
        statement.bindLong(11, entity.updatedAt)
        val _tmp: Int = if (entity.deleted) 1 else 0
        statement.bindLong(12, _tmp.toLong())
        val _tmp_1: Int = if (entity.wholesale) 1 else 0
        statement.bindLong(13, _tmp_1.toLong())
        statement.bindText(14, entity.kind)
        statement.bindText(15, entity.payType)
        statement.bindText(16, entity.cashbookId)
        statement.bindText(17, entity.toWarehouseId)
        statement.bindLong(18, entity.paid)
        statement.bindDouble(19, entity.discountPct)
      }
    }, object : EntityDeleteOrUpdateAdapter<Invoice>() {
      protected override fun createQuery(): String =
          "UPDATE `invoices` SET `id` = ?,`number` = ?,`customerId` = ?,`warehouseId` = ?,`status` = ?,`total` = ?,`discount` = ?,`note` = ?,`userId` = ?,`createdAt` = ?,`updatedAt` = ?,`deleted` = ?,`wholesale` = ?,`kind` = ?,`payType` = ?,`cashbookId` = ?,`toWarehouseId` = ?,`paid` = ?,`discountPct` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Invoice) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.number)
        val _tmpCustomerId: String? = entity.customerId
        if (_tmpCustomerId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpCustomerId)
        }
        statement.bindText(4, entity.warehouseId)
        statement.bindText(5, entity.status)
        statement.bindLong(6, entity.total)
        statement.bindLong(7, entity.discount)
        statement.bindText(8, entity.note)
        statement.bindText(9, entity.userId)
        statement.bindLong(10, entity.createdAt)
        statement.bindLong(11, entity.updatedAt)
        val _tmp: Int = if (entity.deleted) 1 else 0
        statement.bindLong(12, _tmp.toLong())
        val _tmp_1: Int = if (entity.wholesale) 1 else 0
        statement.bindLong(13, _tmp_1.toLong())
        statement.bindText(14, entity.kind)
        statement.bindText(15, entity.payType)
        statement.bindText(16, entity.cashbookId)
        statement.bindText(17, entity.toWarehouseId)
        statement.bindLong(18, entity.paid)
        statement.bindDouble(19, entity.discountPct)
        statement.bindText(20, entity.id)
      }
    })
    this.__upsertAdapterOfInvoiceLine = EntityUpsertAdapter<InvoiceLine>(object :
        EntityInsertAdapter<InvoiceLine>() {
      protected override fun createQuery(): String =
          "INSERT INTO `invoice_lines` (`id`,`invoiceId`,`productId`,`productName`,`qty`,`price`,`cost`,`updatedAt`,`deleted`,`unitName`,`factor`) VALUES (?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: InvoiceLine) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.invoiceId)
        statement.bindText(3, entity.productId)
        statement.bindText(4, entity.productName)
        statement.bindDouble(5, entity.qty)
        statement.bindLong(6, entity.price)
        statement.bindLong(7, entity.cost)
        statement.bindLong(8, entity.updatedAt)
        val _tmp: Int = if (entity.deleted) 1 else 0
        statement.bindLong(9, _tmp.toLong())
        statement.bindText(10, entity.unitName)
        statement.bindDouble(11, entity.factor)
      }
    }, object : EntityDeleteOrUpdateAdapter<InvoiceLine>() {
      protected override fun createQuery(): String =
          "UPDATE `invoice_lines` SET `id` = ?,`invoiceId` = ?,`productId` = ?,`productName` = ?,`qty` = ?,`price` = ?,`cost` = ?,`updatedAt` = ?,`deleted` = ?,`unitName` = ?,`factor` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: InvoiceLine) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.invoiceId)
        statement.bindText(3, entity.productId)
        statement.bindText(4, entity.productName)
        statement.bindDouble(5, entity.qty)
        statement.bindLong(6, entity.price)
        statement.bindLong(7, entity.cost)
        statement.bindLong(8, entity.updatedAt)
        val _tmp: Int = if (entity.deleted) 1 else 0
        statement.bindLong(9, _tmp.toLong())
        statement.bindText(10, entity.unitName)
        statement.bindDouble(11, entity.factor)
        statement.bindText(12, entity.id)
      }
    })
    this.__upsertAdapterOfPayment = EntityUpsertAdapter<Payment>(object :
        EntityInsertAdapter<Payment>() {
      protected override fun createQuery(): String =
          "INSERT INTO `payments` (`id`,`customerId`,`amount`,`invoiceId`,`note`,`userId`,`createdAt`,`updatedAt`,`deleted`,`cashbookId`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Payment) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.customerId)
        statement.bindLong(3, entity.amount)
        val _tmpInvoiceId: String? = entity.invoiceId
        if (_tmpInvoiceId == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpInvoiceId)
        }
        statement.bindText(5, entity.note)
        statement.bindText(6, entity.userId)
        statement.bindLong(7, entity.createdAt)
        statement.bindLong(8, entity.updatedAt)
        val _tmp: Int = if (entity.deleted) 1 else 0
        statement.bindLong(9, _tmp.toLong())
        statement.bindText(10, entity.cashbookId)
      }
    }, object : EntityDeleteOrUpdateAdapter<Payment>() {
      protected override fun createQuery(): String =
          "UPDATE `payments` SET `id` = ?,`customerId` = ?,`amount` = ?,`invoiceId` = ?,`note` = ?,`userId` = ?,`createdAt` = ?,`updatedAt` = ?,`deleted` = ?,`cashbookId` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Payment) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.customerId)
        statement.bindLong(3, entity.amount)
        val _tmpInvoiceId: String? = entity.invoiceId
        if (_tmpInvoiceId == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpInvoiceId)
        }
        statement.bindText(5, entity.note)
        statement.bindText(6, entity.userId)
        statement.bindLong(7, entity.createdAt)
        statement.bindLong(8, entity.updatedAt)
        val _tmp: Int = if (entity.deleted) 1 else 0
        statement.bindLong(9, _tmp.toLong())
        statement.bindText(10, entity.cashbookId)
        statement.bindText(11, entity.id)
      }
    })
    this.__upsertAdapterOfUser = EntityUpsertAdapter<User>(object : EntityInsertAdapter<User>() {
      protected override fun createQuery(): String =
          "INSERT INTO `users` (`id`,`name`,`login`,`passHash`,`salt`,`role`,`active`,`updatedAt`,`deleted`) VALUES (?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: User) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.login)
        statement.bindText(4, entity.passHash)
        statement.bindText(5, entity.salt)
        statement.bindText(6, entity.role)
        val _tmp: Int = if (entity.active) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        statement.bindLong(8, entity.updatedAt)
        val _tmp_1: Int = if (entity.deleted) 1 else 0
        statement.bindLong(9, _tmp_1.toLong())
      }
    }, object : EntityDeleteOrUpdateAdapter<User>() {
      protected override fun createQuery(): String =
          "UPDATE `users` SET `id` = ?,`name` = ?,`login` = ?,`passHash` = ?,`salt` = ?,`role` = ?,`active` = ?,`updatedAt` = ?,`deleted` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: User) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.login)
        statement.bindText(4, entity.passHash)
        statement.bindText(5, entity.salt)
        statement.bindText(6, entity.role)
        val _tmp: Int = if (entity.active) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        statement.bindLong(8, entity.updatedAt)
        val _tmp_1: Int = if (entity.deleted) 1 else 0
        statement.bindLong(9, _tmp_1.toLong())
        statement.bindText(10, entity.id)
      }
    })
  }

  public override suspend fun upsertWarehouse(w: Warehouse): Unit = performSuspending(__db, false,
      true) { _connection ->
    __upsertAdapterOfWarehouse.upsert(_connection, w)
  }

  public override suspend fun upsertBarcodes(list: List<Barcode>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __upsertAdapterOfBarcode.upsert(_connection, list)
  }

  public override suspend fun upsertProductUnits(list: List<ProductUnit>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __upsertAdapterOfProductUnit.upsert(_connection, list)
  }

  public override suspend fun upsertUnit(u: UnitDef): Unit = performSuspending(__db, false, true) {
      _connection ->
    __upsertAdapterOfUnitDef.upsert(_connection, u)
  }

  public override suspend fun upsertCashbook(c: Cashbook): Unit = performSuspending(__db, false,
      true) { _connection ->
    __upsertAdapterOfCashbook.upsert(_connection, c)
  }

  public override suspend fun upsertProduct(p: Product): Unit = performSuspending(__db, false, true)
      { _connection ->
    __upsertAdapterOfProduct.upsert(_connection, p)
  }

  public override suspend fun upsertMove(m: StockMove): Unit = performSuspending(__db, false, true)
      { _connection ->
    __upsertAdapterOfStockMove.upsert(_connection, m)
  }

  public override suspend fun upsertMoves(list: List<StockMove>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __upsertAdapterOfStockMove.upsert(_connection, list)
  }

  public override suspend fun upsertCustomer(c: Customer): Unit = performSuspending(__db, false,
      true) { _connection ->
    __upsertAdapterOfCustomer.upsert(_connection, c)
  }

  public override suspend fun upsertInvoice(i: Invoice): Unit = performSuspending(__db, false, true)
      { _connection ->
    __upsertAdapterOfInvoice.upsert(_connection, i)
  }

  public override suspend fun upsertLines(list: List<InvoiceLine>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __upsertAdapterOfInvoiceLine.upsert(_connection, list)
  }

  public override suspend fun upsertPayment(p: Payment): Unit = performSuspending(__db, false, true)
      { _connection ->
    __upsertAdapterOfPayment.upsert(_connection, p)
  }

  public override suspend fun upsertUser(u: User): Unit = performSuspending(__db, false, true) {
      _connection ->
    __upsertAdapterOfUser.upsert(_connection, u)
  }

  public override suspend fun upsertWarehouses(list: List<Warehouse>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __upsertAdapterOfWarehouse.upsert(_connection, list)
  }

  public override suspend fun upsertProducts(list: List<Product>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __upsertAdapterOfProduct.upsert(_connection, list)
  }

  public override suspend fun upsertCustomers(list: List<Customer>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __upsertAdapterOfCustomer.upsert(_connection, list)
  }

  public override suspend fun upsertInvoices(list: List<Invoice>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __upsertAdapterOfInvoice.upsert(_connection, list)
  }

  public override suspend fun upsertPayments(list: List<Payment>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __upsertAdapterOfPayment.upsert(_connection, list)
  }

  public override suspend fun upsertUsers(list: List<User>): Unit = performSuspending(__db, false,
      true) { _connection ->
    __upsertAdapterOfUser.upsert(_connection, list)
  }

  public override suspend fun upsertUnits(list: List<UnitDef>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __upsertAdapterOfUnitDef.upsert(_connection, list)
  }

  public override suspend fun upsertCashbooks(list: List<Cashbook>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __upsertAdapterOfCashbook.upsert(_connection, list)
  }

  public override fun warehouses(): Flow<List<Warehouse>> {
    val _sql: String = "SELECT * FROM warehouses WHERE deleted = 0 ORDER BY name"
    return createFlow(__db, false, arrayOf("warehouses")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _result: MutableList<Warehouse> = mutableListOf()
        while (_stmt.step()) {
          val _item: Warehouse
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          _item = Warehouse(_tmpId,_tmpName,_tmpUpdatedAt,_tmpDeleted)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun warehouseList(): List<Warehouse> {
    val _sql: String = "SELECT * FROM warehouses WHERE deleted = 0 ORDER BY name"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _result: MutableList<Warehouse> = mutableListOf()
        while (_stmt.step()) {
          val _item: Warehouse
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          _item = Warehouse(_tmpId,_tmpName,_tmpUpdatedAt,_tmpDeleted)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun warehouseCount(): Int {
    val _sql: String = "SELECT COUNT(*) FROM warehouses WHERE deleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun productsWithStockIn(
    q: String,
    wh: String?,
    states: List<String>,
  ): Flow<List<ProductStock>> {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("    SELECT p.*, COALESCE((SELECT SUM(m.qty) FROM stock_moves m")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        WHERE m.productId = p.id AND m.deleted = 0 AND (")
    _stringBuilder.append("?")
    _stringBuilder.append(" IS NULL OR m.warehouseId = ")
    _stringBuilder.append("?")
    _stringBuilder.append(")), 0) AS stock")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("    FROM products p WHERE p.deleted = 0")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append(" AND p.state IN (")
    val _inputSize: Int = states.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(") AND (p.name LIKE '%' || ")
    _stringBuilder.append("?")
    _stringBuilder.append(" || '%' OR p.barcode LIKE ")
    _stringBuilder.append("?")
    _stringBuilder.append(" || '%'")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        OR p.code LIKE '%' || ")
    _stringBuilder.append("?")
    _stringBuilder.append(" || '%'")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        OR EXISTS(SELECT 1 FROM barcodes b WHERE b.productId = p.id AND b.deleted = 0 AND b.code LIKE ")
    _stringBuilder.append("?")
    _stringBuilder.append(" || '%')) ORDER BY p.name")
    val _sql: String = _stringBuilder.toString()
    return createFlow(__db, false, arrayOf("stock_moves", "products", "barcodes")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (wh == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, wh)
        }
        _argIndex = 2
        if (wh == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, wh)
        }
        _argIndex = 3
        for (_item: String in states) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        _argIndex = 3 + _inputSize
        _stmt.bindText(_argIndex, q)
        _argIndex = 4 + _inputSize
        _stmt.bindText(_argIndex, q)
        _argIndex = 5 + _inputSize
        _stmt.bindText(_argIndex, q)
        _argIndex = 6 + _inputSize
        _stmt.bindText(_argIndex, q)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfBarcode: Int = getColumnIndexOrThrow(_stmt, "barcode")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBuyPrice: Int = getColumnIndexOrThrow(_stmt, "buyPrice")
        val _columnIndexOfSellPrice: Int = getColumnIndexOrThrow(_stmt, "sellPrice")
        val _columnIndexOfMinStock: Int = getColumnIndexOrThrow(_stmt, "minStock")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfWholesalePrice: Int = getColumnIndexOrThrow(_stmt, "wholesalePrice")
        val _columnIndexOfNightPrice: Int = getColumnIndexOrThrow(_stmt, "nightPrice")
        val _columnIndexOfCode: Int = getColumnIndexOrThrow(_stmt, "code")
        val _columnIndexOfState: Int = getColumnIndexOrThrow(_stmt, "state")
        val _columnIndexOfStock: Int = getColumnIndexOrThrow(_stmt, "stock")
        val _result: MutableList<ProductStock> = mutableListOf()
        while (_stmt.step()) {
          val _item_1: ProductStock
          val _tmpStock: Double
          _tmpStock = _stmt.getDouble(_columnIndexOfStock)
          val _tmpProduct: Product
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpBarcode: String
          _tmpBarcode = _stmt.getText(_columnIndexOfBarcode)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBuyPrice: Long
          _tmpBuyPrice = _stmt.getLong(_columnIndexOfBuyPrice)
          val _tmpSellPrice: Long
          _tmpSellPrice = _stmt.getLong(_columnIndexOfSellPrice)
          val _tmpMinStock: Double
          _tmpMinStock = _stmt.getDouble(_columnIndexOfMinStock)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          val _tmpWholesalePrice: Long
          _tmpWholesalePrice = _stmt.getLong(_columnIndexOfWholesalePrice)
          val _tmpNightPrice: Long
          _tmpNightPrice = _stmt.getLong(_columnIndexOfNightPrice)
          val _tmpCode: String
          _tmpCode = _stmt.getText(_columnIndexOfCode)
          val _tmpState: String
          _tmpState = _stmt.getText(_columnIndexOfState)
          _tmpProduct =
              Product(_tmpId,_tmpName,_tmpBarcode,_tmpUnit,_tmpBuyPrice,_tmpSellPrice,_tmpMinStock,_tmpUpdatedAt,_tmpDeleted,_tmpWholesalePrice,_tmpNightPrice,_tmpCode,_tmpState)
          _item_1 = ProductStock(_tmpProduct,_tmpStock)
          _result.add(_item_1)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun lowStock(wh: String?): List<ProductStock> {
    val _sql: String = """
        |SELECT * FROM (
        |    SELECT p.*, COALESCE((SELECT SUM(m.qty) FROM stock_moves m
        |        WHERE m.productId = p.id AND m.deleted = 0 AND (? IS NULL OR m.warehouseId = ?)), 0) AS stock
        |    FROM products p WHERE p.deleted = 0
        |) WHERE minStock > 0 AND stock <= minStock ORDER BY name
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (wh == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, wh)
        }
        _argIndex = 2
        if (wh == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, wh)
        }
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfBarcode: Int = getColumnIndexOrThrow(_stmt, "barcode")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBuyPrice: Int = getColumnIndexOrThrow(_stmt, "buyPrice")
        val _columnIndexOfSellPrice: Int = getColumnIndexOrThrow(_stmt, "sellPrice")
        val _columnIndexOfMinStock: Int = getColumnIndexOrThrow(_stmt, "minStock")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfWholesalePrice: Int = getColumnIndexOrThrow(_stmt, "wholesalePrice")
        val _columnIndexOfNightPrice: Int = getColumnIndexOrThrow(_stmt, "nightPrice")
        val _columnIndexOfCode: Int = getColumnIndexOrThrow(_stmt, "code")
        val _columnIndexOfState: Int = getColumnIndexOrThrow(_stmt, "state")
        val _columnIndexOfStock: Int = getColumnIndexOrThrow(_stmt, "stock")
        val _result: MutableList<ProductStock> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductStock
          val _tmpStock: Double
          _tmpStock = _stmt.getDouble(_columnIndexOfStock)
          val _tmpProduct: Product
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpBarcode: String
          _tmpBarcode = _stmt.getText(_columnIndexOfBarcode)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBuyPrice: Long
          _tmpBuyPrice = _stmt.getLong(_columnIndexOfBuyPrice)
          val _tmpSellPrice: Long
          _tmpSellPrice = _stmt.getLong(_columnIndexOfSellPrice)
          val _tmpMinStock: Double
          _tmpMinStock = _stmt.getDouble(_columnIndexOfMinStock)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          val _tmpWholesalePrice: Long
          _tmpWholesalePrice = _stmt.getLong(_columnIndexOfWholesalePrice)
          val _tmpNightPrice: Long
          _tmpNightPrice = _stmt.getLong(_columnIndexOfNightPrice)
          val _tmpCode: String
          _tmpCode = _stmt.getText(_columnIndexOfCode)
          val _tmpState: String
          _tmpState = _stmt.getText(_columnIndexOfState)
          _tmpProduct =
              Product(_tmpId,_tmpName,_tmpBarcode,_tmpUnit,_tmpBuyPrice,_tmpSellPrice,_tmpMinStock,_tmpUpdatedAt,_tmpDeleted,_tmpWholesalePrice,_tmpNightPrice,_tmpCode,_tmpState)
          _item = ProductStock(_tmpProduct,_tmpStock)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun product(id: String): Product? {
    val _sql: String = "SELECT * FROM products WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfBarcode: Int = getColumnIndexOrThrow(_stmt, "barcode")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBuyPrice: Int = getColumnIndexOrThrow(_stmt, "buyPrice")
        val _columnIndexOfSellPrice: Int = getColumnIndexOrThrow(_stmt, "sellPrice")
        val _columnIndexOfMinStock: Int = getColumnIndexOrThrow(_stmt, "minStock")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfWholesalePrice: Int = getColumnIndexOrThrow(_stmt, "wholesalePrice")
        val _columnIndexOfNightPrice: Int = getColumnIndexOrThrow(_stmt, "nightPrice")
        val _columnIndexOfCode: Int = getColumnIndexOrThrow(_stmt, "code")
        val _columnIndexOfState: Int = getColumnIndexOrThrow(_stmt, "state")
        val _result: Product?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpBarcode: String
          _tmpBarcode = _stmt.getText(_columnIndexOfBarcode)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBuyPrice: Long
          _tmpBuyPrice = _stmt.getLong(_columnIndexOfBuyPrice)
          val _tmpSellPrice: Long
          _tmpSellPrice = _stmt.getLong(_columnIndexOfSellPrice)
          val _tmpMinStock: Double
          _tmpMinStock = _stmt.getDouble(_columnIndexOfMinStock)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          val _tmpWholesalePrice: Long
          _tmpWholesalePrice = _stmt.getLong(_columnIndexOfWholesalePrice)
          val _tmpNightPrice: Long
          _tmpNightPrice = _stmt.getLong(_columnIndexOfNightPrice)
          val _tmpCode: String
          _tmpCode = _stmt.getText(_columnIndexOfCode)
          val _tmpState: String
          _tmpState = _stmt.getText(_columnIndexOfState)
          _result =
              Product(_tmpId,_tmpName,_tmpBarcode,_tmpUnit,_tmpBuyPrice,_tmpSellPrice,_tmpMinStock,_tmpUpdatedAt,_tmpDeleted,_tmpWholesalePrice,_tmpNightPrice,_tmpCode,_tmpState)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun productByBarcode(code: String): Product? {
    val _sql: String = """
        |
        |        SELECT * FROM products WHERE deleted = 0 AND (barcode = ? OR code = ?
        |            OR id IN (SELECT productId FROM barcodes WHERE code = ? AND deleted = 0)) LIMIT 1
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, code)
        _argIndex = 2
        _stmt.bindText(_argIndex, code)
        _argIndex = 3
        _stmt.bindText(_argIndex, code)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfBarcode: Int = getColumnIndexOrThrow(_stmt, "barcode")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBuyPrice: Int = getColumnIndexOrThrow(_stmt, "buyPrice")
        val _columnIndexOfSellPrice: Int = getColumnIndexOrThrow(_stmt, "sellPrice")
        val _columnIndexOfMinStock: Int = getColumnIndexOrThrow(_stmt, "minStock")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfWholesalePrice: Int = getColumnIndexOrThrow(_stmt, "wholesalePrice")
        val _columnIndexOfNightPrice: Int = getColumnIndexOrThrow(_stmt, "nightPrice")
        val _columnIndexOfCode: Int = getColumnIndexOrThrow(_stmt, "code")
        val _columnIndexOfState: Int = getColumnIndexOrThrow(_stmt, "state")
        val _result: Product?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpBarcode: String
          _tmpBarcode = _stmt.getText(_columnIndexOfBarcode)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBuyPrice: Long
          _tmpBuyPrice = _stmt.getLong(_columnIndexOfBuyPrice)
          val _tmpSellPrice: Long
          _tmpSellPrice = _stmt.getLong(_columnIndexOfSellPrice)
          val _tmpMinStock: Double
          _tmpMinStock = _stmt.getDouble(_columnIndexOfMinStock)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          val _tmpWholesalePrice: Long
          _tmpWholesalePrice = _stmt.getLong(_columnIndexOfWholesalePrice)
          val _tmpNightPrice: Long
          _tmpNightPrice = _stmt.getLong(_columnIndexOfNightPrice)
          val _tmpCode: String
          _tmpCode = _stmt.getText(_columnIndexOfCode)
          val _tmpState: String
          _tmpState = _stmt.getText(_columnIndexOfState)
          _result =
              Product(_tmpId,_tmpName,_tmpBarcode,_tmpUnit,_tmpBuyPrice,_tmpSellPrice,_tmpMinStock,_tmpUpdatedAt,_tmpDeleted,_tmpWholesalePrice,_tmpNightPrice,_tmpCode,_tmpState)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun barcodeByCode(code: String): Barcode? {
    val _sql: String = "SELECT * FROM barcodes WHERE code = ? AND deleted = 0 LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, code)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfCode: Int = getColumnIndexOrThrow(_stmt, "code")
        val _columnIndexOfUnitName: Int = getColumnIndexOrThrow(_stmt, "unitName")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _result: Barcode?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpCode: String
          _tmpCode = _stmt.getText(_columnIndexOfCode)
          val _tmpUnitName: String
          _tmpUnitName = _stmt.getText(_columnIndexOfUnitName)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          _result = Barcode(_tmpId,_tmpProductId,_tmpCode,_tmpUnitName,_tmpUpdatedAt,_tmpDeleted)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun barcodesOf(productId: String): List<Barcode> {
    val _sql: String = "SELECT * FROM barcodes WHERE productId = ? AND deleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfCode: Int = getColumnIndexOrThrow(_stmt, "code")
        val _columnIndexOfUnitName: Int = getColumnIndexOrThrow(_stmt, "unitName")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _result: MutableList<Barcode> = mutableListOf()
        while (_stmt.step()) {
          val _item: Barcode
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpCode: String
          _tmpCode = _stmt.getText(_columnIndexOfCode)
          val _tmpUnitName: String
          _tmpUnitName = _stmt.getText(_columnIndexOfUnitName)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          _item = Barcode(_tmpId,_tmpProductId,_tmpCode,_tmpUnitName,_tmpUpdatedAt,_tmpDeleted)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun allBarcodesOf(productId: String): List<Barcode> {
    val _sql: String = "SELECT * FROM barcodes WHERE productId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfCode: Int = getColumnIndexOrThrow(_stmt, "code")
        val _columnIndexOfUnitName: Int = getColumnIndexOrThrow(_stmt, "unitName")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _result: MutableList<Barcode> = mutableListOf()
        while (_stmt.step()) {
          val _item: Barcode
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpCode: String
          _tmpCode = _stmt.getText(_columnIndexOfCode)
          val _tmpUnitName: String
          _tmpUnitName = _stmt.getText(_columnIndexOfUnitName)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          _item = Barcode(_tmpId,_tmpProductId,_tmpCode,_tmpUnitName,_tmpUpdatedAt,_tmpDeleted)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun unitsOf(productId: String): List<ProductUnit> {
    val _sql: String =
        "SELECT * FROM product_units WHERE productId = ? AND deleted = 0 ORDER BY factor"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfUnitName: Int = getColumnIndexOrThrow(_stmt, "unitName")
        val _columnIndexOfFactor: Int = getColumnIndexOrThrow(_stmt, "factor")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _result: MutableList<ProductUnit> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductUnit
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpUnitName: String
          _tmpUnitName = _stmt.getText(_columnIndexOfUnitName)
          val _tmpFactor: Double
          _tmpFactor = _stmt.getDouble(_columnIndexOfFactor)
          val _tmpPrice: Long
          _tmpPrice = _stmt.getLong(_columnIndexOfPrice)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          _item =
              ProductUnit(_tmpId,_tmpProductId,_tmpUnitName,_tmpFactor,_tmpPrice,_tmpUpdatedAt,_tmpDeleted)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun allUnitsOf(productId: String): List<ProductUnit> {
    val _sql: String = "SELECT * FROM product_units WHERE productId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfUnitName: Int = getColumnIndexOrThrow(_stmt, "unitName")
        val _columnIndexOfFactor: Int = getColumnIndexOrThrow(_stmt, "factor")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _result: MutableList<ProductUnit> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductUnit
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpUnitName: String
          _tmpUnitName = _stmt.getText(_columnIndexOfUnitName)
          val _tmpFactor: Double
          _tmpFactor = _stmt.getDouble(_columnIndexOfFactor)
          val _tmpPrice: Long
          _tmpPrice = _stmt.getLong(_columnIndexOfPrice)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          _item =
              ProductUnit(_tmpId,_tmpProductId,_tmpUnitName,_tmpFactor,_tmpPrice,_tmpUpdatedAt,_tmpDeleted)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun productCodes(): List<String> {
    val _sql: String = "SELECT code FROM products WHERE code LIKE 'H-%'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: MutableList<String> = mutableListOf()
        while (_stmt.step()) {
          val _item: String
          _item = _stmt.getText(0)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun customerCodes(): List<String> {
    val _sql: String = "SELECT code FROM customers WHERE code LIKE 'M-%'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: MutableList<String> = mutableListOf()
        while (_stmt.step()) {
          val _item: String
          _item = _stmt.getText(0)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun units(): Flow<List<UnitDef>> {
    val _sql: String = "SELECT * FROM units WHERE deleted = 0 ORDER BY name"
    return createFlow(__db, false, arrayOf("units")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _result: MutableList<UnitDef> = mutableListOf()
        while (_stmt.step()) {
          val _item: UnitDef
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          _item = UnitDef(_tmpId,_tmpName,_tmpNote,_tmpUpdatedAt,_tmpDeleted)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun unitCount(): Int {
    val _sql: String = "SELECT COUNT(*) FROM units WHERE deleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun cashbooks(): Flow<List<Cashbook>> {
    val _sql: String = "SELECT * FROM cashbooks WHERE deleted = 0 ORDER BY name"
    return createFlow(__db, false, arrayOf("cashbooks")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _result: MutableList<Cashbook> = mutableListOf()
        while (_stmt.step()) {
          val _item: Cashbook
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          _item = Cashbook(_tmpId,_tmpName,_tmpUpdatedAt,_tmpDeleted)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun cashbookCount(): Int {
    val _sql: String = "SELECT COUNT(*) FROM cashbooks WHERE deleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun cashBalances(): Flow<List<CashBalance>> {
    val _sql: String =
        "SELECT cashbookId, COALESCE(SUM(amount), 0) AS balance FROM payments WHERE deleted = 0 GROUP BY cashbookId"
    return createFlow(__db, false, arrayOf("payments")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfCashbookId: Int = 0
        val _columnIndexOfBalance: Int = 1
        val _result: MutableList<CashBalance> = mutableListOf()
        while (_stmt.step()) {
          val _item: CashBalance
          val _tmpCashbookId: String
          _tmpCashbookId = _stmt.getText(_columnIndexOfCashbookId)
          val _tmpBalance: Long
          _tmpBalance = _stmt.getLong(_columnIndexOfBalance)
          _item = CashBalance(_tmpCashbookId,_tmpBalance)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun cashRows(
    cb: String?,
    from: Long,
    to: Long,
  ): Flow<List<CashRow>> {
    val _sql: String = """
        |
        |        SELECT p.*, c.name AS customerName, b.name AS cashbookName FROM payments p
        |        LEFT JOIN customers c ON c.id = p.customerId LEFT JOIN cashbooks b ON b.id = p.cashbookId
        |        WHERE p.deleted = 0 AND (? IS NULL OR p.cashbookId = ?) AND p.createdAt BETWEEN ? AND ?
        |        ORDER BY p.createdAt DESC LIMIT 1000
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("payments", "customers", "cashbooks")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (cb == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, cb)
        }
        _argIndex = 2
        if (cb == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, cb)
        }
        _argIndex = 3
        _stmt.bindLong(_argIndex, from)
        _argIndex = 4
        _stmt.bindLong(_argIndex, to)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCustomerId: Int = getColumnIndexOrThrow(_stmt, "customerId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfInvoiceId: Int = getColumnIndexOrThrow(_stmt, "invoiceId")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfCashbookId: Int = getColumnIndexOrThrow(_stmt, "cashbookId")
        val _columnIndexOfCustomerName: Int = getColumnIndexOrThrow(_stmt, "customerName")
        val _columnIndexOfCashbookName: Int = getColumnIndexOrThrow(_stmt, "cashbookName")
        val _result: MutableList<CashRow> = mutableListOf()
        while (_stmt.step()) {
          val _item: CashRow
          val _tmpCustomerName: String?
          if (_stmt.isNull(_columnIndexOfCustomerName)) {
            _tmpCustomerName = null
          } else {
            _tmpCustomerName = _stmt.getText(_columnIndexOfCustomerName)
          }
          val _tmpCashbookName: String?
          if (_stmt.isNull(_columnIndexOfCashbookName)) {
            _tmpCashbookName = null
          } else {
            _tmpCashbookName = _stmt.getText(_columnIndexOfCashbookName)
          }
          val _tmpPayment: Payment
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpCustomerId: String
          _tmpCustomerId = _stmt.getText(_columnIndexOfCustomerId)
          val _tmpAmount: Long
          _tmpAmount = _stmt.getLong(_columnIndexOfAmount)
          val _tmpInvoiceId: String?
          if (_stmt.isNull(_columnIndexOfInvoiceId)) {
            _tmpInvoiceId = null
          } else {
            _tmpInvoiceId = _stmt.getText(_columnIndexOfInvoiceId)
          }
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          val _tmpCashbookId: String
          _tmpCashbookId = _stmt.getText(_columnIndexOfCashbookId)
          _tmpPayment =
              Payment(_tmpId,_tmpCustomerId,_tmpAmount,_tmpInvoiceId,_tmpNote,_tmpUserId,_tmpCreatedAt,_tmpUpdatedAt,_tmpDeleted,_tmpCashbookId)
          _item = CashRow(_tmpPayment,_tmpCustomerName,_tmpCashbookName)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun stockOf(productId: String, wh: String): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(qty), 0) FROM stock_moves WHERE productId = ? AND warehouseId = ? AND deleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        _argIndex = 2
        _stmt.bindText(_argIndex, wh)
        val _result: Double
        if (_stmt.step()) {
          val _tmp: Double
          _tmp = _stmt.getDouble(0)
          _result = _tmp
        } else {
          _result = 0.0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun totalStock(id: String): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(qty), 0) FROM stock_moves WHERE productId = ? AND deleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _result: Double
        if (_stmt.step()) {
          val _tmp: Double
          _tmp = _stmt.getDouble(0)
          _result = _tmp
        } else {
          _result = 0.0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun recentMoves(): Flow<List<MoveRow>> {
    val _sql: String = """
        |
        |        SELECT m.*, p.name AS productName, w.name AS warehouseName FROM stock_moves m
        |        JOIN products p ON p.id = m.productId JOIN warehouses w ON w.id = m.warehouseId
        |        WHERE m.deleted = 0 ORDER BY m.createdAt DESC LIMIT 300
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("stock_moves", "products", "warehouses")) {
        _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfWarehouseId: Int = getColumnIndexOrThrow(_stmt, "warehouseId")
        val _columnIndexOfQty: Int = getColumnIndexOrThrow(_stmt, "qty")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfInvoiceId: Int = getColumnIndexOrThrow(_stmt, "invoiceId")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfProductName: Int = getColumnIndexOrThrow(_stmt, "productName")
        val _columnIndexOfWarehouseName: Int = getColumnIndexOrThrow(_stmt, "warehouseName")
        val _result: MutableList<MoveRow> = mutableListOf()
        while (_stmt.step()) {
          val _item: MoveRow
          val _tmpProductName: String
          _tmpProductName = _stmt.getText(_columnIndexOfProductName)
          val _tmpWarehouseName: String
          _tmpWarehouseName = _stmt.getText(_columnIndexOfWarehouseName)
          val _tmpMove: StockMove
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpWarehouseId: String
          _tmpWarehouseId = _stmt.getText(_columnIndexOfWarehouseId)
          val _tmpQty: Double
          _tmpQty = _stmt.getDouble(_columnIndexOfQty)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpInvoiceId: String?
          if (_stmt.isNull(_columnIndexOfInvoiceId)) {
            _tmpInvoiceId = null
          } else {
            _tmpInvoiceId = _stmt.getText(_columnIndexOfInvoiceId)
          }
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          _tmpMove =
              StockMove(_tmpId,_tmpProductId,_tmpWarehouseId,_tmpQty,_tmpType,_tmpInvoiceId,_tmpNote,_tmpUserId,_tmpCreatedAt,_tmpUpdatedAt,_tmpDeleted)
          _item = MoveRow(_tmpMove,_tmpProductName,_tmpWarehouseName)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun movesOfInvoice(invoiceId: String): List<StockMove> {
    val _sql: String = "SELECT * FROM stock_moves WHERE invoiceId = ? AND deleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, invoiceId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfWarehouseId: Int = getColumnIndexOrThrow(_stmt, "warehouseId")
        val _columnIndexOfQty: Int = getColumnIndexOrThrow(_stmt, "qty")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfInvoiceId: Int = getColumnIndexOrThrow(_stmt, "invoiceId")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _result: MutableList<StockMove> = mutableListOf()
        while (_stmt.step()) {
          val _item: StockMove
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpWarehouseId: String
          _tmpWarehouseId = _stmt.getText(_columnIndexOfWarehouseId)
          val _tmpQty: Double
          _tmpQty = _stmt.getDouble(_columnIndexOfQty)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpInvoiceId: String?
          if (_stmt.isNull(_columnIndexOfInvoiceId)) {
            _tmpInvoiceId = null
          } else {
            _tmpInvoiceId = _stmt.getText(_columnIndexOfInvoiceId)
          }
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          _item =
              StockMove(_tmpId,_tmpProductId,_tmpWarehouseId,_tmpQty,_tmpType,_tmpInvoiceId,_tmpNote,_tmpUserId,_tmpCreatedAt,_tmpUpdatedAt,_tmpDeleted)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun customersWithDebtIn(q: String, states: List<String>):
      Flow<List<CustomerBalance>> {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("    SELECT c.*,")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        COALESCE((SELECT SUM(CASE i.kind WHEN 'PURCHASE' THEN -i.total WHEN 'RETURN_IN' THEN -i.total WHEN 'TRANSFER' THEN 0 ELSE i.total END) FROM invoices i")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("            WHERE i.customerId = c.id AND i.status = 'POSTED' AND i.deleted = 0), 0)")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("      - COALESCE((SELECT SUM(p.amount) FROM payments p")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("            WHERE p.customerId = c.id AND p.deleted = 0), 0) AS debt")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("    FROM customers c WHERE c.deleted = 0")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append(" AND c.state IN (")
    val _inputSize: Int = states.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(") AND (c.name LIKE '%' || ")
    _stringBuilder.append("?")
    _stringBuilder.append(" || '%' OR c.phone LIKE '%' || ")
    _stringBuilder.append("?")
    _stringBuilder.append(" || '%' OR c.code LIKE '%' || ")
    _stringBuilder.append("?")
    _stringBuilder.append(" || '%') ORDER BY c.name")
    val _sql: String = _stringBuilder.toString()
    return createFlow(__db, false, arrayOf("invoices", "payments", "customers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: String in states) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        _argIndex = 1 + _inputSize
        _stmt.bindText(_argIndex, q)
        _argIndex = 2 + _inputSize
        _stmt.bindText(_argIndex, q)
        _argIndex = 3 + _inputSize
        _stmt.bindText(_argIndex, q)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfCode: Int = getColumnIndexOrThrow(_stmt, "code")
        val _columnIndexOfPhone2: Int = getColumnIndexOrThrow(_stmt, "phone2")
        val _columnIndexOfAddress: Int = getColumnIndexOrThrow(_stmt, "address")
        val _columnIndexOfCreditLimit: Int = getColumnIndexOrThrow(_stmt, "creditLimit")
        val _columnIndexOfPayDays: Int = getColumnIndexOrThrow(_stmt, "payDays")
        val _columnIndexOfState: Int = getColumnIndexOrThrow(_stmt, "state")
        val _columnIndexOfDebt: Int = getColumnIndexOrThrow(_stmt, "debt")
        val _result: MutableList<CustomerBalance> = mutableListOf()
        while (_stmt.step()) {
          val _item_1: CustomerBalance
          val _tmpDebt: Long
          _tmpDebt = _stmt.getLong(_columnIndexOfDebt)
          val _tmpCustomer: Customer
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_columnIndexOfPhone)
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          val _tmpCode: String
          _tmpCode = _stmt.getText(_columnIndexOfCode)
          val _tmpPhone2: String
          _tmpPhone2 = _stmt.getText(_columnIndexOfPhone2)
          val _tmpAddress: String
          _tmpAddress = _stmt.getText(_columnIndexOfAddress)
          val _tmpCreditLimit: Long
          _tmpCreditLimit = _stmt.getLong(_columnIndexOfCreditLimit)
          val _tmpPayDays: Int
          _tmpPayDays = _stmt.getLong(_columnIndexOfPayDays).toInt()
          val _tmpState: String
          _tmpState = _stmt.getText(_columnIndexOfState)
          _tmpCustomer =
              Customer(_tmpId,_tmpName,_tmpPhone,_tmpNote,_tmpUpdatedAt,_tmpDeleted,_tmpCode,_tmpPhone2,_tmpAddress,_tmpCreditLimit,_tmpPayDays,_tmpState)
          _item_1 = CustomerBalance(_tmpCustomer,_tmpDebt)
          _result.add(_item_1)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun debtors(): List<CustomerBalance> {
    val _sql: String = """
        |SELECT * FROM (
        |    SELECT c.*,
        |        COALESCE((SELECT SUM(CASE i.kind WHEN 'PURCHASE' THEN -i.total WHEN 'RETURN_IN' THEN -i.total WHEN 'TRANSFER' THEN 0 ELSE i.total END) FROM invoices i
        |            WHERE i.customerId = c.id AND i.status = 'POSTED' AND i.deleted = 0), 0)
        |      - COALESCE((SELECT SUM(p.amount) FROM payments p
        |            WHERE p.customerId = c.id AND p.deleted = 0), 0) AS debt
        |    FROM customers c WHERE c.deleted = 0
        |) WHERE debt > 0 ORDER BY debt DESC
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfCode: Int = getColumnIndexOrThrow(_stmt, "code")
        val _columnIndexOfPhone2: Int = getColumnIndexOrThrow(_stmt, "phone2")
        val _columnIndexOfAddress: Int = getColumnIndexOrThrow(_stmt, "address")
        val _columnIndexOfCreditLimit: Int = getColumnIndexOrThrow(_stmt, "creditLimit")
        val _columnIndexOfPayDays: Int = getColumnIndexOrThrow(_stmt, "payDays")
        val _columnIndexOfState: Int = getColumnIndexOrThrow(_stmt, "state")
        val _columnIndexOfDebt: Int = getColumnIndexOrThrow(_stmt, "debt")
        val _result: MutableList<CustomerBalance> = mutableListOf()
        while (_stmt.step()) {
          val _item: CustomerBalance
          val _tmpDebt: Long
          _tmpDebt = _stmt.getLong(_columnIndexOfDebt)
          val _tmpCustomer: Customer
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_columnIndexOfPhone)
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          val _tmpCode: String
          _tmpCode = _stmt.getText(_columnIndexOfCode)
          val _tmpPhone2: String
          _tmpPhone2 = _stmt.getText(_columnIndexOfPhone2)
          val _tmpAddress: String
          _tmpAddress = _stmt.getText(_columnIndexOfAddress)
          val _tmpCreditLimit: Long
          _tmpCreditLimit = _stmt.getLong(_columnIndexOfCreditLimit)
          val _tmpPayDays: Int
          _tmpPayDays = _stmt.getLong(_columnIndexOfPayDays).toInt()
          val _tmpState: String
          _tmpState = _stmt.getText(_columnIndexOfState)
          _tmpCustomer =
              Customer(_tmpId,_tmpName,_tmpPhone,_tmpNote,_tmpUpdatedAt,_tmpDeleted,_tmpCode,_tmpPhone2,_tmpAddress,_tmpCreditLimit,_tmpPayDays,_tmpState)
          _item = CustomerBalance(_tmpCustomer,_tmpDebt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun customers(): Flow<List<Customer>> {
    val _sql: String =
        "SELECT * FROM customers WHERE deleted = 0 AND state = 'ACTIVE' ORDER BY name"
    return createFlow(__db, false, arrayOf("customers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfCode: Int = getColumnIndexOrThrow(_stmt, "code")
        val _columnIndexOfPhone2: Int = getColumnIndexOrThrow(_stmt, "phone2")
        val _columnIndexOfAddress: Int = getColumnIndexOrThrow(_stmt, "address")
        val _columnIndexOfCreditLimit: Int = getColumnIndexOrThrow(_stmt, "creditLimit")
        val _columnIndexOfPayDays: Int = getColumnIndexOrThrow(_stmt, "payDays")
        val _columnIndexOfState: Int = getColumnIndexOrThrow(_stmt, "state")
        val _result: MutableList<Customer> = mutableListOf()
        while (_stmt.step()) {
          val _item: Customer
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_columnIndexOfPhone)
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          val _tmpCode: String
          _tmpCode = _stmt.getText(_columnIndexOfCode)
          val _tmpPhone2: String
          _tmpPhone2 = _stmt.getText(_columnIndexOfPhone2)
          val _tmpAddress: String
          _tmpAddress = _stmt.getText(_columnIndexOfAddress)
          val _tmpCreditLimit: Long
          _tmpCreditLimit = _stmt.getLong(_columnIndexOfCreditLimit)
          val _tmpPayDays: Int
          _tmpPayDays = _stmt.getLong(_columnIndexOfPayDays).toInt()
          val _tmpState: String
          _tmpState = _stmt.getText(_columnIndexOfState)
          _item =
              Customer(_tmpId,_tmpName,_tmpPhone,_tmpNote,_tmpUpdatedAt,_tmpDeleted,_tmpCode,_tmpPhone2,_tmpAddress,_tmpCreditLimit,_tmpPayDays,_tmpState)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun invoiceCountOfCustomer(id: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM invoices WHERE customerId = ? AND deleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun paymentCountOfCustomer(id: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM payments WHERE customerId = ? AND deleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun moveCountOfProduct(id: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM stock_moves WHERE productId = ? AND deleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun lineCountOfProduct(id: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM invoice_lines WHERE productId = ? AND deleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun customerFlow(id: String): Flow<Customer?> {
    val _sql: String = "SELECT * FROM customers WHERE id = ?"
    return createFlow(__db, false, arrayOf("customers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfCode: Int = getColumnIndexOrThrow(_stmt, "code")
        val _columnIndexOfPhone2: Int = getColumnIndexOrThrow(_stmt, "phone2")
        val _columnIndexOfAddress: Int = getColumnIndexOrThrow(_stmt, "address")
        val _columnIndexOfCreditLimit: Int = getColumnIndexOrThrow(_stmt, "creditLimit")
        val _columnIndexOfPayDays: Int = getColumnIndexOrThrow(_stmt, "payDays")
        val _columnIndexOfState: Int = getColumnIndexOrThrow(_stmt, "state")
        val _result: Customer?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_columnIndexOfPhone)
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          val _tmpCode: String
          _tmpCode = _stmt.getText(_columnIndexOfCode)
          val _tmpPhone2: String
          _tmpPhone2 = _stmt.getText(_columnIndexOfPhone2)
          val _tmpAddress: String
          _tmpAddress = _stmt.getText(_columnIndexOfAddress)
          val _tmpCreditLimit: Long
          _tmpCreditLimit = _stmt.getLong(_columnIndexOfCreditLimit)
          val _tmpPayDays: Int
          _tmpPayDays = _stmt.getLong(_columnIndexOfPayDays).toInt()
          val _tmpState: String
          _tmpState = _stmt.getText(_columnIndexOfState)
          _result =
              Customer(_tmpId,_tmpName,_tmpPhone,_tmpNote,_tmpUpdatedAt,_tmpDeleted,_tmpCode,_tmpPhone2,_tmpAddress,_tmpCreditLimit,_tmpPayDays,_tmpState)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun customer(id: String): Customer? {
    val _sql: String = "SELECT * FROM customers WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfCode: Int = getColumnIndexOrThrow(_stmt, "code")
        val _columnIndexOfPhone2: Int = getColumnIndexOrThrow(_stmt, "phone2")
        val _columnIndexOfAddress: Int = getColumnIndexOrThrow(_stmt, "address")
        val _columnIndexOfCreditLimit: Int = getColumnIndexOrThrow(_stmt, "creditLimit")
        val _columnIndexOfPayDays: Int = getColumnIndexOrThrow(_stmt, "payDays")
        val _columnIndexOfState: Int = getColumnIndexOrThrow(_stmt, "state")
        val _result: Customer?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_columnIndexOfPhone)
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          val _tmpCode: String
          _tmpCode = _stmt.getText(_columnIndexOfCode)
          val _tmpPhone2: String
          _tmpPhone2 = _stmt.getText(_columnIndexOfPhone2)
          val _tmpAddress: String
          _tmpAddress = _stmt.getText(_columnIndexOfAddress)
          val _tmpCreditLimit: Long
          _tmpCreditLimit = _stmt.getLong(_columnIndexOfCreditLimit)
          val _tmpPayDays: Int
          _tmpPayDays = _stmt.getLong(_columnIndexOfPayDays).toInt()
          val _tmpState: String
          _tmpState = _stmt.getText(_columnIndexOfState)
          _result =
              Customer(_tmpId,_tmpName,_tmpPhone,_tmpNote,_tmpUpdatedAt,_tmpDeleted,_tmpCode,_tmpPhone2,_tmpAddress,_tmpCreditLimit,_tmpPayDays,_tmpState)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun debtOf(id: String): Flow<Long> {
    val _sql: String = """
        |
        |        SELECT COALESCE((SELECT SUM(CASE i.kind WHEN 'PURCHASE' THEN -i.total WHEN 'RETURN_IN' THEN -i.total WHEN 'TRANSFER' THEN 0 ELSE i.total END) FROM invoices i WHERE i.customerId = ? AND i.status = 'POSTED' AND i.deleted = 0), 0)
        |             - COALESCE((SELECT SUM(amount) FROM payments WHERE customerId = ? AND deleted = 0), 0)
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("invoices", "payments")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        _argIndex = 2
        _stmt.bindText(_argIndex, id)
        val _result: Long
        if (_stmt.step()) {
          val _tmp: Long
          _tmp = _stmt.getLong(0)
          _result = _tmp
        } else {
          _result = 0L
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun invoices(status: String?): Flow<List<InvoiceRow>> {
    val _sql: String = """
        |
        |        SELECT i.*, c.name AS customerName FROM invoices i LEFT JOIN customers c ON c.id = i.customerId
        |        WHERE i.deleted = 0 AND (? IS NULL OR i.status = ?)
        |        ORDER BY i.createdAt DESC LIMIT 1000
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("invoices", "customers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (status == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, status)
        }
        _argIndex = 2
        if (status == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, status)
        }
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNumber: Int = getColumnIndexOrThrow(_stmt, "number")
        val _columnIndexOfCustomerId: Int = getColumnIndexOrThrow(_stmt, "customerId")
        val _columnIndexOfWarehouseId: Int = getColumnIndexOrThrow(_stmt, "warehouseId")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTotal: Int = getColumnIndexOrThrow(_stmt, "total")
        val _columnIndexOfDiscount: Int = getColumnIndexOrThrow(_stmt, "discount")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfWholesale: Int = getColumnIndexOrThrow(_stmt, "wholesale")
        val _columnIndexOfKind: Int = getColumnIndexOrThrow(_stmt, "kind")
        val _columnIndexOfPayType: Int = getColumnIndexOrThrow(_stmt, "payType")
        val _columnIndexOfCashbookId: Int = getColumnIndexOrThrow(_stmt, "cashbookId")
        val _columnIndexOfToWarehouseId: Int = getColumnIndexOrThrow(_stmt, "toWarehouseId")
        val _columnIndexOfPaid: Int = getColumnIndexOrThrow(_stmt, "paid")
        val _columnIndexOfDiscountPct: Int = getColumnIndexOrThrow(_stmt, "discountPct")
        val _columnIndexOfCustomerName: Int = getColumnIndexOrThrow(_stmt, "customerName")
        val _result: MutableList<InvoiceRow> = mutableListOf()
        while (_stmt.step()) {
          val _item: InvoiceRow
          val _tmpCustomerName: String?
          if (_stmt.isNull(_columnIndexOfCustomerName)) {
            _tmpCustomerName = null
          } else {
            _tmpCustomerName = _stmt.getText(_columnIndexOfCustomerName)
          }
          val _tmpInvoice: Invoice
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpNumber: String
          _tmpNumber = _stmt.getText(_columnIndexOfNumber)
          val _tmpCustomerId: String?
          if (_stmt.isNull(_columnIndexOfCustomerId)) {
            _tmpCustomerId = null
          } else {
            _tmpCustomerId = _stmt.getText(_columnIndexOfCustomerId)
          }
          val _tmpWarehouseId: String
          _tmpWarehouseId = _stmt.getText(_columnIndexOfWarehouseId)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTotal: Long
          _tmpTotal = _stmt.getLong(_columnIndexOfTotal)
          val _tmpDiscount: Long
          _tmpDiscount = _stmt.getLong(_columnIndexOfDiscount)
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          val _tmpWholesale: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfWholesale).toInt()
          _tmpWholesale = _tmp_1 != 0
          val _tmpKind: String
          _tmpKind = _stmt.getText(_columnIndexOfKind)
          val _tmpPayType: String
          _tmpPayType = _stmt.getText(_columnIndexOfPayType)
          val _tmpCashbookId: String
          _tmpCashbookId = _stmt.getText(_columnIndexOfCashbookId)
          val _tmpToWarehouseId: String
          _tmpToWarehouseId = _stmt.getText(_columnIndexOfToWarehouseId)
          val _tmpPaid: Long
          _tmpPaid = _stmt.getLong(_columnIndexOfPaid)
          val _tmpDiscountPct: Double
          _tmpDiscountPct = _stmt.getDouble(_columnIndexOfDiscountPct)
          _tmpInvoice =
              Invoice(_tmpId,_tmpNumber,_tmpCustomerId,_tmpWarehouseId,_tmpStatus,_tmpTotal,_tmpDiscount,_tmpNote,_tmpUserId,_tmpCreatedAt,_tmpUpdatedAt,_tmpDeleted,_tmpWholesale,_tmpKind,_tmpPayType,_tmpCashbookId,_tmpToWarehouseId,_tmpPaid,_tmpDiscountPct)
          _item = InvoiceRow(_tmpInvoice,_tmpCustomerName)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun postedInvoicesOf(customerId: String): Flow<List<Invoice>> {
    val _sql: String =
        "SELECT * FROM invoices WHERE customerId = ? AND status = 'POSTED' AND kind != 'TRANSFER' AND deleted = 0 ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("invoices")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, customerId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNumber: Int = getColumnIndexOrThrow(_stmt, "number")
        val _columnIndexOfCustomerId: Int = getColumnIndexOrThrow(_stmt, "customerId")
        val _columnIndexOfWarehouseId: Int = getColumnIndexOrThrow(_stmt, "warehouseId")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTotal: Int = getColumnIndexOrThrow(_stmt, "total")
        val _columnIndexOfDiscount: Int = getColumnIndexOrThrow(_stmt, "discount")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfWholesale: Int = getColumnIndexOrThrow(_stmt, "wholesale")
        val _columnIndexOfKind: Int = getColumnIndexOrThrow(_stmt, "kind")
        val _columnIndexOfPayType: Int = getColumnIndexOrThrow(_stmt, "payType")
        val _columnIndexOfCashbookId: Int = getColumnIndexOrThrow(_stmt, "cashbookId")
        val _columnIndexOfToWarehouseId: Int = getColumnIndexOrThrow(_stmt, "toWarehouseId")
        val _columnIndexOfPaid: Int = getColumnIndexOrThrow(_stmt, "paid")
        val _columnIndexOfDiscountPct: Int = getColumnIndexOrThrow(_stmt, "discountPct")
        val _result: MutableList<Invoice> = mutableListOf()
        while (_stmt.step()) {
          val _item: Invoice
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpNumber: String
          _tmpNumber = _stmt.getText(_columnIndexOfNumber)
          val _tmpCustomerId: String?
          if (_stmt.isNull(_columnIndexOfCustomerId)) {
            _tmpCustomerId = null
          } else {
            _tmpCustomerId = _stmt.getText(_columnIndexOfCustomerId)
          }
          val _tmpWarehouseId: String
          _tmpWarehouseId = _stmt.getText(_columnIndexOfWarehouseId)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTotal: Long
          _tmpTotal = _stmt.getLong(_columnIndexOfTotal)
          val _tmpDiscount: Long
          _tmpDiscount = _stmt.getLong(_columnIndexOfDiscount)
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          val _tmpWholesale: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfWholesale).toInt()
          _tmpWholesale = _tmp_1 != 0
          val _tmpKind: String
          _tmpKind = _stmt.getText(_columnIndexOfKind)
          val _tmpPayType: String
          _tmpPayType = _stmt.getText(_columnIndexOfPayType)
          val _tmpCashbookId: String
          _tmpCashbookId = _stmt.getText(_columnIndexOfCashbookId)
          val _tmpToWarehouseId: String
          _tmpToWarehouseId = _stmt.getText(_columnIndexOfToWarehouseId)
          val _tmpPaid: Long
          _tmpPaid = _stmt.getLong(_columnIndexOfPaid)
          val _tmpDiscountPct: Double
          _tmpDiscountPct = _stmt.getDouble(_columnIndexOfDiscountPct)
          _item =
              Invoice(_tmpId,_tmpNumber,_tmpCustomerId,_tmpWarehouseId,_tmpStatus,_tmpTotal,_tmpDiscount,_tmpNote,_tmpUserId,_tmpCreatedAt,_tmpUpdatedAt,_tmpDeleted,_tmpWholesale,_tmpKind,_tmpPayType,_tmpCashbookId,_tmpToWarehouseId,_tmpPaid,_tmpDiscountPct)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun invoice(id: String): Invoice? {
    val _sql: String = "SELECT * FROM invoices WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNumber: Int = getColumnIndexOrThrow(_stmt, "number")
        val _columnIndexOfCustomerId: Int = getColumnIndexOrThrow(_stmt, "customerId")
        val _columnIndexOfWarehouseId: Int = getColumnIndexOrThrow(_stmt, "warehouseId")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTotal: Int = getColumnIndexOrThrow(_stmt, "total")
        val _columnIndexOfDiscount: Int = getColumnIndexOrThrow(_stmt, "discount")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfWholesale: Int = getColumnIndexOrThrow(_stmt, "wholesale")
        val _columnIndexOfKind: Int = getColumnIndexOrThrow(_stmt, "kind")
        val _columnIndexOfPayType: Int = getColumnIndexOrThrow(_stmt, "payType")
        val _columnIndexOfCashbookId: Int = getColumnIndexOrThrow(_stmt, "cashbookId")
        val _columnIndexOfToWarehouseId: Int = getColumnIndexOrThrow(_stmt, "toWarehouseId")
        val _columnIndexOfPaid: Int = getColumnIndexOrThrow(_stmt, "paid")
        val _columnIndexOfDiscountPct: Int = getColumnIndexOrThrow(_stmt, "discountPct")
        val _result: Invoice?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpNumber: String
          _tmpNumber = _stmt.getText(_columnIndexOfNumber)
          val _tmpCustomerId: String?
          if (_stmt.isNull(_columnIndexOfCustomerId)) {
            _tmpCustomerId = null
          } else {
            _tmpCustomerId = _stmt.getText(_columnIndexOfCustomerId)
          }
          val _tmpWarehouseId: String
          _tmpWarehouseId = _stmt.getText(_columnIndexOfWarehouseId)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTotal: Long
          _tmpTotal = _stmt.getLong(_columnIndexOfTotal)
          val _tmpDiscount: Long
          _tmpDiscount = _stmt.getLong(_columnIndexOfDiscount)
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          val _tmpWholesale: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfWholesale).toInt()
          _tmpWholesale = _tmp_1 != 0
          val _tmpKind: String
          _tmpKind = _stmt.getText(_columnIndexOfKind)
          val _tmpPayType: String
          _tmpPayType = _stmt.getText(_columnIndexOfPayType)
          val _tmpCashbookId: String
          _tmpCashbookId = _stmt.getText(_columnIndexOfCashbookId)
          val _tmpToWarehouseId: String
          _tmpToWarehouseId = _stmt.getText(_columnIndexOfToWarehouseId)
          val _tmpPaid: Long
          _tmpPaid = _stmt.getLong(_columnIndexOfPaid)
          val _tmpDiscountPct: Double
          _tmpDiscountPct = _stmt.getDouble(_columnIndexOfDiscountPct)
          _result =
              Invoice(_tmpId,_tmpNumber,_tmpCustomerId,_tmpWarehouseId,_tmpStatus,_tmpTotal,_tmpDiscount,_tmpNote,_tmpUserId,_tmpCreatedAt,_tmpUpdatedAt,_tmpDeleted,_tmpWholesale,_tmpKind,_tmpPayType,_tmpCashbookId,_tmpToWarehouseId,_tmpPaid,_tmpDiscountPct)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun linesOf(invoiceId: String): List<InvoiceLine> {
    val _sql: String = "SELECT * FROM invoice_lines WHERE invoiceId = ? AND deleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, invoiceId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfInvoiceId: Int = getColumnIndexOrThrow(_stmt, "invoiceId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfProductName: Int = getColumnIndexOrThrow(_stmt, "productName")
        val _columnIndexOfQty: Int = getColumnIndexOrThrow(_stmt, "qty")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfCost: Int = getColumnIndexOrThrow(_stmt, "cost")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfUnitName: Int = getColumnIndexOrThrow(_stmt, "unitName")
        val _columnIndexOfFactor: Int = getColumnIndexOrThrow(_stmt, "factor")
        val _result: MutableList<InvoiceLine> = mutableListOf()
        while (_stmt.step()) {
          val _item: InvoiceLine
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpInvoiceId: String
          _tmpInvoiceId = _stmt.getText(_columnIndexOfInvoiceId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpProductName: String
          _tmpProductName = _stmt.getText(_columnIndexOfProductName)
          val _tmpQty: Double
          _tmpQty = _stmt.getDouble(_columnIndexOfQty)
          val _tmpPrice: Long
          _tmpPrice = _stmt.getLong(_columnIndexOfPrice)
          val _tmpCost: Long
          _tmpCost = _stmt.getLong(_columnIndexOfCost)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          val _tmpUnitName: String
          _tmpUnitName = _stmt.getText(_columnIndexOfUnitName)
          val _tmpFactor: Double
          _tmpFactor = _stmt.getDouble(_columnIndexOfFactor)
          _item =
              InvoiceLine(_tmpId,_tmpInvoiceId,_tmpProductId,_tmpProductName,_tmpQty,_tmpPrice,_tmpCost,_tmpUpdatedAt,_tmpDeleted,_tmpUnitName,_tmpFactor)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun allLinesOf(invoiceId: String): List<InvoiceLine> {
    val _sql: String = "SELECT * FROM invoice_lines WHERE invoiceId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, invoiceId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfInvoiceId: Int = getColumnIndexOrThrow(_stmt, "invoiceId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfProductName: Int = getColumnIndexOrThrow(_stmt, "productName")
        val _columnIndexOfQty: Int = getColumnIndexOrThrow(_stmt, "qty")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfCost: Int = getColumnIndexOrThrow(_stmt, "cost")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfUnitName: Int = getColumnIndexOrThrow(_stmt, "unitName")
        val _columnIndexOfFactor: Int = getColumnIndexOrThrow(_stmt, "factor")
        val _result: MutableList<InvoiceLine> = mutableListOf()
        while (_stmt.step()) {
          val _item: InvoiceLine
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpInvoiceId: String
          _tmpInvoiceId = _stmt.getText(_columnIndexOfInvoiceId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpProductName: String
          _tmpProductName = _stmt.getText(_columnIndexOfProductName)
          val _tmpQty: Double
          _tmpQty = _stmt.getDouble(_columnIndexOfQty)
          val _tmpPrice: Long
          _tmpPrice = _stmt.getLong(_columnIndexOfPrice)
          val _tmpCost: Long
          _tmpCost = _stmt.getLong(_columnIndexOfCost)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          val _tmpUnitName: String
          _tmpUnitName = _stmt.getText(_columnIndexOfUnitName)
          val _tmpFactor: Double
          _tmpFactor = _stmt.getDouble(_columnIndexOfFactor)
          _item =
              InvoiceLine(_tmpId,_tmpInvoiceId,_tmpProductId,_tmpProductName,_tmpQty,_tmpPrice,_tmpCost,_tmpUpdatedAt,_tmpDeleted,_tmpUnitName,_tmpFactor)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun paymentsOf(customerId: String): Flow<List<Payment>> {
    val _sql: String =
        "SELECT * FROM payments WHERE customerId = ? AND deleted = 0 ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("payments")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, customerId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCustomerId: Int = getColumnIndexOrThrow(_stmt, "customerId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfInvoiceId: Int = getColumnIndexOrThrow(_stmt, "invoiceId")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfCashbookId: Int = getColumnIndexOrThrow(_stmt, "cashbookId")
        val _result: MutableList<Payment> = mutableListOf()
        while (_stmt.step()) {
          val _item: Payment
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpCustomerId: String
          _tmpCustomerId = _stmt.getText(_columnIndexOfCustomerId)
          val _tmpAmount: Long
          _tmpAmount = _stmt.getLong(_columnIndexOfAmount)
          val _tmpInvoiceId: String?
          if (_stmt.isNull(_columnIndexOfInvoiceId)) {
            _tmpInvoiceId = null
          } else {
            _tmpInvoiceId = _stmt.getText(_columnIndexOfInvoiceId)
          }
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          val _tmpCashbookId: String
          _tmpCashbookId = _stmt.getText(_columnIndexOfCashbookId)
          _item =
              Payment(_tmpId,_tmpCustomerId,_tmpAmount,_tmpInvoiceId,_tmpNote,_tmpUserId,_tmpCreatedAt,_tmpUpdatedAt,_tmpDeleted,_tmpCashbookId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun paymentsOfInvoice(invoiceId: String): List<Payment> {
    val _sql: String = "SELECT * FROM payments WHERE invoiceId = ? AND deleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, invoiceId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCustomerId: Int = getColumnIndexOrThrow(_stmt, "customerId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfInvoiceId: Int = getColumnIndexOrThrow(_stmt, "invoiceId")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfCashbookId: Int = getColumnIndexOrThrow(_stmt, "cashbookId")
        val _result: MutableList<Payment> = mutableListOf()
        while (_stmt.step()) {
          val _item: Payment
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpCustomerId: String
          _tmpCustomerId = _stmt.getText(_columnIndexOfCustomerId)
          val _tmpAmount: Long
          _tmpAmount = _stmt.getLong(_columnIndexOfAmount)
          val _tmpInvoiceId: String?
          if (_stmt.isNull(_columnIndexOfInvoiceId)) {
            _tmpInvoiceId = null
          } else {
            _tmpInvoiceId = _stmt.getText(_columnIndexOfInvoiceId)
          }
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          val _tmpCashbookId: String
          _tmpCashbookId = _stmt.getText(_columnIndexOfCashbookId)
          _item =
              Payment(_tmpId,_tmpCustomerId,_tmpAmount,_tmpInvoiceId,_tmpNote,_tmpUserId,_tmpCreatedAt,_tmpUpdatedAt,_tmpDeleted,_tmpCashbookId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun userCount(): Int {
    val _sql: String = "SELECT COUNT(*) FROM users WHERE deleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun userByLogin(login: String): User? {
    val _sql: String = "SELECT * FROM users WHERE login = ? AND deleted = 0 LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, login)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfLogin: Int = getColumnIndexOrThrow(_stmt, "login")
        val _columnIndexOfPassHash: Int = getColumnIndexOrThrow(_stmt, "passHash")
        val _columnIndexOfSalt: Int = getColumnIndexOrThrow(_stmt, "salt")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfActive: Int = getColumnIndexOrThrow(_stmt, "active")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _result: User?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpLogin: String
          _tmpLogin = _stmt.getText(_columnIndexOfLogin)
          val _tmpPassHash: String
          _tmpPassHash = _stmt.getText(_columnIndexOfPassHash)
          val _tmpSalt: String
          _tmpSalt = _stmt.getText(_columnIndexOfSalt)
          val _tmpRole: String
          _tmpRole = _stmt.getText(_columnIndexOfRole)
          val _tmpActive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfActive).toInt()
          _tmpActive = _tmp != 0
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp_1 != 0
          _result =
              User(_tmpId,_tmpName,_tmpLogin,_tmpPassHash,_tmpSalt,_tmpRole,_tmpActive,_tmpUpdatedAt,_tmpDeleted)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun users(): Flow<List<User>> {
    val _sql: String = "SELECT * FROM users WHERE deleted = 0 ORDER BY name"
    return createFlow(__db, false, arrayOf("users")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfLogin: Int = getColumnIndexOrThrow(_stmt, "login")
        val _columnIndexOfPassHash: Int = getColumnIndexOrThrow(_stmt, "passHash")
        val _columnIndexOfSalt: Int = getColumnIndexOrThrow(_stmt, "salt")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfActive: Int = getColumnIndexOrThrow(_stmt, "active")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _result: MutableList<User> = mutableListOf()
        while (_stmt.step()) {
          val _item: User
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpLogin: String
          _tmpLogin = _stmt.getText(_columnIndexOfLogin)
          val _tmpPassHash: String
          _tmpPassHash = _stmt.getText(_columnIndexOfPassHash)
          val _tmpSalt: String
          _tmpSalt = _stmt.getText(_columnIndexOfSalt)
          val _tmpRole: String
          _tmpRole = _stmt.getText(_columnIndexOfRole)
          val _tmpActive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfActive).toInt()
          _tmpActive = _tmp != 0
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp_1 != 0
          _item =
              User(_tmpId,_tmpName,_tmpLogin,_tmpPassHash,_tmpSalt,_tmpRole,_tmpActive,_tmpUpdatedAt,_tmpDeleted)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun salesByDay(from: Long, to: Long): List<DaySum> {
    val _sql: String = """
        |
        |        SELECT strftime('%Y-%m-%d', createdAt / 1000, 'unixepoch', 'localtime') AS day, SUM(total) AS total
        |        FROM invoices WHERE kind = 'SALE' AND status = 'POSTED' AND deleted = 0 AND createdAt BETWEEN ? AND ?
        |        GROUP BY day ORDER BY day
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, from)
        _argIndex = 2
        _stmt.bindLong(_argIndex, to)
        val _columnIndexOfDay: Int = 0
        val _columnIndexOfTotal: Int = 1
        val _result: MutableList<DaySum> = mutableListOf()
        while (_stmt.step()) {
          val _item: DaySum
          val _tmpDay: String
          _tmpDay = _stmt.getText(_columnIndexOfDay)
          val _tmpTotal: Long
          _tmpTotal = _stmt.getLong(_columnIndexOfTotal)
          _item = DaySum(_tmpDay,_tmpTotal)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun topProducts(from: Long, to: Long): List<TopProduct> {
    val _sql: String = """
        |
        |        SELECT l.productName AS productName, SUM(l.qty * l.factor) AS qty, CAST(SUM(l.qty * l.price) AS INTEGER) AS total
        |        FROM invoice_lines l JOIN invoices i ON i.id = l.invoiceId
        |        WHERE i.kind = 'SALE' AND i.status = 'POSTED' AND i.deleted = 0 AND l.deleted = 0 AND i.createdAt BETWEEN ? AND ?
        |        GROUP BY l.productId ORDER BY total DESC LIMIT 10
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, from)
        _argIndex = 2
        _stmt.bindLong(_argIndex, to)
        val _columnIndexOfProductName: Int = 0
        val _columnIndexOfQty: Int = 1
        val _columnIndexOfTotal: Int = 2
        val _result: MutableList<TopProduct> = mutableListOf()
        while (_stmt.step()) {
          val _item: TopProduct
          val _tmpProductName: String
          _tmpProductName = _stmt.getText(_columnIndexOfProductName)
          val _tmpQty: Double
          _tmpQty = _stmt.getDouble(_columnIndexOfQty)
          val _tmpTotal: Long
          _tmpTotal = _stmt.getLong(_columnIndexOfTotal)
          _item = TopProduct(_tmpProductName,_tmpQty,_tmpTotal)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun salesTotal(from: Long, to: Long): Long {
    val _sql: String =
        "SELECT COALESCE(SUM(total), 0) FROM invoices WHERE kind = 'SALE' AND status = 'POSTED' AND deleted = 0 AND createdAt BETWEEN ? AND ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, from)
        _argIndex = 2
        _stmt.bindLong(_argIndex, to)
        val _result: Long
        if (_stmt.step()) {
          val _tmp: Long
          _tmp = _stmt.getLong(0)
          _result = _tmp
        } else {
          _result = 0L
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun invoiceCount(from: Long, to: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM invoices WHERE kind = 'SALE' AND status = 'POSTED' AND deleted = 0 AND createdAt BETWEEN ? AND ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, from)
        _argIndex = 2
        _stmt.bindLong(_argIndex, to)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun grossProfit(from: Long, to: Long): Long {
    val _sql: String = """
        |
        |        SELECT CAST(COALESCE(SUM(l.qty * (l.price - l.cost)), 0) AS INTEGER)
        |        FROM invoice_lines l JOIN invoices i ON i.id = l.invoiceId
        |        WHERE i.kind = 'SALE' AND i.status = 'POSTED' AND i.deleted = 0 AND l.deleted = 0 AND i.createdAt BETWEEN ? AND ?
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, from)
        _argIndex = 2
        _stmt.bindLong(_argIndex, to)
        val _result: Long
        if (_stmt.step()) {
          val _tmp: Long
          _tmp = _stmt.getLong(0)
          _result = _tmp
        } else {
          _result = 0L
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun discountTotal(from: Long, to: Long): Long {
    val _sql: String =
        "SELECT COALESCE(SUM(discount), 0) FROM invoices WHERE kind = 'SALE' AND status = 'POSTED' AND deleted = 0 AND createdAt BETWEEN ? AND ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, from)
        _argIndex = 2
        _stmt.bindLong(_argIndex, to)
        val _result: Long
        if (_stmt.step()) {
          val _tmp: Long
          _tmp = _stmt.getLong(0)
          _result = _tmp
        } else {
          _result = 0L
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun paymentsTotal(from: Long, to: Long): Long {
    val _sql: String =
        "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE deleted = 0 AND amount > 0 AND createdAt BETWEEN ? AND ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, from)
        _argIndex = 2
        _stmt.bindLong(_argIndex, to)
        val _result: Long
        if (_stmt.step()) {
          val _tmp: Long
          _tmp = _stmt.getLong(0)
          _result = _tmp
        } else {
          _result = 0L
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun totalDebt(): Long {
    val _sql: String = """
        |
        |        SELECT COALESCE((SELECT SUM(CASE i.kind WHEN 'PURCHASE' THEN -i.total WHEN 'RETURN_IN' THEN -i.total WHEN 'TRANSFER' THEN 0 ELSE i.total END) FROM invoices i WHERE i.status = 'POSTED' AND i.deleted = 0
        |                    AND i.customerId IS NOT NULL AND i.customerId != ''), 0)
        |             - COALESCE((SELECT SUM(amount) FROM payments WHERE deleted = 0 AND customerId != ''), 0)
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Long
        if (_stmt.step()) {
          val _tmp: Long
          _tmp = _stmt.getLong(0)
          _result = _tmp
        } else {
          _result = 0L
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun chWarehouses(since: Long): List<Warehouse> {
    val _sql: String = "SELECT * FROM warehouses WHERE updatedAt > ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, since)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _result: MutableList<Warehouse> = mutableListOf()
        while (_stmt.step()) {
          val _item: Warehouse
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          _item = Warehouse(_tmpId,_tmpName,_tmpUpdatedAt,_tmpDeleted)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun chProducts(since: Long): List<Product> {
    val _sql: String = "SELECT * FROM products WHERE updatedAt > ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, since)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfBarcode: Int = getColumnIndexOrThrow(_stmt, "barcode")
        val _columnIndexOfUnit: Int = getColumnIndexOrThrow(_stmt, "unit")
        val _columnIndexOfBuyPrice: Int = getColumnIndexOrThrow(_stmt, "buyPrice")
        val _columnIndexOfSellPrice: Int = getColumnIndexOrThrow(_stmt, "sellPrice")
        val _columnIndexOfMinStock: Int = getColumnIndexOrThrow(_stmt, "minStock")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfWholesalePrice: Int = getColumnIndexOrThrow(_stmt, "wholesalePrice")
        val _columnIndexOfNightPrice: Int = getColumnIndexOrThrow(_stmt, "nightPrice")
        val _columnIndexOfCode: Int = getColumnIndexOrThrow(_stmt, "code")
        val _columnIndexOfState: Int = getColumnIndexOrThrow(_stmt, "state")
        val _result: MutableList<Product> = mutableListOf()
        while (_stmt.step()) {
          val _item: Product
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpBarcode: String
          _tmpBarcode = _stmt.getText(_columnIndexOfBarcode)
          val _tmpUnit: String
          _tmpUnit = _stmt.getText(_columnIndexOfUnit)
          val _tmpBuyPrice: Long
          _tmpBuyPrice = _stmt.getLong(_columnIndexOfBuyPrice)
          val _tmpSellPrice: Long
          _tmpSellPrice = _stmt.getLong(_columnIndexOfSellPrice)
          val _tmpMinStock: Double
          _tmpMinStock = _stmt.getDouble(_columnIndexOfMinStock)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          val _tmpWholesalePrice: Long
          _tmpWholesalePrice = _stmt.getLong(_columnIndexOfWholesalePrice)
          val _tmpNightPrice: Long
          _tmpNightPrice = _stmt.getLong(_columnIndexOfNightPrice)
          val _tmpCode: String
          _tmpCode = _stmt.getText(_columnIndexOfCode)
          val _tmpState: String
          _tmpState = _stmt.getText(_columnIndexOfState)
          _item =
              Product(_tmpId,_tmpName,_tmpBarcode,_tmpUnit,_tmpBuyPrice,_tmpSellPrice,_tmpMinStock,_tmpUpdatedAt,_tmpDeleted,_tmpWholesalePrice,_tmpNightPrice,_tmpCode,_tmpState)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun chMoves(since: Long): List<StockMove> {
    val _sql: String = "SELECT * FROM stock_moves WHERE updatedAt > ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, since)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfWarehouseId: Int = getColumnIndexOrThrow(_stmt, "warehouseId")
        val _columnIndexOfQty: Int = getColumnIndexOrThrow(_stmt, "qty")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfInvoiceId: Int = getColumnIndexOrThrow(_stmt, "invoiceId")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _result: MutableList<StockMove> = mutableListOf()
        while (_stmt.step()) {
          val _item: StockMove
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpWarehouseId: String
          _tmpWarehouseId = _stmt.getText(_columnIndexOfWarehouseId)
          val _tmpQty: Double
          _tmpQty = _stmt.getDouble(_columnIndexOfQty)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpInvoiceId: String?
          if (_stmt.isNull(_columnIndexOfInvoiceId)) {
            _tmpInvoiceId = null
          } else {
            _tmpInvoiceId = _stmt.getText(_columnIndexOfInvoiceId)
          }
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          _item =
              StockMove(_tmpId,_tmpProductId,_tmpWarehouseId,_tmpQty,_tmpType,_tmpInvoiceId,_tmpNote,_tmpUserId,_tmpCreatedAt,_tmpUpdatedAt,_tmpDeleted)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun chCustomers(since: Long): List<Customer> {
    val _sql: String = "SELECT * FROM customers WHERE updatedAt > ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, since)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfCode: Int = getColumnIndexOrThrow(_stmt, "code")
        val _columnIndexOfPhone2: Int = getColumnIndexOrThrow(_stmt, "phone2")
        val _columnIndexOfAddress: Int = getColumnIndexOrThrow(_stmt, "address")
        val _columnIndexOfCreditLimit: Int = getColumnIndexOrThrow(_stmt, "creditLimit")
        val _columnIndexOfPayDays: Int = getColumnIndexOrThrow(_stmt, "payDays")
        val _columnIndexOfState: Int = getColumnIndexOrThrow(_stmt, "state")
        val _result: MutableList<Customer> = mutableListOf()
        while (_stmt.step()) {
          val _item: Customer
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_columnIndexOfPhone)
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          val _tmpCode: String
          _tmpCode = _stmt.getText(_columnIndexOfCode)
          val _tmpPhone2: String
          _tmpPhone2 = _stmt.getText(_columnIndexOfPhone2)
          val _tmpAddress: String
          _tmpAddress = _stmt.getText(_columnIndexOfAddress)
          val _tmpCreditLimit: Long
          _tmpCreditLimit = _stmt.getLong(_columnIndexOfCreditLimit)
          val _tmpPayDays: Int
          _tmpPayDays = _stmt.getLong(_columnIndexOfPayDays).toInt()
          val _tmpState: String
          _tmpState = _stmt.getText(_columnIndexOfState)
          _item =
              Customer(_tmpId,_tmpName,_tmpPhone,_tmpNote,_tmpUpdatedAt,_tmpDeleted,_tmpCode,_tmpPhone2,_tmpAddress,_tmpCreditLimit,_tmpPayDays,_tmpState)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun chInvoices(since: Long): List<Invoice> {
    val _sql: String = "SELECT * FROM invoices WHERE updatedAt > ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, since)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNumber: Int = getColumnIndexOrThrow(_stmt, "number")
        val _columnIndexOfCustomerId: Int = getColumnIndexOrThrow(_stmt, "customerId")
        val _columnIndexOfWarehouseId: Int = getColumnIndexOrThrow(_stmt, "warehouseId")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTotal: Int = getColumnIndexOrThrow(_stmt, "total")
        val _columnIndexOfDiscount: Int = getColumnIndexOrThrow(_stmt, "discount")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfWholesale: Int = getColumnIndexOrThrow(_stmt, "wholesale")
        val _columnIndexOfKind: Int = getColumnIndexOrThrow(_stmt, "kind")
        val _columnIndexOfPayType: Int = getColumnIndexOrThrow(_stmt, "payType")
        val _columnIndexOfCashbookId: Int = getColumnIndexOrThrow(_stmt, "cashbookId")
        val _columnIndexOfToWarehouseId: Int = getColumnIndexOrThrow(_stmt, "toWarehouseId")
        val _columnIndexOfPaid: Int = getColumnIndexOrThrow(_stmt, "paid")
        val _columnIndexOfDiscountPct: Int = getColumnIndexOrThrow(_stmt, "discountPct")
        val _result: MutableList<Invoice> = mutableListOf()
        while (_stmt.step()) {
          val _item: Invoice
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpNumber: String
          _tmpNumber = _stmt.getText(_columnIndexOfNumber)
          val _tmpCustomerId: String?
          if (_stmt.isNull(_columnIndexOfCustomerId)) {
            _tmpCustomerId = null
          } else {
            _tmpCustomerId = _stmt.getText(_columnIndexOfCustomerId)
          }
          val _tmpWarehouseId: String
          _tmpWarehouseId = _stmt.getText(_columnIndexOfWarehouseId)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTotal: Long
          _tmpTotal = _stmt.getLong(_columnIndexOfTotal)
          val _tmpDiscount: Long
          _tmpDiscount = _stmt.getLong(_columnIndexOfDiscount)
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          val _tmpWholesale: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfWholesale).toInt()
          _tmpWholesale = _tmp_1 != 0
          val _tmpKind: String
          _tmpKind = _stmt.getText(_columnIndexOfKind)
          val _tmpPayType: String
          _tmpPayType = _stmt.getText(_columnIndexOfPayType)
          val _tmpCashbookId: String
          _tmpCashbookId = _stmt.getText(_columnIndexOfCashbookId)
          val _tmpToWarehouseId: String
          _tmpToWarehouseId = _stmt.getText(_columnIndexOfToWarehouseId)
          val _tmpPaid: Long
          _tmpPaid = _stmt.getLong(_columnIndexOfPaid)
          val _tmpDiscountPct: Double
          _tmpDiscountPct = _stmt.getDouble(_columnIndexOfDiscountPct)
          _item =
              Invoice(_tmpId,_tmpNumber,_tmpCustomerId,_tmpWarehouseId,_tmpStatus,_tmpTotal,_tmpDiscount,_tmpNote,_tmpUserId,_tmpCreatedAt,_tmpUpdatedAt,_tmpDeleted,_tmpWholesale,_tmpKind,_tmpPayType,_tmpCashbookId,_tmpToWarehouseId,_tmpPaid,_tmpDiscountPct)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun chLines(since: Long): List<InvoiceLine> {
    val _sql: String = "SELECT * FROM invoice_lines WHERE updatedAt > ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, since)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfInvoiceId: Int = getColumnIndexOrThrow(_stmt, "invoiceId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfProductName: Int = getColumnIndexOrThrow(_stmt, "productName")
        val _columnIndexOfQty: Int = getColumnIndexOrThrow(_stmt, "qty")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfCost: Int = getColumnIndexOrThrow(_stmt, "cost")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfUnitName: Int = getColumnIndexOrThrow(_stmt, "unitName")
        val _columnIndexOfFactor: Int = getColumnIndexOrThrow(_stmt, "factor")
        val _result: MutableList<InvoiceLine> = mutableListOf()
        while (_stmt.step()) {
          val _item: InvoiceLine
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpInvoiceId: String
          _tmpInvoiceId = _stmt.getText(_columnIndexOfInvoiceId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpProductName: String
          _tmpProductName = _stmt.getText(_columnIndexOfProductName)
          val _tmpQty: Double
          _tmpQty = _stmt.getDouble(_columnIndexOfQty)
          val _tmpPrice: Long
          _tmpPrice = _stmt.getLong(_columnIndexOfPrice)
          val _tmpCost: Long
          _tmpCost = _stmt.getLong(_columnIndexOfCost)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          val _tmpUnitName: String
          _tmpUnitName = _stmt.getText(_columnIndexOfUnitName)
          val _tmpFactor: Double
          _tmpFactor = _stmt.getDouble(_columnIndexOfFactor)
          _item =
              InvoiceLine(_tmpId,_tmpInvoiceId,_tmpProductId,_tmpProductName,_tmpQty,_tmpPrice,_tmpCost,_tmpUpdatedAt,_tmpDeleted,_tmpUnitName,_tmpFactor)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun chPayments(since: Long): List<Payment> {
    val _sql: String = "SELECT * FROM payments WHERE updatedAt > ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, since)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCustomerId: Int = getColumnIndexOrThrow(_stmt, "customerId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfInvoiceId: Int = getColumnIndexOrThrow(_stmt, "invoiceId")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _columnIndexOfCashbookId: Int = getColumnIndexOrThrow(_stmt, "cashbookId")
        val _result: MutableList<Payment> = mutableListOf()
        while (_stmt.step()) {
          val _item: Payment
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpCustomerId: String
          _tmpCustomerId = _stmt.getText(_columnIndexOfCustomerId)
          val _tmpAmount: Long
          _tmpAmount = _stmt.getLong(_columnIndexOfAmount)
          val _tmpInvoiceId: String?
          if (_stmt.isNull(_columnIndexOfInvoiceId)) {
            _tmpInvoiceId = null
          } else {
            _tmpInvoiceId = _stmt.getText(_columnIndexOfInvoiceId)
          }
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          val _tmpCashbookId: String
          _tmpCashbookId = _stmt.getText(_columnIndexOfCashbookId)
          _item =
              Payment(_tmpId,_tmpCustomerId,_tmpAmount,_tmpInvoiceId,_tmpNote,_tmpUserId,_tmpCreatedAt,_tmpUpdatedAt,_tmpDeleted,_tmpCashbookId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun chUsers(since: Long): List<User> {
    val _sql: String = "SELECT * FROM users WHERE updatedAt > ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, since)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfLogin: Int = getColumnIndexOrThrow(_stmt, "login")
        val _columnIndexOfPassHash: Int = getColumnIndexOrThrow(_stmt, "passHash")
        val _columnIndexOfSalt: Int = getColumnIndexOrThrow(_stmt, "salt")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfActive: Int = getColumnIndexOrThrow(_stmt, "active")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _result: MutableList<User> = mutableListOf()
        while (_stmt.step()) {
          val _item: User
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpLogin: String
          _tmpLogin = _stmt.getText(_columnIndexOfLogin)
          val _tmpPassHash: String
          _tmpPassHash = _stmt.getText(_columnIndexOfPassHash)
          val _tmpSalt: String
          _tmpSalt = _stmt.getText(_columnIndexOfSalt)
          val _tmpRole: String
          _tmpRole = _stmt.getText(_columnIndexOfRole)
          val _tmpActive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfActive).toInt()
          _tmpActive = _tmp != 0
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp_1 != 0
          _item =
              User(_tmpId,_tmpName,_tmpLogin,_tmpPassHash,_tmpSalt,_tmpRole,_tmpActive,_tmpUpdatedAt,_tmpDeleted)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun chUnits(since: Long): List<UnitDef> {
    val _sql: String = "SELECT * FROM units WHERE updatedAt > ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, since)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _result: MutableList<UnitDef> = mutableListOf()
        while (_stmt.step()) {
          val _item: UnitDef
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          _item = UnitDef(_tmpId,_tmpName,_tmpNote,_tmpUpdatedAt,_tmpDeleted)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun chProductUnits(since: Long): List<ProductUnit> {
    val _sql: String = "SELECT * FROM product_units WHERE updatedAt > ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, since)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfUnitName: Int = getColumnIndexOrThrow(_stmt, "unitName")
        val _columnIndexOfFactor: Int = getColumnIndexOrThrow(_stmt, "factor")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _result: MutableList<ProductUnit> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductUnit
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpUnitName: String
          _tmpUnitName = _stmt.getText(_columnIndexOfUnitName)
          val _tmpFactor: Double
          _tmpFactor = _stmt.getDouble(_columnIndexOfFactor)
          val _tmpPrice: Long
          _tmpPrice = _stmt.getLong(_columnIndexOfPrice)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          _item =
              ProductUnit(_tmpId,_tmpProductId,_tmpUnitName,_tmpFactor,_tmpPrice,_tmpUpdatedAt,_tmpDeleted)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun chBarcodes(since: Long): List<Barcode> {
    val _sql: String = "SELECT * FROM barcodes WHERE updatedAt > ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, since)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfCode: Int = getColumnIndexOrThrow(_stmt, "code")
        val _columnIndexOfUnitName: Int = getColumnIndexOrThrow(_stmt, "unitName")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _result: MutableList<Barcode> = mutableListOf()
        while (_stmt.step()) {
          val _item: Barcode
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpCode: String
          _tmpCode = _stmt.getText(_columnIndexOfCode)
          val _tmpUnitName: String
          _tmpUnitName = _stmt.getText(_columnIndexOfUnitName)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          _item = Barcode(_tmpId,_tmpProductId,_tmpCode,_tmpUnitName,_tmpUpdatedAt,_tmpDeleted)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun chCashbooks(since: Long): List<Cashbook> {
    val _sql: String = "SELECT * FROM cashbooks WHERE updatedAt > ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, since)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDeleted: Int = getColumnIndexOrThrow(_stmt, "deleted")
        val _result: MutableList<Cashbook> = mutableListOf()
        while (_stmt.step()) {
          val _item: Cashbook
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDeleted).toInt()
          _tmpDeleted = _tmp != 0
          _item = Cashbook(_tmpId,_tmpName,_tmpUpdatedAt,_tmpDeleted)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearWarehouses() {
    val _sql: String = "DELETE FROM warehouses"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearProducts() {
    val _sql: String = "DELETE FROM products"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearMoves() {
    val _sql: String = "DELETE FROM stock_moves"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearCustomers() {
    val _sql: String = "DELETE FROM customers"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearInvoices() {
    val _sql: String = "DELETE FROM invoices"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearLines() {
    val _sql: String = "DELETE FROM invoice_lines"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearPayments() {
    val _sql: String = "DELETE FROM payments"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearUsers() {
    val _sql: String = "DELETE FROM users"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearUnits() {
    val _sql: String = "DELETE FROM units"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearProductUnits() {
    val _sql: String = "DELETE FROM product_units"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearBarcodes() {
    val _sql: String = "DELETE FROM barcodes"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearCashbooks() {
    val _sql: String = "DELETE FROM cashbooks"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}

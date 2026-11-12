package tm.sowdahasap.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

// Pul hemişe teňňede (manat * 100) saklanýar: 1250 = 12.50 TMT
// Ähli ýazgylarda updatedAt we deleted bar — serwer bilen sinhronlaşdyrmak üçin.

@Serializable
@Entity(tableName = "warehouses")
data class Warehouse(
    @PrimaryKey val id: String,
    val name: String,
    val updatedAt: Long,
    val deleted: Boolean = false,
)

@Serializable
@Entity(tableName = "products", indices = [Index("barcode")])
data class Product(
    @PrimaryKey val id: String,
    val name: String,
    val barcode: String = "",       // köne wersiýadan galan (täzeleri barcodes tablisasynda)
    val unit: String = "san",       // esasy ölçeg
    val buyPrice: Long = 0,
    val sellPrice: Long = 0,        // bölek satuw bahasy (esasy ölçeg üçin)
    val minStock: Double = 0.0,
    val updatedAt: Long,
    val deleted: Boolean = false,
    @ColumnInfo(defaultValue = "0") val wholesalePrice: Long = 0, // opt bahasy (0 = bölek bahasy)
    @ColumnInfo(defaultValue = "0") val nightPrice: Long = 0,     // gijeki baha (0 = ýok)
    @ColumnInfo(defaultValue = "") val code: String = "",         // H-000001
    @ColumnInfo(defaultValue = "ACTIVE") val state: String = ItemState.ACTIVE,
)

/**
 * Haryt/müşderi hiç haçan pozulmaýar (hasabatlar we bergiler dogry galsyn diýip).
 * Diňe ýagdaýy üýtgeýär. Ýagdaýy yzyna "Ulanylýan" edip bolýar.
 */
object ItemState {
    const val ACTIVE = "ACTIVE"     // Ulanylýan
    const val HIDDEN = "HIDDEN"     // Wagtlaýynça görünmesiz
    const val ERROR = "ERROR"       // Ýalňyş (ýalňyş açylan, hereketi ýok)
    const val ARCHIVED = "ARCHIVED" // Öçürilen (hereketi bar, ýöne indi ulanylmaýar)
    val all = listOf(ACTIVE, HIDDEN, ERROR, ARCHIVED)

    fun title(s: String) = when (s) {
        ACTIVE -> "Ulanylýanlar"; HIDDEN -> "Wagtlaýynça görünmesizler"; ERROR -> "Ýalňyşlar"; ARCHIVED -> "Öçürilenler"; else -> s
    }
    fun one(s: String) = when (s) {
        ACTIVE -> "Ulanylýan"; HIDDEN -> "Wagtlaýynça görünmesiz"; ERROR -> "Ýalňyş"; ARCHIVED -> "Öçürilen"; else -> s
    }
}

object MoveType {
    const val IN = "IN"
    const val OUT = "OUT"
    const val SALE = "SALE"
    const val TRANSFER = "TRANSFER"
    const val ADJUST = "ADJUST"

    fun title(t: String) = when (t) {
        IN -> "Giriş"; OUT -> "Çykyş"; SALE -> "Satuw"; TRANSFER -> "Geçiriş"; ADJUST -> "Düzediş"; else -> t
    }
}

@Serializable
@Entity(tableName = "stock_moves", indices = [Index("productId"), Index("warehouseId"), Index("invoiceId")])
data class StockMove(
    @PrimaryKey val id: String,
    val productId: String,
    val warehouseId: String,
    val qty: Double,            // esasy ölçegde: + girýär, - çykýar
    val type: String,
    val invoiceId: String? = null,
    val note: String = "",
    val userId: String = "",
    val createdAt: Long,
    val updatedAt: Long,
    val deleted: Boolean = false,
)

@Serializable
@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String = "",
    val note: String = "",
    val updatedAt: Long,
    val deleted: Boolean = false,
    @ColumnInfo(defaultValue = "") val code: String = "",
    @ColumnInfo(defaultValue = "") val phone2: String = "",
    @ColumnInfo(defaultValue = "") val address: String = "",
    @ColumnInfo(defaultValue = "0") val creditLimit: Long = 0,  // iň köp bergi (0 = çäksiz)
    @ColumnInfo(defaultValue = "0") val payDays: Int = 0,       // tölegiň möhleti (gün)
    @ColumnInfo(defaultValue = "ACTIVE") val state: String = ItemState.ACTIVE,
)

object InvoiceStatus {
    const val DRAFT = "DRAFT"
    const val POSTED = "POSTED"
    const val CANCELLED = "CANCELLED"

    fun title(s: String) = when (s) {
        DRAFT -> "Garalama"; POSTED -> "Tassyklanan"; CANCELLED -> "Ýatyrylan"; else -> s
    }
}

object InvoiceKind {
    const val SALE = "SALE"             // Çykyş / Satylan
    const val PURCHASE = "PURCHASE"     // Giriş / Satyn alnan
    const val RETURN_IN = "RETURN_IN"   // Müşderiden yzyna gelen
    const val RETURN_OUT = "RETURN_OUT" // Üpjünçä yzyna
    const val TRANSFER = "TRANSFER"     // Ambarlaryň arasy
    val all = listOf(SALE, PURCHASE, RETURN_IN, RETURN_OUT, TRANSFER)

    fun title(k: String) = when (k) {
        SALE -> "Çykyş / Satylan"; PURCHASE -> "Giriş / Satyn alnan"; RETURN_IN -> "Müşderiden yzyna gelen"
        RETURN_OUT -> "Üpjünçä yzyna"; TRANSFER -> "Ambarlaryň arasy"; else -> k
    }

    /** Ammara täsiri: -1 çykýar, +1 girýär */
    fun stockSign(k: String): Double = when (k) { PURCHASE, RETURN_IN -> 1.0; else -> -1.0 }

    /** Kassa täsiri: +1 pul girýär, -1 pul çykýar */
    fun cashSign(k: String): Long = when (k) { SALE, RETURN_OUT -> 1L; PURCHASE, RETURN_IN -> -1L; else -> 0L }

    /** Satyn alnanda alyş bahasy, beýlekilerde satuw bahasy ulanylýar */
    fun usesBuyPrice(k: String) = k == PURCHASE || k == RETURN_OUT
}

object PayType {
    const val CASH = "CASH"       // Nagt (doly tölendi)
    const val PARTIAL = "PARTIAL" // Bölekleýin
    const val CREDIT = "CREDIT"   // Ýazgy (bergä)
    val all = listOf(CASH, PARTIAL, CREDIT)
    fun title(t: String) = when (t) { CASH -> "Nagt hasaplaşyk"; PARTIAL -> "Bölek töleg"; CREDIT -> "Ýazgy (bergä)"; else -> t }
}

@Serializable
@Entity(tableName = "invoices", indices = [Index("customerId"), Index("createdAt")])
data class Invoice(
    @PrimaryKey val id: String,
    val number: String,
    val customerId: String? = null,
    val warehouseId: String,
    val status: String = InvoiceStatus.DRAFT,
    val total: Long = 0,        // arzanladyşdan soňky jemi
    val discount: Long = 0,
    val note: String = "",
    val userId: String = "",
    val createdAt: Long,
    val updatedAt: Long,
    val deleted: Boolean = false,
    @ColumnInfo(defaultValue = "0") val wholesale: Boolean = false,
    @ColumnInfo(defaultValue = "SALE") val kind: String = InvoiceKind.SALE,
    @ColumnInfo(defaultValue = "CASH") val payType: String = PayType.CASH,
    @ColumnInfo(defaultValue = "main") val cashbookId: String = "main",
    @ColumnInfo(defaultValue = "") val toWarehouseId: String = "",
    @ColumnInfo(defaultValue = "0") val paid: Long = 0,
    @ColumnInfo(defaultValue = "0") val discountPct: Double = 0.0,
)

@Serializable
@Entity(tableName = "invoice_lines", indices = [Index("invoiceId"), Index("productId")])
data class InvoiceLine(
    @PrimaryKey val id: String,
    val invoiceId: String,
    val productId: String,
    val productName: String,
    val qty: Double,            // saýlanan ölçegde
    val price: Long,            // saýlanan ölçegiň 1-niň bahasy
    val cost: Long = 0,         // saýlanan ölçegiň 1-niň alyş bahasy
    val updatedAt: Long,
    val deleted: Boolean = false,
    @ColumnInfo(defaultValue = "") val unitName: String = "",
    @ColumnInfo(defaultValue = "1.0") val factor: Double = 1.0, // 1 ölçegde näçe esasy ölçeg
)

@Serializable
@Entity(tableName = "payments", indices = [Index("customerId"), Index("invoiceId")])
data class Payment(
    @PrimaryKey val id: String,
    val customerId: String,     // "" = müşderisiz kassa hereketi
    val amount: Long,           // + kassa girdi (müşderiniň bergisi azalýar), - kassadan çykdy
    val invoiceId: String? = null,
    val note: String = "",
    val userId: String = "",
    val createdAt: Long,
    val updatedAt: Long,
    val deleted: Boolean = false,
    @ColumnInfo(defaultValue = "main") val cashbookId: String = "main",
)

@Serializable
@Entity(tableName = "users", indices = [Index("login")])
data class User(
    @PrimaryKey val id: String,
    val name: String,
    val login: String,
    val passHash: String,
    val salt: String,
    val role: String,
    val active: Boolean = true,
    val updatedAt: Long,
    val deleted: Boolean = false,
)

@Serializable
@Entity(tableName = "units")
data class UnitDef(
    @PrimaryKey val id: String,
    val name: String,
    val note: String = "",
    val updatedAt: Long,
    val deleted: Boolean = false,
)

/** Goşmaça ölçeg: mysal pacga = 20 san */
@Serializable
@Entity(tableName = "product_units", indices = [Index("productId")])
data class ProductUnit(
    @PrimaryKey val id: String,
    val productId: String,
    val unitName: String,
    val factor: Double,
    val price: Long = 0,        // 0 = esasy baha × factor
    val updatedAt: Long,
    val deleted: Boolean = false,
)

@Serializable
@Entity(tableName = "barcodes", indices = [Index("code"), Index("productId")])
data class Barcode(
    @PrimaryKey val id: String,
    val productId: String,
    val code: String,
    val unitName: String = "",  // "" = esasy ölçeg
    val updatedAt: Long,
    val deleted: Boolean = false,
)

@Serializable
@Entity(tableName = "cashbooks")
data class Cashbook(
    @PrimaryKey val id: String,
    val name: String,
    val updatedAt: Long,
    val deleted: Boolean = false,
)

// ---- Sorag netijeleri ----

data class ProductStock(@Embedded val product: Product, val stock: Double)

data class CustomerBalance(@Embedded val customer: Customer, val debt: Long)

data class InvoiceRow(@Embedded val invoice: Invoice, val customerName: String?)

data class MoveRow(@Embedded val move: StockMove, val productName: String, val warehouseName: String)

data class CashRow(@Embedded val payment: Payment, val customerName: String?, val cashbookName: String?)

data class CashBalance(val cashbookId: String, val balance: Long)

data class DaySum(val day: String, val total: Long)

data class TopProduct(val productName: String, val qty: Double, val total: Long)

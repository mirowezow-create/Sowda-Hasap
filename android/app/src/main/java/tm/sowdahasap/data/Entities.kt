package tm.sowdahasap.data

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
    val barcode: String = "",
    val unit: String = "sany",
    val buyPrice: Long = 0,
    val sellPrice: Long = 0,
    val minStock: Double = 0.0,
    val updatedAt: Long,
    val deleted: Boolean = false,
)

object MoveType {
    const val IN = "IN"          // Giriş
    const val OUT = "OUT"        // Çykyş
    const val SALE = "SALE"      // Satuw (fakturadan)
    const val TRANSFER = "TRANSFER"
    const val ADJUST = "ADJUST"  // Inwentar düzedişi

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
    val qty: Double,            // + girýär, - çykýar
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
)

object InvoiceStatus {
    const val DRAFT = "DRAFT"
    const val POSTED = "POSTED"
    const val CANCELLED = "CANCELLED"

    fun title(s: String) = when (s) {
        DRAFT -> "Garalama"; POSTED -> "Tassyklanan"; CANCELLED -> "Ýatyrylan"; else -> s
    }
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
)

@Serializable
@Entity(tableName = "invoice_lines", indices = [Index("invoiceId"), Index("productId")])
data class InvoiceLine(
    @PrimaryKey val id: String,
    val invoiceId: String,
    val productId: String,
    val productName: String,
    val qty: Double,
    val price: Long,
    val cost: Long = 0,         // girdeji hasaplamak üçin alyş bahasy
    val updatedAt: Long,
    val deleted: Boolean = false,
)

@Serializable
@Entity(tableName = "payments", indices = [Index("customerId"), Index("invoiceId")])
data class Payment(
    @PrimaryKey val id: String,
    val customerId: String,
    val amount: Long,
    val invoiceId: String? = null,
    val note: String = "",
    val userId: String = "",
    val createdAt: Long,
    val updatedAt: Long,
    val deleted: Boolean = false,
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

// ---- Sorag netijeleri ----

data class ProductStock(@Embedded val product: Product, val stock: Double)

data class CustomerBalance(@Embedded val customer: Customer, val debt: Long)

data class InvoiceRow(@Embedded val invoice: Invoice, val customerName: String?)

data class MoveRow(@Embedded val move: StockMove, val productName: String, val warehouseName: String)

data class DaySum(val day: String, val total: Long)

data class TopProduct(val productName: String, val qty: Double, val total: Long)

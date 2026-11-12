package tm.sowdahasap.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

private const val PRODUCT_STOCK_SELECT = """
    SELECT p.*, COALESCE((SELECT SUM(m.qty) FROM stock_moves m
        WHERE m.productId = p.id AND m.deleted = 0 AND (:wh IS NULL OR m.warehouseId = :wh)), 0) AS stock
    FROM products p WHERE p.deleted = 0
"""

private const val CUSTOMER_DEBT_SELECT = """
    SELECT c.*,
        COALESCE((SELECT SUM(i.total) FROM invoices i
            WHERE i.customerId = c.id AND i.status = 'POSTED' AND i.deleted = 0), 0)
      - COALESCE((SELECT SUM(p.amount) FROM payments p
            WHERE p.customerId = c.id AND p.deleted = 0), 0) AS debt
    FROM customers c WHERE c.deleted = 0
"""

@Dao
interface AppDao {
    // ---------- Ammarlar ----------
    @Query("SELECT * FROM warehouses WHERE deleted = 0 ORDER BY name")
    fun warehouses(): Flow<List<Warehouse>>

    @Query("SELECT * FROM warehouses WHERE deleted = 0 ORDER BY name")
    suspend fun warehouseList(): List<Warehouse>

    @Query("SELECT COUNT(*) FROM warehouses WHERE deleted = 0")
    suspend fun warehouseCount(): Int

    @Upsert
    suspend fun upsertWarehouse(w: Warehouse)

    // ---------- Harytlar ----------
    @Query("$PRODUCT_STOCK_SELECT AND (p.name LIKE '%' || :q || '%' OR p.barcode LIKE :q || '%') ORDER BY p.name")
    fun productsWithStock(q: String, wh: String?): Flow<List<ProductStock>>

    @Query("SELECT * FROM ($PRODUCT_STOCK_SELECT) WHERE minStock > 0 AND stock <= minStock ORDER BY name")
    suspend fun lowStock(wh: String?): List<ProductStock>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun product(id: String): Product?

    @Query("SELECT * FROM products WHERE barcode = :code AND deleted = 0 LIMIT 1")
    suspend fun productByBarcode(code: String): Product?

    @Query("SELECT COALESCE(SUM(qty), 0) FROM stock_moves WHERE productId = :productId AND warehouseId = :wh AND deleted = 0")
    suspend fun stockOf(productId: String, wh: String): Double

    @Upsert
    suspend fun upsertProduct(p: Product)

    // ---------- Ammar hereketleri ----------
    @Query("""
        SELECT m.*, p.name AS productName, w.name AS warehouseName FROM stock_moves m
        JOIN products p ON p.id = m.productId JOIN warehouses w ON w.id = m.warehouseId
        WHERE m.deleted = 0 ORDER BY m.createdAt DESC LIMIT 300
    """)
    fun recentMoves(): Flow<List<MoveRow>>

    @Query("SELECT * FROM stock_moves WHERE invoiceId = :invoiceId AND deleted = 0")
    suspend fun movesOfInvoice(invoiceId: String): List<StockMove>

    @Upsert
    suspend fun upsertMove(m: StockMove)

    @Upsert
    suspend fun upsertMoves(list: List<StockMove>)

    // ---------- Müşderiler ----------
    @Query("$CUSTOMER_DEBT_SELECT AND (c.name LIKE '%' || :q || '%' OR c.phone LIKE '%' || :q || '%') ORDER BY c.name")
    fun customersWithDebt(q: String): Flow<List<CustomerBalance>>

    @Query("SELECT * FROM ($CUSTOMER_DEBT_SELECT) WHERE debt > 0 ORDER BY debt DESC")
    suspend fun debtors(): List<CustomerBalance>

    @Query("SELECT * FROM customers WHERE deleted = 0 ORDER BY name")
    fun customers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :id")
    fun customerFlow(id: String): Flow<Customer?>

    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun customer(id: String): Customer?

    @Query("""
        SELECT COALESCE((SELECT SUM(total) FROM invoices WHERE customerId = :id AND status = 'POSTED' AND deleted = 0), 0)
             - COALESCE((SELECT SUM(amount) FROM payments WHERE customerId = :id AND deleted = 0), 0)
    """)
    fun debtOf(id: String): Flow<Long>

    @Upsert
    suspend fun upsertCustomer(c: Customer)

    // ---------- Fakturalar ----------
    @Query("""
        SELECT i.*, c.name AS customerName FROM invoices i LEFT JOIN customers c ON c.id = i.customerId
        WHERE i.deleted = 0 AND (:status IS NULL OR i.status = :status)
        ORDER BY i.createdAt DESC LIMIT 500
    """)
    fun invoices(status: String?): Flow<List<InvoiceRow>>

    @Query("SELECT * FROM invoices WHERE customerId = :customerId AND status = 'POSTED' AND deleted = 0 ORDER BY createdAt DESC")
    fun postedInvoicesOf(customerId: String): Flow<List<Invoice>>

    @Query("SELECT * FROM invoices WHERE id = :id")
    suspend fun invoice(id: String): Invoice?

    @Query("SELECT * FROM invoice_lines WHERE invoiceId = :invoiceId AND deleted = 0")
    suspend fun linesOf(invoiceId: String): List<InvoiceLine>

    @Query("SELECT * FROM invoice_lines WHERE invoiceId = :invoiceId")
    suspend fun allLinesOf(invoiceId: String): List<InvoiceLine>

    @Upsert
    suspend fun upsertInvoice(i: Invoice)

    @Upsert
    suspend fun upsertLines(list: List<InvoiceLine>)

    // ---------- Tölegler ----------
    @Query("SELECT * FROM payments WHERE customerId = :customerId AND deleted = 0 ORDER BY createdAt DESC")
    fun paymentsOf(customerId: String): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE invoiceId = :invoiceId AND deleted = 0")
    suspend fun paymentsOfInvoice(invoiceId: String): List<Payment>

    @Upsert
    suspend fun upsertPayment(p: Payment)

    // ---------- Ulanyjylar ----------
    @Query("SELECT COUNT(*) FROM users WHERE deleted = 0")
    suspend fun userCount(): Int

    @Query("SELECT * FROM users WHERE login = :login AND deleted = 0 LIMIT 1")
    suspend fun userByLogin(login: String): User?

    @Query("SELECT * FROM users WHERE deleted = 0 ORDER BY name")
    fun users(): Flow<List<User>>

    @Upsert
    suspend fun upsertUser(u: User)

    // ---------- Hasabatlar ----------
    @Query("""
        SELECT strftime('%Y-%m-%d', createdAt / 1000, 'unixepoch', 'localtime') AS day, SUM(total) AS total
        FROM invoices WHERE status = 'POSTED' AND deleted = 0 AND createdAt BETWEEN :from AND :to
        GROUP BY day ORDER BY day
    """)
    suspend fun salesByDay(from: Long, to: Long): List<DaySum>

    @Query("""
        SELECT l.productName AS productName, SUM(l.qty) AS qty, CAST(SUM(l.qty * l.price) AS INTEGER) AS total
        FROM invoice_lines l JOIN invoices i ON i.id = l.invoiceId
        WHERE i.status = 'POSTED' AND i.deleted = 0 AND l.deleted = 0 AND i.createdAt BETWEEN :from AND :to
        GROUP BY l.productId ORDER BY total DESC LIMIT 10
    """)
    suspend fun topProducts(from: Long, to: Long): List<TopProduct>

    @Query("SELECT COALESCE(SUM(total), 0) FROM invoices WHERE status = 'POSTED' AND deleted = 0 AND createdAt BETWEEN :from AND :to")
    suspend fun salesTotal(from: Long, to: Long): Long

    @Query("SELECT COUNT(*) FROM invoices WHERE status = 'POSTED' AND deleted = 0 AND createdAt BETWEEN :from AND :to")
    suspend fun invoiceCount(from: Long, to: Long): Int

    @Query("""
        SELECT CAST(COALESCE(SUM(l.qty * (l.price - l.cost)), 0) AS INTEGER)
        FROM invoice_lines l JOIN invoices i ON i.id = l.invoiceId
        WHERE i.status = 'POSTED' AND i.deleted = 0 AND l.deleted = 0 AND i.createdAt BETWEEN :from AND :to
    """)
    suspend fun grossProfit(from: Long, to: Long): Long

    @Query("SELECT COALESCE(SUM(discount), 0) FROM invoices WHERE status = 'POSTED' AND deleted = 0 AND createdAt BETWEEN :from AND :to")
    suspend fun discountTotal(from: Long, to: Long): Long

    @Query("SELECT COALESCE(SUM(amount), 0) FROM payments WHERE deleted = 0 AND createdAt BETWEEN :from AND :to")
    suspend fun paymentsTotal(from: Long, to: Long): Long

    @Query("""
        SELECT COALESCE((SELECT SUM(total) FROM invoices WHERE status = 'POSTED' AND deleted = 0 AND customerId IS NOT NULL), 0)
             - COALESCE((SELECT SUM(amount) FROM payments WHERE deleted = 0), 0)
    """)
    suspend fun totalDebt(): Long

    // ---------- Sinhronlaşdyrma ----------
    @Query("SELECT * FROM warehouses WHERE updatedAt > :since") suspend fun chWarehouses(since: Long): List<Warehouse>
    @Query("SELECT * FROM products WHERE updatedAt > :since") suspend fun chProducts(since: Long): List<Product>
    @Query("SELECT * FROM stock_moves WHERE updatedAt > :since") suspend fun chMoves(since: Long): List<StockMove>
    @Query("SELECT * FROM customers WHERE updatedAt > :since") suspend fun chCustomers(since: Long): List<Customer>
    @Query("SELECT * FROM invoices WHERE updatedAt > :since") suspend fun chInvoices(since: Long): List<Invoice>
    @Query("SELECT * FROM invoice_lines WHERE updatedAt > :since") suspend fun chLines(since: Long): List<InvoiceLine>
    @Query("SELECT * FROM payments WHERE updatedAt > :since") suspend fun chPayments(since: Long): List<Payment>
    @Query("SELECT * FROM users WHERE updatedAt > :since") suspend fun chUsers(since: Long): List<User>

    @Upsert suspend fun upsertWarehouses(list: List<Warehouse>)
    @Upsert suspend fun upsertProducts(list: List<Product>)
    @Upsert suspend fun upsertCustomers(list: List<Customer>)
    @Upsert suspend fun upsertInvoices(list: List<Invoice>)
    @Upsert suspend fun upsertPayments(list: List<Payment>)
    @Upsert suspend fun upsertUsers(list: List<User>)

    // ---------- Ätiýaçlykdan dikeltmek ----------
    @Query("DELETE FROM warehouses") suspend fun clearWarehouses()
    @Query("DELETE FROM products") suspend fun clearProducts()
    @Query("DELETE FROM stock_moves") suspend fun clearMoves()
    @Query("DELETE FROM customers") suspend fun clearCustomers()
    @Query("DELETE FROM invoices") suspend fun clearInvoices()
    @Query("DELETE FROM invoice_lines") suspend fun clearLines()
    @Query("DELETE FROM payments") suspend fun clearPayments()
    @Query("DELETE FROM users") suspend fun clearUsers()
}


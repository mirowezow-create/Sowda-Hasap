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
        COALESCE((SELECT SUM(CASE i.kind WHEN 'PURCHASE' THEN -i.total WHEN 'RETURN_IN' THEN -i.total WHEN 'TRANSFER' THEN 0 ELSE i.total END) FROM invoices i
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
    @Query("""$PRODUCT_STOCK_SELECT AND p.state IN (:states) AND (p.name LIKE '%' || :q || '%' OR p.barcode LIKE :q || '%'
        OR p.code LIKE '%' || :q || '%'
        OR EXISTS(SELECT 1 FROM barcodes b WHERE b.productId = p.id AND b.deleted = 0 AND b.code LIKE :q || '%')) ORDER BY p.name""")
    fun productsWithStockIn(q: String, wh: String?, states: List<String>): Flow<List<ProductStock>>

    /** Diňe ulanylýan harytlar (faktura we saýlaw üçin) */
    fun productsWithStock(q: String, wh: String?): Flow<List<ProductStock>> =
        productsWithStockIn(q, wh, listOf(ItemState.ACTIVE))

    @Query("SELECT * FROM ($PRODUCT_STOCK_SELECT) WHERE minStock > 0 AND stock <= minStock ORDER BY name")
    suspend fun lowStock(wh: String?): List<ProductStock>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun product(id: String): Product?

    @Query("""
        SELECT * FROM products WHERE deleted = 0 AND (barcode = :code OR code = :code
            OR id IN (SELECT productId FROM barcodes WHERE code = :code AND deleted = 0)) LIMIT 1
    """)
    suspend fun productByBarcode(code: String): Product?

    @Query("SELECT * FROM barcodes WHERE code = :code AND deleted = 0 LIMIT 1")
    suspend fun barcodeByCode(code: String): Barcode?

    @Query("SELECT * FROM barcodes WHERE productId = :productId AND deleted = 0")
    suspend fun barcodesOf(productId: String): List<Barcode>

    @Query("SELECT * FROM barcodes WHERE productId = :productId")
    suspend fun allBarcodesOf(productId: String): List<Barcode>

    @Query("SELECT * FROM product_units WHERE productId = :productId AND deleted = 0 ORDER BY factor")
    suspend fun unitsOf(productId: String): List<ProductUnit>

    @Query("SELECT * FROM product_units WHERE productId = :productId")
    suspend fun allUnitsOf(productId: String): List<ProductUnit>

    @Query("SELECT code FROM products WHERE code LIKE 'H-%'")
    suspend fun productCodes(): List<String>

    @Query("SELECT code FROM customers WHERE code LIKE 'M-%'")
    suspend fun customerCodes(): List<String>

    @Upsert suspend fun upsertBarcodes(list: List<Barcode>)
    @Upsert suspend fun upsertProductUnits(list: List<ProductUnit>)

    // ---------- Ölçegler ----------
    @Query("SELECT * FROM units WHERE deleted = 0 ORDER BY name")
    fun units(): Flow<List<UnitDef>>

    @Query("SELECT COUNT(*) FROM units WHERE deleted = 0")
    suspend fun unitCount(): Int

    @Upsert suspend fun upsertUnit(u: UnitDef)

    // ---------- Depderler (kassa) ----------
    @Query("SELECT * FROM cashbooks WHERE deleted = 0 ORDER BY name")
    fun cashbooks(): Flow<List<Cashbook>>

    @Query("SELECT COUNT(*) FROM cashbooks WHERE deleted = 0")
    suspend fun cashbookCount(): Int

    @Upsert suspend fun upsertCashbook(c: Cashbook)

    @Query("SELECT cashbookId, COALESCE(SUM(amount), 0) AS balance FROM payments WHERE deleted = 0 GROUP BY cashbookId")
    fun cashBalances(): Flow<List<CashBalance>>

    @Query("""
        SELECT p.*, c.name AS customerName, b.name AS cashbookName FROM payments p
        LEFT JOIN customers c ON c.id = p.customerId LEFT JOIN cashbooks b ON b.id = p.cashbookId
        WHERE p.deleted = 0 AND (:cb IS NULL OR p.cashbookId = :cb) AND p.createdAt BETWEEN :from AND :to
        ORDER BY p.createdAt DESC LIMIT 1000
    """)
    fun cashRows(cb: String?, from: Long, to: Long): Flow<List<CashRow>>

    @Query("SELECT COALESCE(SUM(qty), 0) FROM stock_moves WHERE productId = :productId AND warehouseId = :wh AND deleted = 0")
    suspend fun stockOf(productId: String, wh: String): Double

    @Query("SELECT COALESCE(SUM(qty), 0) FROM stock_moves WHERE productId = :id AND deleted = 0")
    suspend fun totalStock(id: String): Double

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
    @Query("$CUSTOMER_DEBT_SELECT AND c.state IN (:states) AND (c.name LIKE '%' || :q || '%' OR c.phone LIKE '%' || :q || '%' OR c.code LIKE '%' || :q || '%') ORDER BY c.name")
    fun customersWithDebtIn(q: String, states: List<String>): Flow<List<CustomerBalance>>

    fun customersWithDebt(q: String): Flow<List<CustomerBalance>> = customersWithDebtIn(q, listOf(ItemState.ACTIVE))

    @Query("SELECT * FROM ($CUSTOMER_DEBT_SELECT) WHERE debt > 0 ORDER BY debt DESC")
    suspend fun debtors(): List<CustomerBalance>

    @Query("SELECT * FROM customers WHERE deleted = 0 AND state = 'ACTIVE' ORDER BY name")
    fun customers(): Flow<List<Customer>>

    @Query("SELECT COUNT(*) FROM invoices WHERE customerId = :id AND deleted = 0")
    suspend fun invoiceCountOfCustomer(id: String): Int

    @Query("SELECT COUNT(*) FROM payments WHERE customerId = :id AND deleted = 0")
    suspend fun paymentCountOfCustomer(id: String): Int

    @Query("SELECT COUNT(*) FROM stock_moves WHERE productId = :id AND deleted = 0")
    suspend fun moveCountOfProduct(id: String): Int

    @Query("SELECT COUNT(*) FROM invoice_lines WHERE productId = :id AND deleted = 0")
    suspend fun lineCountOfProduct(id: String): Int

    @Query("SELECT * FROM customers WHERE id = :id")
    fun customerFlow(id: String): Flow<Customer?>

    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun customer(id: String): Customer?

    @Query("""
        SELECT COALESCE((SELECT SUM(CASE i.kind WHEN 'PURCHASE' THEN -i.total WHEN 'RETURN_IN' THEN -i.total WHEN 'TRANSFER' THEN 0 ELSE i.total END) FROM invoices i WHERE i.customerId = :id AND i.status = 'POSTED' AND i.deleted = 0), 0)
             - COALESCE((SELECT SUM(amount) FROM payments WHERE customerId = :id AND deleted = 0), 0)
    """)
    fun debtOf(id: String): Flow<Long>

    @Upsert
    suspend fun upsertCustomer(c: Customer)

    // ---------- Fakturalar ----------
    @Query("""
        SELECT i.*, c.name AS customerName FROM invoices i LEFT JOIN customers c ON c.id = i.customerId
        WHERE i.deleted = 0 AND (:status IS NULL OR i.status = :status)
        ORDER BY i.createdAt DESC LIMIT 1000
    """)
    fun invoices(status: String?): Flow<List<InvoiceRow>>

    @Query("SELECT * FROM invoices WHERE customerId = :customerId AND status = 'POSTED' AND kind != 'TRANSFER' AND deleted = 0 ORDER BY createdAt DESC")
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
        FROM invoices WHERE kind = 'SALE' AND status = 'POSTED' AND deleted = 0 AND createdAt BETWEEN :from AND :to
        GROUP BY day ORDER BY day
    """)
    suspend fun salesByDay(from: Long, to: Long): List<DaySum>

    @Query("""
        SELECT l.productName AS productName, SUM(l.qty * l.factor) AS qty, CAST(SUM(l.qty * l.price) AS INTEGER) AS total
        FROM invoice_lines l JOIN invoices i ON i.id = l.invoiceId
        WHERE i.kind = 'SALE' AND i.status = 'POSTED' AND i.deleted = 0 AND l.deleted = 0 AND i.createdAt BETWEEN :from AND :to
        GROUP BY l.productId ORDER BY total DESC LIMIT 10
    """)
    suspend fun topProducts(from: Long, to: Long): List<TopProduct>

    @Query("SELECT COALESCE(SUM(total), 0) FROM invoices WHERE kind = 'SALE' AND status = 'POSTED' AND deleted = 0 AND createdAt BETWEEN :from AND :to")
    suspend fun salesTotal(from: Long, to: Long): Long

    @Query("SELECT COUNT(*) FROM invoices WHERE kind = 'SALE' AND status = 'POSTED' AND deleted = 0 AND createdAt BETWEEN :from AND :to")
    suspend fun invoiceCount(from: Long, to: Long): Int

    @Query("""
        SELECT CAST(COALESCE(SUM(l.qty * (l.price - l.cost)), 0) AS INTEGER)
        FROM invoice_lines l JOIN invoices i ON i.id = l.invoiceId
        WHERE i.kind = 'SALE' AND i.status = 'POSTED' AND i.deleted = 0 AND l.deleted = 0 AND i.createdAt BETWEEN :from AND :to
    """)
    suspend fun grossProfit(from: Long, to: Long): Long

    @Query("SELECT COALESCE(SUM(discount), 0) FROM invoices WHERE kind = 'SALE' AND status = 'POSTED' AND deleted = 0 AND createdAt BETWEEN :from AND :to")
    suspend fun discountTotal(from: Long, to: Long): Long

    @Query("SELECT COALESCE(SUM(amount), 0) FROM payments WHERE deleted = 0 AND amount > 0 AND createdAt BETWEEN :from AND :to")
    suspend fun paymentsTotal(from: Long, to: Long): Long

    @Query("""
        SELECT COALESCE((SELECT SUM(CASE i.kind WHEN 'PURCHASE' THEN -i.total WHEN 'RETURN_IN' THEN -i.total WHEN 'TRANSFER' THEN 0 ELSE i.total END) FROM invoices i WHERE i.status = 'POSTED' AND i.deleted = 0
                    AND i.customerId IS NOT NULL AND i.customerId != ''), 0)
             - COALESCE((SELECT SUM(amount) FROM payments WHERE deleted = 0 AND customerId != ''), 0)
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
    @Query("SELECT * FROM units WHERE updatedAt > :since") suspend fun chUnits(since: Long): List<UnitDef>
    @Query("SELECT * FROM product_units WHERE updatedAt > :since") suspend fun chProductUnits(since: Long): List<ProductUnit>
    @Query("SELECT * FROM barcodes WHERE updatedAt > :since") suspend fun chBarcodes(since: Long): List<Barcode>
    @Query("SELECT * FROM cashbooks WHERE updatedAt > :since") suspend fun chCashbooks(since: Long): List<Cashbook>
    @Upsert suspend fun upsertUnits(list: List<UnitDef>)
    @Upsert suspend fun upsertCashbooks(list: List<Cashbook>)

    // ---------- Ätiýaçlykdan dikeltmek ----------
    @Query("DELETE FROM warehouses") suspend fun clearWarehouses()
    @Query("DELETE FROM products") suspend fun clearProducts()
    @Query("DELETE FROM stock_moves") suspend fun clearMoves()
    @Query("DELETE FROM customers") suspend fun clearCustomers()
    @Query("DELETE FROM invoices") suspend fun clearInvoices()
    @Query("DELETE FROM invoice_lines") suspend fun clearLines()
    @Query("DELETE FROM payments") suspend fun clearPayments()
    @Query("DELETE FROM users") suspend fun clearUsers()
    @Query("DELETE FROM units") suspend fun clearUnits()
    @Query("DELETE FROM product_units") suspend fun clearProductUnits()
    @Query("DELETE FROM barcodes") suspend fun clearBarcodes()
    @Query("DELETE FROM cashbooks") suspend fun clearCashbooks()
}


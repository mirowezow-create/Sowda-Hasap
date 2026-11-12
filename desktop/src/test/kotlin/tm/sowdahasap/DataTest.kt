package tm.sowdahasap

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import tm.sowdahasap.data.ALL_MIGRATIONS
import tm.sowdahasap.data.AppDatabase
import tm.sowdahasap.data.AppDatabase_Impl
import tm.sowdahasap.data.Customer
import tm.sowdahasap.data.Invoice
import tm.sowdahasap.data.InvoiceKind
import tm.sowdahasap.data.InvoiceStatus
import tm.sowdahasap.data.ItemState
import tm.sowdahasap.data.Payment
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals

class DataTest {
    private val tmp: File = Files.createTempDirectory("sh").toFile()

    private fun build(path: String) = Room.databaseBuilder<AppDatabase>(name = path, factory = { AppDatabase_Impl() })
        .setDriver(BundledSQLiteDriver()).setQueryCoroutineContext(Dispatchers.IO)
        .addMigrations(*ALL_MIGRATIONS).build()

    /** 1.1 (v2) bazasy 1.2 (v3) bazasyna ýitgisiz geçýärmi? */
    @Test
    fun migrate2to3() = runBlocking {
        val fresh = File(tmp, "fresh.db").path
        build(fresh).also { it.dao().userCount(); it.close() }

        val drv = BundledSQLiteDriver()
        val stmts = mutableListOf<String>()
        drv.open(fresh).also { c ->
            c.prepare("SELECT sql FROM sqlite_master WHERE sql IS NOT NULL AND name NOT LIKE 'sqlite_%' AND name != 'room_master_table'")
                .use { st -> while (st.step()) stmts += st.getText(0) }
            c.close()
        }
        val old = File(tmp, "old.db").path
        drv.open(old).also { o ->
            stmts.forEach { o.execSQL(it) }
            mapOf(
                "products" to listOf("code", "state"),
                "customers" to listOf("code", "phone2", "address", "creditLimit", "payDays", "state"),
                "invoices" to listOf("kind", "payType", "cashbookId", "toWarehouseId", "paid", "discountPct"),
                "invoice_lines" to listOf("unitName", "factor"),
                "payments" to listOf("cashbookId"),
            ).forEach { (t, cols) -> cols.forEach { o.execSQL("ALTER TABLE $t DROP COLUMN $it") } }
            listOf("units", "product_units", "barcodes", "cashbooks").forEach { o.execSQL("DROP TABLE $it") }
            o.execSQL("INSERT INTO products (id,name,barcode,unit,buyPrice,sellPrice,minStock,updatedAt,deleted,wholesalePrice,nightPrice) " +
                "VALUES ('p1','Çaý','123','sany',100,150,0,1,1,0,0)")
            o.execSQL("INSERT INTO invoices (id,number,customerId,warehouseId,status,total,discount,note,userId,createdAt,updatedAt,deleted,wholesale) " +
                "VALUES ('i1','F1','c1','main','POSTED',500,0,'','',1,1,0,0)")
            o.execSQL("PRAGMA user_version = 2")
            o.close()
        }
        val db = build(old)
        val p = db.dao().product("p1")!!
        assertEquals(ItemState.ARCHIVED, p.state)
        assertEquals(false, p.deleted)
        assertEquals("san", p.unit)
        assertEquals("123", db.dao().productByBarcode("123")?.id)
        val inv = db.dao().invoice("i1")!!
        assertEquals(InvoiceKind.SALE, inv.kind)
        assertEquals("main", inv.cashbookId)
        db.close()
    }

    /** Algy-bergi dogry hasaplanýarmy? */
    @Test
    fun debtCalculation() = runBlocking {
        val db = build(File(tmp, "debt.db").path)
        val d = db.dao()
        d.upsertCustomer(Customer("c1", "Aman", updatedAt = 1))
        d.upsertCustomer(Customer("c2", "Bagt", updatedAt = 1, state = ItemState.ARCHIVED))
        fun inv(id: String, c: String, kind: String, total: Long, status: String = InvoiceStatus.POSTED) =
            Invoice(id, id, c, "main", status, total, createdAt = 1, updatedAt = 1, kind = kind)
        fun pay(id: String, c: String, amount: Long, deleted: Boolean = false) =
            Payment(id, c, amount, null, "", "", 1, 1, deleted)

        d.upsertInvoice(inv("s1", "c1", InvoiceKind.SALE, 1000))          // bize 1000 bergili
        d.upsertPayment(pay("p1", "c1", 300))                             // 300 töledi -> 700
        d.upsertInvoice(inv("r1", "c1", InvoiceKind.RETURN_IN, 200))      // 200 yzyna getirdi -> 500
        d.upsertInvoice(inv("b1", "c1", InvoiceKind.PURCHASE, 400))       // ondan 400 haryt aldyk -> 100
        d.upsertPayment(pay("p2", "c1", -100))                            // oňa 100 pul berdik -> 200
        d.upsertInvoice(inv("x1", "c1", InvoiceKind.SALE, 9999, InvoiceStatus.CANCELLED)) // hasaba girmeli däl
        d.upsertInvoice(inv("x2", "c1", InvoiceKind.SALE, 9999, InvoiceStatus.DRAFT))     // hasaba girmeli däl
        d.upsertPayment(pay("x3", "c1", 5000, deleted = true))                           // hasaba girmeli däl
        d.upsertInvoice(inv("t1", "c1", InvoiceKind.TRANSFER, 777))                      // bergä täsir etmeýär
        d.upsertInvoice(inv("s2", "c2", InvoiceKind.SALE, 50))            // öçürilen müşderi hem hasaba girýär
        d.upsertPayment(pay("p3", "", 1000))                              // müşderisiz kassa girdisi

        assertEquals(200L, d.debtOf("c1").first())
        assertEquals(50L, d.debtOf("c2").first())
        assertEquals(250L, d.totalDebt())
        assertEquals(200L, d.customersWithDebt("").first().first { it.customer.id == "c1" }.debt)
        assertEquals(1, d.customersWithDebt("").first().size)  // diňe ulanylýanlar
        assertEquals(2, d.customersWithDebtIn("", ItemState.all).first().size)
        db.close()
    }
}

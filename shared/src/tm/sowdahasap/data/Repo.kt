package tm.sowdahasap.data

import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import kotlinx.serialization.json.Json
import tm.sowdahasap.App
import tm.sowdahasap.Role
import tm.sowdahasap.Session
import tm.sowdahasap.sync.SyncData
import tm.sowdahasap.util.isNightNow
import tm.sowdahasap.util.newId
import tm.sowdahasap.util.now
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlin.math.roundToLong

object Repo {
    private val dao get() = App.dao

    const val MAIN_WAREHOUSE = "main"

    /** Bir tranzaksiýa (içinde ýene tx çagyrmaň) */
    suspend fun <R> tx(block: suspend () -> R): R =
        App.db.useWriterConnection { conn -> conn.immediateTransaction { block() } }

    const val MAIN_CASHBOOK = "main"

    suspend fun ensureDefaults() {
        // Hemme enjamda şol bir ID — sinhronlaşdyrylanda iki gezek döremez
        if (dao.warehouseCount() == 0) dao.upsertWarehouse(Warehouse(MAIN_WAREHOUSE, "Esasy ambar", 1L))
        if (dao.cashbookCount() == 0) dao.upsertCashbook(Cashbook(MAIN_CASHBOOK, "Nagt depder", 1L))
        if (dao.unitCount() == 0) {
            listOf("san", "kg", "pacga", "blok", "karapka", "latok", "litr", "metr").forEach {
                dao.upsertUnit(UnitDef("u-$it", it, "", 1L))
            }
        }
    }

    // ---------- Kodlar ----------
    private fun nextCode(prefix: String, existing: List<String>, width: Int): String {
        val max = existing.mapNotNull { it.removePrefix(prefix).toIntOrNull() }.maxOrNull() ?: 0
        return prefix + (max + 1).toString().padStart(width, '0')
    }
    suspend fun nextProductCode() = nextCode("H-", dao.productCodes(), 6)
    suspend fun nextCustomerCode() = nextCode("M-", dao.customerCodes(), 5)

    // ---------- Ştrihkod ----------
    /** Ştrihkod boýunça haryt we ölçeg (mysal: pacganyň ştrihkody -> pacga) */
    suspend fun findByCode(code: String): Pair<Product, ProductUnit?>? {
        val c = code.trim()
        if (c.isEmpty()) return null
        val p = dao.productByBarcode(c) ?: return null
        if (p.state != ItemState.ACTIVE) return null
        val bc = dao.barcodeByCode(c)
        val unit = if (bc != null && bc.productId == p.id && bc.unitName.isNotBlank() && bc.unitName != p.unit)
            dao.unitsOf(p.id).firstOrNull { it.unitName == bc.unitName } else null
        return p to unit
    }

    // ---------- Bahalar ----------
    /** Opt ýa-da bölek satuw, gijeki wagt bolsa gijeki baha */
    fun priceFor(p: Product, wholesale: Boolean): Long = when {
        wholesale -> if (p.wholesalePrice > 0) p.wholesalePrice else p.sellPrice
        p.nightPrice > 0 && isNightNow() -> p.nightPrice
        else -> p.sellPrice
    }

    // ---------- Harytlar ----------
    suspend fun saveProduct(p: Product) = dao.upsertProduct(p.copy(updatedAt = now()))

    /** Harydy goşmaça ölçegleri we ştrihkodlary bilen bilelikde ýatda saklaýar */
    suspend fun saveProductFull(p: Product, units: List<ProductUnit>, codes: List<Barcode>) {
        val t = now()
        tx {
            dao.upsertProduct(p.copy(barcode = "", updatedAt = t))
            val keepU = units.map { it.id }.toSet()
            dao.upsertProductUnits(dao.allUnitsOf(p.id).filter { it.id !in keepU && !it.deleted }
                .map { it.copy(deleted = true, updatedAt = t) } + units.map { it.copy(productId = p.id, updatedAt = t) })
            val keepB = codes.map { it.id }.toSet()
            dao.upsertBarcodes(dao.allBarcodesOf(p.id).filter { it.id !in keepB && !it.deleted }
                .map { it.copy(deleted = true, updatedAt = t) } + codes.map { it.copy(productId = p.id, updatedAt = t) })
        }
    }

    /** Ýagdaýy üýtgetmek. Hereketi bar haryt "Ýalňyş" bolup bilmez, diňe "Öçürilen". */
    suspend fun setProductState(p: Product, state: String): String? {
        if (state == ItemState.ERROR && (dao.moveCountOfProduct(p.id) > 0 || dao.lineCountOfProduct(p.id) > 0))
            return "Bu harydyň hereketi bar. Ony diňe «Öçürilen» edip bolýar."
        dao.upsertProduct(p.copy(state = state, updatedAt = now()))
        return null
    }

    suspend fun setCustomerState(c: Customer, state: String): String? {
        if (state == ItemState.ERROR && (dao.invoiceCountOfCustomer(c.id) > 0 || dao.paymentCountOfCustomer(c.id) > 0))
            return "Bu müşderiniň hereketi bar. Ony diňe «Öçürilen» edip bolýar."
        dao.upsertCustomer(c.copy(state = state, updatedAt = now()))
        return null
    }

    // ---------- Ammar ----------
    suspend fun addMove(productId: String, warehouseId: String, qty: Double, type: String, note: String = "") {
        val t = now()
        dao.upsertMove(StockMove(newId(), productId, warehouseId, qty, type, null, note, Session.userId, t, t))
    }

    suspend fun transfer(productId: String, from: String, to: String, qty: Double, note: String) {
        val t = now()
        dao.upsertMoves(listOf(
            StockMove(newId(), productId, from, -qty, MoveType.TRANSFER, null, note, Session.userId, t, t),
            StockMove(newId(), productId, to, qty, MoveType.TRANSFER, null, note, Session.userId, t, t),
        ))
    }

    /** Inwentar: hakyky sanaly mukdary girizýärler, tapawudy ýazylýar */
    suspend fun adjustTo(productId: String, wh: String, actual: Double, note: String) {
        val diff = actual - dao.stockOf(productId, wh)
        if (diff != 0.0) addMove(productId, wh, diff, MoveType.ADJUST, note)
    }

    // ---------- Fakturalar ----------
    fun newInvoiceNumber(): String = "F" + SimpleDateFormat("yyMMdd-HHmmss", Locale.US).format(Date())

    fun lineTotal(l: InvoiceLine): Long = (l.qty * l.price).roundToLong()

    fun calcTotal(lines: List<InvoiceLine>, discount: Long): Long =
        (lines.sumOf { lineTotal(it) } - discount).coerceAtLeast(0)

    private suspend fun writeDraft(inv: Invoice, lines: List<InvoiceLine>, t: Long): Invoice {
        val saved = inv.copy(total = calcTotal(lines, inv.discount), updatedAt = t)
        dao.upsertInvoice(saved)
        val keep = lines.map { it.id }.toSet()
        val removed = dao.allLinesOf(inv.id).filter { it.id !in keep && !it.deleted }
            .map { it.copy(deleted = true, updatedAt = t) }
        dao.upsertLines(removed + lines.map { it.copy(invoiceId = inv.id, updatedAt = t) })
        return saved
    }

    suspend fun saveDraft(inv: Invoice, lines: List<InvoiceLine>): Invoice {
        val t = now()
        return tx { writeDraft(inv, lines, t) }
    }

    /** Fakturany tassyklaýar: ammar we kassa hereketleri ýazylýar */
    suspend fun post(inv: Invoice, lines: List<InvoiceLine>, paid: Long): Invoice {
        val t = now()
        return tx {
            val saved = writeDraft(inv, lines, t).copy(status = InvoiceStatus.POSTED, paid = paid, updatedAt = t)
            dao.upsertInvoice(saved)
            val moves = mutableListOf<StockMove>()
            for (l in lines) {
                val base = l.qty * l.factor
                if (saved.kind == InvoiceKind.TRANSFER) {
                    moves += StockMove(newId(), l.productId, saved.warehouseId, -base, MoveType.TRANSFER, saved.id, saved.number, Session.userId, t, t)
                    moves += StockMove(newId(), l.productId, saved.toWarehouseId, base, MoveType.TRANSFER, saved.id, saved.number, Session.userId, t, t)
                } else {
                    val type = when (saved.kind) {
                        InvoiceKind.SALE -> MoveType.SALE
                        InvoiceKind.RETURN_OUT -> MoveType.OUT
                        else -> MoveType.IN
                    }
                    moves += StockMove(newId(), l.productId, saved.warehouseId, InvoiceKind.stockSign(saved.kind) * base,
                        type, saved.id, saved.number, Session.userId, t, t)
                }
            }
            dao.upsertMoves(moves)
            val sign = InvoiceKind.cashSign(saved.kind)
            if (paid != 0L && sign != 0L) {
                dao.upsertPayment(Payment(newId(), saved.customerId ?: "", paid * sign, saved.id,
                    "Faktura ${saved.number}", Session.userId, t, t, cashbookId = saved.cashbookId))
            }
            saved
        }
    }

    suspend fun cancel(inv: Invoice): Invoice {
        val t = now()
        val c = inv.copy(status = InvoiceStatus.CANCELLED, updatedAt = t)
        tx {
            dao.upsertInvoice(c)
            dao.upsertMoves(dao.movesOfInvoice(inv.id).map { it.copy(deleted = true, updatedAt = t) })
            dao.paymentsOfInvoice(inv.id).forEach { dao.upsertPayment(it.copy(deleted = true, updatedAt = t)) }
        }
        return c
    }

    // ---------- Müşderiler ----------
    suspend fun saveCustomer(c: Customer) = dao.upsertCustomer(c.copy(updatedAt = now()))

    /** amount: + müşderiden pul aldyk, - müşderä pul berdik */
    suspend fun addPayment(customerId: String, amount: Long, note: String, cashbookId: String = MAIN_CASHBOOK) {
        val t = now()
        dao.upsertPayment(Payment(newId(), customerId, amount, null, note, Session.userId, t, t, cashbookId = cashbookId))
    }

    /** Ýalňyş ýazylan tölegi ýatyrmak (diňe admin) */
    suspend fun cancelPayment(p: Payment) = dao.upsertPayment(p.copy(deleted = true, updatedAt = now()))

    // ---------- Ulanyjylar ----------
    private fun hash(password: String, salt: String): String {
        val spec = PBEKeySpec(password.toCharArray(), salt.toByteArray(), 20000, 256)
        val bytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun newSalt(): String {
        val b = ByteArray(16).also { SecureRandom().nextBytes(it) }
        return b.joinToString("") { "%02x".format(it) }
    }

    suspend fun saveUser(existing: User?, name: String, login: String, password: String, role: Role, active: Boolean): User {
        val salt = if (password.isNotEmpty()) newSalt() else existing?.salt ?: newSalt()
        val ph = if (password.isNotEmpty()) hash(password, salt) else existing?.passHash ?: ""
        val u = User(existing?.id ?: newId(), name.trim(), login.trim().lowercase(), ph, salt, role.name, active, now())
        dao.upsertUser(u)
        return u
    }

    suspend fun login(login: String, password: String): User? {
        val u = dao.userByLogin(login.trim().lowercase()) ?: return null
        if (!u.active) return null
        val ok = MessageDigest.isEqual(hash(password, u.salt).toByteArray(), u.passHash.toByteArray())
        return if (ok) u else null
    }

    // ---------- Ätiýaçlyk ----------
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true; prettyPrint = false }

    suspend fun exportAll(): SyncData = SyncData(
        dao.chWarehouses(-1), dao.chProducts(-1), dao.chMoves(-1), dao.chCustomers(-1),
        dao.chInvoices(-1), dao.chLines(-1), dao.chPayments(-1), dao.chUsers(-1),
        dao.chUnits(-1), dao.chProductUnits(-1), dao.chBarcodes(-1), dao.chCashbooks(-1),
    )

    suspend fun backupBytes(): ByteArray {
        val b = Backup(app = "sowdahasap", version = 3, created = now(), data = exportAll())
        return json.encodeToString(Backup.serializer(), b).toByteArray(Charsets.UTF_8)
    }

    /** Ätiýaçlykdan dikeltmek: häzirki maglumatlar pozulýar we faýldakylar ýazylýar */
    suspend fun restore(bytes: ByteArray): Int {
        val b = json.decodeFromString(Backup.serializer(), bytes.toString(Charsets.UTF_8))
        require(b.app == "sowdahasap") { "Bu Söwda Hasap ätiýaçlyk faýly däl" }
        val d = b.data
        tx {
            dao.clearLines(); dao.clearInvoices(); dao.clearPayments(); dao.clearMoves()
            dao.clearProducts(); dao.clearCustomers(); dao.clearWarehouses(); dao.clearUsers()
            dao.clearUnits(); dao.clearProductUnits(); dao.clearBarcodes(); dao.clearCashbooks()
            dao.upsertUnits(d.units); dao.upsertProductUnits(d.productUnits); dao.upsertBarcodes(d.barcodes)
            dao.upsertCashbooks(d.cashbooks)
            dao.upsertWarehouses(d.warehouses); dao.upsertProducts(d.products); dao.upsertMoves(d.stockMoves)
            dao.upsertCustomers(d.customers); dao.upsertInvoices(d.invoices); dao.upsertLines(d.invoiceLines)
            dao.upsertPayments(d.payments); dao.upsertUsers(d.users)
        }
        // Serwere täzeden ugradylsyn
        App.prefs.lastPush = 0
        App.prefs.lastPull = 0
        return d.size
    }
}

@kotlinx.serialization.Serializable
data class Backup(val app: String, val version: Int, val created: Long, val data: SyncData)

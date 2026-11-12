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

    suspend fun ensureDefaults() {
        if (dao.warehouseCount() == 0) {
            // Hemme enjamda şol bir ID — sinhronlaşdyrylanda iki gezek döremez
            dao.upsertWarehouse(Warehouse(MAIN_WAREHOUSE, "Esasy ammar", 1L))
        }
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

    suspend fun deleteProduct(p: Product) = dao.upsertProduct(p.copy(deleted = true, updatedAt = now()))

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

    /** Fakturany tassyklaýar: ammardan düşýär, töleg bolsa ýazylýar */
    suspend fun post(inv: Invoice, lines: List<InvoiceLine>, paid: Long): Invoice {
        val t = now()
        return tx {
            val saved = writeDraft(inv, lines, t).copy(status = InvoiceStatus.POSTED, updatedAt = t)
            dao.upsertInvoice(saved)
            dao.upsertMoves(lines.map {
                StockMove(newId(), it.productId, saved.warehouseId, -it.qty, MoveType.SALE, saved.id,
                    saved.number, Session.userId, t, t)
            })
            if (paid > 0 && saved.customerId != null) {
                dao.upsertPayment(Payment(newId(), saved.customerId, paid, saved.id,
                    "Faktura ${saved.number}", Session.userId, t, t))
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

    suspend fun addPayment(customerId: String, amount: Long, note: String) {
        val t = now()
        dao.upsertPayment(Payment(newId(), customerId, amount, null, note, Session.userId, t, t))
    }

    suspend fun deletePayment(p: Payment) = dao.upsertPayment(p.copy(deleted = true, updatedAt = now()))

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
    )

    suspend fun backupBytes(): ByteArray {
        val b = Backup(app = "sowdahasap", version = 2, created = now(), data = exportAll())
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

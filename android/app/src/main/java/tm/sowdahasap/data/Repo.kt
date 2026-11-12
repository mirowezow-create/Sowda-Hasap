package tm.sowdahasap.data

import androidx.room.withTransaction
import tm.sowdahasap.App
import tm.sowdahasap.Role
import tm.sowdahasap.Session
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
    private val db get() = App.instance.db

    const val MAIN_WAREHOUSE = "main"

    suspend fun ensureDefaults() {
        if (dao.warehouseCount() == 0) {
            // Hemme enjamda şol bir ID — sinhronlaşdyrylanda iki gezek döremez
            dao.upsertWarehouse(Warehouse(MAIN_WAREHOUSE, "Esasy ammar", 1L))
        }
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
        db.withTransaction {
            dao.upsertMove(StockMove(newId(), productId, from, -qty, MoveType.TRANSFER, null, note, Session.userId, t, t))
            dao.upsertMove(StockMove(newId(), productId, to, qty, MoveType.TRANSFER, null, note, Session.userId, t, t))
        }
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

    suspend fun saveDraft(inv: Invoice, lines: List<InvoiceLine>): Invoice {
        val t = now()
        val saved = inv.copy(total = calcTotal(lines, inv.discount), updatedAt = t)
        db.withTransaction {
            dao.upsertInvoice(saved)
            val keep = lines.map { it.id }.toSet()
            val removed = dao.allLinesOf(inv.id).filter { it.id !in keep && !it.deleted }
                .map { it.copy(deleted = true, updatedAt = t) }
            dao.upsertLines(removed + lines.map { it.copy(invoiceId = inv.id, updatedAt = t) })
        }
        return saved
    }

    /** Fakturany tassyklaýar: ammardan düşýär, töleg bolsa ýazylýar */
    suspend fun post(inv: Invoice, lines: List<InvoiceLine>, paid: Long): Invoice {
        val t = now()
        var result = inv
        db.withTransaction {
            val saved = saveDraft(inv, lines).copy(status = InvoiceStatus.POSTED, updatedAt = t)
            dao.upsertInvoice(saved)
            dao.upsertMoves(lines.map {
                StockMove(newId(), it.productId, saved.warehouseId, -it.qty, MoveType.SALE, saved.id,
                    saved.number, Session.userId, t, t)
            })
            if (paid > 0 && saved.customerId != null) {
                dao.upsertPayment(Payment(newId(), saved.customerId, paid, saved.id,
                    "Faktura ${saved.number}", Session.userId, t, t))
            }
            result = saved
        }
        return result
    }

    suspend fun cancel(inv: Invoice): Invoice {
        val t = now()
        val c = inv.copy(status = InvoiceStatus.CANCELLED, updatedAt = t)
        db.withTransaction {
            dao.upsertInvoice(c)
            dao.upsertMoves(dao.movesOfInvoice(inv.id).map { it.copy(deleted = true, updatedAt = t) })
            dao.paymentsOfInvoice(inv.id).forEach { dao.upsertPayment(it.copy(deleted = true, updatedAt = t)) }
        }
        return c
    }

    suspend fun deleteDraft(inv: Invoice) {
        if (inv.status == InvoiceStatus.DRAFT) dao.upsertInvoice(inv.copy(deleted = true, updatedAt = now()))
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
}

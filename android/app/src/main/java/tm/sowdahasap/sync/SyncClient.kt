package tm.sowdahasap.sync

import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import tm.sowdahasap.App
import tm.sowdahasap.data.Customer
import tm.sowdahasap.data.Invoice
import tm.sowdahasap.data.InvoiceLine
import tm.sowdahasap.data.Payment
import tm.sowdahasap.data.Product
import tm.sowdahasap.data.StockMove
import tm.sowdahasap.data.User
import tm.sowdahasap.data.Warehouse
import tm.sowdahasap.license.LicenseManager
import tm.sowdahasap.util.dateTime
import java.net.HttpURLConnection
import java.net.URL

@Serializable
data class SyncData(
    val warehouses: List<Warehouse> = emptyList(),
    val products: List<Product> = emptyList(),
    val stockMoves: List<StockMove> = emptyList(),
    val customers: List<Customer> = emptyList(),
    val invoices: List<Invoice> = emptyList(),
    val invoiceLines: List<InvoiceLine> = emptyList(),
    val payments: List<Payment> = emptyList(),
    val users: List<User> = emptyList(),
) {
    val size: Int get() = warehouses.size + products.size + stockMoves.size + customers.size +
        invoices.size + invoiceLines.size + payments.size + users.size
}

@Serializable
data class SyncRequest(val deviceId: String, val since: Long, val changes: SyncData)

@Serializable
data class SyncResponse(val now: Long, val accepted: Int = 0, val changes: SyncData)

object SyncClient {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    /** Üstünlik: netije teksti. Ýalňyşlyk bolsa Exception zyňýar. */
    suspend fun sync(): String = withContext(Dispatchers.IO) {
        val prefs = App.prefs
        val dao = App.dao
        require(prefs.serverUrl.startsWith("http")) { "Serwer salgysy sazlanmady (http:// ýa-da https://)" }
        require(prefs.syncToken.isNotBlank()) { "Serwer tokeni sazlanmady" }

        val started = System.currentTimeMillis()
        val since = prefs.lastPush
        val local = SyncData(
            dao.chWarehouses(since), dao.chProducts(since), dao.chMoves(since), dao.chCustomers(since),
            dao.chInvoices(since), dao.chLines(since), dao.chPayments(since), dao.chUsers(since),
        )
        val req = SyncRequest(LicenseManager.deviceCode(App.instance), prefs.lastPull, local)
        val body = json.encodeToString(SyncRequest.serializer(), req).toByteArray(Charsets.UTF_8)

        val c = URL(prefs.serverUrl + "/api/sync").openConnection() as HttpURLConnection
        c.requestMethod = "POST"
        c.doOutput = true
        c.connectTimeout = 15000
        c.readTimeout = 60000
        c.setRequestProperty("Content-Type", "application/json; charset=utf-8")
        c.setRequestProperty("Authorization", "Bearer " + prefs.syncToken)
        c.outputStream.use { it.write(body) }
        val code = c.responseCode
        val text = (if (code in 200..299) c.inputStream else c.errorStream)?.bufferedReader()?.readText() ?: ""
        c.disconnect()
        if (code == 401) error("Serwer tokeni nädogry")
        if (code !in 200..299) error("Serwer ýalňyşlygy $code: $text")

        val resp = json.decodeFromString(SyncResponse.serializer(), text)
        val r = resp.changes
        App.instance.db.withTransaction {
            dao.upsertWarehouses(r.warehouses)
            dao.upsertProducts(r.products)
            dao.upsertMoves(r.stockMoves)
            dao.upsertCustomers(r.customers)
            dao.upsertInvoices(r.invoices)
            dao.upsertLines(r.invoiceLines)
            dao.upsertPayments(r.payments)
            dao.upsertUsers(r.users)
        }
        prefs.lastPush = started
        prefs.lastPull = resp.now
        val info = "${started.dateTime()}: ugradyldy ${local.size}, alyndy ${r.size}"
        prefs.lastSyncInfo = info
        info
    }
}

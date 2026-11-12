package tm.sowdahasap.platform

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put
import java.io.File
import java.net.InetSocketAddress
import java.security.MessageDigest
import java.util.concurrent.Executors

/**
 * Esasy kompýuteriň içindäki sinhron serweri (server/server.js bilen birmeňzeş protokol we faýl formaty).
 * Telefonlar we beýleki kompýuterler Wi-Fi arkaly şu ýere birigýär.
 */
object LanServer {
    private val TABLES = listOf("warehouses", "products", "stockMoves", "customers", "invoices", "invoiceLines", "payments", "users", "units", "productUnits", "barcodes", "cashbooks")
    private data class Entry(val row: JsonObject, val srv: Long)

    private var server: HttpServer? = null
    private val tables = HashMap<String, HashMap<String, Entry>>()
    private var clock = 0L
    private var token = ""
    private lateinit var file: File

    val running: Boolean get() = server != null

    @Synchronized
    fun start(port: Int, syncToken: String, dir: File): String? {
        stop()
        return try {
            token = syncToken
            file = File(dir, "server-db.json")
            load()
            val s = HttpServer.create(InetSocketAddress(port), 0)
            s.createContext("/api/health") { ex -> reply(ex, 200, """{"ok":true,"app":"sowdahasap"}""") }
            s.createContext("/api/sync") { ex -> handle(ex) }
            s.executor = Executors.newFixedThreadPool(4)
            s.start()
            server = s
            null
        } catch (e: Exception) {
            server = null
            e.message ?: e.toString()
        }
    }

    @Synchronized
    fun stop() {
        server?.stop(0)
        server = null
    }

    private fun reply(ex: HttpExchange, code: Int, body: String) {
        val b = body.toByteArray(Charsets.UTF_8)
        ex.responseHeaders.add("Content-Type", "application/json; charset=utf-8")
        ex.sendResponseHeaders(code, b.size.toLong())
        ex.responseBody.use { it.write(b) }
    }

    private fun handle(ex: HttpExchange) {
        try {
            if (ex.requestMethod != "POST") return reply(ex, 405, """{"error":"POST"}""")
            val auth = ex.requestHeaders.getFirst("Authorization") ?: ""
            if (token.isEmpty() || !MessageDigest.isEqual(auth.toByteArray(), "Bearer $token".toByteArray())) {
                return reply(ex, 401, """{"error":"Token nädogry"}""")
            }
            val body = ex.requestBody.use { it.readBytes() }.toString(Charsets.UTF_8)
            reply(ex, 200, sync(body))
        } catch (e: Exception) {
            reply(ex, 400, buildJsonObject { put("error", e.message ?: "error") }.toString())
        }
    }

    @Synchronized
    private fun sync(body: String): String {
        val req = Json.parseToJsonElement(body).jsonObject
        val since = req["since"]?.jsonPrimitive?.longOrNull ?: 0L
        val changes = req["changes"]?.jsonObject
        var accepted = 0
        for (t in TABLES) {
            val arr = changes?.get(t) as? JsonArray ?: continue
            val table = tables.getOrPut(t) { HashMap() }
            for (el in arr) {
                val row = el as? JsonObject ?: continue
                val id = row["id"]?.jsonPrimitive?.contentOrNull ?: continue
                val upd = row["updatedAt"]?.jsonPrimitive?.longOrNull ?: 0L
                val old = table[id]
                if (old == null || upd >= (old.row["updatedAt"]?.jsonPrimitive?.longOrNull ?: 0L)) {
                    clock = maxOf(clock + 1, System.currentTimeMillis())
                    table[id] = Entry(row, clock)
                    accepted++
                }
            }
        }
        if (accepted > 0) save()
        val out = buildJsonObject {
            put("now", clock)
            put("accepted", accepted)
            put("changes", JsonObject(TABLES.associateWith { t ->
                JsonArray(tables[t].orEmpty().values.filter { it.srv > since }.map { it.row })
            }))
        }
        return out.toString()
    }

    private fun load() {
        tables.clear()
        clock = 0
        if (!file.exists()) return
        val root = Json.parseToJsonElement(file.readText()).jsonObject
        clock = root["clock"]?.jsonPrimitive?.longOrNull ?: 0L
        val ts = root["tables"]?.jsonObject ?: return
        for (t in TABLES) {
            val m = HashMap<String, Entry>()
            ts[t]?.jsonObject?.forEach { (id, v) ->
                val o = v.jsonObject
                val row = o["row"]?.jsonObject ?: return@forEach
                m[id] = Entry(row, o["srv"]?.jsonPrimitive?.longOrNull ?: 0L)
            }
            tables[t] = m
        }
    }

    private fun save() {
        val root = buildJsonObject {
            put("clock", clock)
            put("tables", JsonObject(TABLES.associateWith { t ->
                JsonObject(tables[t].orEmpty().mapValues { (_, e) ->
                    buildJsonObject { put("row", e.row); put("srv", e.srv) }
                })
            }))
        }
        val tmp = File(file.parentFile, file.name + ".tmp")
        tmp.writeText(root.toString())
        if (!tmp.renameTo(file)) { file.delete(); tmp.renameTo(file) }
    }
}

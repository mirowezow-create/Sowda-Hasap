package tm.sowdahasap.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tm.sowdahasap.Msg
import tm.sowdahasap.util.KeyValueStore
import java.awt.Desktop
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.Properties
import java.util.UUID
import javax.print.DocFlavor
import javax.print.PrintServiceLookup
import javax.print.SimpleDoc

object Platform {
    const val isDesktop = true
    const val supportsSms = false
    const val serverSupported = true

    fun dataDir(): File {
        val base = System.getenv("APPDATA")?.let { File(it) } ?: File(System.getProperty("user.home"), ".config")
        return File(base, "SowdaHasap").apply { mkdirs() }
    }

    /** Windows MachineGuid (kompýuter täzeden gurnalmasa üýtgemeýär) */
    fun rawDeviceId(): String {
        val guid = try {
            val p = ProcessBuilder("reg", "query", "HKLM\\SOFTWARE\\Microsoft\\Cryptography", "/v", "MachineGuid")
                .redirectErrorStream(true).start()
            val out = p.inputStream.bufferedReader().readText()
            p.waitFor()
            Regex("MachineGuid\\s+REG_SZ\\s+([0-9a-fA-F-]+)").find(out)?.groupValues?.get(1)
        } catch (e: Exception) { null }
        val id = guid ?: run {
            // Windows däl ýa-da okalmady: bir gezek döredilen ID
            val f = File(dataDir(), "device.id")
            if (!f.exists()) f.writeText(UUID.randomUUID().toString())
            f.readText().trim()
        }
        return "$id|tm.sowdahasap.desktop"
    }

    // ---------- Printer (Windows-da gurnalan printerler) ----------
    fun printers(): List<Pair<String, String>> =
        PrintServiceLookup.lookupPrintServices(null, null).map { it.name to it.name }

    suspend fun print(printerId: String, bytes: ByteArray) = withContext(Dispatchers.IO) {
        require(printerId.isNotBlank()) { "Sazlamalarda printer saýlaň" }
        val svc = PrintServiceLookup.lookupPrintServices(null, null).firstOrNull { it.name == printerId }
            ?: error("Printer tapylmady: $printerId")
        svc.createPrintJob().print(SimpleDoc(bytes, DocFlavor.BYTE_ARRAY.AUTOSENSE, null), null)
    }

    // ---------- SMS (Windows-da ýok) ----------
    fun sendSms(phone: String, text: String) { error("SMS diňe telefonda ugradylýar") }
    fun dial(phone: String) {}

    // ---------- Faýllar ----------
    private fun exportDir(): File {
        val docs = File(System.getProperty("user.home"), "Documents")
        return File(if (docs.exists()) docs else File(System.getProperty("user.home")), "SowdaHasap").apply { mkdirs() }
    }

    /** Faýly Resminamalar\SowdaHasap bukjasyna ýazyp açýar */
    fun shareFile(name: String, mime: String, bytes: ByteArray) {
        val f = File(exportDir(), name)
        f.writeBytes(bytes)
        Msg.show("Saklandy: ${f.absolutePath}")
        try { if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(f) } catch (e: Exception) { }
    }

    fun exitApp() { LanServer.stop(); kotlin.system.exitProcess(0) }

    // ---------- Esasy serwer (Wi-Fi) ----------
    fun startServer(port: Int, token: String): String? = LanServer.start(port, token, dataDir())
    fun stopServer() = LanServer.stop()
    fun serverRunning(): Boolean = LanServer.running

    fun localAddresses(): List<String> = try {
        NetworkInterface.getNetworkInterfaces().toList()
            .filter { it.isUp && !it.isLoopback && !it.isVirtual }
            .flatMap { it.inetAddresses.toList() }
            .filterIsInstance<Inet4Address>()
            .map { it.hostAddress }
    } catch (e: Exception) { emptyList() }
}

class FileKeyValueStore(private val file: File) : KeyValueStore {
    private val props = Properties().apply { if (file.exists()) file.inputStream().use { load(it) } }

    @Synchronized private fun save() { file.outputStream().use { props.store(it, "Sowda Hasap") } }

    override fun getString(key: String, def: String): String = props.getProperty(key) ?: def
    override fun putString(key: String, value: String) { props.setProperty(key, value); save() }
    override fun getLong(key: String, def: Long): Long = props.getProperty(key)?.toLongOrNull() ?: def
    override fun putLong(key: String, value: Long) { props.setProperty(key, value.toString()); save() }
}

@Composable
fun rememberScanner(onResult: (String) -> Unit): (() -> Unit)? = null // USB barkod okaýjy klawiatura ýaly ýazýar

@Composable
fun rememberWithPermission(need: NeedPerm, onGranted: () -> Unit): () -> Unit {
    val cb by rememberUpdatedState(onGranted)
    return { cb() }
}

@Composable
fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {}

@Composable
fun rememberFileSaver(): (name: String, mime: String, bytes: ByteArray) -> Unit = { name, _, bytes ->
    val d = FileDialog(null as Frame?, "Ätiýaçlyk faýly sakla", FileDialog.SAVE)
    d.file = name
    d.isVisible = true
    if (d.file != null) {
        val f = File(d.directory, d.file)
        try { f.writeBytes(bytes); Msg.show("Saklandy: ${f.absolutePath}") } catch (e: Exception) { Msg.show("Saklanmady: ${e.message}") }
    }
}

@Composable
fun rememberFileOpener(onBytes: (ByteArray) -> Unit): () -> Unit {
    val cb by rememberUpdatedState(onBytes)
    return {
        val d = FileDialog(null as Frame?, "Ätiýaçlyk faýlyny saýla", FileDialog.LOAD)
        d.isVisible = true
        if (d.file != null) {
            try { cb(File(d.directory, d.file).readBytes()) } catch (e: Exception) { Msg.show("Faýl okalmady: ${e.message}") }
        }
    }
}

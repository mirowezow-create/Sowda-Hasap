package tm.sowdahasap.platform

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.telephony.SmsManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tm.sowdahasap.Msg
import java.io.File
import java.util.UUID

object Platform {
    lateinit var context: Context
    var activity: Activity? = null

    const val isDesktop = false
    const val supportsSms = true
    const val serverSupported = false

    @SuppressLint("HardwareIds")
    fun rawDeviceId(): String {
        val aid = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown"
        return "$aid|${context.packageName}"
    }

    // ---------- Bluetooth printer ----------
    private val SPP: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    @SuppressLint("MissingPermission")
    fun printers(): List<Pair<String, String>> {
        val adapter = context.getSystemService(BluetoothManager::class.java)?.adapter ?: return emptyList()
        if (!adapter.isEnabled) return emptyList()
        return adapter.bondedDevices.map { (it.name ?: it.address) to it.address }
    }

    @SuppressLint("MissingPermission")
    suspend fun print(printerId: String, bytes: ByteArray) = withContext(Dispatchers.IO) {
        val adapter = context.getSystemService(BluetoothManager::class.java)?.adapter
            ?: error("Bu telefonda Bluetooth ýok")
        if (!adapter.isEnabled) error("Bluetooth-y açyň")
        require(printerId.isNotBlank()) { "Sazlamalarda printer saýlaň" }
        adapter.cancelDiscovery()
        val socket = adapter.getRemoteDevice(printerId).createRfcommSocketToServiceRecord(SPP)
        socket.use {
            it.connect()
            it.outputStream.write(bytes)
            it.outputStream.flush()
            Thread.sleep(400)
        }
    }

    // ---------- SMS / jaň ----------
    fun sendSms(phone: String, text: String) {
        val sm: SmsManager = if (Build.VERSION.SDK_INT >= 31) context.getSystemService(SmsManager::class.java)
        else @Suppress("DEPRECATION") SmsManager.getDefault()
        sm.sendMultipartTextMessage(phone, null, sm.divideMessage(text), null, null)
    }

    fun dial(phone: String) {
        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    // ---------- Faýllar ----------
    fun shareFile(name: String, mime: String, bytes: ByteArray) {
        val dir = File(context.cacheDir, "exports").apply { mkdirs() }
        val f = File(dir, name)
        f.writeBytes(bytes)
        val uri = FileProvider.getUriForFile(context, context.packageName + ".files", f)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mime
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Paýlaş").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    // ---------- Serwer (diňe Windows) ----------
    fun startServer(port: Int, token: String): String? = "Diňe Windows-da"
    fun stopServer() {}
    fun serverRunning(): Boolean = false
    fun localAddresses(): List<String> = emptyList()
}

/** Kamera bilen barkod okamak */
@Composable
fun rememberScanner(onResult: (String) -> Unit): (() -> Unit)? {
    val cb by rememberUpdatedState(onResult)
    val launcher = rememberLauncherForActivityResult(ScanContract()) { r ->
        r.contents?.let { cb(it.trim()) }
    }
    return {
        launcher.launch(ScanOptions().apply {
            setPrompt("Barkody kamera bilen okadyň")
            setBeepEnabled(true)
            setOrientationLocked(false)
        })
    }
}

@Composable
fun rememberWithPermission(need: NeedPerm, onGranted: () -> Unit): () -> Unit {
    val ctx = LocalContext.current
    val cb by rememberUpdatedState(onGranted)
    val perms: Array<String> = when (need) {
        NeedPerm.SMS -> arrayOf(Manifest.permission.SEND_SMS)
        NeedPerm.BLUETOOTH -> if (Build.VERSION.SDK_INT >= 31) arrayOf(Manifest.permission.BLUETOOTH_CONNECT) else emptyArray()
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { res ->
        if (res.values.all { it }) cb() else Msg.show("Rugsat berilmedi")
    }
    return {
        val missing = perms.filter { ContextCompat.checkSelfPermission(ctx, it) != PackageManager.PERMISSION_GRANTED }
        if (missing.isEmpty()) cb() else launcher.launch(missing.toTypedArray())
    }
}

@Composable
fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(enabled = enabled, onBack = onBack)
}

/** Faýly telefonda saklamak (ulanyjy ýerini saýlaýar) */
@Composable
fun rememberFileSaver(): (name: String, mime: String, bytes: ByteArray) -> Unit {
    val ctx = LocalContext.current
    var pending by remember { mutableStateOf<ByteArray?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
        val data = pending
        pending = null
        if (uri != null && data != null) {
            try {
                ctx.contentResolver.openOutputStream(uri)?.use { it.write(data) }
                Msg.show("Saklandy")
            } catch (e: Exception) { Msg.show("Saklanmady: ${e.message}") }
        }
    }
    return { name, _, bytes ->
        pending = bytes
        launcher.launch(name)
    }
}

@Composable
fun rememberFileOpener(onBytes: (ByteArray) -> Unit): () -> Unit {
    val ctx = LocalContext.current
    val cb by rememberUpdatedState(onBytes)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try {
                val bytes = ctx.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                if (bytes != null) cb(bytes)
            } catch (e: Exception) { Msg.show("Faýl okalmady: ${e.message}") }
        }
    }
    return { launcher.launch(arrayOf("*/*")) }
}

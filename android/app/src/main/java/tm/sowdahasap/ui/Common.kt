@file:OptIn(ExperimentalMaterial3Api::class)

package tm.sowdahasap.ui

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import tm.sowdahasap.App
import tm.sowdahasap.data.Product
import tm.sowdahasap.util.money
import tm.sowdahasap.util.qty

private val Green = Color(0xFF00695C)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val scheme = if (isSystemInDarkTheme())
        darkColorScheme(primary = Color(0xFF4DB6AC), secondary = Color(0xFFFFB74D))
    else lightColorScheme(primary = Green, secondary = Color(0xFFEF6C00), primaryContainer = Color(0xFFB2DFDB))
    MaterialTheme(colorScheme = scheme, content = content)
}

@Composable
fun Screen(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    fab: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (onBack != null) IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Yza")
                    }
                },
                actions = actions,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
        floatingActionButton = fab,
        content = content,
    )
}

@Composable
fun toast(): (String) -> Unit {
    val ctx = LocalContext.current
    return { msg -> Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show() }
}

/** Kamera bilen barkod okamak. Netije onResult-a gelýär. */
@Composable
fun rememberScanner(onResult: (String) -> Unit): () -> Unit {
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

/** Rugsat sorap, berlen bolsa işi ýerine ýetirýär */
@Composable
fun rememberWithPermissions(perms: Array<String>, onGranted: () -> Unit): () -> Unit {
    val ctx = LocalContext.current
    val cb by rememberUpdatedState(onGranted)
    val show = toast()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { res ->
        if (res.values.all { it }) cb() else show("Rugsat berilmedi")
    }
    return {
        val missing = perms.filter { ContextCompat.checkSelfPermission(ctx, it) != PackageManager.PERMISSION_GRANTED }
        if (missing.isEmpty()) cb() else launcher.launch(missing.toTypedArray())
    }
}

@Composable
fun Field(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    number: Boolean = false,
    phone: Boolean = false,
    password: Boolean = false,
    singleLine: Boolean = true,
    trailing: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = modifier,
        singleLine = singleLine,
        trailingIcon = trailing,
        visualTransformation = if (password) androidx.compose.ui.text.input.PasswordVisualTransformation()
        else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = when {
                number -> KeyboardType.Decimal
                phone -> KeyboardType.Phone
                password -> KeyboardType.Password
                else -> KeyboardType.Text
            }
        ),
    )
}

/** Ýönekeý saýlaw düwmesi */
@Composable
fun <T> Select(
    label: String,
    options: List<T>,
    selected: T?,
    text: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    var open by remember { mutableStateOf(false) }
    Box(modifier) {
        OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) {
            Text("$label: " + (selected?.let(text) ?: "—"))
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            options.forEach { o ->
                DropdownMenuItem(text = { Text(text(o)) }, onClick = { onSelect(o); open = false })
            }
        }
    }
}

@Composable
fun ConfirmDialog(title: String, text: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = { TextButton(onClick = { onDismiss(); onConfirm() }) { Text("Hawa") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Ýok") } },
    )
}

/** Haryt saýlamak penjiresi: gözleg + barkod */
@Composable
fun ProductPicker(warehouseId: String?, onPick: (Product) -> Unit, onDismiss: () -> Unit) {
    var q by remember { mutableStateOf("") }
    val show = toast()
    val flow = remember(q, warehouseId) { App.dao.productsWithStock(q, warehouseId) }
    val list by flow.collectAsState(initial = emptyList())
    var scanned by remember { mutableStateOf<String?>(null) }
    val scan = rememberScanner { scanned = it }
    LaunchedEffect(scanned) {
        val code = scanned ?: return@LaunchedEffect
        val p = App.dao.productByBarcode(code)
        if (p != null) onPick(p) else { show("Barkod tapylmady: $code"); q = code }
        scanned = null
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Ýap") } },
        title = { Text("Haryt saýlaň") },
        text = {
            Column {
                Field(q, { q = it }, "Gözleg (ady ýa-da barkod)", trailing = {
                    IconButton(onClick = scan) { Icon(Icons.Default.Search, "Barkod okat") }
                })
                Text("🔍 düwmesi — kamera bilen barkod", style = MaterialTheme.typography.bodySmall)
                LazyColumn(Modifier.height(380.dp)) {
                    items(list, key = { it.product.id }) { ps ->
                        ListItem(
                            headlineContent = { Text(ps.product.name) },
                            supportingContent = { Text("${ps.product.sellPrice.money()} TMT · galyndy ${ps.stock.qty()} ${ps.product.unit}") },
                            modifier = Modifier.clickable { onPick(ps.product) },
                        )
                    }
                }
            }
        },
    )
}

/** Ýönekeý sütün grafigi */
@Composable
fun BarChart(labels: List<String>, values: List<Long>, modifier: Modifier = Modifier.fillMaxWidth().height(200.dp)) {
    val barColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    Canvas(modifier) {
        if (values.isEmpty()) return@Canvas
        val max = (values.maxOrNull() ?: 1L).coerceAtLeast(1L).toFloat()
        val bottomPad = 36f
        val chartH = size.height - bottomPad
        val slot = size.width / values.size
        val barW = slot * 0.7f
        val paint = android.graphics.Paint().apply {
            color = textColor; textSize = 24f; textAlign = android.graphics.Paint.Align.CENTER; isAntiAlias = true
        }
        val step = (values.size / 8).coerceAtLeast(1)
        values.forEachIndexed { i, v ->
            val h = chartH * (v / max)
            val x = i * slot + (slot - barW) / 2
            drawRect(barColor, topLeft = androidx.compose.ui.geometry.Offset(x, chartH - h),
                size = androidx.compose.ui.geometry.Size(barW, h))
            if (i % step == 0) drawContext.canvas.nativeCanvas.drawText(labels[i], x + barW / 2, size.height - 8f, paint)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun Centered(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) { content() }
}

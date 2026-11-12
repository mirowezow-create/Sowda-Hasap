@file:OptIn(ExperimentalMaterial3Api::class)

package tm.sowdahasap.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tm.sowdahasap.App
import tm.sowdahasap.Msg
import tm.sowdahasap.data.Product
import tm.sowdahasap.platform.rememberScanner
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

/** Nyşan düwmesi (emoji) — Android-da we Windows-da birmeňzeş görünýär */
@Composable
fun IconText(symbol: String, onClick: () -> Unit, enabled: Boolean = true) {
    IconButton(onClick = onClick, enabled = enabled) { Text(symbol, fontSize = 20.sp) }
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
                navigationIcon = { if (onBack != null) IconText("⬅", onBack) },
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
fun toast(): (String) -> Unit = { msg -> Msg.show(msg) }

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
    onDone: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = modifier,
        singleLine = singleLine,
        trailingIcon = trailing,
        visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = when {
                number -> KeyboardType.Decimal
                phone -> KeyboardType.Phone
                password -> KeyboardType.Password
                else -> KeyboardType.Text
            },
            imeAction = if (onDone != null) ImeAction.Done else ImeAction.Default,
        ),
        keyboardActions = KeyboardActions(onDone = { onDone?.invoke() }),
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

/** Haryt saýlamak penjiresi: gözleg, kamera barkody (telefon), USB barkod okaýjy (Enter) */
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
                Field(q, { q = it }, "Gözleg (ady ýa-da barkod)",
                    onDone = { if (q.isNotBlank()) scanned = q.trim() },
                    trailing = { if (scan != null) IconText("📷", scan) })
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

/** Ýönekeý sütün grafigi (ýazgylar aşakda Text bilen) */
@Composable
fun BarChart(labels: List<String>, values: List<Long>) {
    val barColor = MaterialTheme.colorScheme.primary
    Column(Modifier.fillMaxWidth()) {
        Canvas(Modifier.fillMaxWidth().height(180.dp)) {
            if (values.isEmpty()) return@Canvas
            val max = (values.maxOrNull() ?: 1L).coerceAtLeast(1L).toFloat()
            val slot = size.width / values.size
            val barW = slot * 0.7f
            values.forEachIndexed { i, v ->
                val h = size.height * (v / max)
                val x = i * slot + (slot - barW) / 2
                drawRect(barColor, topLeft = androidx.compose.ui.geometry.Offset(x, size.height - h),
                    size = androidx.compose.ui.geometry.Size(barW, h))
            }
        }
        val step = (labels.size / 8).coerceAtLeast(1)
        Row(Modifier.fillMaxWidth()) {
            labels.forEachIndexed { i, l ->
                Text(if (i % step == 0) l else "", Modifier.weight(1f), fontSize = 10.sp, textAlign = TextAlign.Center)
            }
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
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
}

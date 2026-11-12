package tm.sowdahasap.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/** Programmanyň reňkleri (aish-e meňzeş) */
object Pal {
    val TitleBlue = Color(0xFF1C7CC0)
    val TabActive = Color(0xFF1565A8)
    val TabInactive = Color(0xFF6E8FA8)
    val Status = Color(0xFF17A2C6)
    val Header = Color(0xFF2B6AA6)
    val Row = Color(0xFFF8F6F1)
    val RowAlt = Color(0xFFDCE6EF)
    val Selected = Color(0xFFF4A38C)
    val Link = Color(0xFF1846B5)
    val RedX = Color(0xFFE5533D)
    val GreenBand = Color(0xFF2ECC71)
    val FieldBg = Color(0xF2FFFFFF)
    val Panel = Color(0xCCFFFFFF)
    val Text = Color(0xFF1B1B1B)
    val Muted = Color(0xFF5B6B78)
    private val pattern = listOf(
        Color(0xFFBFE8CF), Color(0xFFD8F0DC), Color(0xFFF3EFD8), Color(0xFFA9DEBD), Color(0xFFE7F4E2), Color(0xFFCBEBD4),
    )
    fun pattern(i: Int) = pattern[i % pattern.size]
}

/** Ýaşyl-krem nagyşly fon */
@Composable
fun PatternBackground(modifier: Modifier = Modifier.fillMaxSize(), content: @Composable BoxScope.() -> Unit) {
    Box(modifier) {
        Canvas(Modifier.fillMaxSize()) {
            drawRect(Color(0xFFE9F5E6))
            val r = 34.dp.toPx()
            val w = r * 1.732f
            val h = r * 1.5f
            val rnd = Random(7)
            var row = 0
            var y = -r
            while (y < size.height + r) {
                var x = if (row % 2 == 0) 0f else w / 2
                x -= w
                while (x < size.width + w) {
                    val c = Pal.pattern(rnd.nextInt(6))
                    val p = Path()
                    for (k in 0 until 6) {
                        val a = Math.toRadians((60.0 * k) - 30.0 + rnd.nextInt(-8, 8))
                        val rr = r * (0.9f + rnd.nextFloat() * 0.25f)
                        val px = x + (rr * cos(a)).toFloat()
                        val py = y + (rr * sin(a)).toFloat()
                        if (k == 0) p.moveTo(px, py) else p.lineTo(px, py)
                    }
                    p.close()
                    drawPath(p, c)
                    x += w
                }
                y += h
                row++
            }
            // Ýeňil ak örtük — ýazgylar aňsat okalsyn
            drawRect(Color(0x55FFFFFF), topLeft = Offset.Zero)
        }
        content()
    }
}

/** Gyzyl ✖ düwmesi */
@Composable
fun RedX(onClick: () -> Unit, enabled: Boolean = true) {
    Box(
        Modifier.size(26.dp).clip(CircleShape).background(if (enabled) Pal.RedX else Color(0xFFCCCCCC))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { Text("✕", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold) }
}

/** Gök salgy (basylýan) tekst */
@Composable
fun LinkText(text: String, onClick: (() -> Unit)?, modifier: Modifier = Modifier, size: Int = 14) {
    Text(
        text.ifBlank { "????" },
        color = Pal.Link,
        fontSize = size.sp,
        textDecoration = TextDecoration.Underline,
        textAlign = TextAlign.Center,
        modifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier,
    )
}

/** Sag paneliň setiri: ýokarda ady, aşagynda gök salgy-baha; çepde ✖ */
@Composable
fun PanelLink(
    label: String,
    value: String,
    onClick: (() -> Unit)?,
    onClear: (() -> Unit)? = null,
    band: Color? = null,
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().background(band ?: Color.Transparent).padding(vertical = 5.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onClear != null) RedX(onClear) else Spacer(Modifier.width(26.dp))
            Column(Modifier.weight(1f).padding(horizontal = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Pal.Text, textAlign = TextAlign.Center)
                LinkText(value, onClick)
            }
            if (onClick != null) Text("🔍", fontSize = 16.sp, modifier = Modifier.clickable(onClick = onClick))
        }
        HorizontalDivider(color = Color(0x55000000))
    }
}

/** Uly kwadrat gural düwmesi */
@Composable
fun ToolButton(icon: String, label: String, onClick: () -> Unit, enabled: Boolean = true, badge: String? = null) {
    Box {
        Column(
            Modifier.padding(3.dp).size(width = 74.dp, height = 62.dp)
                .background(if (enabled) Color(0xF5FFFFFF) else Color(0x88FFFFFF), RoundedCornerShape(4.dp))
                .border(1.dp, Color(0x22000000), RoundedCornerShape(4.dp))
                .clickable(enabled = enabled, onClick = onClick),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(icon, fontSize = 24.sp, color = if (enabled) Color.Unspecified else Color(0x55000000))
            Text(label, fontSize = 10.sp, color = if (enabled) Pal.Text else Pal.Muted, maxLines = 1)
        }
        if (badge != null) Box(
            Modifier.align(Alignment.TopEnd).background(Pal.Header, RoundedCornerShape(8.dp)).padding(horizontal = 4.dp),
        ) { Text(badge, color = Color.White, fontSize = 9.sp) }
    }
}

// ---------------- Tablisa ----------------

class Col(val title: String, val weight: Float, val align: TextAlign = TextAlign.Start)

@Composable
fun TableHeader(cols: List<Col>, lead: Int = 0) {
    Row(Modifier.fillMaxWidth().background(Pal.Header).padding(vertical = 7.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically) {
        if (lead > 0) Spacer(Modifier.width(lead.dp))
        cols.forEach { c ->
            Text(c.title, Modifier.weight(c.weight).padding(horizontal = 4.dp), color = Color.White,
                fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = c.align, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun TableRow(index: Int, selected: Boolean = false, onClick: (() -> Unit)? = null, content: @Composable RowScope.() -> Unit) {
    val bg = when {
        selected -> Pal.Selected
        index % 2 == 1 -> Pal.RowAlt
        else -> Pal.Row
    }
    Row(
        Modifier.fillMaxWidth().background(bg).let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(vertical = 5.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
fun RowScope.Cell(text: String, weight: Float, align: TextAlign = TextAlign.Start, bold: Boolean = false, color: Color = Pal.Text) {
    Text(text, Modifier.weight(weight).padding(horizontal = 4.dp), textAlign = align, fontSize = 14.sp,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal, color = color, maxLines = 2, overflow = TextOverflow.Ellipsis)
}

/** "Gözlemek: ..." penjiresi — gök başlykly sanaw */
@Composable
fun <T> SearchDialog(
    title: String,
    items: List<T>,
    cols: List<Pair<Col, (T) -> String>>,
    onPick: (T) -> Unit,
    onDismiss: () -> Unit,
    searchText: (T) -> String = { t -> cols.joinToString(" ") { it.second(t) } },
    extra: (@Composable () -> Unit)? = null,
    initialQuery: String = "",
) {
    var q by remember { mutableStateOf(initialQuery) }
    val list = if (q.isBlank()) items else items.filter { searchText(it).contains(q.trim(), ignoreCase = true) }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(Modifier.widthIn(max = 760.dp).fillMaxWidth(0.95f).heightIn(max = 620.dp), shape = RoundedCornerShape(2.dp),
            color = Color(0xFFF7F7F7), shadowElevation = 8.dp) {
            Column {
                Row(Modifier.fillMaxWidth().background(Pal.TitleBlue).padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("Gözlemek : $title", color = Color.White, fontSize = 17.sp, modifier = Modifier.weight(1f))
                    Text("✕", color = Color.White, fontSize = 18.sp, modifier = Modifier.clickable(onClick = onDismiss).padding(4.dp))
                }
                Column(Modifier.padding(8.dp)) {
                    Field(q, { q = it }, "Gözleg", onDone = { list.firstOrNull()?.let(onPick) })
                    extra?.invoke()
                    Spacer(Modifier.height(6.dp))
                    TableHeader(cols.map { it.first })
                    LazyColumn(Modifier.weight(1f, fill = false).heightIn(max = 420.dp)) {
                        itemsIndexed(list) { i, t ->
                            TableRow(i, onClick = { onPick(t) }) {
                                cols.forEach { (c, f) -> Cell(f(t), c.weight, c.align) }
                            }
                        }
                    }
                    Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Tapyldy : ${list.size}", Modifier.weight(1f), fontSize = 13.sp, color = Pal.Muted)
                        TextButton(onClick = onDismiss) { Text("Ýap") }
                    }
                }
            }
        }
    }
}

/** Ýönekeý saýlaw penjiresi (görnüşi, töleg görnüşi we ş.m.) */
@Composable
fun <T> ChoiceDialog(title: String, items: List<T>, text: (T) -> String, onPick: (T) -> Unit, onDismiss: () -> Unit) {
    SearchDialog(title, items, listOf(Col("Ady", 1f) to text), onPick, onDismiss)
}

/** Ak ýarym-dury panel */
@Composable
fun WhitePanel(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier.background(Pal.Panel, RoundedCornerShape(3.dp)).border(1.dp, Color(0x33000000), RoundedCornerShape(3.dp))
        .padding(8.dp)) { content() }
}

/** Ýagdaý zolagy (Ulanylýar / Öçürilen ...) */
@Composable
fun StateBand(text: String, color: Color, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().background(color).clickable(onClick = onClick).padding(vertical = 6.dp),
        contentAlignment = Alignment.Center) {
        Text("$text  ▾", fontWeight = FontWeight.Bold, color = Color(0xFF0B3D1E))
    }
}

@Composable
fun SmallTitle(t: String) {
    Text(t, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Pal.Text, modifier = Modifier.padding(top = 6.dp, bottom = 2.dp))
}

@Composable
fun MutedText(t: String) {
    Text(t, fontSize = 12.sp, color = Pal.Muted)
}

@Composable
fun BigTotal(text: String) {
    Text(text, fontSize = 30.sp, color = Color(0xFF2A2A2A), fontWeight = FontWeight.Light)
}

@Composable
fun LinkButton(text: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) { Text(text, color = Pal.Link, textDecoration = TextDecoration.Underline) }
}

@Composable
fun themePrimary() = MaterialTheme.colorScheme.primary

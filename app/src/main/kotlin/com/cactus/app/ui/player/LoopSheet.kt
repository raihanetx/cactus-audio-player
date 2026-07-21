package com.cactus.app.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import com.cactus.app.CactusViewModel
import com.cactus.app.model.Loop
import com.cactus.app.model.LoopCount
import com.cactus.app.model.formatDuration
import com.cactus.app.ui.components.SortChip
import com.cactus.app.ui.theme.onSurfaceVariant
import kotlin.math.max
import kotlin.math.min

/* PRD §8.3 — A-B repeat bottom sheet, redesigned for clarity and delight. */
@Composable
fun LoopSheet(vm: CactusViewModel, videoId: String, onClose: () -> Unit) {
    val position by vm.player.positionMs.collectAsState()
    val duration by vm.player.durationMs.collectAsState()
    val savedLoops by vm.loops.collectAsState()
    val activeLoop = vm.player.activeLoop
    val list = savedLoops[videoId] ?: emptyList()

    var a by remember { mutableStateOf<Long?>(null) }
    var b by remember { mutableStateOf<Long?>(null) }
    var chosenCount by remember { mutableStateOf(LoopCount.Infinite) }

    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Repeat, "Loops", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(8.dp))
            Text("Loops", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(6.dp))
        Text("Drop two pins to repeat a phrase until it sticks.", style = MaterialTheme.typography.bodySmall, color = onSurfaceVariant)
        Spacer(Modifier.height(18.dp))

        // Visual timeline with A/B markers.
        val aPos = a ?: 0L
        val bPos = b ?: 0L
        val dur = if (duration > 0) duration else if (bPos > aPos) bPos else 1L
        BoxWithConstraints(
            Modifier.fillMaxWidth().height(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable {
                    // Tapping the track sets A then B alternately.
                    if (a == null || (a != null && b != null)) { a = position; b = null }
                    else { b = position }
                },
        ) {
            val trackW = maxWidth
            val loopStart = minOf(aPos, bPos)
            val loopEnd = maxOf(aPos, bPos)
            if (a != null && b != null && loopEnd > loopStart) {
                Box(
                    Modifier.align(Alignment.CenterStart).height(40.dp)
                        .width(trackW * ((loopEnd - loopStart).toFloat() / dur))
                        .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.45f)),
                )
            }
            if (a != null) {
                Box(Modifier.align(Alignment.CenterStart).offset(x = trackW * (aPos.toFloat() / dur)).size(3.dp, 40.dp).background(MaterialTheme.colorScheme.primary))
            }
            if (b != null) {
                Box(Modifier.align(Alignment.CenterStart).offset(x = trackW * (bPos.toFloat() / dur)).size(3.dp, 40.dp).background(MaterialTheme.colorScheme.secondary))
            }
        }
        Spacer(Modifier.height(14.dp))

        // A / B set buttons.
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (a != null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.weight(1f).clickable { a = position },
            ) {
                Row(Modifier.fillMaxWidth().padding(vertical = 14.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                    Spacer(Modifier.width(8.dp))
                    Text("Set A   ${a?.let { formatDuration(it) } ?: "--:--"}", style = MaterialTheme.typography.labelMedium)
                }
            }
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (b != null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.weight(1f).clickable { b = position },
            ) {
                Row(Modifier.fillMaxWidth().padding(vertical = 14.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondary))
                    Spacer(Modifier.width(8.dp))
                    Text("Set B   ${b?.let { formatDuration(it) } ?: "--:--"}", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        // Repeat count selector.
        Text("Repeat", style = MaterialTheme.typography.labelMedium, color = onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LoopCount.entries.forEach { count ->
                val selected = chosenCount == count
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (selected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f).clickable { chosenCount = count },
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                        Text(count.label, style = MaterialTheme.typography.labelLarge, color = if (selected) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        val canSave = a != null && b != null && (b ?: 0) > (a ?: 0)
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (canSave) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth().clickable(enabled = canSave) {
                if (canSave) {
                    vm.enableLoop(videoId, Loop("loop_${System.currentTimeMillis()}", a!!, b!!, chosenCount))
                    onClose()
                }
            },
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp)) {
                Text("Save loop", style = MaterialTheme.typography.labelLarge, color = if (canSave) MaterialTheme.colorScheme.onPrimary else onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(16.dp))

        if (activeLoop != null) {
            Surface(
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Repeat, "Active loop", tint = MaterialTheme.colorScheme.onTertiary)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Active  ${formatDuration(activeLoop.startMs)} → ${formatDuration(activeLoop.endMs)}  ·  ${activeLoop.count.label}",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = { vm.clearLoop() }) { Text("Clear", color = MaterialTheme.colorScheme.tertiary) }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        if (list.isNotEmpty()) {
            Text("Saved loops", style = MaterialTheme.typography.labelMedium, color = onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(list) { loop ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().clickable { vm.enableLoop(videoId, loop) },
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Repeat, "Loop", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "${formatDuration(loop.startMs)}  →  ${formatDuration(loop.endMs)}   ·   ${loop.count.label}",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.weight(1f),
                            )
                            IconButton(onClick = { vm.removeLoop(videoId, loop.id) }) {
                                Icon(Icons.Filled.Delete, "Delete", tint = onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

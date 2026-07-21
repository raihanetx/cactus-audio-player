package com.cactus.app.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import com.cactus.app.CactusViewModel
import com.cactus.app.model.SubtitleCue
import com.cactus.app.model.formatDuration
import com.cactus.app.ui.theme.BengaliBody
import com.cactus.app.ui.theme.tertiary
import com.cactus.app.ui.theme.onSurfaceVariant
import com.cactus.app.ui.theme.PronunciationStyle

/* PRD §8.4 — Subtitles bottom sheet. */
@Composable
fun SubtitleSheet(vm: CactusViewModel, videoId: String) {
    val position by vm.player.positionMs.collectAsState()
    val cues = remember(videoId) { vm.subtitlesFor(videoId) }
    var autoScroll by remember { mutableStateOf(true) }

    val currentIndex = cues.indexOfFirst { position in it.startMs..it.endMs }.coerceAtLeast(0)
    val listState = rememberLazyListState()

    LaunchedEffect(currentIndex) {
        if (autoScroll && currentIndex >= 0) listState.animateScrollToItem(currentIndex)
    }

    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Subtitles", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
            Text("Auto-scroll", style = MaterialTheme.typography.labelMedium, color = onSurfaceVariant)
            Spacer(Modifier.width(8.dp))
            Switch(
                checked = autoScroll,
                onCheckedChange = { autoScroll = it },
                colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary),
            )
        }
        Spacer(Modifier.height(12.dp))

        if (cues.isEmpty()) {
            Box(
                Modifier.fillMaxWidth().padding(vertical = 28.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("No subtitles for this track yet.", color = onSurfaceVariant)
            }
        } else {
            // Current cue — the focal card.
            cues.getOrNull(currentIndex)?.let { cue ->
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                            Spacer(Modifier.width(8.dp))
                            Text(formatDuration(cue.startMs), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(Modifier.weight(1f))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { vm.player.seekTo(cue.startMs) },
                            ) {
                                Row(Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.PlayArrow, "Replay", tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Replay", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary)
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(cue.textEn, style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(6.dp))
                        Text(cue.textBn, style = BengaliBody.copy(fontSize = 17.sp))
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("/ ${cue.pronunciation} /", style = PronunciationStyle, modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp))
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
            }

            // Timeline of cues — tap any line to jump.
            LazyColumn(state = listState, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(cues) { cue ->
                    val active = cue == cues.getOrNull(currentIndex)
                    Surface(
                        color = if (active) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                            .then(if (active) Modifier.border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(14.dp)) else Modifier)
                            .clickable { vm.player.seekTo(cue.startMs) },
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(formatDuration(cue.startMs), style = MaterialTheme.typography.labelSmall, color = tertiary, modifier = Modifier.padding(end = 12.dp))
                            Column {
                                Text(cue.textEn, style = MaterialTheme.typography.bodyMedium, color = if (active) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.82f))
                                Spacer(Modifier.height(2.dp))
                                Text(cue.textBn, style = BengaliBody.copy(fontSize = 12.sp), color = onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

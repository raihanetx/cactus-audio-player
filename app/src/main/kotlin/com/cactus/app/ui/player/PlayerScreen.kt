package com.cactus.app.ui.player

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import com.cactus.app.CactusViewModel
import com.cactus.app.model.formatDuration
import com.cactus.app.ui.components.BlurredArtwork
import com.cactus.app.ui.theme.BengaliBody
import com.cactus.app.ui.theme.onSurfaceVariant
import com.cactus.app.ui.components.ProgressSlider

private sealed interface Sheet {
    data object Loop : Sheet
    data object Subtitle : Sheet
    data object Note : Sheet
    data object Volume : Sheet
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(videoId: String, vm: CactusViewModel, onBack: () -> Unit) {
    val videos by vm.videos.collectAsState()
    val target = videos.firstOrNull { it.id == videoId } ?: videos.first()

    val isPlaying by vm.player.isPlaying.collectAsState()
    val position by vm.player.positionMs.collectAsState()
    val duration by vm.player.durationMs.collectAsState()
    val speed by vm.player.speed.collectAsState()
    val playlists by vm.playlists.collectAsState()

    val fromPlaylist = playlists.firstOrNull { it.videoIds.contains(target.id) }

    var showBn by remember { mutableStateOf(false) }
    var activeSheet by remember { mutableStateOf<Sheet?>(null) }

    var sleepMinutes by remember { mutableStateOf(0) }
    var sleepEnd by remember { mutableStateOf(0L) }
    LaunchedEffect(sleepEnd) {
        if (sleepEnd > 0) {
            val delayMs = sleepEnd - System.currentTimeMillis()
            if (delayMs > 0) kotlinx.coroutines.delay(delayMs)
            vm.player.pause()
            sleepEnd = 0L
            sleepMinutes = 0
        }
    }

    val sheetState = rememberModalBottomSheetState()

    Box(Modifier.fillMaxSize()) {
        BlurredArtwork(video = target, modifier = Modifier.fillMaxSize())
        Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 14.dp)) {
            // Top bar
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground) }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("PLAYING FROM", style = MaterialTheme.typography.labelSmall, color = onSurfaceVariant)
                    Text(fromPlaylist?.name ?: "Library", style = MaterialTheme.typography.labelMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                IconButton(onClick = { }) { Icon(Icons.Filled.MoreVert, "More", tint = MaterialTheme.colorScheme.onBackground) }
            }

            Spacer(Modifier.height(18.dp))

            // Artwork card — no background; title sits on a single line as the first line.
            Box(
                Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    // Title — single line, first line of the card (PRD requests 1 & 2).
                    Text(
                        if (showBn && target.titleBn != null) target.titleBn else target.titleEn,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .clickable { showBn = !showBn },
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                    Spacer(Modifier.height(20.dp))
                    // Central round, circular vintage music-player (PRD request 4).
                    VinylPlayer(
                        isPlaying = isPlaying,
                        gradientStart = target.gradientStart,
                        gradientEnd = target.gradientEnd,
                        progressMs = position,
                        durationMs = duration,
                        onClick = { vm.player.toggle() },
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Track info — file name only, secondary line.
            Text(target.fileName, style = MaterialTheme.typography.bodySmall, color = onSurfaceVariant, fontFamily = FontFamily.Monospace)

            Spacer(Modifier.height(10.dp))
            ProgressSlider(progressMs = position, durationMs = duration, onSeek = { vm.player.seekTo(it) })

            Spacer(Modifier.height(6.dp))

            // Main controls
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                IconButton(onClick = { vm.player.seekBy(-15_000) }) {
                    Icon(Icons.Filled.Replay10, "Back 15s", tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(36.dp))
                }
                // Placeholder keeps symmetry; the play action lives on the central vinyl.
                Spacer(Modifier.size(88.dp))
                IconButton(onClick = { vm.player.seekBy(15_000) }) {
                    Icon(Icons.Filled.Forward10, "Forward 15s", tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(36.dp))
                }
            }

            Spacer(Modifier.height(8.dp))

            // Feature pills
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                FeaturePill(icon = Icons.Filled.Repeat, label = "Loops", onClick = { activeSheet = Sheet.Loop })
                FeaturePill(icon = Icons.Filled.Subtitles, label = "Subtitles", onClick = { activeSheet = Sheet.Subtitle })
                FeaturePill(icon = Icons.Filled.Note, label = "Notes", onClick = { activeSheet = Sheet.Note })
            }

            Spacer(Modifier.height(10.dp))

            // Bottom row: sleep, speed, volume
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                SleepTimerButton(minutes = sleepMinutes, onPick = { m ->
                    sleepMinutes = m
                    sleepEnd = if (m > 0) System.currentTimeMillis() + m * 60_000L else 0L
                })
                Spacer(Modifier.weight(1f))
                var speedMenu by remember { mutableStateOf(false) }
                Box {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { speedMenu = true },
                    ) {
                        Text("${speed}x", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
                    }
                    DropdownMenu(expanded = speedMenu, onDismissRequest = { speedMenu = false }) {
                        listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f).forEach { s ->
                            DropdownMenuItem(text = { Text("${s}x") }, onClick = {
                                vm.player.setSpeed(s); speedMenu = false
                            })
                        }
                    }
                }
                Spacer(Modifier.width(10.dp))
                IconButton(onClick = { activeSheet = Sheet.Volume }) {
                    Icon(Icons.Filled.VolumeUp, "Volume", tint = MaterialTheme.colorScheme.onBackground)
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }

    if (activeSheet != null) {
        ModalBottomSheet(
            onDismissRequest = { activeSheet = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            when (activeSheet) {
                Sheet.Loop -> LoopSheet(vm, target.id, onClose = { activeSheet = null })
                Sheet.Subtitle -> SubtitleSheet(vm, target.id)
                Sheet.Note -> NoteSheet(vm, target.id)
                Sheet.Volume -> VolumeSheet(vm, onClose = { activeSheet = null })
                null -> {}
            }
        }
    }
}

/* PRD request 4 — a round, circular "vintage music player" at the centre of the
 * player page. A slowly spinning vinyl record with a coloured centre label and a
 * glowing ring while playing. No background card, no corner marks. */
@Composable
private fun VinylPlayer(
    isPlaying: Boolean,
    gradientStart: androidx.compose.ui.graphics.Color,
    gradientEnd: androidx.compose.ui.graphics.Color,
    progressMs: Long,
    durationMs: Long,
    onClick: () -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "vinyl")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(9000, easing = androidx.compose.animation.core.LinearEasing), RepeatMode.Restart),
        label = "spin",
    )
    val ringAlpha by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(tween(1300), RepeatMode.Restart),
        label = "glow",
    )

    val size = 260.dp
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(size).clickable(onClick = onClick)) {
        // Soft pulsing glow while playing.
        if (isPlaying) {
            Box(
                Modifier.size(size + 36.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = ringAlpha)),
            )
        }
        // Outer platter shadow.
        Box(
            Modifier.size(size).clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainer),
        )
        // Spinning vinyl record (grooves rotate; centre label stays still).
        Box(
            Modifier.size(size - 8.dp).clip(CircleShape)
                .graphicsLayer { rotationZ = if (isPlaying) rotation else 0f }
                .background(
                    Brush.radialGradient(
                        0.0f to Color(0xFF23231C),
                        0.62f to Color(0xFF101009),
                        1.0f to Color(0xFF050503),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            // Concentric groove rings.
            repeat(5) { i ->
                val r = (size - 8.dp) * (0.74f - i * 0.1f)
                Box(
                    Modifier.size(r).clip(CircleShape)
                        .border(1.dp, Color(0xFF2C2C22), CircleShape),
                )
            }
        }
        // Centre label — coloured from the track artwork (static, on top of the record).
        Box(
            Modifier.size(96.dp).clip(CircleShape)
                .background(Brush.verticalGradient(0.0f to gradientStart, 1.0f to gradientEnd)),
            contentAlignment = Alignment.Center,
        ) {
            // Spindle hole.
            Box(Modifier.size(12.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface))
            // Play / pause glyph on the label.
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(44.dp),
            )
        }
    }
}

@Composable
private fun FeaturePill(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, label, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun SleepTimerButton(minutes: Int, onPick: (Int) -> Unit) {
    var menu by remember { mutableStateOf(false) }
    Box {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.clickable { menu = true },
        ) {
            Row(Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Bedtime, "Sleep", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(if (minutes > 0) "Sleep ${minutes}m" else "Sleep", style = MaterialTheme.typography.labelMedium)
            }
        }
        DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
            listOf(15, 30, 45, 60).forEach { m ->
                DropdownMenuItem(text = { Text("$m min") }, onClick = { onPick(m); menu = false })
            }
            DropdownMenuItem(text = { Text("Off") }, onClick = { onPick(0); menu = false })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VolumeSheet(vm: CactusViewModel, onClose: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
        Text("Volume", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        var vol by remember { mutableStateOf(1f) }
        Slider(value = vol, onValueChange = {
            vol = it
            vm.player.setVolume(it)
        }, valueRange = 0f..1f)
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun EmptyNowPlaying(onGoLibrary: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Nothing playing yet", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text("Pick something from your Library.", style = MaterialTheme.typography.bodyMedium, color = onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable(onClick = onGoLibrary)) {
                Text("Go to Library", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp))
            }
        }
    }
}

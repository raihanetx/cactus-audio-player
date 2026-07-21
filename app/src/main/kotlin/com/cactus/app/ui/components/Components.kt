package com.cactus.app.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cactus.app.model.VideoItem
import com.cactus.app.model.formatDuration
import com.cactus.app.ui.theme.BengaliBody
import com.cactus.app.ui.theme.onSurfaceVariant
import com.cactus.app.ui.theme.PronunciationStyle

/* PRD §9.2 — three animated bars convey ambient playback without text. */
@Composable
fun AmbientWaveform(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    val transition = rememberInfiniteTransition(label = "wave")
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        repeat(3) { i ->
            val scale by transition.animateFloat(
                initialValue = 0.3f,
                targetValue = if (isPlaying) 0.5f + i * 0.25f else 0.3f,
                animationSpec = infiniteRepeatable(
                    animation = tween(420 + i * 160),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "bar$i",
            )
            Box(
                Modifier
                    .size(3.dp, 16.dp)
                    .graphicsLayer {
                        scaleY = if (isPlaying) scale else 1f
                        transformOrigin = TransformOrigin.Center
                    }
                    .background(color, RoundedCornerShape(2.dp)),
            )
        }
    }
}

/* PRD §8.2 — dominant gradient extracted from the video frame, blurred. */
@Composable
fun BlurredArtwork(
    video: VideoItem,
    modifier: Modifier = Modifier,
) {
    val surfaceBg = MaterialTheme.colorScheme.surface
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0.0f to video.gradientStart,
                    0.5f to video.gradientEnd,
                    1.0f to surfaceBg,
                ),
            )
            .drawWithContent {
                drawContent()
                drawRect(
                    Brush.verticalGradient(
                        0.0f to Color.Transparent,
                        0.45f to Color.Transparent,
                        1.0f to surfaceBg,
                    )
                )
            },
    )
}

/* Pill-shaped sort chip. PRD §10 — active uses primary container. */
@Composable
fun SortChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        color = if (selected) colors.primaryContainer else colors.surfaceVariant,
        contentColor = if (selected) colors.onPrimaryContainer else colors.onSurfaceVariant,
        tonalElevation = if (selected) 2.dp else 0.dp,
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}

/* PRD §8.1 / §7.2 — slim frosted mini-player fixed above the bottom nav. */
@Composable
fun MiniPlayer(
    video: VideoItem?,
    isPlaying: Boolean,
    onTap: () -> Unit,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (video == null) return
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onTap),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f),
        tonalElevation = 6.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.verticalGradient(0.0f to video.gradientStart, 1.0f to video.gradientEnd)),
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    video.titleEn,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                video.titleBn?.let {
                    Text(it, style = BengaliBody.copy(fontSize = 11.sp), maxLines = 1, overflow = TextOverflow.Ellipsis, color = onSurfaceVariant)
                }
            }
            AmbientWaveform(isPlaying = isPlaying, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(onClick = onToggle),
                contentAlignment = Alignment.Center,
            ) {
                androidx.compose.material3.Icon(
                    imageVector = if (isPlaying) androidx.compose.material.icons.Icons.Filled.Pause
                    else androidx.compose.material.icons.Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

/* PRD §7.1 / §10 — 4-item bottom navigation with notification bell. */
enum class BottomTab(val route: String, val label: String) {
    Library("library", "Library"),
    Playlists("playlists", "Playlists"),
    NowPlaying("nowPlaying", "Now Playing"),
    Settings("settings", "Settings"),
}

@Composable
fun CactusBottomBar(
    current: String,
    onSelect: (BottomTab) -> Unit,
    unreadCount: Int = 0,
    onNotificationClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val items = BottomTab.entries
    Surface(
        tonalElevation = 0.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NavigationBar(
                modifier = Modifier.weight(1f),
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                items.forEach { tab ->
                    val selected = current == tab.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = { onSelect(tab) },
                        icon = {
                            androidx.compose.material3.Icon(
                                imageVector = when (tab) {
                                    BottomTab.Library -> androidx.compose.material.icons.Icons.Filled.VideoLibrary
                                    BottomTab.Playlists -> androidx.compose.material.icons.Icons.Filled.QueueMusic
                                    BottomTab.NowPlaying -> androidx.compose.material.icons.Icons.Filled.PlayCircle
                                    BottomTab.Settings -> androidx.compose.material.icons.Icons.Filled.Settings
                                },
                                contentDescription = tab.label,
                            )
                        },
                        label = { Text(tab.label, style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = onSurfaceVariant,
                            unselectedTextColor = onSurfaceVariant,
                        ),
                    )
                }
            }
            Spacer(Modifier.width(4.dp))
            IconButton(onClick = onNotificationClick) {
                Box(contentAlignment = Alignment.TopEnd) {
                    Icon(
                        Icons.Filled.Notifications,
                        "Notifications",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp),
                    )
                    if (unreadCount > 0) {
                        Box(
                            Modifier
                                .size(8.dp)
                                .background(MaterialTheme.colorScheme.secondary, CircleShape)
                                .align(Alignment.TopEnd),
                        )
                    }
                }
            }
        }
    }
}

/* PRD §8.2 — thick (6dp) progress bar with glowing thumb. */
@Composable
fun ProgressSlider(
    progressMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val duration = if (durationMs > 0) durationMs else 1L
    Column(modifier.fillMaxWidth()) {
        Slider(
            value = progressMs.toFloat().coerceIn(0f, duration.toFloat()),
            onValueChange = { onSeek(it.toLong()) },
            valueRange = 0f..duration.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.outline,
            ),
            modifier = Modifier.height(24.dp),
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(formatDuration(progressMs), style = MaterialTheme.typography.labelSmall, color = onSurfaceVariant)
            Text("-" + formatDuration((duration - progressMs).coerceAtLeast(0)), style = MaterialTheme.typography.labelSmall, color = onSurfaceVariant)
        }
    }
}

@Composable
fun Transliteration(text: String, modifier: Modifier = Modifier) {
    Text(text, style = PronunciationStyle, modifier = modifier)
}

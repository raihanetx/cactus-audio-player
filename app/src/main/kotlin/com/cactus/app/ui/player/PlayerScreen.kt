package com.cactus.app.ui.player

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cactus.app.model.VideoItem
import com.cactus.app.ui.theme.Black
import com.cactus.app.ui.theme.Neutral200
import com.cactus.app.ui.theme.Neutral300
import com.cactus.app.ui.theme.Neutral400
import com.cactus.app.ui.theme.Neutral500
import com.cactus.app.ui.theme.Neutral700
import com.cactus.app.ui.theme.Neutral800
import com.cactus.app.ui.theme.White
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingPage(
    player: PlayerState,
    video: VideoItem,
    onBack: () -> Unit,
    onSelectPage: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().background(White)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Back", tint = Neutral700, modifier = Modifier.size(20.dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("NOW PLAYING", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Neutral400, letterSpacing = 1.sp)
                Text(video.title.ifBlank { "Untitled" }, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Black, maxLines = 1)
            }
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).clickable { },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.MoreVert, contentDescription = "More", tint = Neutral700, modifier = Modifier.size(20.dp))
            }
        }

        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Neutral800, Black),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                        )
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.MusicNote, contentDescription = null, tint = White, modifier = Modifier.size(56.dp))
            }

            Spacer(Modifier.height(32.dp))

            Text(video.title.ifBlank { "Untitled" }, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Black, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(getParentFolder(video.path), fontSize = 12.sp, color = Neutral400, textAlign = TextAlign.Center, modifier = Modifier.width(280.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(formatTime(player.currentPosition), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Neutral500, modifier = Modifier.width(40.dp))
                Slider(
                    value = if (player.dragging) player.sliderPos else player.currentPosition.toFloat().coerceIn(0f, player.duration.toFloat().coerceAtLeast(1f)),
                    onValueChange = {
                        player.sliderPos = it
                        player.dragging = true
                        player.currentPosition = it.toLong()
                    },
                    onValueChangeFinished = {
                        player.seekTo(player.sliderPos.toLong())
                        player.dragging = false
                    },
                    valueRange = 0f..player.duration.toFloat().coerceAtLeast(1f),
                    colors = SliderDefaults.colors(thumbColor = Black, activeTrackColor = Black, inactiveTrackColor = Neutral200),
                    modifier = Modifier.weight(1f).height(24.dp),
                )
                Text(formatTime(player.duration), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Neutral500, modifier = Modifier.width(40.dp), textAlign = TextAlign.End)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("-10s", color = Neutral700, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.clickable { player.skip(-10) })
                Spacer(Modifier.width(24.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100))
                        .background(if (player.isPrepared) Black else Neutral200)
                        .clickable(enabled = player.isPrepared) { player.toggle() }
                        .padding(horizontal = 28.dp, vertical = 10.dp),
                ) {
                    Text(if (player.isPlaying) "Pause" else "Play", color = White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(Modifier.width(24.dp))
                Text("+10s", color = Neutral700, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.clickable { player.skip(10) })
            }

            Spacer(Modifier.height(12.dp))
            PageDots(active = 0, onSelect = onSelectPage)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun PageDots(active: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (i in 0 until 4) {
            val selected = i == active
            Box(
                modifier = Modifier
                    .size(width = if (selected) 16.dp else 6.dp, height = 6.dp)
                    .clip(CircleShape)
                    .background(if (selected) Black else Neutral300)
                    .clickable { onSelect(i) },
            )
            if (i < 3) Spacer(Modifier.width(6.dp))
        }
    }
}

private fun getParentFolder(path: String): String {
    return try { File(path).parentFile?.name ?: "Unknown" } catch (_: Exception) { "Unknown" }
}

fun formatTime(ms: Long): String {
    if (ms <= 0) return "0:00"
    val totalSec = ms / 1000
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}

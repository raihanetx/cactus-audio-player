package com.cactus.app.ui.player

import android.media.MediaPlayer
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cactus.app.model.VideoItem
import com.cactus.app.ui.theme.Black
import com.cactus.app.ui.theme.Neutral200
import com.cactus.app.ui.theme.Neutral400
import com.cactus.app.ui.theme.Neutral500
import com.cactus.app.ui.theme.Neutral700
import com.cactus.app.ui.theme.Neutral800
import com.cactus.app.ui.theme.Neutral900
import com.cactus.app.ui.theme.White
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun PlayerScreen(
    video: VideoItem,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val mediaPlayer = remember { MediaPlayer() }
    var isPrepared by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var duration by remember { mutableLongStateOf(0L) }
    var currentPosition by remember { mutableLongStateOf(0L) }

    DisposableEffect(video.id) {
        try {
            mediaPlayer.setDataSource(video.path)
            mediaPlayer.setOnPreparedListener { mp ->
                isPrepared = true
                duration = mp.duration.toLong()
            }
            mediaPlayer.setOnCompletionListener {
                isPlaying = false
                currentPosition = 0
            }
            mediaPlayer.prepareAsync()
        } catch (_: Exception) { }

        onDispose {
            mediaPlayer.release()
        }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = mediaPlayer.currentPosition.toLong()
            delay(250)
        }
    }

    val togglePlay: () -> Unit = {
        if (isPrepared) {
            if (isPlaying) {
                mediaPlayer.pause()
                isPlaying = false
            } else {
                mediaPlayer.start()
                isPlaying = true
            }
        }
    }

    val skip: (Int) -> Unit = { seconds ->
        if (isPrepared) {
            val newPos = (mediaPlayer.currentPosition + seconds * 1000).coerceIn(0, mediaPlayer.duration)
            mediaPlayer.seekTo(newPos)
            currentPosition = newPos.toLong()
        }
    }

    Column(modifier = modifier.fillMaxSize().background(White)) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).clickable { onBack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, tint = Neutral700, modifier = Modifier.size(20.dp))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "NOW PLAYING",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = Neutral400,
                        letterSpacing = 1.sp,
                    )
                    Text(
                        video.title.ifBlank { "Untitled" },
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Black,
                    )
                }
                Box(modifier = Modifier.size(36.dp))
            }
        }

        Column(
            modifier = Modifier.weight(1f).padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier.size(160.dp).clip(RoundedCornerShape(24.dp)).background(
                    Brush.linearGradient(
                        colors = listOf(Neutral800, Black),
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                    )
                ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = getInitials(video.title),
                    color = White.copy(alpha = 0.9f),
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Black,
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                video.title.ifBlank { "Untitled" },
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Black,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                getParentFolder(video.path),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Neutral400,
            )

            Spacer(Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)).background(Neutral200)
                        .clickable(enabled = false) { }
                ) {
                    val progress = if (duration > 0) currentPosition.toFloat() / duration else 0f
                    Box(
                        modifier = Modifier.fillMaxWidth(progress).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Black)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(formatTime(currentPosition), color = Neutral500, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Text(formatTime(duration), color = Neutral500, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "-10s",
                    color = Neutral700,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { skip(-10) },
                )
                Spacer(Modifier.width(24.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100))
                        .background(if (isPrepared) Black else Neutral200)
                        .clickable(enabled = isPrepared) { togglePlay() }
                        .padding(horizontal = 32.dp, vertical = 10.dp),
                ) {
                    Text(
                        if (isPlaying) "Pause" else "Play",
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                }
                Spacer(Modifier.width(24.dp))
                Text(
                    "+10s",
                    color = Neutral700,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { skip(10) },
                )
            }
        }
    }
}

private fun getInitials(title: String): String {
    if (title.isBlank()) return "?"
    return title.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")
}

private fun getParentFolder(path: String): String {
    return try { File(path).parentFile?.name ?: "Unknown" } catch (_: Exception) { "Unknown" }
}

private fun formatTime(ms: Long): String {
    if (ms <= 0) return "0:00"
    val totalSec = ms / 1000
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}

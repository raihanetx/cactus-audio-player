package com.cactus.app.ui.videolist

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cactus.app.model.VideoItem
import com.cactus.app.ui.theme.Black
import com.cactus.app.ui.theme.Neutral100
import com.cactus.app.ui.theme.Neutral200
import com.cactus.app.ui.theme.Neutral400
import com.cactus.app.ui.theme.Neutral500
import com.cactus.app.ui.theme.Neutral600
import com.cactus.app.ui.theme.Neutral700
import com.cactus.app.ui.theme.Neutral800
import com.cactus.app.ui.theme.Neutral900
import com.cactus.app.ui.theme.White
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VideoListScreen(videos: List<VideoItem>, modifier: Modifier = Modifier) {
    val contentResolver = LocalContext.current.contentResolver

    Column(modifier = modifier.fillMaxSize().background(White)) {
        Text(
            text = "Videos",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Black,
            modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 8.dp),
        )
        Text(
            text = "${videos.size} video${if (videos.size != 1) "s" else ""} found",
            fontSize = 12.sp,
            color = Neutral500,
            modifier = Modifier.padding(start = 20.dp, bottom = 12.dp),
        )

        if (videos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("\uD83C\uDFA5", fontSize = 48.sp)
                    Spacer(Modifier.height(16.dp))
                    Text("No videos found", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Neutral800)
                    Spacer(Modifier.height(4.dp))
                    Text("Make sure you have videos on your device", fontSize = 13.sp, color = Neutral500)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(1.dp),
            ) {
                items(videos, key = { it.id }) { video ->
                    VideoRow(video = video, contentResolver = contentResolver)
                }
            }
        }
    }
}

@Composable
private fun VideoRow(video: VideoItem, contentResolver: ContentResolver) {
    var thumbnail by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(video.id) {
        thumbnail = withContext(Dispatchers.IO) {
            try {
                MediaStore.Video.Thumbnails.getThumbnail(
                    contentResolver,
                    video.id,
                    MediaStore.Video.Thumbnails.MINI_KIND,
                    null,
                )
            } catch (_: Exception) { null }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(width = 120.dp, height = 68.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Neutral100),
            contentAlignment = Alignment.Center,
        ) {
            if (thumbnail != null) {
                Image(
                    bitmap = thumbnail!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Text("\uD83C\uDFA5", fontSize = 20.sp)
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = video.title.ifBlank { "Untitled" },
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Neutral900,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = formatDuration(video.durationMs),
                fontSize = 12.sp,
                color = Neutral600,
            )
            Spacer(Modifier.height(1.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(formatDate(video.dateAdded), fontSize = 11.sp, color = Neutral500)
                Text("\u00B7", fontSize = 11.sp, color = Neutral200)
                Text(formatSize(video.sizeBytes), fontSize = 11.sp, color = Neutral500)
            }
        }
    }
}

private fun formatDuration(ms: Long): String {
    if (ms <= 0) return "0:00"
    val totalSec = ms / 1000
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s)
    else "%d:%02d".format(m, s)
}

private fun formatSize(bytes: Long): String {
    return when {
        bytes < 1024 * 1024 -> "%.0f KB".format(bytes / 1024.0)
        bytes < 1024 * 1024 * 1024 -> "%.1f MB".format(bytes / (1024.0 * 1024.0))
        else -> "%.1f GB".format(bytes / (1024.0 * 1024.0 * 1024.0))
    }
}

private fun formatDate(epochSec: Long): String {
    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return sdf.format(Date(epochSec * 1000))
}

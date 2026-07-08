package com.cactus.app.ui.videolist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cactus.app.model.VideoItem
import com.cactus.app.ui.theme.Black
import com.cactus.app.ui.theme.Neutral100
import com.cactus.app.ui.theme.Neutral300
import com.cactus.app.ui.theme.Neutral400
import com.cactus.app.ui.theme.Neutral500
import com.cactus.app.ui.theme.Neutral600
import com.cactus.app.ui.theme.Neutral700
import com.cactus.app.ui.theme.Neutral900
import com.cactus.app.ui.theme.White
import java.io.File

@Composable
fun VideoListScreen(
    videos: List<VideoItem>,
    onItemClick: (VideoItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }

    val filtered by remember(videos, query) {
        derivedStateOf {
            if (query.isBlank()) {
                videos
            } else {
                videos.filter {
                    it.title.contains(query, ignoreCase = true) ||
                        getParentFolder(it.path).contains(query, ignoreCase = true)
                }
            }
        }
    }

    Column(modifier = modifier.fillMaxSize().background(White)) {
        LibraryHeader(
            total = videos.size,
            query = query,
            onQueryChange = { query = it },
        )

        when {
            videos.isEmpty() -> EmptyState(
                modifier = Modifier.fillMaxSize().weight(1f),
            )
            filtered.isEmpty() -> NoResultsState(
                modifier = Modifier.fillMaxSize().weight(1f),
                query = query,
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp),
            ) {
                items(filtered, key = { it.id }) { video ->
                    TrackRow(video = video, onClick = { onItemClick(video) })
                    HorizontalDivider(
                        color = Neutral100,
                        thickness = 1.dp,
                        modifier = Modifier.padding(start = 72.dp, end = 16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun LibraryHeader(
    total: Int,
    query: String,
    onQueryChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 12.dp),
    ) {
        Text(
            text = "Library",
            style = MaterialTheme.typography.headlineLarge.copy(color = Black),
        )
        Text(
            text = "%d track%s".format(total, if (total == 1) "" else "s"),
            style = MaterialTheme.typography.bodyMedium.copy(color = Neutral500),
            modifier = Modifier.padding(top = 2.dp, bottom = 16.dp),
        )

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Search tracks or folders", color = Neutral400)
            },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = null, tint = Neutral500)
            },
            trailingIcon = {
                AnimatedVisibility(visible = query.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Filled.Close, contentDescription = "Clear", tint = Neutral500)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Neutral100,
                unfocusedContainerColor = Neutral100,
                disabledContainerColor = Neutral100,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Black,
            ),
        )
    }
}

@Composable
private fun TrackRow(video: VideoItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Neutral100),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = getInitials(video.title),
                color = Neutral900,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = video.title.ifBlank { "Untitled" },
                style = MaterialTheme.typography.titleMedium.copy(color = Neutral900),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(3.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    getParentFolder(video.path),
                    style = MaterialTheme.typography.bodySmall.copy(color = Neutral500),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Text("\u00B7", color = Neutral300, style = MaterialTheme.typography.bodySmall)
                Text(
                    formatDuration(video.durationMs),
                    style = MaterialTheme.typography.bodySmall.copy(color = Neutral500),
                )
            }
        }

        Icon(
            Icons.Filled.PlayArrow,
            contentDescription = "Play",
            tint = Neutral700,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("\uD83C\uDFB5", fontSize = 48.sp)
            Spacer(Modifier.height(16.dp))
            Text("No tracks found", style = MaterialTheme.typography.titleLarge.copy(color = Neutral900))
            Spacer(Modifier.height(6.dp))
            Text(
                "Videos on your device will appear here",
                style = MaterialTheme.typography.bodyMedium.copy(color = Neutral500),
            )
        }
    }
}

@Composable
private fun NoResultsState(modifier: Modifier = Modifier, query: String) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("\uD83D\uDD0D", fontSize = 44.sp)
            Spacer(Modifier.height(16.dp))
            Text("No matches", style = MaterialTheme.typography.titleLarge.copy(color = Neutral900))
            Spacer(Modifier.height(6.dp))
            Text(
                "Nothing found for \"$query\"",
                style = MaterialTheme.typography.bodyMedium.copy(color = Neutral500),
            )
        }
    }
}

private fun formatDuration(ms: Long): String {
    if (ms <= 0) return "0:00"
    val totalSec = ms / 1000
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}

private fun getInitials(title: String): String {
    if (title.isBlank()) return "?"
    return title.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")
}

private fun getParentFolder(path: String): String {
    return try { File(path).parentFile?.name ?: "Unknown" } catch (_: Exception) { "Unknown" }
}

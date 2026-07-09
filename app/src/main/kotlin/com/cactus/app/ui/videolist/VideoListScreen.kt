package com.cactus.app.ui.videolist

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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

private enum class SortMode { DATE_DESC, DATE_ASC, TITLE_ASC, TITLE_DESC, DURATION_DESC, DURATION_ASC }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoListScreen(
    videos: List<VideoItem>,
    onItemClick: (VideoItem) -> Unit,
    onOpenSettings: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }
    var sortMode by remember { mutableStateOf(SortMode.DATE_DESC) }
    var showSortSheet by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    val totalMs by remember(videos) {
        derivedStateOf { videos.sumOf { it.durationMs } }
    }

    val filtered by remember(videos, query, sortMode) {
        derivedStateOf {
            val base = if (query.isBlank()) videos else videos.filter {
                it.title.contains(query, ignoreCase = true) ||
                    getParentFolder(it.path).contains(query, ignoreCase = true)
            }
            when (sortMode) {
                SortMode.DATE_DESC -> base.sortedByDescending { it.dateAdded }
                SortMode.DATE_ASC -> base.sortedBy { it.dateAdded }
                SortMode.TITLE_ASC -> base.sortedBy { it.title.lowercase() }
                SortMode.TITLE_DESC -> base.sortedByDescending { it.title.lowercase() }
                SortMode.DURATION_DESC -> base.sortedByDescending { it.durationMs }
                SortMode.DURATION_ASC -> base.sortedBy { it.durationMs }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(
                containerColor = Color.Transparent,
                tonalElevation = 0.dp,
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.LibraryMusic, contentDescription = "Tracks") },
                    label = { Text("Tracks") },
                    selected = selectedTab == 0 && !showSearch,
                    onClick = { selectedTab = 0; showSearch = false; query = "" },
                    colors = itemColors(),
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                    label = { Text("Search") },
                    selected = showSearch,
                    onClick = { showSearch = true; selectedTab = 1 },
                    colors = itemColors(),
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.FilterList, contentDescription = "Sort") },
                    label = { Text("Sort") },
                    selected = showSortSheet,
                    onClick = { showSortSheet = true },
                    colors = itemColors(),
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { onOpenSettings?.invoke() },
                    colors = itemColors(),
                )
            }
        },
    ) { innerPadding ->
        AnimatedContent(
            targetState = showSearch,
            modifier = Modifier.padding(innerPadding),
            transitionSpec = {
                if (targetState) {
                    slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                } else {
                    slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                }
            },
        ) { isSearch ->
            if (isSearch) {
                SearchPage(
                    query = query,
                    onQueryChange = { query = it },
                    onBack = { showSearch = false; selectedTab = 0 },
                    results = filtered,
                    onItemClick = onItemClick,
                )
            } else {
                TrackListPage(
                    videos = videos,
                    filtered = filtered,
                    totalMs = totalMs,
                    onItemClick = onItemClick,
                )
            }
        }
    }

    if (showSortSheet) {
        SortSheet(
            current = sortMode,
            onSelect = { sortMode = it; showSortSheet = false },
            onDismiss = { showSortSheet = false },
        )
    }
}

@Composable
private fun TrackListPage(
    videos: List<VideoItem>,
    filtered: List<VideoItem>,
    totalMs: Long,
    onItemClick: (VideoItem) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
    ) {
        if (videos.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${filtered.size} track${if (filtered.size != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Neutral500),
                )
                Text(
                    text = formatTotalTime(totalMs),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Neutral900,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
        }

        when {
            videos.isEmpty() -> EmptyState(Modifier.fillMaxSize().weight(1f))
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 8.dp),
            ) {
                items(filtered, key = { it.id }) { video ->
                    TrackRow(video = video, onClick = { onItemClick(video) })
                }
            }
        }
    }
}

@Composable
private fun SearchPage(
    query: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    results: List<VideoItem>,
    onItemClick: (VideoItem) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Neutral700)
            }
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search tracks or folders", color = Neutral400) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = Neutral500) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
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
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Black,
                ),
            )
        }

        if (results.isEmpty() && query.isNotEmpty()) {
            NoResultsState(Modifier.fillMaxSize().weight(1f), query)
        } else if (results.isNotEmpty()) {
            Text(
                text = "${results.size} result${if (results.size != 1) "s" else ""}",
                style = MaterialTheme.typography.bodySmall.copy(color = Neutral500),
                modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 4.dp),
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 8.dp),
            ) {
                items(results, key = { it.id }) { video ->
                    TrackRow(video = video, onClick = { onItemClick(video) })
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = Neutral300, modifier = Modifier.size(56.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Type to search", style = MaterialTheme.typography.bodyLarge.copy(color = Neutral500))
                }
            }
        }
    }
}

@Composable
private fun TrackRow(video: VideoItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 12.dp),
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
            Spacer(Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    formatDuration(video.durationMs),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Neutral900,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
                Text("\u00B7", color = Neutral300, style = MaterialTheme.typography.bodySmall)
                Text(
                    if (hasSubtitle(video.path)) "Generated" else "Not Generated",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (hasSubtitle(video.path)) Neutral600 else Neutral400,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text("\u00B7", color = Neutral300, style = MaterialTheme.typography.bodySmall)
                Text(
                    formatFileSize(video.sizeBytes),
                    style = MaterialTheme.typography.bodySmall.copy(color = Neutral500),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortSheet(
    current: SortMode,
    onSelect: (SortMode) -> Unit,
    onDismiss: () -> Unit,
) {
    androidx.compose.material3.ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(bottom = 32.dp)) {
            Text(
                "Sort by",
                style = MaterialTheme.typography.titleLarge.copy(color = Neutral900),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            )
            SortOption(label = "Newest first", selected = current == SortMode.DATE_DESC, onClick = { onSelect(SortMode.DATE_DESC) })
            SortOption(label = "Oldest first", selected = current == SortMode.DATE_ASC, onClick = { onSelect(SortMode.DATE_ASC) })
            SortOption(label = "Title A-Z", selected = current == SortMode.TITLE_ASC, onClick = { onSelect(SortMode.TITLE_ASC) })
            SortOption(label = "Title Z-A", selected = current == SortMode.TITLE_DESC, onClick = { onSelect(SortMode.TITLE_DESC) })
            SortOption(label = "Longest first", selected = current == SortMode.DURATION_DESC, onClick = { onSelect(SortMode.DURATION_DESC) })
            SortOption(label = "Shortest first", selected = current == SortMode.DURATION_ASC, onClick = { onSelect(SortMode.DURATION_ASC) })
        }
    }
}

@Composable
private fun SortOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = if (selected) Black else Neutral600,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            ),
            modifier = Modifier.weight(1f),
        )
        if (selected) {
            Icon(Icons.Filled.FilterList, contentDescription = null, tint = Black, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun itemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = Black,
    unselectedIconColor = Neutral500,
    indicatorColor = Neutral100,
)

private fun getInitials(title: String): String {
    if (title.isBlank()) return "?"
    return title.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")
}

private fun formatDuration(ms: Long): String {
    if (ms <= 0) return "0:00"
    val totalSec = ms / 1000
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}

private fun formatTotalTime(ms: Long): String {
    val totalSec = ms / 1000
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    return buildString {
        if (h > 0) append("${h}h ")
        append("${m}m")
        append(" total")
    }
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "%.1f KB".format(bytes / 1024f)
        else -> "%.1f MB".format(bytes / (1024f * 1024f))
    }
}

private fun hasSubtitle(path: String): Boolean {
    return try {
        val file = File(path)
        val base = file.parentFile?.resolve(file.nameWithoutExtension) ?: return false
        listOf(".srt", ".vtt", ".sub", ".sbv", ".ass", ".ssa").any {
            File(base.path + it).exists()
        }
    } catch (_: Exception) { false }
}

private fun getParentFolder(path: String): String {
    return try { File(path).parentFile?.name ?: "Unknown" } catch (_: Exception) { "Unknown" }
}

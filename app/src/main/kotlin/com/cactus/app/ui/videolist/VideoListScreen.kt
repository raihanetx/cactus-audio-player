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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
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
import androidx.compose.material3.darkColorScheme
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
import java.io.File

private enum class SortMode { DATE_DESC, DATE_ASC, TITLE_ASC, TITLE_DESC, DURATION_DESC, DURATION_ASC }

private val Zinc900 = Color(0xFF18181B)
private val Zinc800 = Color(0xFF27272A)
private val Zinc700 = Color(0xFF3F3F46)
private val Zinc600 = Color(0xFF52525B)
private val Zinc500 = Color(0xFF71717A)
private val Zinc400 = Color(0xFFA1A1AA)
private val Zinc300 = Color(0xFFD4D4D8)
private val Emerald500 = Color(0xFF10B981)

private val DarkColorScheme = darkColorScheme(
    background = Zinc900,
    onBackground = Color.White,
    surface = Zinc900,
    onSurface = Color.White,
    surfaceVariant = Zinc800,
    onSurfaceVariant = Zinc400,
    outline = Zinc800,
    outlineVariant = Zinc700,
    primary = Emerald500,
    onPrimary = Color.White,
    secondary = Zinc400,
)

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
    var activeId by remember { mutableStateOf<Long?>(null) }

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

    MaterialTheme(colorScheme = DarkColorScheme) {
        Scaffold(
            modifier = modifier,
            bottomBar = {
                NavigationBar(
                    containerColor = Zinc900,
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
                        activeId = activeId,
                        totalMs = totalMs,
                        onItemClick = { video ->
                            activeId = video.id
                            onItemClick(video)
                        },
                    )
                } else {
                    TrackListPage(
                        videos = videos,
                        filtered = filtered,
                        totalMs = totalMs,
                        activeId = activeId,
                        onItemClick = { video ->
                            activeId = video.id
                            onItemClick(video)
                        },
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
}

@Composable
private fun TrackListPage(
    videos: List<VideoItem>,
    filtered: List<VideoItem>,
    totalMs: Long,
    activeId: Long?,
    onItemClick: (VideoItem) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Zinc900),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 48.dp, bottom = 8.dp),
        ) {
            Text(
                text = "Your Audio Library",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "${videos.size} tracks · ${formatTotalHours(totalMs)}",
                fontSize = 14.sp,
                color = Zinc400,
            )
        }

        when {
            videos.isEmpty() -> EmptyState(Modifier.fillMaxSize().weight(1f))
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            ) {
                items(filtered, key = { it.id }) { video ->
                    TrackRow(
                        video = video,
                        active = video.id == activeId,
                        totalMs = totalMs,
                        onClick = { onItemClick(video) },
                    )
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
    activeId: Long?,
    totalMs: Long,
    onItemClick: (VideoItem) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Zinc900),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 16.dp, top = 12.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Zinc400)
            }
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search tracks or folders", color = Zinc500) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = Zinc500) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear", tint = Zinc500)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Zinc800,
                    unfocusedContainerColor = Zinc800,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Emerald500,
                ),
            )
        }

        if (results.isEmpty() && query.isNotEmpty()) {
            NoResultsState(Modifier.fillMaxSize().weight(1f), query)
        } else if (results.isNotEmpty()) {
            Text(
                text = "${results.size} result${if (results.size != 1) "s" else ""}",
                fontSize = 13.sp,
                color = Zinc500,
                modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 4.dp),
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            ) {
                items(results, key = { it.id }) { video ->
                    TrackRow(
                        video = video,
                        active = video.id == activeId,
                        totalMs = totalMs,
                        onClick = { onItemClick(video) },
                    )
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = Zinc700, modifier = Modifier.size(56.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Type to search", fontSize = 16.sp, color = Zinc500)
                }
            }
        }
    }
}

@Composable
private fun TrackRow(video: VideoItem, active: Boolean, totalMs: Long, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(if (active) Emerald500.copy(alpha = 0.15f) else Zinc800),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.MusicNote,
                contentDescription = null,
                tint = if (active) Emerald500 else Zinc500,
                modifier = Modifier.size(18.dp),
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = video.title.ifBlank { "Untitled" },
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (active) Color.White else Zinc400,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            val sub = hasSubtitle(video.path)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    formatDuration(video.durationMs),
                    fontSize = 12.sp,
                    color = if (active) Zinc400 else Zinc500,
                )
                Dot(if (active) Zinc600 else Zinc700)
                Text(
                    if (sub) "Ready" else "Not Ready",
                    fontSize = 12.sp,
                    color = if (active) Zinc400 else Zinc500,
                )
                Dot(if (active) Zinc600 else Zinc700)
                Text(
                    "time spend ${formatTotalHours(totalMs)}",
                    fontSize = 12.sp,
                    color = if (active) Zinc400 else Zinc500,
                )
            }
        }

        Icon(
            Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = if (active) Zinc500 else Zinc600,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun Dot(color: Color) {
    Box(
        modifier = Modifier
            .size(3.dp)
            .clip(CircleShape)
            .background(color),
    )
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("\uD83C\uDFB5", fontSize = 48.sp)
            Spacer(Modifier.height(16.dp))
            Text("No tracks found", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            Spacer(Modifier.height(6.dp))
            Text(
                "Videos on your device will appear here",
                fontSize = 14.sp,
                color = Zinc500,
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
            Text("No matches", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            Spacer(Modifier.height(6.dp))
            Text(
                "Nothing found for \"$query\"",
                fontSize = 14.sp,
                color = Zinc500,
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
    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Zinc900,
    ) {
        Column(modifier = Modifier.padding(bottom = 32.dp)) {
            Text(
                "Sort by",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
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
            fontSize = 15.sp,
            color = if (selected) Emerald500 else Zinc400,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f),
        )
        if (selected) {
            Icon(Icons.Filled.FilterList, contentDescription = null, tint = Emerald500, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun itemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = Emerald500,
    unselectedIconColor = Zinc500,
    indicatorColor = Zinc800,
)

private fun formatDuration(ms: Long): String {
    if (ms <= 0) return "0:00"
    val totalSec = ms / 1000
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}

private fun formatTotalHours(ms: Long): String {
    val totalMin = ms / 60000
    val h = totalMin / 60
    val m = totalMin % 60
    return if (m > 0) "${h}h ${m}m" else "${h}h"
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

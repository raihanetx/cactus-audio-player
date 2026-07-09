package com.cactus.app.ui.videolist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.layout.ContentScale
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

    val filtered by remember(videos, query, sortMode) {
        derivedStateOf {
            val base = if (query.isBlank()) {
                videos
            } else {
                videos.filter {
                    it.title.contains(query, ignoreCase = true) ||
                        getParentFolder(it.path).contains(query, ignoreCase = true)
                }
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
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Black,
                        unselectedIconColor = Neutral500,
                        indicatorColor = Neutral100,
                    ),
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                    label = { Text("Search") },
                    selected = showSearch,
                    onClick = { showSearch = !showSearch; if (showSearch) selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Black,
                        unselectedIconColor = Neutral500,
                        indicatorColor = Neutral100,
                    ),
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.FilterList, contentDescription = "Sort") },
                    label = { Text("Sort") },
                    selected = showSortSheet,
                    onClick = { showSortSheet = true },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Black,
                        unselectedIconColor = Neutral500,
                        indicatorColor = Neutral100,
                    ),
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { onOpenSettings?.invoke() },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Black,
                        unselectedIconColor = Neutral500,
                        indicatorColor = Neutral100,
                    ),
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(innerPadding),
        ) {
            AnimatedVisibility(
                visible = showSearch,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search tracks or folders", color = Neutral400) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = Neutral500) },
                    trailingIcon = {
                        AnimatedVisibility(visible = query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
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

            when {
                videos.isEmpty() -> EmptyState(Modifier.fillMaxSize().weight(1f))
                filtered.isEmpty() && showSearch -> NoResultsState(Modifier.fillMaxSize().weight(1f), query)
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(filtered, key = { it.id }) { video ->
                        TrackCard(video = video, onClick = { onItemClick(video) })
                    }
                }
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
private fun TrackCard(video: VideoItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Neutral100),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(Neutral300, Neutral400),
                        )
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.LibraryMusic,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(28.dp),
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
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        getParentFolder(video.path),
                        style = MaterialTheme.typography.bodySmall.copy(color = Neutral600),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    Text("\u00B7", color = Neutral400, style = MaterialTheme.typography.bodySmall)
                    Text(
                        formatDuration(video.durationMs),
                        style = MaterialTheme.typography.bodySmall.copy(color = Neutral600),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(White),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.PlayArrow,
                    contentDescription = "Play",
                    tint = Neutral700,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
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

private fun formatDuration(ms: Long): String {
    if (ms <= 0) return "0:00"
    val totalSec = ms / 1000
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}

private fun getParentFolder(path: String): String {
    return try { File(path).parentFile?.name ?: "Unknown" } catch (_: Exception) { "Unknown" }
}
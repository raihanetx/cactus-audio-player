package com.cactus.app.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.cactus.app.CactusViewModel
import com.cactus.app.model.SortMode
import com.cactus.app.model.SubtitleFilter
import com.cactus.app.model.VideoItem
import com.cactus.app.model.formatDate
import com.cactus.app.model.formatDuration
import com.cactus.app.ui.theme.onSurfaceVariant
import com.cactus.app.ui.theme.primaryContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(navController: NavHostController, vm: CactusViewModel) {
    val videos by vm.videos.collectAsState()
    val sortMode by vm.sortMode.collectAsState()
    val search by vm.search.collectAsState()
    val subtitleFilter by vm.subtitleFilter.collectAsState()
    val showNotifications by vm.showNotifications.collectAsState()

    var searchActive by remember { mutableStateOf(false) }
    var durationAsc by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { _ -> vm.scanDevice() }
    LaunchedEffect(Unit) {
        val perms = if (android.os.Build.VERSION.SDK_INT >= 33) {
            listOf(
                android.Manifest.permission.READ_MEDIA_AUDIO,
                android.Manifest.permission.READ_MEDIA_VIDEO,
            )
        } else {
            listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        val needs = perms.any {
            androidx.core.content.ContextCompat.checkSelfPermission(context, it) !=
                android.content.pm.PackageManager.PERMISSION_GRANTED
        }
        if (needs) permissionLauncher.launch(perms.toTypedArray()) else vm.scanDevice()
    }

    // --- Search filter ---
    val searched = videos.filter {
        search.isBlank() ||
            it.titleEn.contains(search, ignoreCase = true) ||
            (it.titleBn?.contains(search, ignoreCase = true) ?: false) ||
            it.fileName.contains(search, ignoreCase = true)
    }

    // --- Subtitle filter ---
    val filtered = searched.filter { video ->
        when (subtitleFilter) {
            SubtitleFilter.All -> true
            SubtitleFilter.WithSubtitles -> vm.subtitlesFor(video.id).isNotEmpty()
            SubtitleFilter.WithoutSubtitles -> vm.subtitlesFor(video.id).isEmpty()
        }
    }

    // --- Sort ---
    val sorted = when (sortMode) {
        SortMode.Recent -> filtered.sortedByDescending { it.dateAddedMs }
        SortMode.Title -> filtered.sortedBy { it.titleEn.lowercase() }
        SortMode.Duration -> {
            if (durationAsc) filtered.sortedBy { it.durationMs }
            else filtered.sortedByDescending { it.durationMs }
        }
        SortMode.Language -> filtered.sortedBy { it.language }
        SortMode.RecentlyListened -> filtered.sortedByDescending { it.lastPlayedMs ?: 0L }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            // --- Library title row ---
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Library",
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = { searchActive = true }) {
                    Icon(Icons.Filled.Search, "Search", tint = MaterialTheme.colorScheme.onBackground)
                }
            }

            Spacer(Modifier.height(12.dp))

            // --- Google-style search card ---
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                onClick = { searchActive = true },
            ) {
                Row(
                    Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Filled.Search,
                        "Search",
                        tint = onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Search your library",
                        style = MaterialTheme.typography.bodyMedium,
                        color = onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        Icons.Filled.Mic,
                        "Voice search",
                        tint = onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // --- Filter chips (always visible, pinned) ---
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterPill(
                    text = "Recently Added",
                    selected = sortMode == SortMode.Recent,
                    onClick = { durationAsc = false; vm.setSort(SortMode.Recent) },
                )
                FilterPill(
                    text = if (durationAsc) "Duration ↑" else "Duration ↓",
                    selected = sortMode == SortMode.Duration,
                    onClick = { durationAsc = !durationAsc; vm.setSort(SortMode.Duration) },
                )
                FilterPill(
                    text = subtitleFilter.label,
                    selected = subtitleFilter != SubtitleFilter.All,
                    onClick = {
                        vm.setSubtitleFilter(
                            when (subtitleFilter) {
                                SubtitleFilter.All -> SubtitleFilter.WithSubtitles
                                SubtitleFilter.WithSubtitles -> SubtitleFilter.WithoutSubtitles
                                SubtitleFilter.WithoutSubtitles -> SubtitleFilter.All
                            }
                        )
                    },
                )
                FilterPill(
                    text = "Recently Listened",
                    selected = sortMode == SortMode.RecentlyListened,
                    onClick = { durationAsc = false; vm.setSort(SortMode.RecentlyListened) },
                )
            }

            Spacer(Modifier.height(8.dp))

            // --- Video list ---
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (sorted.isEmpty()) {
                    item {
                        Box(
                            Modifier.fillMaxWidth().padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "No videos match your filters",
                                style = MaterialTheme.typography.bodyMedium,
                                color = onSurfaceVariant,
                            )
                        }
                    }
                }
                items(sorted, key = { it.id }) { video ->
                    VideoRow(
                        video = video,
                        onOpen = {
                            vm.playVideo(video)
                            navController.navigate("player/${video.id}")
                        },
                        onPlay = {
                            vm.playVideo(video)
                            navController.navigate("player/${video.id}")
                        },
                        onToggleFinished = { vm.toggleFinished(video.id) },
                        onAddToUpNext = { vm.addToUpNext(video) },
                    )
                }
            }
        }

        // --- Search overlay ---
        if (searchActive) {
            SearchOverlay(
                query = search,
                onQueryChange = vm::setSearch,
                onDismiss = {
                    searchActive = false
                    vm.setSearch("")
                },
            )
        }

        // --- Notifications bottom sheet ---
        if (showNotifications) {
            val notifications by vm.notifications.collectAsState()
            NotificationsSheet(
                notifications = notifications,
                onDismiss = { vm.dismissNotifications() },
                onMarkRead = vm::markNotificationRead,
                onMarkAll = vm::markAllNotificationsRead,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VideoRow(
    video: VideoItem,
    onOpen: () -> Unit,
    onPlay: () -> Unit,
    onToggleFinished: () -> Unit,
    onAddToUpNext: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> onToggleFinished()
                SwipeToDismissBoxValue.StartToEnd -> onAddToUpNext()
                else -> return@rememberSwipeToDismissBoxState false
            }
            true
        },
    )
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val dir = dismissState.dismissDirection
            val isEndToStart = dir == SwipeToDismissBoxValue.EndToStart
            val color = if (isEndToStart)
                MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondary
            val icon = if (isEndToStart)
                Icons.Filled.CheckCircle else Icons.AutoMirrored.Filled.QueueMusic
            val label = if (isEndToStart) "Finished" else "Up Next"
            Row(
                Modifier.fillMaxSize().background(color).padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (isEndToStart) Arrangement.End else Arrangement.Start,
            ) {
                if (!isEndToStart) {
                    Icon(icon, label, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(Modifier.width(8.dp))
                    Text(label, color = MaterialTheme.colorScheme.onPrimaryContainer, style = MaterialTheme.typography.labelMedium)
                } else {
                    Text(label, color = MaterialTheme.colorScheme.onPrimaryContainer, style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.width(8.dp))
                    Icon(icon, label, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        },
    ) {
        Surface(
            tonalElevation = 0.dp,
            shape = RoundedCornerShape(18.dp),
            color = Color.Transparent,
            modifier = Modifier.fillMaxWidth().clickable(onClick = onOpen),
        ) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(56.dp).clip(RoundedCornerShape(12.dp))
                        .background(Brush.verticalGradient(0.0f to video.gradientStart, 1.0f to video.gradientEnd)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.PlayArrow,
                        "Artwork",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.size(28.dp),
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        video.titleEn,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "${formatDuration(video.durationMs)}  •  ${formatDate(video.dateAddedMs)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = onSurfaceVariant,
                        )
                        if (video.isFinished) {
                            Spacer(Modifier.width(6.dp))
                            Icon(Icons.Filled.CheckCircle, "Finished", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                        }
                    }
                }
                Box(
                    Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary)
                        .clickable(onClick = onPlay),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.PlayArrow, "Play", tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationsSheet(
    notifications: List<com.cactus.app.model.AppNotification>,
    onDismiss: () -> Unit,
    onMarkRead: (String) -> Unit,
    onMarkAll: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Notifications", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
                TextButton(onClick = onMarkAll) { Text("Mark all read", color = MaterialTheme.colorScheme.primary) }
            }
            Spacer(Modifier.height(8.dp))
            if (notifications.isEmpty()) {
                Text("You're all caught up.", color = onSurfaceVariant, modifier = Modifier.padding(vertical = 16.dp))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(notifications) { n ->
                        Surface(
                            tonalElevation = 1.dp,
                            shape = RoundedCornerShape(14.dp),
                            color = if (n.read) MaterialTheme.colorScheme.surfaceVariant else primaryContainer,
                            modifier = Modifier.fillMaxWidth().clickable { onMarkRead(n.id) },
                        ) {
                            Column(Modifier.padding(14.dp)) {
                                Text(n.title, style = MaterialTheme.typography.labelLarge)
                                Spacer(Modifier.height(2.dp))
                                Text(n.body, style = MaterialTheme.typography.bodySmall, color = onSurfaceVariant)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SearchOverlay(
    query: String,
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    Box(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface).padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
                TextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = { Text("Search your library", color = onSurfaceVariant) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                    ),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/* Simple pill-shaped filter chip — uses basic Box + Text for reliability. */
@Composable
private fun FilterPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val bg = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bg,
        onClick = onClick,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = fg,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

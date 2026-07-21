package com.cactus.app.ui.githubui // adapted package

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable as foundationClickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- Colors ---
val Primary = Color(0xFF6FCF87)
val OnPrimary = Color(0xFF00391B)
val PrimaryContainer = Color(0xFF1B5E2E)
val Secondary = Color(0xFFF0B86C)
val Tertiary = Color(0xFFD99CBE)
val SurfaceColor = Color(0xFF1A1A14)
val SurfaceContainer = Color(0xFF25251E)
val Background = Color(0xFF121210)
val TextWhite = Color.White
val TextGray = Color(0xFFB3A89B)

// --- Data Models ---
data class VideoItem(val title: String, val duration: String, val date: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CactusApp() {
    var isPlayerVisible by remember { mutableStateOf(false) }
    var currentTab by remember { mutableStateOf(0) } // 0:Player, 1:Sub, 2:Loop, 3:Note
    var showAddLoop by remember { mutableStateOf(false) }
    var showAddNote by remember { mutableStateOf(false) }
    var showActionSheet by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(true) }
    var progress by remember { mutableStateOf(0.33f) }

    val videos = listOf(
        VideoItem("TED Talk - AI Future", "15:30", "Today"),
        VideoItem("BBC Podcast - Global News", "42:10", "Yesterday"),
        VideoItem("Movie - Inception (Audio)", "2:28:15", "2 days ago"),
        VideoItem("YouTube - Coding Interviews", "22:05", "Last week")
    )

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        // Main Screen Content
        Scaffold(
            containerColor = Background,
            bottomBar = { MainBottomNav() }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                LibraryScreen(videos = videos, onItemClick = { isPlayerVisible = true })

                // Mini Player
                MiniPlayer(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp),
                    onClick = { isPlayerVisible = true }
                )
            }
        }

        // Player Screen Overlay
        AnimatedVisibility(
            visible = isPlayerVisible,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            PlayerScreen(
                currentTab = currentTab,
                onTabChange = { currentTab = it },
                onBack = { isPlayerVisible = false },
                onHeaderPlus = {
                    when (currentTab) {
                        2 -> showAddLoop = !showAddLoop
                        3 -> showAddNote = !showAddNote
                    }
                },
                showAddLoop = showAddLoop,
                showAddNote = showAddNote,
                onLongPress = { showActionSheet = true },
                isPlaying = isPlaying,
                onPlayPause = { isPlaying = !isPlaying },
                progress = progress,
                onProgressChange = { progress = it }
            )
        }

        // Long Press Action Bottom Sheet
        if (showActionSheet) {
            ModalBottomSheet(
                onDismissRequest = { showActionSheet = false },
                sheetState = sheetState,
                containerColor = SurfaceContainer
            ) {
                Column(modifier = Modifier.padding(16.dp).padding(bottom = 24.dp)) {
                    ActionItem(icon = Icons.Filled.Edit, title = "Edit", color = TextWhite) {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showActionSheet = false
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    ActionItem(icon = Icons.Filled.Delete, title = "Delete", color = Color(0xFFEF4444)) {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showActionSheet = false
                        }
                    }
                }
            }
        }
    }
}

// --- UI Components ---

@Composable
fun LibraryScreen(videos: List<VideoItem>, onItemClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 48.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Library", color = TextWhite, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Row {
                IconButton(onClick = {}) {
                    BadgedBox(badge = { Badge { Text("3") } }) {
                        Icon(Icons.Filled.Notifications, "Notif", tint = TextGray)
                    }
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.Search, "Search", tint = TextGray)
                }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(videos) { video ->
                VideoRow(video = video, onClick = onItemClick)
            }
        }
    }
}

@Composable
fun VideoRow(video: VideoItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).noRippleClickable(onClick = onClick).padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)).background(PrimaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.MusicNote, null, tint = Primary.copy(alpha = 0.6f))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(video.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${video.duration} • ${video.date}", color = TextGray, fontSize = 12.sp)
        }
        IconButton(onClick = onClick) {
            Icon(Icons.Filled.PlayCircleFilled, "Play", tint = Primary.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun MiniPlayer(modifier: Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(SurfaceContainer.copy(alpha = 0.9f)).noRippleClickable(onClick = onClick).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Primary), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.MusicNote, null, tint = OnPrimary, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text("TED Talk - AI Future", color = TextWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text("TED Talks • 15:30", color = TextGray, fontSize = 12.sp)
        }
    }
}

@Composable
fun MainBottomNav() {
    NavigationBar(containerColor = SurfaceColor) {
        val items = listOf("Library" to Icons.Filled.LibraryMusic, "Playlists" to Icons.Filled.List, "Playing" to Icons.Filled.PlayCircle, "Settings" to Icons.Filled.Settings)
        items.forEachIndexed { index, (label, icon) ->
            NavigationBarItem(
                selected = index == 0,
                onClick = {},
                icon = { Icon(icon, label) },
                label = { Text(label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = Primary, selectedTextColor = Primary, unselectedIconColor = TextGray, unselectedTextColor = TextGray)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    currentTab: Int,
    onTabChange: (Int) -> Unit,
    onBack: () -> Unit,
    onHeaderPlus: () -> Unit,
    showAddLoop: Boolean,
    showAddNote: Boolean,
    onLongPress: () -> Unit,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    progress: Float,
    onProgressChange: (Float) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(SurfaceColor)) {
        // Background Glow
        Box(modifier = Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Primary.copy(alpha = 0.2f), Background), center = Offset(0.5f, 0.3f), radius = 1000f)))

        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            // Header
            Row(modifier = Modifier.fillMaxWidth().padding(top = 32.dp, bottom = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = TextWhite) }
                Text("Playing From Library", color = TextGray, fontSize = 14.sp)
                IconButton(onClick = onHeaderPlus) { Icon(Icons.Filled.Add, "Add", tint = TextWhite) }
            }

            // Content Area
            Box(modifier = Modifier.weight(1f)) {
                when (currentTab) {
                    0 -> PlayerTab(isPlaying = isPlaying, onPlayPause = onPlayPause, progress = progress, onProgressChange = onProgressChange)
                    1 -> SubtitlesTab()
                    2 -> LoopsTab(showAddLoop = showAddLoop, onLongPress = onLongPress)
                    3 -> NotesTab(showAddNote = showAddNote, onLongPress = onLongPress)
                }
            }

            // Bottom Action Nav
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(SurfaceContainer).padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceAround) {
                val tabs = listOf("Player" to Icons.Filled.PlayArrow, "Subtitles" to Icons.Filled.Subtitles, "Loops" to Icons.Filled.Repeat, "Notes" to Icons.Filled.Notes)
                tabs.forEachIndexed { index, (label, icon) ->
                    Column(
                        modifier = Modifier.noRippleClickable { onTabChange(index) }.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(icon, label, tint = if (currentTab == index) Primary else TextGray)
                        Text(label, color = if (currentTab == index) Primary else TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerTab(isPlaying: Boolean, onPlayPause: () -> Unit, progress: Float, onProgressChange: (Float) -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        // Music Box
        Box(
            modifier = Modifier.size(200.dp).clip(RoundedCornerShape(24.dp)).background(PrimaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.MusicNote, null, tint = Primary, modifier = Modifier.size(80.dp))
        }
        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text("TED Talk - AI Future", color = TextWhite, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)

        // Secondary Line
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = PrimaryContainer, shape = RoundedCornerShape(50)) {
                Text("ENGLISH", color = Primary, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("audio_file.mp4", color = TextGray, fontSize = 12.sp)
        }

        // Clean Timeline
        Slider(
            value = progress,
            onValueChange = onProgressChange,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = TextWhite,
                activeTrackColor = Primary,
                inactiveTrackColor = TextWhite.copy(alpha = 0.1f)
            )
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("05:15", color = TextGray, fontSize = 11.sp)
            Text("15:30", color = TextGray, fontSize = 11.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Controls
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Replay10, "Back 10s", tint = TextWhite, modifier = Modifier.size(32.dp))
            }
            IconButton(onClick = onPlayPause, modifier = Modifier.size(80.dp).clip(CircleShape).background(Primary)) {
                Icon(if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow, "Play", tint = OnPrimary, modifier = Modifier.size(40.dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Forward10, "Forward 10s", tint = TextWhite, modifier = Modifier.size(32.dp))
            }
        }
    }
}

@Composable
fun SubtitlesTab() {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        items(listOf("0:45 - 1:02", "0:20 - 0:45", "0:05 - 0:20")) { time ->
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(time, color = Primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(modifier = Modifier.height(1.dp).fillMaxWidth().background(TextGray.copy(alpha = 0.2f)))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("AI is transforming the future.", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text("এই আই ইজ ট্রান্সফর্মিং দ্য ফিউচার", color = TextGray, fontSize = 14.sp)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoopsTab(showAddLoop: Boolean, onLongPress: () -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        if (showAddLoop) {
            item {
                Surface(color = SurfaceColor, shape = RoundedCornerShape(16.dp), modifier = Modifier.padding(bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Set Start & End Time", color = TextGray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            OutlinedTextField(value = "00:00", onValueChange = {}, label = { Text("Start") }, modifier = Modifier.weight(1f), colors = TextFieldDefaults.colors(focusedContainerColor = SurfaceContainer, unfocusedContainerColor = SurfaceContainer))
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(value = "00:00", onValueChange = {}, label = { Text("End") }, modifier = Modifier.weight(1f), colors = TextFieldDefaults.colors(focusedContainerColor = SurfaceContainer, unfocusedContainerColor = SurfaceContainer))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {}, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                            Text("Save Loop", color = OnPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        item { Text("Saved Loops", color = TextGray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp)) }
        item {
            Surface(
                color = SurfaceColor, shape = RoundedCornerShape(16.dp),
                modifier = Modifier.combinedClickable(onClick = {}, onLongClick = onLongPress)
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Intro Concept", color = TextWhite, fontWeight = FontWeight.SemiBold)
                        Text("02:10 - 02:35", color = Primary, fontSize = 12.sp)
                    }
                    IconButton(onClick = {}) { Icon(Icons.Filled.PlayCircleFilled, null, tint = TextGray) }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesTab(showAddNote: Boolean, onLongPress: () -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        if (showAddNote) {
            item {
                Surface(color = SurfaceColor, shape = RoundedCornerShape(16.dp), modifier = Modifier.padding(bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = PrimaryContainer, shape = RoundedCornerShape(4.dp)) {
                                Text("00:00", color = Primary, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Current position", color = TextGray, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = "", onValueChange = {}, placeholder = { Text("Write a note...") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Button(onClick = {}, shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                                Text("Save Note", color = OnPrimary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
        item { Text("Saved Notes", color = TextGray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp)) }
        item {
            Surface(
                color = SurfaceColor, shape = RoundedCornerShape(16.dp),
                modifier = Modifier.combinedClickable(onClick = {}, onLongClick = onLongPress)
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Interesting point about neural networks", color = TextWhite, fontWeight = FontWeight.SemiBold)
                        Text("02:45", color = Primary, fontSize = 12.sp)
                    }
                    IconButton(onClick = {}) { Icon(Icons.Filled.Notes, null, tint = TextGray) }
                }
            }
        }
    }
}

@Composable
fun ActionItem(icon: ImageVector, title: String, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().noRippleClickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(SurfaceColor), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = color)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, color = color, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}

// Helper for clickable without ripple
@Composable
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return this.then(
        foundationClickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    )
}

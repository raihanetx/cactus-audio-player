package com.cactus.app.ui.player

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cactus.app.model.Loop
import com.cactus.app.model.Note
import com.cactus.app.model.SampleData
import com.cactus.app.model.SubtitleLine
import com.cactus.app.model.Track
import com.cactus.app.ui.theme.Black
import com.cactus.app.ui.theme.Blue500
import com.cactus.app.ui.theme.Neutral100
import com.cactus.app.ui.theme.Neutral200
import com.cactus.app.ui.theme.Neutral300
import com.cactus.app.ui.theme.Neutral400
import com.cactus.app.ui.theme.Neutral500
import com.cactus.app.ui.theme.Neutral600
import com.cactus.app.ui.theme.Neutral700
import com.cactus.app.ui.theme.Neutral800
import com.cactus.app.ui.theme.Neutral900
import com.cactus.app.ui.theme.White

private val Neutral50 = Color(0xFFFAFAFA)

private val tabLabels = listOf("Dialogue", "Loops", "Notes")

@Composable
fun PlayerScreen(modifier: Modifier = Modifier) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val activeTrack = SampleData.tracks.find { it.id == 1 } ?: SampleData.tracks.first()

    Column(modifier = modifier.fillMaxSize().background(White)) {
        NowPlayingHeader(track = activeTrack)

        TabRow(selectedTab = selectedTab, onTabSelected = { selectedTab = it })

        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> SubtitlesContent(subtitles = SampleData.subtitles)
                1 -> LoopsContent(loops = SampleData.loops)
                2 -> NotesContent(notes = SampleData.notes)
            }
        }

        AudioDock()

        PageIndicator(selectedTab = selectedTab, tabCount = 3)
    }
}

@Composable
private fun NowPlayingHeader(track: Track) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.size(36.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "NOW PLAYING",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Neutral400,
                    letterSpacing = 1.sp,
                )
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).clickable { },
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "\u2022\u2022\u2022", color = Neutral700, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).background(Black),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = track.initials, color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    fontWeight = FontWeight.SemiBold,
                    color = Black,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = track.subtitle,
                    color = Neutral500,
                    fontSize = 13.sp,
                )
            }
        }
    }
}

@Composable
private fun TabRow(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        tabLabels.forEachIndexed { index, label ->
            val isActive = index == selectedTab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(100))
                    .background(if (isActive) Black else Neutral100)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (isActive) White else Neutral600,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 11.sp,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun SubtitlesContent(subtitles: List<SubtitleLine>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        if (subtitles.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(64.dp).clip(CircleShape).background(Neutral100),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("\uD83C\uDF10", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("No translation available", fontWeight = FontWeight.Bold, color = Neutral800)
                    Text(
                        "Tap the \"Translate\" button below to generate English, Pronunciation & Bangla subtitles.",
                        color = Neutral500,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp).padding(top = 6.dp),
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "DIALOGUE LINES",
                color = Neutral400,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            )
            subtitles.forEach { line ->
                var isActive by remember { mutableStateOf(false) }
                val textColor by animateColorAsState(
                    targetValue = if (isActive) Neutral900 else Neutral400,
                    animationSpec = tween(150),
                    label = "textColor",
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { isActive = !isActive }
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                ) {
                    Text(
                        text = "[${line.start} - ${line.end}] ${line.english}",
                        color = textColor,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                    )
                    if (line.pronunciation.isNotEmpty()) {
                        val pronColor = if (isActive) Color(0xFF1D4ED8) else Blue500.copy(alpha = 0.6f)
                        Text(
                            text = "-- ${line.pronunciation} --",
                            color = pronColor,
                            fontWeight = FontWeight.Medium,
                            fontStyle = if (isActive) FontStyle.Normal else FontStyle.Italic,
                            fontSize = 14.sp,
                        )
                    }
                    if (line.bangla.isNotEmpty()) {
                        Text(text = line.bangla, color = textColor, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun LoopsContent(loops: List<Loop>) {
    var showModal by remember { mutableStateOf(false) }
    var loopName by remember { mutableStateOf("") }
    var loopStart by remember { mutableStateOf("00:12") }
    var loopEnd by remember { mutableStateOf("00:16") }
    var loopCount by remember { mutableStateOf("10") }
    var localLoops by remember { mutableStateOf(loops) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "SAVED LOOPS",
                color = Neutral400,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100))
                    .background(Neutral100)
                    .clickable { showModal = true }
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            ) {
                Text(text = "+ Add Loop", color = Neutral700, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
            }
        }

        localLoops.forEach { loop ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Neutral100),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, tint = Neutral600, modifier = Modifier.size(16.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = loop.name,
                        fontWeight = FontWeight.SemiBold,
                        color = Neutral900,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(loop.startA, color = Neutral500, fontSize = 12.sp)
                        Box(modifier = Modifier.width(12.dp).height(1.dp).background(Neutral300))
                        Text(loop.endB, color = Neutral500, fontSize = 12.sp)
                        Text("\u00B7", color = Neutral300)
                        Text("${loop.count} Loops", color = Neutral500, fontSize = 12.sp)
                    }
                }
                Text("\u22EE", color = Neutral400, fontSize = 16.sp)
            }
        }
    }

    if (showModal) {
        CreateLoopModal(
            name = loopName,
            onNameChange = { loopName = it },
            start = loopStart,
            onStartChange = { loopStart = it },
            end = loopEnd,
            onEndChange = { loopEnd = it },
            count = loopCount,
            onCountChange = { loopCount = it },
            onSave = {
                localLoops = listOf(
                    Loop(
                        name = loopName.ifEmpty { "Untitled Loop" },
                        startA = loopStart,
                        endB = loopEnd,
                        count = loopCount.toIntOrNull() ?: 1,
                    )
                ) + localLoops
                showModal = false
                loopName = ""
            },
            onDismiss = { showModal = false },
        )
    }
}

@Composable
private fun CreateLoopModal(
    name: String,
    onNameChange: (String) -> Unit,
    start: String,
    onStartChange: (String) -> Unit,
    end: String,
    onEndChange: (String) -> Unit,
    count: String,
    onCountChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Black.copy(alpha = 0.4f)).clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.9f).clip(RoundedCornerShape(16.dp)).background(White).clickable(enabled = false) { }.padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Create Custom Loop", fontWeight = FontWeight.Bold, color = Black, fontSize = 18.sp)
                Text("\u2715", color = Neutral400, modifier = Modifier.clickable(onClick = onDismiss))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Neutral50).padding(12.dp),
            ) {
                Text("LIVE PREVIEW", color = Neutral400, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(White), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Neutral600, modifier = Modifier.size(16.dp))
                    }
                    Column {
                        Text(name.ifEmpty { "Untitled Loop" }, fontWeight = FontWeight.Bold, color = Black, fontSize = 14.sp)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(start, color = Neutral500, fontSize = 12.sp)
                            Box(Modifier.width(12.dp).height(1.dp).background(Neutral300))
                            Text(end, color = Neutral500, fontSize = 12.sp)
                            Text("\u00B7", color = Neutral300)
                            Text("${count} Loops", color = Neutral500, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column {
                Text("LOOP NAME", color = Neutral500, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                TextField(
                    value = name, onValueChange = onNameChange,
                    placeholder = { Text("e.g., Hard Sentence 1", fontSize = 14.sp, color = Neutral400) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(unfocusedContainerColor = Neutral100, focusedContainerColor = White, unfocusedIndicatorColor = Neutral200, focusedIndicatorColor = Black),
                    singleLine = true,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("START (A)", color = Neutral500, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.sp)
                    Spacer(Modifier.height(4.dp))
                    TextField(value = start, onValueChange = onStartChange, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(unfocusedContainerColor = Neutral100, focusedContainerColor = White, unfocusedIndicatorColor = Neutral200, focusedIndicatorColor = Black),
                        singleLine = true)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("END (B)", color = Neutral500, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.sp)
                    Spacer(Modifier.height(4.dp))
                    TextField(value = end, onValueChange = onEndChange, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(unfocusedContainerColor = Neutral100, focusedContainerColor = White, unfocusedIndicatorColor = Neutral200, focusedIndicatorColor = Black),
                        singleLine = true)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column {
                Text("LOOP COUNT", color = Neutral500, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.sp)
                Spacer(Modifier.height(4.dp))
                TextField(value = count, onValueChange = onCountChange, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(unfocusedContainerColor = Neutral100, focusedContainerColor = White, unfocusedIndicatorColor = Neutral200, focusedIndicatorColor = Black),
                    singleLine = true)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Neutral100).clickable(onClick = onDismiss).padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                    Text("Cancel", color = Neutral700, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Black).clickable(onClick = onSave).padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                    Text("Save Loop", color = White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun NotesContent(notes: List<Note>) {
    var showModal by remember { mutableStateOf(false) }
    var noteText by remember { mutableStateOf("") }
    var localNotes by remember { mutableStateOf(notes) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "SAVED NOTES",
                color = Neutral400,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100))
                    .background(Neutral100)
                    .clickable { showModal = true }
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            ) {
                Text(text = "+ Add Note", color = Neutral700, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
            }
        }

        localNotes.forEach { note ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { }
                    .padding(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text("\uD83D\uDCCB", fontSize = 12.sp, color = Neutral400)
                        Text(
                            text = "${note.dateLabel} \u00B7 ${note.wordCount} Words",
                            color = Neutral400,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp,
                        )
                    }
                    Text("\u22EE", color = Neutral400, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = note.text,
                    color = Neutral800,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }

    if (showModal) {
        AddNoteModal(
            text = noteText,
            onTextChange = { noteText = it },
            onSave = {
                val words = noteText.trim().split("\\s+".toRegex()).size
                localNotes = listOf(
                    Note(
                        text = noteText,
                        wordCount = words,
                        dateLabel = "Today",
                    )
                ) + localNotes
                showModal = false
                noteText = ""
            },
            onDismiss = { showModal = false },
        )
    }
}

@Composable
private fun AddNoteModal(text: String, onTextChange: (String) -> Unit, onSave: () -> Unit, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Black.copy(alpha = 0.4f)).clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.9f).clip(RoundedCornerShape(16.dp)).background(White).clickable(enabled = false) { }.padding(20.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Add Note", fontWeight = FontWeight.Bold, color = Black, fontSize = 18.sp)
                Text("\u2715", color = Neutral400, modifier = Modifier.clickable(onClick = onDismiss))
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = text, onValueChange = onTextChange,
                placeholder = { Text("Write your note here...", color = Neutral400) },
                modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(unfocusedContainerColor = Neutral100, focusedContainerColor = White, unfocusedIndicatorColor = Neutral200, focusedIndicatorColor = Black),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Neutral100).clickable(onClick = onDismiss).padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                    Text("Cancel", color = Neutral700, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Black).clickable(onClick = onSave).padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                    Text("Save Note", color = White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun AudioDock() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("00:12", color = Neutral500, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Neutral200)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.25f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Black)
                )
            }
            Text("01:15", color = Neutral500, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "-15s", color = Neutral700, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.clickable { })
            Spacer(modifier = Modifier.width(24.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100))
                    .background(Black)
                    .clickable { }
                    .padding(horizontal = 24.dp, vertical = 10.dp),
            ) {
                Text(text = "Play", color = White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(24.dp))
            Text(text = "+15s", color = Neutral700, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.clickable { })
        }
    }
}

@Composable
private fun PageIndicator(selectedTab: Int, tabCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(tabCount) { index ->
            val isActive = index == selectedTab
            val width = if (isActive) 20.dp else 6.dp
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .width(width)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (isActive) Black else Neutral300)
            )
        }
    }
}

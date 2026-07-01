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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.Brush
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

@Composable
fun PlayerScreen(modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val track = SampleData.tracks.find { it.id == 1 } ?: SampleData.tracks.first()

    Column(modifier = modifier.fillMaxSize().background(White)) {

        PlayerHeader(currentPage = pagerState.currentPage, track = track)

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            when (page) {
                0 -> NowPlayingPage(track = track)
                1 -> DialoguePage(subtitles = SampleData.subtitles)
                2 -> LoopsPage(loops = SampleData.loops)
                3 -> NotesPage(notes = SampleData.notes)
            }
        }

        if (pagerState.currentPage != 0) {
            AudioDock()
        }

        PageIndicator(currentPage = pagerState.currentPage, pageCount = 4)
    }
}

private val pageTitles = listOf(
    "Now Playing" to "Basic Greetings",
    "Dialogue" to "Bilingual Subtitles",
    "Custom Loops" to "A-B Timeframes",
    "My Notes" to "Timestamped Notes",
)

@Composable
private fun PlayerHeader(currentPage: Int, track: Track) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).clickable { }, contentAlignment = Alignment.Center) {
                Text("\uF0D7", color = Neutral700, fontSize = 16.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = pageTitles[currentPage].first.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Neutral400,
                    letterSpacing = 1.sp,
                )
                Text(
                    text = if (currentPage == 0) track.title else pageTitles[currentPage].second,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).clickable { }, contentAlignment = Alignment.Center) {
                Text("\u2022\u2022\u2022", color = Neutral700, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun NowPlayingPage(track: Track) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier.size(224.dp).clip(RoundedCornerShape(16.dp)).background(
                Brush.verticalGradient(listOf(Neutral900, Black))
            ),
            contentAlignment = Alignment.Center,
        ) {
            Text(track.initials, color = White.copy(alpha = 0.9f), fontSize = 72.sp, fontWeight = FontWeight.Black)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(track.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Black, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(4.dp))
        Text(track.subtitle, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Neutral600)
        Spacer(modifier = Modifier.height(24.dp))

        var progress by remember { mutableStateOf(0.16f) }

        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)).background(Neutral200).clickable { }
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(progress).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Blue500)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("0:12", color = Neutral500, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                Text("-1:03", color = Neutral500, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "-10s", color = Black, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.clickable { })
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100))
                    .background(Blue500)
                    .clickable { }
                    .padding(horizontal = 48.dp, vertical = 10.dp),
            ) {
                Text(text = "Play", color = White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Text(text = "+10s", color = Black, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.clickable { })
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DialoguePage(subtitles: List<SubtitleLine>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 4.dp),
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
private fun LoopsPage(loops: List<Loop>) {
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
            .padding(horizontal = 20.dp, vertical = 4.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text("SAVED LOOPS", color = Neutral400, fontWeight = FontWeight.Bold, fontSize = 10.sp,
                letterSpacing = 1.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp))
            Text("+", color = Neutral700, fontSize = 18.sp, fontWeight = FontWeight.Light,
                modifier = Modifier.clip(CircleShape).clickable { showModal = true }.padding(4.dp))
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
                    Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Neutral600, modifier = Modifier.size(16.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(loop.name, fontWeight = FontWeight.SemiBold, color = Neutral900, fontSize = 14.sp,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
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
        Box(
            modifier = Modifier.fillMaxSize().background(Black.copy(alpha = 0.4f)).clickable { showModal = false },
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.9f).clip(RoundedCornerShape(16.dp)).background(White)
                    .clickable(enabled = false) { }.padding(20.dp),
            ) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text("Create Custom Loop", fontWeight = FontWeight.Bold, color = Black, fontSize = 18.sp)
                    Text("\u2715", color = Neutral400, modifier = Modifier.clickable { showModal = false })
                }
                Spacer(Modifier.height(16.dp))
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Neutral50).padding(12.dp)) {
                    Text("LIVE PREVIEW", color = Neutral400, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(White), contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.PlayArrow, null, tint = Neutral600, modifier = Modifier.size(16.dp))
                        }
                        Column {
                            Text(loopName.ifEmpty { "Untitled Loop" }, fontWeight = FontWeight.Bold, color = Black, fontSize = 14.sp)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(loopStart, color = Neutral500, fontSize = 12.sp)
                                Box(Modifier.width(12.dp).height(1.dp).background(Neutral300))
                                Text(loopEnd, color = Neutral500, fontSize = 12.sp)
                                Text("\u00B7", color = Neutral300)
                                Text("${loopCount} Loops", color = Neutral500, fontSize = 12.sp)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Column {
                    Text("LOOP NAME", color = Neutral500, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.sp)
                    Spacer(Modifier.height(4.dp))
                    TextField(value = loopName, onValueChange = { loopName = it },
                        placeholder = { Text("e.g., Hard Sentence 1", fontSize = 14.sp, color = Neutral400) },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(unfocusedContainerColor = Neutral100, focusedContainerColor = White, unfocusedIndicatorColor = Neutral200, focusedIndicatorColor = Black),
                        singleLine = true)
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text("START (A)", color = Neutral500, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.sp)
                        Spacer(Modifier.height(4.dp))
                        TextField(value = loopStart, onValueChange = { loopStart = it }, modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.colors(unfocusedContainerColor = Neutral100, focusedContainerColor = White, unfocusedIndicatorColor = Neutral200, focusedIndicatorColor = Black),
                            singleLine = true)
                    }
                    Column(Modifier.weight(1f)) {
                        Text("END (B)", color = Neutral500, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.sp)
                        Spacer(Modifier.height(4.dp))
                        TextField(value = loopEnd, onValueChange = { loopEnd = it }, modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.colors(unfocusedContainerColor = Neutral100, focusedContainerColor = White, unfocusedIndicatorColor = Neutral200, focusedIndicatorColor = Black),
                            singleLine = true)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Column {
                    Text("LOOP COUNT", color = Neutral500, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.sp)
                    Spacer(Modifier.height(4.dp))
                    TextField(value = loopCount, onValueChange = { loopCount = it }, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(unfocusedContainerColor = Neutral100, focusedContainerColor = White, unfocusedIndicatorColor = Neutral200, focusedIndicatorColor = Black),
                        singleLine = true)
                }
                Spacer(Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Neutral100).clickable { showModal = false }.padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center) {
                        Text("Cancel", color = Neutral700, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Box(Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Black).clickable {
                        localLoops = listOf(Loop(name = loopName.ifEmpty { "Untitled Loop" }, startA = loopStart, endB = loopEnd, count = loopCount.toIntOrNull() ?: 1)) + localLoops
                        showModal = false; loopName = ""
                    }.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                        Text("Save Loop", color = White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun NotesPage(notes: List<Note>) {
    var showModal by remember { mutableStateOf(false) }
    var noteText by remember { mutableStateOf("") }
    var localNotes by remember { mutableStateOf(notes) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 4.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text("SAVED NOTES", color = Neutral400, fontWeight = FontWeight.Bold, fontSize = 10.sp,
                letterSpacing = 1.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp))
            Text("+", color = Neutral700, fontSize = 18.sp, fontWeight = FontWeight.Light,
                modifier = Modifier.clip(CircleShape).clickable { showModal = true }.padding(4.dp))
        }
        localNotes.forEach { note ->
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { }.padding(12.dp),
            ) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("\uD83D\uDCCB", fontSize = 12.sp, color = Neutral400)
                        Text("${note.dateLabel} \u00B7 ${note.wordCount} Words", color = Neutral400,
                            fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, letterSpacing = 0.5.sp)
                    }
                    Text("\u22EE", color = Neutral400, fontSize = 14.sp)
                }
                Spacer(Modifier.height(6.dp))
                Text(note.text, color = Neutral800, fontWeight = FontWeight.Medium, fontSize = 14.sp,
                    lineHeight = 22.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }

    if (showModal) {
        Box(
            modifier = Modifier.fillMaxSize().background(Black.copy(alpha = 0.4f)).clickable { showModal = false },
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.9f).clip(RoundedCornerShape(16.dp)).background(White)
                    .clickable(enabled = false) { }.padding(20.dp),
            ) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text("Add Note", fontWeight = FontWeight.Bold, color = Black, fontSize = 18.sp)
                    Text("\u2715", color = Neutral400, modifier = Modifier.clickable { showModal = false })
                }
                Spacer(Modifier.height(16.dp))
                TextField(value = noteText, onValueChange = { noteText = it },
                    placeholder = { Text("Write your note here...", color = Neutral400) },
                    modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(unfocusedContainerColor = Neutral100, focusedContainerColor = White, unfocusedIndicatorColor = Neutral200, focusedIndicatorColor = Black))
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Neutral100).clickable { showModal = false }.padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center) {
                        Text("Cancel", color = Neutral700, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Box(Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Black).clickable {
                        val words = noteText.trim().split("\\s+".toRegex()).size
                        localNotes = listOf(Note(text = noteText, wordCount = words, dateLabel = "Today")) + localNotes
                        showModal = false; noteText = ""
                    }.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                        Text("Save Note", color = White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AudioDock() {
    Column(
        modifier = Modifier.fillMaxWidth().background(White).padding(horizontal = 20.dp).padding(top = 12.dp, bottom = 4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("00:12", color = Neutral500, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Box(modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)).background(Neutral200)) {
                Box(modifier = Modifier.fillMaxWidth(0.25f).height(6.dp).clip(RoundedCornerShape(3.dp)).background(Black))
            }
            Text("01:15", color = Neutral500, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("-15s", color = Neutral700, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.clickable { })
            Spacer(modifier = Modifier.width(24.dp))
            Box(
                modifier = Modifier.clip(RoundedCornerShape(100)).background(Black).clickable { }
                    .padding(horizontal = 24.dp, vertical = 10.dp),
            ) {
                Text("Play", color = White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(24.dp))
            Text("+15s", color = Neutral700, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.clickable { })
        }
    }
}

@Composable
private fun PageIndicator(currentPage: Int, pageCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val isActive = index == currentPage
            val w = if (isActive) 16.dp else 6.dp
            Box(
                modifier = Modifier.padding(horizontal = 3.dp).width(w).height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (isActive) Black else Neutral300)
            )
        }
    }
}

package com.cactus.app.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cactus.app.model.VideoItem
import com.cactus.app.ui.theme.Black
import com.cactus.app.ui.theme.Neutral100
import com.cactus.app.ui.theme.Neutral200
import com.cactus.app.ui.theme.Neutral300
import com.cactus.app.ui.theme.Neutral400
import com.cactus.app.ui.theme.Neutral500
import com.cactus.app.ui.theme.Neutral600
import com.cactus.app.ui.theme.Neutral700
import com.cactus.app.ui.theme.Neutral800
import com.cactus.app.ui.theme.Neutral900
import com.cactus.app.ui.theme.Red500
import com.cactus.app.ui.theme.White

private data class NoteItem(
    val text: String,
    val whenLabel: String,
    val wordCount: Int,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotePage(
    player: PlayerState,
    video: VideoItem,
    onBack: () -> Unit,
    onSelectPage: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var notes by remember {
        mutableStateOf(
            listOf(
                NoteItem("Remember to practice the intonation for \"How are you?\" and try to stress the word \"how\" slightly more than the others in this sentence.", "Today", 14),
                NoteItem("The word \"fine\" is stressed here.", "Yesterday", 5),
            ),
        )
    }
    var showDialog by remember { mutableStateOf(false) }
    var editingIndex by remember { mutableStateOf(-1) }
    var menuIndex by remember { mutableStateOf(-1) }
    var expandedIndex by remember { mutableStateOf(-1) }

    val openDialog = { index: Int ->
        editingIndex = index
        showDialog = true
    }

    Column(modifier = modifier.fillMaxSize().background(White)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Back", tint = Neutral700, modifier = Modifier.size(20.dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("MY NOTES", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Neutral400, letterSpacing = 1.sp)
                Text("Timestamped Notes", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Black, maxLines = 1)
            }
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).clickable { openDialog(-1) },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add note", tint = Neutral700, modifier = Modifier.size(20.dp))
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().weight(1f).padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Text("Saved Notes", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Neutral400, letterSpacing = 1.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp))
            }
            items(notes.size) { index ->
                val note = notes[index]
                val expanded = expandedIndex == index
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Neutral100, RoundedCornerShape(12.dp))
                        .clickable { expandedIndex = if (expanded) -1 else index }
                        .padding(12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Filled.Note, contentDescription = null, tint = Neutral400, modifier = Modifier.size(12.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("${note.whenLabel} · ${note.wordCount} Words", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Neutral400, letterSpacing = 1.sp)
                        }
                        Box {
                            Box(
                                modifier = Modifier.size(24.dp).clip(CircleShape).clickable { menuIndex = index },
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Filled.MoreVert, contentDescription = "Menu", tint = Neutral400, modifier = Modifier.size(14.dp))
                            }
                            DropdownMenu(expanded = menuIndex == index, onDismissRequest = { menuIndex = -1 }) {
                                DropdownMenuItem(text = { Text("Edit") }, onClick = { menuIndex = -1; openDialog(index) })
                                DropdownMenuItem(text = { Text("Delete", color = Red500) }, onClick = {
                                    notes = notes.toMutableList().also { it.removeAt(index) }
                                    menuIndex = -1
                                })
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        note.text,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Neutral800,
                        lineHeight = 20.sp,
                        maxLines = if (expanded) Int.MAX_VALUE else 2,
                    )
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().background(White).padding(horizontal = 20.dp).padding(top = 16.dp, bottom = 8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(formatTime(player.currentPosition), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Neutral500, modifier = Modifier.width(32.dp))
                Slider(
                    value = if (player.dragging) player.sliderPos else player.currentPosition.toFloat().coerceIn(0f, player.duration.toFloat().coerceAtLeast(1f)),
                    onValueChange = { player.sliderPos = it; player.dragging = true; player.currentPosition = it.toLong() },
                    onValueChangeFinished = { player.seekTo(player.sliderPos.toLong()); player.dragging = false },
                    valueRange = 0f..player.duration.toFloat().coerceAtLeast(1f),
                    colors = SliderDefaults.colors(thumbColor = Black, activeTrackColor = Black, inactiveTrackColor = Neutral200),
                    modifier = Modifier.weight(1f).height(24.dp),
                )
                Text(formatTime(player.duration), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Neutral500, modifier = Modifier.width(32.dp), textAlign = TextAlign.End)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("-15s", color = Neutral700, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.clickable { player.skip(-15) })
                Spacer(Modifier.width(24.dp))
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(100)).background(if (player.isPrepared) Black else Neutral200).clickable(enabled = player.isPrepared) { player.toggle() }.padding(horizontal = 28.dp, vertical = 10.dp),
                ) {
                    Text(if (player.isPlaying) "Pause" else "Play", color = White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(Modifier.width(24.dp))
                Text("+15s", color = Neutral700, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.clickable { player.skip(15) })
            }
            Spacer(Modifier.height(8.dp))
            PageDots(active = 3, onSelect = onSelectPage)
            Spacer(Modifier.height(8.dp))
        }
    }

    if (showDialog) {
        val editing = if (editingIndex >= 0) notes[editingIndex] else null
        var text by remember(showDialog, editingIndex) { mutableStateOf(editing?.text ?: "") }

        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (editing != null) "Edit Note" else "Add Note", fontWeight = FontWeight.Bold, color = Black) },
            text = {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Note") },
                    placeholder = { Text("Write your note here...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    singleLine = false,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val trimmed = text.trim()
                    if (trimmed.isNotEmpty()) {
                        val words = trimmed.split(Regex("\\s+")).size
                        val newItem = NoteItem(trimmed, "Today", words)
                        notes = if (editingIndex >= 0) notes.toMutableList().also { it[editingIndex] = newItem } else listOf(newItem) + notes
                    }
                    showDialog = false
                }) { Text("Save", color = Black, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel", color = Neutral500) }
            },
        )
    }
}

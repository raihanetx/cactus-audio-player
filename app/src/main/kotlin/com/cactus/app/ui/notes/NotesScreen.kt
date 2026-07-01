package com.cactus.app.ui.notes

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cactus.app.model.Note
import com.cactus.app.model.SampleData
import com.cactus.app.ui.theme.Black
import com.cactus.app.ui.theme.Neutral100
import com.cactus.app.ui.theme.Neutral200
import com.cactus.app.ui.theme.Neutral400
import com.cactus.app.ui.theme.Neutral500
import com.cactus.app.ui.theme.Neutral700
import com.cactus.app.ui.theme.Neutral800
import com.cactus.app.ui.theme.White

@Composable
fun NotesScreen(modifier: Modifier = Modifier) {
    var showModal by remember { mutableStateOf(false) }
    var noteText by remember { mutableStateOf("") }
    var localNotes by remember { mutableStateOf(SampleData.notes) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "MY NOTES",
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Neutral400,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Text(
            text = "Timestamped Notes",
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp),
        )

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
        Box(
            modifier = Modifier.fillMaxSize().background(Black.copy(alpha = 0.4f)).clickable { showModal = false },
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(White)
                    .clickable(enabled = false) { }
                    .padding(20.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Add Note", fontWeight = FontWeight.Bold, color = Black, fontSize = 18.sp)
                    Text("\u2715", color = Neutral400, modifier = Modifier.clickable { showModal = false })
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = noteText, onValueChange = { noteText = it },
                    placeholder = { Text("Write your note here...", color = Neutral400) },
                    modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(unfocusedContainerColor = Neutral100, focusedContainerColor = White, unfocusedIndicatorColor = Neutral200, focusedIndicatorColor = Black),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Neutral100).clickable { showModal = false }.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                        Text("Cancel", color = Neutral700, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Black).clickable {
                        val words = noteText.trim().split("\\s+".toRegex()).size
                        localNotes = listOf(Note(text = noteText, wordCount = words, dateLabel = "Today")) + localNotes
                        showModal = false
                        noteText = ""
                    }.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                        Text("Save Note", color = White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

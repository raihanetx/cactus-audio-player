package com.cactus.app.ui.loops

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
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
import com.cactus.app.model.Loop
import com.cactus.app.model.SampleData
import com.cactus.app.ui.theme.Black
import com.cactus.app.ui.theme.Neutral100
import com.cactus.app.ui.theme.Neutral200
import com.cactus.app.ui.theme.Neutral300
import com.cactus.app.ui.theme.Neutral400
import com.cactus.app.ui.theme.Neutral500
import com.cactus.app.ui.theme.Neutral600
import com.cactus.app.ui.theme.Neutral700
import com.cactus.app.ui.theme.Neutral900
import com.cactus.app.ui.theme.White

private val Neutral50 = Color(0xFFFAFAFA)

@Composable
fun LoopsScreen(modifier: Modifier = Modifier) {
    var showModal by remember { mutableStateOf(false) }
    var loopName by remember { mutableStateOf("") }
    var loopStart by remember { mutableStateOf("00:12") }
    var loopEnd by remember { mutableStateOf("00:16") }
    var loopCount by remember { mutableStateOf("10") }
    var localLoops by remember { mutableStateOf(SampleData.loops) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "CUSTOM LOOPS",
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Neutral400,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Text(
            text = "A-B Timeframes",
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
                    Text("Create Custom Loop", fontWeight = FontWeight.Bold, color = Black, fontSize = 18.sp)
                    Text("\u2715", color = Neutral400, modifier = Modifier.clickable { showModal = false })
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

                Spacer(modifier = Modifier.height(12.dp))

                Column {
                    Text("LOOP NAME", color = Neutral500, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(
                        value = loopName, onValueChange = { loopName = it },
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
                        TextField(value = loopStart, onValueChange = { loopStart = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.colors(unfocusedContainerColor = Neutral100, focusedContainerColor = White, unfocusedIndicatorColor = Neutral200, focusedIndicatorColor = Black),
                            singleLine = true)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("END (B)", color = Neutral500, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.sp)
                        Spacer(Modifier.height(4.dp))
                        TextField(value = loopEnd, onValueChange = { loopEnd = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.colors(unfocusedContainerColor = Neutral100, focusedContainerColor = White, unfocusedIndicatorColor = Neutral200, focusedIndicatorColor = Black),
                            singleLine = true)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column {
                    Text("LOOP COUNT", color = Neutral500, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.sp)
                    Spacer(Modifier.height(4.dp))
                    TextField(value = loopCount, onValueChange = { loopCount = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(unfocusedContainerColor = Neutral100, focusedContainerColor = White, unfocusedIndicatorColor = Neutral200, focusedIndicatorColor = Black),
                        singleLine = true)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Neutral100).clickable { showModal = false }.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                        Text("Cancel", color = Neutral700, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Black).clickable {
                        localLoops = listOf(Loop(name = loopName.ifEmpty { "Untitled Loop" }, startA = loopStart, endB = loopEnd, count = loopCount.toIntOrNull() ?: 1)) + localLoops
                        showModal = false
                        loopName = ""
                    }.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                        Text("Save Loop", color = White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

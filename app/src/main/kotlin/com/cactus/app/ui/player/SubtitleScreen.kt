package com.cactus.app.ui.player

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cactus.app.model.VideoItem
import com.cactus.app.ui.theme.Black
import com.cactus.app.ui.theme.Neutral100
import com.cactus.app.ui.theme.Neutral200
import com.cactus.app.ui.theme.Neutral300
import com.cactus.app.ui.theme.Neutral400
import com.cactus.app.ui.theme.Neutral500
import com.cactus.app.ui.theme.Neutral700
import com.cactus.app.ui.theme.Neutral800
import com.cactus.app.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

private val BlueFaded = Color(0xFF93C5FD)
private val BlueSolid = Color(0xFF2563EB)

private data class DialogueLine(
    val time: String,
    val english: String,
    val bangla: String,
    val pron: String,
)

private val SAMPLE_DIALOGUE = listOf(
    DialogueLine("[00:01 - 00:02]", "Hello.", "হ্যালো।", "-- হ্যালো --"),
    DialogueLine("[00:03 - 00:04]", "Good morning.", "শুভ সকাল।", "-- গুড-মর্নিং --"),
    DialogueLine("[00:05 - 00:07]", "How are you?", "তুমি কেমন আছো?", "-- হাউ-আর-ইউ? --"),
    DialogueLine("[00:08 - 00:10]", "I am fine, thanks.", "আমি ভালো আছি, ধন্যবাদ।", "-- আই-অ্যাম-ফাইন,-থ্যাংকস। --"),
    DialogueLine("[00:11 - 00:14]", "What is your name, sir?", "আপনার নাম কী, স্যার?", "-- হোয়াট-ইজ-ইয়োর-নেইম,-সার? --"),
    DialogueLine("[00:15 - 00:18]", "My name is John, nice to meet.", "আমার নাম জন, আপনার সাথে দেখা হয়ে ভালো লাগলো।", "-- মাই-নেইম-ইজ-জন,-নাইস-টু-মিট। --"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubtitlePage(
    player: PlayerState,
    video: VideoItem,
    onBack: () -> Unit,
    onSelectPage: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var isTranslated by remember { mutableStateOf(false) }
    var isTranslating by remember { mutableStateOf(false) }
    var translateProgress by remember { mutableIntStateOf(0) }
    val selected = remember { mutableStateListOf<Int>() }
    val scope = rememberCoroutineScope()

    val startTranslate: () -> Unit = {
        isTranslating = true
        translateProgress = 0
        scope.launch {
            var p = 0
            while (p < 100) {
                delay(400)
                p += Random.nextInt(5, 16)
                if (p >= 100) p = 100
                translateProgress = p
                if (p >= 100) {
                    isTranslating = false
                    isTranslated = true
                }
            }
        }
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
                Text("DIALOGUE", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Neutral400, letterSpacing = 1.sp)
                Text("Bilingual Subtitles", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Black, maxLines = 1)
            }
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).clickable { },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.MoreVert, contentDescription = "More", tint = Neutral700, modifier = Modifier.size(20.dp))
            }
        }

        if (!isTranslated) {
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier.size(64.dp).clip(CircleShape).background(Neutral100),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("文", fontSize = 28.sp, color = Neutral400)
                }
                Spacer(Modifier.height(20.dp))
                Text("No translation available", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Neutral800)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Tap the \"Translate\" button below to generate English, Pronunciation & Bangla subtitles.",
                    fontSize = 12.sp, color = Neutral500, textAlign = TextAlign.Center,
                    modifier = Modifier.width(220.dp),
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                itemsIndexed(SAMPLE_DIALOGUE) { index, line ->
                    val isSelected = selected.contains(index)
                    val mainColor = if (isSelected) Black else Neutral400
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(enabled = isTranslated) {
                                if (selected.contains(index)) selected.remove(index) else selected.add(index)
                            }
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                    ) {
                        Text(
                            buildAnnotatedString {
                                append(line.time + " ")
                                append(line.english + " ")
                                withStyle(SpanStyle(color = mainColor)) {
                                    append(line.bangla)
                                }
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = mainColor,
                            lineHeight = 20.sp,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            line.pron,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            fontStyle = if (isSelected) FontStyle.Normal else FontStyle.Italic,
                            color = if (isSelected) BlueSolid else BlueFaded,
                        )
                    }
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
                    onValueChange = {
                        player.sliderPos = it
                        player.dragging = true
                        player.currentPosition = it.toLong()
                    },
                    onValueChangeFinished = {
                        player.seekTo(player.sliderPos.toLong())
                        player.dragging = false
                    },
                    valueRange = 0f..player.duration.toFloat().coerceAtLeast(1f),
                    colors = SliderDefaults.colors(thumbColor = Black, activeTrackColor = Black, inactiveTrackColor = Neutral200),
                    modifier = Modifier.weight(1f).height(24.dp),
                )
                Text(formatTime(player.duration), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Neutral500, modifier = Modifier.width(32.dp), textAlign = TextAlign.End)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("-15s", color = Neutral700, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.clickable { player.skip(-15) })
                Spacer(Modifier.width(24.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100))
                        .background(if (player.isPrepared) Black else Neutral200)
                        .clickable {
                            if (!isTranslated) {
                                if (!isTranslating) startTranslate()
                            } else {
                                player.toggle()
                            }
                        }
                        .padding(horizontal = 24.dp, vertical = 10.dp),
                ) {
                    Text(
                        when {
                            !isTranslated && isTranslating -> "$translateProgress%"
                            !isTranslated -> "Translate"
                            player.isPlaying -> "Pause"
                            else -> "Play"
                        },
                        color = White, fontWeight = FontWeight.Bold, fontSize = 14.sp,
                    )
                }
                Spacer(Modifier.width(24.dp))
                Text("+15s", color = Neutral700, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.clickable { player.skip(15) })
            }

            Spacer(Modifier.height(8.dp))
            PageDots(active = 1, onSelect = onSelectPage)
            Spacer(Modifier.height(8.dp))
        }
    }
}

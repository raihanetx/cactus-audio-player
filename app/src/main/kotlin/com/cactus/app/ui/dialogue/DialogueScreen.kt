package com.cactus.app.ui.dialogue

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cactus.app.model.SampleData
import com.cactus.app.model.SubtitleLine
import com.cactus.app.ui.theme.Blue500
import com.cactus.app.ui.theme.Neutral100
import com.cactus.app.ui.theme.Neutral400
import com.cactus.app.ui.theme.Neutral500
import com.cactus.app.ui.theme.Neutral800
import com.cactus.app.ui.theme.Neutral900
import com.cactus.app.ui.theme.White

@Composable
fun DialogueScreen(modifier: Modifier = Modifier) {
    val subtitles = SampleData.subtitles

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "DIALOGUE",
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Neutral400,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Text(
            text = "Bilingual Subtitles",
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = androidx.compose.ui.graphics.Color.Black,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        if (subtitles.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(64.dp).clip(CircleShape).background(Neutral100),
                        contentAlignment = androidx.compose.ui.Alignment.Center,
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
            Text(
                text = "DIALOGUE LINES",
                color = Neutral400,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
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

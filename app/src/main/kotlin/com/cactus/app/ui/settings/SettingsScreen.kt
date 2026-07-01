package com.cactus.app.ui.settings

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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cactus.app.ui.theme.Black
import com.cactus.app.ui.theme.Neutral100
import com.cactus.app.ui.theme.Neutral200
import com.cactus.app.ui.theme.Neutral300
import com.cactus.app.ui.theme.Neutral400
import com.cactus.app.ui.theme.Neutral50
import com.cactus.app.ui.theme.Neutral500
import com.cactus.app.ui.theme.Neutral600
import com.cactus.app.ui.theme.Neutral700
import com.cactus.app.ui.theme.Neutral900
import com.cactus.app.ui.theme.Red50
import com.cactus.app.ui.theme.Red500
import com.cactus.app.ui.theme.White

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Neutral50)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.size(36.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "PREFERENCES",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Neutral400,
                    letterSpacing = 1.sp,
                )
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Black,
                )
            }
            Box(modifier = Modifier.size(36.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        ApiKeySection()

        Spacer(modifier = Modifier.height(24.dp))

        AiModelsSection()

        Spacer(modifier = Modifier.height(24.dp))

        PlaybackSection()

        Spacer(modifier = Modifier.height(24.dp))

        AboutSection()

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(100))
                .background(Black)
                .clickable { }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text("Save Changes", color = White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        color = Neutral500,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
    )
}

@Composable
private fun ApiKeySection() {
    Column {
        SectionLabel("API Configuration")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(White)
                .padding(16.dp),
        ) {
            Text(
                text = "Platform API Key",
                fontWeight = FontWeight.SemiBold,
                color = Neutral900,
                fontSize = 14.sp,
            )

            Spacer(modifier = Modifier.height(12.dp))

            var apiKey by remember { mutableStateOf("") }
            var visible by remember { mutableStateOf(false) }

            Box {
                androidx.compose.material3.TextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    placeholder = { Text("Enter your API key here", color = Neutral400, fontSize = 14.sp) },
                    visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.TextFieldDefaults.colors(
                        unfocusedContainerColor = Neutral50,
                        focusedContainerColor = White,
                        unfocusedIndicatorColor = Neutral200,
                        focusedIndicatorColor = Black,
                    ),
                    singleLine = true,
                )
                IconButton(
                    onClick = { visible = !visible },
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 4.dp),
                ) {
                    Icon(
                        imageVector = if (visible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle visibility",
                        tint = Neutral400,
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Required for accessing transcription and translation services.",
                color = Neutral400,
                fontSize = 11.sp,
            )
        }
    }
}

@Composable
private fun AiModelsSection() {
    Column {
        SectionLabel("AI Models")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(White)
                .padding(4.dp),
        ) {
            AiModelRow(
                icon = "\uD83C\uDF99\uFE0F",
                label = "Audio to Text Engine",
                value = "Whisper Large V3",
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Neutral100,
                thickness = 1.dp,
            )
            AiModelRow(
                icon = "\uD83C\uDF10",
                label = "Text Translation & Accent",
                value = "Random Model",
            )
        }
    }
}

@Composable
private fun AiModelRow(icon: String, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(12.dp)).background(Neutral100),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = icon, fontSize = 14.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontWeight = FontWeight.SemiBold, color = Neutral900, fontSize = 14.sp)
            Text(value, color = Neutral500, fontSize = 12.sp)
        }
        Text("\u203A", color = Neutral300, fontSize = 16.sp)
    }
}

@Composable
private fun PlaybackSection() {
    Column {
        SectionLabel("Playback Controls")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(White)
                .padding(16.dp),
        ) {
            Text(
                text = "Skip Backward / Forward Amount",
                fontWeight = FontWeight.SemiBold,
                color = Neutral900,
                fontSize = 14.sp,
            )

            Spacer(modifier = Modifier.height(12.dp))

            var skipAmount by remember { mutableStateOf("15s") }
            val skipOptions = listOf("5s", "10s", "15s", "30s")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                skipOptions.forEach { option ->
                    val selected = option == skipAmount
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(100))
                            .background(if (selected) Black else Neutral100)
                            .clickable { skipAmount = option }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = option,
                            color = if (selected) White else Neutral700,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(White)
                .padding(16.dp),
        ) {
            Text(
                text = "Playback Speed",
                fontWeight = FontWeight.SemiBold,
                color = Neutral900,
                fontSize = 14.sp,
            )

            Spacer(modifier = Modifier.height(12.dp))

            var speed by remember { mutableStateOf("1.0x") }
            val speedOptions = listOf("0.5x", "1.0x", "1.5x", "2.0x")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                speedOptions.forEach { option ->
                    val selected = option == speed
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(100))
                            .background(if (selected) Black else Neutral100)
                            .clickable { speed = option }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = option,
                            color = if (selected) White else Neutral700,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AboutSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .padding(4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(12.dp)).background(Neutral100),
                contentAlignment = Alignment.Center,
            ) {
                Text("\u2139\uFE0F", fontSize = 14.sp)
            }
            Text("About App", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold, color = Neutral900, fontSize = 14.sp)
            Text("\u203A", color = Neutral300, fontSize = 16.sp)
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Neutral100,
            thickness = 1.dp,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(12.dp)).background(Red50),
                contentAlignment = Alignment.Center,
            ) {
                Text("\uD83D\uDEAA", fontSize = 14.sp)
            }
            Text("Logout", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold, color = Red500, fontSize = 14.sp)
        }
    }
}

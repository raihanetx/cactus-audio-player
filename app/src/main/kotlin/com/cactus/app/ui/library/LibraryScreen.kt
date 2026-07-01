package com.cactus.app.ui.library

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cactus.app.model.SampleData
import com.cactus.app.model.Track
import com.cactus.app.ui.theme.Black
import com.cactus.app.ui.theme.Blue500
import com.cactus.app.ui.theme.Neutral100
import com.cactus.app.ui.theme.Neutral200
import com.cactus.app.ui.theme.Neutral300
import com.cactus.app.ui.theme.Neutral400
import com.cactus.app.ui.theme.Neutral500
import com.cactus.app.ui.theme.Neutral700
import com.cactus.app.ui.theme.White

@Composable
fun LibraryScreen(modifier: Modifier = Modifier, onTrackClick: () -> Unit = {}) {
    var selectedChip by remember { mutableStateOf("Recent") }
    val chips = listOf("Recent", "Title", "Duration", "Language")

    Column(modifier = modifier.fillMaxSize().background(White)) {
        Spacer(modifier = Modifier.height(24.dp))

        Header()

        Spacer(modifier = Modifier.height(24.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(chips) { chip ->
                FilterChip(
                    selected = chip == selectedChip,
                    onClick = { selectedChip = chip },
                    label = {
                        Text(
                            text = chip,
                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            fontWeight = FontWeight.SemiBold,
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Black,
                        selectedLabelColor = White,
                        containerColor = White,
                        labelColor = Neutral700,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = Neutral200,
                        selectedBorderColor = Black,
                        enabled = true,
                        selected = chip == selectedChip,
                    ),
                    shape = RoundedCornerShape(100),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)
        ) {
            items(SampleData.tracks) { track ->
                TrackCard(track = track, onClick = onTrackClick)
            }
        }
    }
}

@Composable
private fun Header() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "Horizon",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Black,
                color = Black,
            )
            Text(
                text = "Learn through listening",
                style = MaterialTheme.typography.bodySmall,
                color = Neutral500,
                fontWeight = FontWeight.Medium,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                onClick = { },
                modifier = Modifier.size(40.dp).background(White, RoundedCornerShape(100))
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = Black,
                )
            }
            IconButton(
                onClick = { },
                modifier = Modifier.size(40.dp).background(White, RoundedCornerShape(100))
            ) {
                Box {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "Notifications",
                        tint = Black,
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Blue500, RoundedCornerShape(100))
                            .align(Alignment.TopEnd)
                    )
                }
            }
        }
    }
}

@Composable
private fun TrackCard(track: Track, onClick: () -> Unit) {
    val isActive = track.id == 1

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isActive) Blue500 else Black),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = track.initials,
                color = White,
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.titleSmall.fontSize,
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isActive) {
                    PlayingBars()
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (track.hasCC) {
                    Text(
                        text = "CC",
                        color = Blue500,
                        fontSize = MaterialTheme.typography.labelSmall.fontSize,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(text = "·", color = Neutral300)
                } else {
                    Text(
                        text = "No CC",
                        color = Neutral400,
                        fontSize = MaterialTheme.typography.labelSmall.fontSize,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(text = "·", color = Neutral300)
                }
                Text(
                    text = if (track.listened == "0:00") "Not started" else "Listened ${track.listened}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isActive) Neutral700 else Neutral500,
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = track.duration,
                color = Neutral500,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = "\u203A",
                color = if (isActive) Blue500 else Neutral300,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.Normal,
            )
        }
    }

    HorizontalDivider(color = Neutral100, thickness = 1.dp)
}

@Composable
private fun PlayingBars() {
    val infiniteTransition = rememberInfiniteTransition(label = "playing_bars")
    val heights = listOf(
        infiniteTransition.animateFloat(3f, 12f, infiniteRepeatable(tween(700, easing = LinearEasing), RepeatMode.Reverse), label = "b1"),
        infiniteTransition.animateFloat(3f, 12f, infiniteRepeatable(tween(700, 150, easing = LinearEasing), RepeatMode.Reverse), label = "b2"),
        infiniteTransition.animateFloat(3f, 12f, infiniteRepeatable(tween(700, 300, easing = LinearEasing), RepeatMode.Reverse), label = "b3"),
        infiniteTransition.animateFloat(3f, 12f, infiniteRepeatable(tween(700, 100, easing = LinearEasing), RepeatMode.Reverse), label = "b4"),
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.height(12.dp),
    ) {
        heights.forEach { anim ->
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(anim.value.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Blue500)
            )
        }
    }
}

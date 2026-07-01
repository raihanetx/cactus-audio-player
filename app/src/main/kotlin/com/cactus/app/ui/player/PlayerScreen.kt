package com.cactus.app.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cactus.app.model.SampleData
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
import com.cactus.app.ui.theme.White

@Composable
fun PlayerScreen(modifier: Modifier = Modifier) {
    val track = SampleData.tracks.find { it.id == 1 } ?: SampleData.tracks.first()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.size(36.dp))
            Text(
                text = "NOW PLAYING",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Neutral400,
                letterSpacing = 1.sp,
            )
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { },
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "\u2022\u2022\u2022", color = Neutral700, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier.size(224.dp).clip(RoundedCornerShape(16.dp)).background(Black),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = track.initials,
                color = White.copy(alpha = 0.9f),
                fontSize = 72.sp,
                fontWeight = FontWeight.Black,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = track.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = track.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Neutral600,
        )

        Spacer(modifier = Modifier.weight(1f))

        AudioDock()

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun AudioDock() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
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

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "-15s",
                color = Neutral700,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { },
            )
            Spacer(modifier = Modifier.width(24.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100))
                    .background(Black)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { }
                    .padding(horizontal = 24.dp, vertical = 10.dp),
            ) {
                Text(text = "Play", color = White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = "+15s",
                color = Neutral700,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { },
            )
        }
    }
}

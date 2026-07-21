package com.cactus.app.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cactus.app.CactusViewModel
import com.cactus.app.ui.components.SortChip
import com.cactus.app.ui.theme.onSurfaceVariant

/* PRD §8.6 — Settings. Manual rescan lives here (FAB removed per §12.3). */
@Composable
fun SettingsScreen(vm: CactusViewModel) {
    var darkTheme by remember { mutableStateOf(true) }
    var language by remember { mutableStateOf("EN") }
    var dynamicColor by remember { mutableStateOf(false) }
    var defaultSpeed by remember { mutableStateOf("1.0x") }
    var justScanned by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 14.dp),
    ) {
        Text("Settings", style = MaterialTheme.typography.displaySmall, modifier = Modifier.padding(bottom = 12.dp))

        SettingsGroup(title = "Media Sources") {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Folders", style = MaterialTheme.typography.labelLarge)
                    Text("Downloads, Movies, Music", style = MaterialTheme.typography.bodySmall, color = onSurfaceVariant)
                }
                Icon(Icons.Filled.Refresh, "Folder", tint = onSurfaceVariant)
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Rescan now", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
                androidx.compose.material3.FilledTonalButton(onClick = {
                    vm.scanNow()
                    justScanned = true
                }) {
                    Icon(Icons.Filled.Refresh, "Scan", modifier = Modifier.width(18.dp).height(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Scan")
                }
            }
            if (justScanned) {
                Spacer(Modifier.height(8.dp))
                Text("Background scan complete — check the bell on Library.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
        }

        SettingsGroup(title = "Playback") {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Default speed", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
                Row {
                    listOf("0.75x", "1.0x", "1.25x", "1.5x").forEach { s ->
                        SortChip(label = s, selected = defaultSpeed == s, onClick = { defaultSpeed = s }, modifier = Modifier.padding(start = 6.dp))
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Crossfade", style = MaterialTheme.typography.labelLarge)
                    Text("Smooth transitions between tracks", style = MaterialTheme.typography.bodySmall, color = onSurfaceVariant)
                }
                Switch(checked = true, onCheckedChange = {}, colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary))
            }
        }

        SettingsGroup(title = "Appearance & Language") {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Dark theme", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
                Switch(checked = darkTheme, onCheckedChange = { darkTheme = it }, colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary))
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Language", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
                SortChip("EN", language == "EN", onClick = { language = "EN" }, modifier = Modifier.padding(end = 6.dp))
                SortChip("বাংলা", language == "BN", onClick = { language = "BN" })
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Dynamic color", style = MaterialTheme.typography.labelLarge)
                    Text("Match your wallpaper palette", style = MaterialTheme.typography.bodySmall, color = onSurfaceVariant)
                }
                Switch(checked = dynamicColor, onCheckedChange = { dynamicColor = it }, colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary))
            }
        }

        SettingsGroup(title = "About") {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Cactus", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
                Text("Learn English without studying. Just listen.", style = MaterialTheme.typography.bodySmall, color = onSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
            Text("Version 1.0.0", style = MaterialTheme.typography.labelSmall, color = onSurfaceVariant)
        }
    }
}

@Composable
private fun SettingsGroup(title: String, content: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(title.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(16.dp)) { content() }
        }
    }
}

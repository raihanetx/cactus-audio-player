package com.cactus.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.automirrored.outlined.QueueMusic
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.cactus.app.ui.library.LibraryScreen
import com.cactus.app.ui.player.PlayerScreen
import com.cactus.app.ui.settings.SettingsScreen
import com.cactus.app.ui.theme.Blue500
import com.cactus.app.ui.theme.Neutral400
import com.cactus.app.ui.theme.Neutral500

data class NavItem(
    val label: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector,
)

@Composable
fun CactusApp() {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val navItems = listOf(
        NavItem("Library", Icons.Filled.Book, Icons.Outlined.Book),
        NavItem("Playlists", Icons.AutoMirrored.Filled.QueueMusic, Icons.AutoMirrored.Outlined.QueueMusic),
        NavItem("Playing", Icons.Filled.MusicNote, Icons.Outlined.MusicNote),
        NavItem("Settings", Icons.Filled.Settings, Icons.Outlined.Settings),
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == index) item.activeIcon else item.inactiveIcon,
                                contentDescription = item.label,
                                tint = if (selectedTab == index) Blue500 else Neutral400,
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (selectedTab == index) Blue500 else Neutral500,
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Blue500.copy(alpha = 0.12f),
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> LibraryScreen(
                    modifier = Modifier.padding(paddingValues),
                    onTrackClick = { selectedTab = 2 },
                )
            1 -> com.cactus.app.ui.playlists.PlaylistsScreen(modifier = Modifier.padding(paddingValues))
            2 -> PlayerScreen(modifier = Modifier.padding(paddingValues))
            3 -> SettingsScreen(modifier = Modifier.padding(paddingValues))
        }
    }
}

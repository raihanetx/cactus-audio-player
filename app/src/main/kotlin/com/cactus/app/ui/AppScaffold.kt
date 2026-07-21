package com.cactus.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cactus.app.CactusViewModel
import com.cactus.app.ui.components.CactusBottomBar
import com.cactus.app.ui.components.MiniPlayer
import com.cactus.app.ui.components.BottomTab
import com.cactus.app.ui.navigation.CactusNavHost

private val TAB_ROUTES = listOf("library", "playlists", "nowPlaying", "settings")

@Composable
fun CactusApp(
    navController: NavHostController,
    vm: CactusViewModel = viewModel(),
) {
    val backStack by navController.currentBackStackEntryAsState()
    val route = backStack?.destination?.route ?: "library"

    val currentVideo by vm.player.currentVideo.collectAsState()
    val isPlaying by vm.player.isPlaying.collectAsState()
    val notifications by vm.notifications.collectAsState()
    val unreadCount = notifications.count { !it.read }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        bottomBar = {
            if (TAB_ROUTES.contains(route)) {
                Column {
                    if (currentVideo != null) {
                        MiniPlayer(
                            video = currentVideo,
                            isPlaying = isPlaying,
                            onTap = { navController.navigate("player/${currentVideo!!.id}") },
                            onToggle = { vm.player.toggle() },
                        )
                    }
                    CactusBottomBar(
                        current = route,
                        unreadCount = unreadCount,
                        onNotificationClick = { vm.toggleNotifications() },
                        onSelect = { tab ->
                            if (tab == BottomTab.NowPlaying) {
                                val vid = currentVideo
                                if (vid != null) navController.navigate("player/${vid.id}")
                                else navController.navigate("nowPlaying")
                            } else {
                                navController.navigate(tab.route) {
                                    popUpTo("library") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                    )
                }
            }
        },
    ) { padding ->
        CactusNavHost(
            navController = navController,
            vm = vm,
            modifier = Modifier.padding(padding),
        )
    }
}

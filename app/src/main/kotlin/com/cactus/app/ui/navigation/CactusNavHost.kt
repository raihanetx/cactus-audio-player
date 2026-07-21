package com.cactus.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cactus.app.CactusViewModel
import com.cactus.app.ui.library.LibraryScreen
import com.cactus.app.ui.playlists.PlaylistsScreen
import com.cactus.app.ui.player.EmptyNowPlaying
import com.cactus.app.ui.player.PlayerScreen
import com.cactus.app.ui.settings.SettingsScreen

@Composable
fun CactusNavHost(
    navController: NavHostController,
    vm: CactusViewModel,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = "library",
        modifier = modifier,
    ) {
        composable("library") { LibraryScreen(navController, vm) }
        composable("playlists") { PlaylistsScreen(navController, vm) }
        composable("nowPlaying") {
            val current by vm.player.currentVideo.collectAsState()
            if (current != null) {
                PlayerScreen(videoId = current!!.id, vm = vm, onBack = { navController.popBackStack() })
            } else {
                EmptyNowPlaying(onGoLibrary = { navController.navigate("library") })
            }
        }
        composable("settings") { SettingsScreen(vm) }
        composable(
            route = "player/{videoId}",
            arguments = listOf(navArgument("videoId") { type = NavType.StringType }),
        ) { back ->
            val id = back.arguments?.getString("videoId") ?: "v1"
            PlayerScreen(videoId = id, vm = vm, onBack = { navController.popBackStack() })
        }
    }
}

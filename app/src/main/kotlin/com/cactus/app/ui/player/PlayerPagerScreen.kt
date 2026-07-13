package com.cactus.app.ui.player

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.cactus.app.model.VideoItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerPagerScreen(
    video: VideoItem,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val player = rememberTrackPlayer(video)
    val pagerState = rememberPagerState(initialPage = 0) { 4 }
    val scope = rememberCoroutineScope()

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
        beyondViewportPageCount = 1,
    ) { page ->
        when (page) {
            0 -> NowPlayingPage(player, video, onBack, onSelectPage = { scope.launch { pagerState.scrollToPage(it) } })
            1 -> SubtitlePage(player, video, onBack, onSelectPage = { scope.launch { pagerState.scrollToPage(it) } })
            2 -> LoopPage(player, video, onBack, onSelectPage = { scope.launch { pagerState.scrollToPage(it) } })
            3 -> NotePage(player, video, onBack, onSelectPage = { scope.launch { pagerState.scrollToPage(it) } })
        }
    }
}

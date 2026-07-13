package com.cactus.app.ui.player

import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import com.cactus.app.model.VideoItem
import kotlinx.coroutines.delay

class PlayerState {
    var isPrepared by mutableStateOf(false)
    var isPlaying by mutableStateOf(false)
    var duration by mutableLongStateOf(0L)
    var currentPosition by mutableLongStateOf(0L)
    var dragging by mutableStateOf(false)
    var sliderPos by mutableFloatStateOf(0f)
    private var mp: MediaPlayer? = null

    fun attach(player: MediaPlayer) { mp = player }
    fun detach() { mp = null }

    fun toggle() {
        val m = mp ?: return
        if (!isPrepared) return
        if (isPlaying) {
            m.pause()
            isPlaying = false
        } else {
            m.start()
            isPlaying = true
        }
    }

    fun skip(seconds: Int) {
        val m = mp ?: return
        if (!isPrepared) return
        val newPos = (m.currentPosition + seconds * 1000).coerceIn(0, m.duration)
        m.seekTo(newPos)
        currentPosition = newPos.toLong()
    }

    fun seekTo(pos: Long) {
        val m = mp ?: return
        if (!isPrepared || duration <= 0) return
        m.seekTo(pos.toInt())
        currentPosition = pos
    }
}

@Composable
fun rememberTrackPlayer(video: VideoItem): PlayerState {
    val state = remember { PlayerState() }
    val mediaPlayer = remember { MediaPlayer() }

    DisposableEffect(video.id) {
        state.attach(mediaPlayer)
        try {
            mediaPlayer.setDataSource(video.path)
            mediaPlayer.setOnPreparedListener { mp ->
                state.isPrepared = true
                state.duration = mp.duration.toLong()
            }
            mediaPlayer.setOnCompletionListener {
                state.isPlaying = false
                state.currentPosition = 0
            }
            mediaPlayer.prepareAsync()
        } catch (_: Exception) { }

        onDispose {
            mediaPlayer.release()
            state.detach()
        }
    }

    LaunchedEffect(state.isPlaying) {
        while (state.isPlaying) {
            state.currentPosition = mediaPlayer.currentPosition.toLong()
            delay(250)
        }
    }

    return state
}

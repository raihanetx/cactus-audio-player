package com.cactus.app.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.cactus.app.model.Loop
import com.cactus.app.model.LoopCount
import com.cactus.app.model.VideoItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/* Bridges the PRD's "background, audio-first" playback (§1.2, §12.2) to
 * AndroidX Media3 ExoPlayer. In production this would live inside a
 * MediaSession + ForegroundService; here it is a single controller owned by
 * the ViewModel so the mini-player and full player stay in sync. */
class CactusPlayer(private val context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    val exoPlayer = ExoPlayer.Builder(context)
        .build().apply { playWhenReady = false }

    private val _isPlaying = MutableStateFlow(false)
    private val _positionMs = MutableStateFlow(0L)
    private val _durationMs = MutableStateFlow(0L)
    private val _speed = MutableStateFlow(1f)
    private val _currentVideo = MutableStateFlow<VideoItem?>(null)
    private val _isBuffering = MutableStateFlow(false)

    val isPlaying = _isPlaying.asStateFlow()
    val positionMs = _positionMs.asStateFlow()
    val durationMs = _durationMs.asStateFlow()
    val speed = _speed.asStateFlow()
    val currentVideo = _currentVideo.asStateFlow()
    val isBuffering = _isBuffering.asStateFlow()

    /* Active A-B loop (PRD §8.3). */
    var activeLoop: Loop? = null
        private set
    var loopIteration = 0
        private set

    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            _isBuffering.value = state == Player.STATE_BUFFERING
            if (state == Player.STATE_ENDED) _isPlaying.value = false
        }
        override fun onIsPlayingChanged(playing: Boolean) {
            _isPlaying.value = playing
        }
    }

    init {
        exoPlayer.addListener(listener)
        scope.launch {
            while (isActive) {
                val pos = exoPlayer.currentPosition.coerceAtLeast(0)
                _positionMs.value = pos
                if (exoPlayer.duration > 0) _durationMs.value = exoPlayer.duration
                activeLoop?.let { loop ->
                    if (pos >= loop.endMs) {
                        loopIteration++
                        if (loop.count != LoopCount.Infinite && loopIteration >= loop.count.value) {
                            activeLoop = null
                            loopIteration = 0
                        } else {
                            exoPlayer.seekTo(loop.startMs)
                        }
                    }
                }
                delay(200)
            }
        }
    }

    fun load(video: VideoItem, autoplay: Boolean = true) {
        _currentVideo.value = video
        activeLoop = null
        loopIteration = 0
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        if (video.uri != null) {
            exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(video.uri)))
            exoPlayer.prepare()
        }
        _durationMs.value = video.durationMs
        _positionMs.value = 0
        if (autoplay) exoPlayer.play() else exoPlayer.pause()
    }

    fun play() = exoPlayer.play()
    fun pause() = exoPlayer.pause()
    fun toggle() = if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
    fun seekTo(ms: Long) = exoPlayer.seekTo(ms.coerceIn(0, exoPlayer.duration.coerceAtLeast(0)))
    fun seekBy(deltaMs: Long) = seekTo(exoPlayer.currentPosition + deltaMs)
    fun setSpeed(multiplier: Float) {
        _speed.value = multiplier
        exoPlayer.setPlaybackSpeed(multiplier)
    }
    fun setVolume(level: Float) {
        exoPlayer.volume = level.coerceIn(0f, 1f)
    }

    fun enableLoop(loop: Loop) {
        activeLoop = loop
        loopIteration = 0
        exoPlayer.seekTo(loop.startMs)
    }
    fun clearLoop() {
        activeLoop = null
        loopIteration = 0
    }

    fun release() {
        scope.cancel()
        exoPlayer.removeListener(listener)
        exoPlayer.release()
    }
}

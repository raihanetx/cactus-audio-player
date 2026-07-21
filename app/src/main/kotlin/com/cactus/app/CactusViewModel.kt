package com.cactus.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cactus.app.data.MediaScanner
import com.cactus.app.data.SampleData
import com.cactus.app.model.*
import com.cactus.app.model.SubtitleFilter
import com.cactus.app.player.CactusPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/* Single app-level ViewModel. Owns the CactusPlayer (so the mini-player and
 * the full Player screen share one playback session, PRD §7.2) plus all
 * library / playlist / notification state. */
class CactusViewModel(application: Application) : AndroidViewModel(application) {

    private val _videos: MutableStateFlow<List<VideoItem>> = MutableStateFlow(emptyList())
    val videos = _videos.asStateFlow()

    private val _playlists: MutableStateFlow<List<Playlist>> = MutableStateFlow(SampleData.playlists())
    val playlists = _playlists.asStateFlow()

    private val _notifications: MutableStateFlow<List<AppNotification>> = MutableStateFlow(SampleData.initialNotifications())
    val notifications = _notifications.asStateFlow()

    private val _sortMode: MutableStateFlow<SortMode> = MutableStateFlow(SortMode.Recent)
    val sortMode = _sortMode.asStateFlow()

    private val _search: MutableStateFlow<String> = MutableStateFlow("")
    val search = _search.asStateFlow()

    private val _subtitleFilter: MutableStateFlow<SubtitleFilter> = MutableStateFlow(SubtitleFilter.All)
    val subtitleFilter = _subtitleFilter.asStateFlow()

    private val _showNotifications: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showNotifications = _showNotifications.asStateFlow()

    private val _upNext = MutableStateFlow<List<VideoItem>>(emptyList())
    val upNext = _upNext.asStateFlow()

    private val _loops = MutableStateFlow<Map<String, List<Loop>>>(emptyMap())
    val loops = _loops.asStateFlow()

    private val _notes = MutableStateFlow<Map<String, List<Note>>>(emptyMap())
    val notes = _notes.asStateFlow()

    val player = CactusPlayer(application.applicationContext)

    fun subtitlesFor(videoId: String): List<SubtitleCue> = SampleData.subtitlesFor(videoId)

    /* ---- Library actions ---- */
    fun setSort(mode: SortMode) = _sortMode.update { mode }
    fun setSearch(q: String) = _search.update { q }
    fun setSubtitleFilter(filter: SubtitleFilter) = _subtitleFilter.update { filter }

    fun toggleNotifications() = _showNotifications.update { !it }
    fun dismissNotifications() = _showNotifications.update { false }

    fun toggleFinished(id: String) = _videos.update { list ->
        list.map { if (it.id == id) it.copy(isFinished = !it.isFinished) else it }
    }

    fun addToUpNext(video: VideoItem) = _upNext.update { if (video in it) it else it + video }
    fun removeFromUpNext(id: String) = _upNext.update { it.filter { v -> v.id != id } }
    fun moveUpNext(from: Int, to: Int) = _upNext.update { list ->
        list.toMutableList().also { it.add(to, it.removeAt(from)) }
    }

    /* Real device scan: reads audio + video files via MediaStore and presents
     * them as audio (PRD §1.2 / §8.1). Falls back to the sample library when no
     * files are found (e.g. permission not granted), so the UI is never empty. */
    fun scanDevice() {
        try {
            val scanned = MediaScanner.scan(getApplication())
                .map { MediaScanner.toVideoItem(it) }
            _videos.value = if (scanned.isNotEmpty()) scanned else SampleData.videos()
        } catch (_: Exception) {
            _videos.value = SampleData.videos()
        }
    }

    /* Simulated background scan (PRD §8.1 / §12.4). */
    fun scanNow() = viewModelScope.launch {
        scanDevice()
        _notifications.update { listOf(SampleData.newlyDetected()) + it }
    }

    init {
        scanDevice()
    }

    /* ---- Notifications ---- */
    fun markNotificationRead(id: String) = _notifications.update { list ->
        list.map { if (it.id == id) it.copy(read = true) else it }
    }
    fun markAllNotificationsRead() = _notifications.update { list ->
        list.map { it.copy(read = true) }
    }

    /* ---- Playback ---- */
    fun playVideo(video: VideoItem) {
        player.load(video, autoplay = true)
        _videos.update { list ->
            list.map { if (it.id == video.id) it.copy(lastPlayedMs = System.currentTimeMillis()) else it }
        }
    }

    /* ---- Loops (PRD §8.3) ---- */
    fun addLoop(videoId: String, loop: Loop) = _loops.update { map ->
        map + (videoId to ((map[videoId] ?: emptyList()) + loop))
    }
    fun removeLoop(videoId: String, loopId: String) = _loops.update { map ->
        map + (videoId to (map[videoId] ?: emptyList()).filter { it.id != loopId })
    }
    fun enableLoop(videoId: String, loop: Loop) {
        player.enableLoop(loop)
        addLoop(videoId, loop)
    }
    fun clearLoop() = player.clearLoop()

    /* ---- Notes (PRD §8.5) ---- */
    fun addNote(videoId: String, text: String, atMs: Long) {
        if (text.isBlank()) return
        val note = Note("note_${System.currentTimeMillis()}", atMs, text)
        _notes.update { map -> map + (videoId to ((map[videoId] ?: emptyList()) + note)) }
    }
    fun removeNote(videoId: String, noteId: String) = _notes.update { map ->
        map + (videoId to (map[videoId] ?: emptyList()).filter { it.id != noteId })
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}

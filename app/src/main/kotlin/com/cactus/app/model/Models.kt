package com.cactus.app.model

import androidx.compose.ui.graphics.Color
import com.cactus.app.ui.theme.primary
import com.cactus.app.ui.theme.secondary
import com.cactus.app.ui.theme.tertiary
import com.cactus.app.ui.theme.primaryContainer

/* Detected video (audio source). PRD §1.2, §8.1. */
data class VideoItem(
    val id: String,
    val titleEn: String,
    val titleBn: String? = null,
    val fileName: String,
    val durationMs: Long,
    val dateAddedMs: Long,
    val language: String = "EN",
    val gradientStart: Color,
    val gradientEnd: Color,
    val uri: String? = null,
    val isFinished: Boolean = false,
    val inUpNext: Boolean = false,
    val lastPlayedMs: Long? = null,
)

/* User-created playlist. PRD §7.1. */
data class Playlist(
    val id: String,
    val name: String,
    val videoIds: List<String>,
    val coverColor: Color,
)

/* A subtitle chunk with English text, Bangla translation and pronunciation.
 * PRD §8.4 (Subtitles — Bottom Sheet). */
data class SubtitleCue(
    val id: String,
    val startMs: Long,
    val endMs: Long,
    val textEn: String,
    val textBn: String,
    val pronunciation: String,
)

/* A-B loop. PRD §8.3 (Loops — Bottom Sheet). */
data class Loop(
    val id: String,
    val startMs: Long,
    val endMs: Long,
    val count: LoopCount = LoopCount.Infinite,
)

enum class LoopCount(val label: String, val value: Int) {
    Infinite("∞", -1),
    Three("3x", 3),
    Five("5x", 5),
    Ten("10x", 10),
}

/* Timestamped note captured during playback. PRD §8.5 (Notes — Bottom Sheet). */
data class Note(
    val id: String,
    val timestampMs: Long,
    val text: String,
)

/* Library sort modes. PRD §8.1 — Recent | Title | Duration | Language. */
enum class SortMode(val label: String) {
    Recent("Recent"),
    Title("Title"),
    Duration("Duration"),
    Language("Language"),
    RecentlyListened("Recently Listened"),
}

enum class SubtitleFilter(val label: String) {
    All("All"),
    WithSubtitles("Subtitled"),
    WithoutSubtitles("Audio Only"),
}

/* A passive, in-app notification (no push). PRD §8.1 / §12.4. */
data class AppNotification(
    val id: String,
    val title: String,
    val body: String,
    val timestampMs: Long,
    val read: Boolean = false,
)

fun formatDuration(ms: Long): String {
    val totalSeconds = (ms / 1000).toInt()
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}

fun formatDate(ms: Long): String {
    val now = System.currentTimeMillis()
    val day = 24 * 60 * 60 * 1000L
    val diff = now - ms
    return when {
        diff < day -> "Today"
        diff < 2 * day -> "Yesterday"
        diff < 7 * day -> "${diff / day}d ago"
        else -> {
            val d = java.util.Date(ms)
            val fmt = java.text.SimpleDateFormat("d MMM", java.util.Locale.ENGLISH)
            fmt.format(d)
        }
    }
}

fun formatClock(ms: Long): String = formatDuration(ms)

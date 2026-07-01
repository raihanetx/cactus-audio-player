package com.cactus.app.model

data class Track(
    val id: Int,
    val title: String,
    val subtitle: String,
    val initials: String,
    val duration: String,
    val listened: String,
    val hasCC: Boolean,
)

data class SubtitleLine(
    val start: String,
    val end: String,
    val english: String,
    val bangla: String,
    val pronunciation: String,
)

data class Loop(
    val name: String,
    val startA: String,
    val endB: String,
    val count: Int,
)

data class Note(
    val text: String,
    val wordCount: Int,
    val dateLabel: String,
)

data class PlaybackState(
    val currentTime: String = "00:12",
    val totalTime: String = "01:15",
    val progress: Float = 0.25f,
    val isPlaying: Boolean = false,
    val isTranslated: Boolean = false,
)

data class AppSettings(
    val apiKey: String = "",
    val skipAmount: Int = 15,
    val playbackSpeed: Float = 1.0f,
)

package com.cactus.app.model

data class VideoItem(
    val id: Long,
    val title: String,
    val durationMs: Long,
    val sizeBytes: Long,
    val path: String,
    val dateAdded: Long,
    val mimeType: String,
)

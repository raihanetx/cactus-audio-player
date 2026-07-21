package com.cactus.app.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.cactus.app.model.VideoItem
import androidx.compose.ui.graphics.Color

/* Scans the device for real audio and video files via MediaStore (PRD §1.2 / §8.1)
 * and presents them as audio-first items. Videos are treated as audio sources:
 * the app extracts/plays their audio track, never shows video. */
object MediaScanner {

    data class MediaFile(
        val id: Long,
        val title: String,
        val fileName: String,
        val durationMs: Long,
        val dateAddedMs: Long,
        val isVideo: Boolean,
        val uri: Uri,
    )

    private val audioProjection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.DATE_ADDED,
    )

    private val videoProjection = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.TITLE,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.DURATION,
        MediaStore.Video.Media.DATE_ADDED,
    )

    fun scan(context: Context): List<MediaFile> {
        val files = mutableListOf<MediaFile>()
        files += query(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, audioProjection, isVideo = false)
        files += query(context, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoProjection, isVideo = true)
        return files.sortedByDescending { it.dateAddedMs }
    }

    private fun query(
        context: Context,
        collection: Uri,
        projection: Array<String>,
        isVideo: Boolean,
    ): List<MediaFile> {
        val idCol = if (isVideo) MediaStore.Video.Media._ID else MediaStore.Audio.Media._ID
        val titleCol = if (isVideo) MediaStore.Video.Media.TITLE else MediaStore.Audio.Media.TITLE
        val nameCol = if (isVideo) MediaStore.Video.Media.DISPLAY_NAME else MediaStore.Audio.Media.DISPLAY_NAME
        val durCol = if (isVideo) MediaStore.Video.Media.DURATION else MediaStore.Audio.Media.DURATION
        val dateCol = if (isVideo) MediaStore.Video.Media.DATE_ADDED else MediaStore.Audio.Media.DATE_ADDED

        val result = mutableListOf<MediaFile>()
        try {
            context.contentResolver.query(
                collection,
                projection,
                null,
                null,
                "${MediaStore.MediaColumns.DATE_ADDED} DESC",
            )?.use { cursor ->
                val idIdx = cursor.getColumnIndexOrThrow(idCol)
                val titleIdx = cursor.getColumnIndexOrThrow(titleCol)
                val nameIdx = cursor.getColumnIndexOrThrow(nameCol)
                val durIdx = cursor.getColumnIndexOrThrow(durCol)
                val dateIdx = cursor.getColumnIndexOrThrow(dateCol)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIdx)
                    val title = cursor.getString(titleIdx)?.takeIf { it.isNotBlank() }
                        ?: cursor.getString(nameIdx)?.removeExtension()
                        ?: "Unknown"
                    val name = cursor.getString(nameIdx) ?: title
                    // MediaStore stores DATE_ADDED in seconds.
                    val dateAddedSec = cursor.getLong(dateIdx)
                    result.add(
                        MediaFile(
                            id = id,
                            title = title,
                            fileName = name,
                            durationMs = cursor.getLong(durIdx).coerceAtLeast(0),
                            dateAddedMs = dateAddedSec * 1000L,
                            isVideo = isVideo,
                            uri = ContentUris.withAppendedId(collection, id),
                        ),
                    )
                }
            }
        } catch (_: Exception) {
            // Permission denied or query failed — return whatever we have.
        }
        return result
    }

    /* Builds a VideoItem from a scanned media file. Colors are derived from the
     * title so the existing artwork/thumbnail rendering still works offline. */
    fun toVideoItem(file: MediaFile): VideoItem {
        val (start, end) = colorsFromTitle(file.title)
        return VideoItem(
            id = "${if (file.isVideo) "vid" else "aud"}_${file.id}",
            titleEn = file.title,
            titleBn = null,
            fileName = file.fileName,
            durationMs = file.durationMs,
            dateAddedMs = file.dateAddedMs,
            language = "EN",
            gradientStart = start,
            gradientEnd = end,
            uri = file.uri.toString(),
        )
    }

    private fun String.removeExtension(): String = substringBeforeLast('.')

    /* Deterministic warm palette derived from the title hash. */
    private fun colorsFromTitle(title: String): Pair<Color, Color> {
        val h = title.hashCode().toLong()
        val hue = ((h % 360L) + 360L) % 360L  // always positive 0..359
        val start = Color.hsv(hue.toFloat(), 0.45f, 0.55f)
        val end = Color.hsv(((hue + 40) % 360).toFloat(), 0.55f, 0.30f)
        return start to end
    }
}

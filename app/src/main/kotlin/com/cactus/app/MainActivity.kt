package com.cactus.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.cactus.app.ui.player.SubtitleScreen
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.cactus.app.model.VideoItem
import com.cactus.app.ui.player.PlayerScreen
import com.cactus.app.ui.theme.Black
import com.cactus.app.ui.theme.Neutral500
import com.cactus.app.ui.theme.White
import com.cactus.app.ui.videolist.VideoListScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoApp()
        }
    }
}

@Composable
private fun VideoApp() {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(false) }
    var videos by remember { mutableStateOf<List<VideoItem>>(emptyList()) }
    var screen by remember { mutableStateOf<AppScreen>(AppScreen.List) }

    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_VIDEO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasPermission = granted
        if (granted) {
            scanVideos(context) { result -> videos = result }
        }
    }

    LaunchedEffect(Unit) {
        hasPermission = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            scanVideos(context) { result -> videos = result }
        }
    }

    if (!hasPermission) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("\uD83C\uDFB5", fontSize = 64.sp)
            Text(
                "Access Videos",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Black,
                modifier = Modifier.padding(top = 24.dp),
            )
            Text(
                "Allow access to your device's videos to extract and play audio.",
                fontSize = 14.sp,
                color = Neutral500,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp, bottom = 32.dp),
            )
            Button(
                onClick = { launcher.launch(permission) },
                modifier = Modifier.clip(RoundedCornerShape(100)),
                colors = ButtonDefaults.buttonColors(containerColor = Black),
            ) {
                Text("Grant Permission", color = White, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 8.dp))
            }
        }
    } else {
        when (val s = screen) {
            AppScreen.List -> VideoListScreen(
                videos = videos,
                onItemClick = { video -> screen = AppScreen.Player(video) },
                modifier = Modifier.fillMaxSize(),
            )
            is AppScreen.Player -> {
                val v = s.video
                PlayerScreen(
                    video = v,
                    onBack = { screen = AppScreen.List },
                    onOpenSubtitle = { screen = AppScreen.Subtitle(v) },
                )
            }
            is AppScreen.Subtitle -> {
                val v = s.video
                SubtitleScreen(video = v, onBack = { screen = AppScreen.Player(v) })
            }
        }
    }
}

private sealed interface AppScreen {
    data object List : AppScreen
    data class Player(val video: VideoItem) : AppScreen
    data class Subtitle(val video: VideoItem) : AppScreen
}

private fun scanVideos(context: android.content.Context, onResult: (List<VideoItem>) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        val list = mutableListOf<VideoItem>()
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.MIME_TYPE,
        )
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder,
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
            val durCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val pathCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)

            while (cursor.moveToNext()) {
                val item = VideoItem(
                    id = cursor.getLong(idCol),
                    title = cursor.getString(titleCol) ?: "",
                    durationMs = cursor.getLong(durCol),
                    sizeBytes = cursor.getLong(sizeCol),
                    path = cursor.getString(pathCol) ?: "",
                    dateAdded = cursor.getLong(dateCol),
                    mimeType = cursor.getString(mimeCol) ?: "",
                )
                if (item.durationMs > 0) list.add(item)
            }
        }

        withContext(Dispatchers.Main) {
            onResult(list)
        }
    }
}

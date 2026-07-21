package com.cactus.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cactus.app.ui.githubui.CactusApp
import com.cactus.app.ui.theme.CactusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CactusTheme(darkTheme = true) {
                CactusApp()
            }
        }
    }
}

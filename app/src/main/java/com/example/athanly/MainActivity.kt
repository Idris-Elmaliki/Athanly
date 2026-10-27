package com.example.athanly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.athanly.ui.AppRoute
import com.example.athanly.ui.theme.AthanlyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Must run before super.onCreate — it swaps Theme.Athanly.Starting back to
        // Theme.Athanly. No keep-on-screen condition: the Compose splash does the waiting.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AthanlyTheme {
                // AppRoute owns all navigation — MainActivity holds no routing logic
                AppRoute(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
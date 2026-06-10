package com.example.athanly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.athanly.ui.AppRoute
import com.example.athanly.ui.theme.AthanlyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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
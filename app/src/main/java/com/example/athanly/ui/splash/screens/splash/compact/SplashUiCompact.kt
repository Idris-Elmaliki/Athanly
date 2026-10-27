package com.example.athanly.ui.splash.screens.splash.compact

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.athanly.R

// Compact (< 600dp): layout only — the parent decides when the name appears and when to leave.
@Composable
fun SplashUiCompact(
    nameVisible: Boolean,
    nameAnimationMillis: Int,
    modifier: Modifier = Modifier
) {
    // Black matches the OS splash's @color/black, so the hand-off shows no seam
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Same image and size the OS splash draws, so the first frame is identical
            Image(
                painter = painterResource(R.drawable.ic_launcher_minaret_foreground),
                contentDescription = null,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.splash_icon_size) * 0.6f)
            )
            AnimatedVisibility(
                visible = nameVisible,
                enter = expandHorizontally(animationSpec = tween(durationMillis = nameAnimationMillis)) +
                    fadeIn(animationSpec = tween(durationMillis = nameAnimationMillis))
            ) {
                Text(
                    text = stringResource(R.string.app_name).uppercase(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,

                    ),
                    color = Color.White,
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.splash_icon_name_gap))
                )
            }
        }
    }
}

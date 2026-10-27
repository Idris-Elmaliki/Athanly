package com.example.athanly.ui.splash.screens.splash.medium

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import com.example.athanly.R

// Medium (600–839dp): same lockup as Compact with a larger name — the extra width lets the
// text grow without the pair outgrowing the screen.
@Composable
fun SplashUiMedium(
    nameVisible: Boolean,
    nameAnimationMillis: Int,
    modifier: Modifier = Modifier
) {
    val nameOverlap = dimensionResource(R.dimen.splash_name_overlap)

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
                modifier = Modifier.size(dimensionResource(R.dimen.splash_icon_size) * 0.6f)
            )

            AnimatedVisibility(
                visible = nameVisible,
                enter = expandHorizontally(animationSpec = tween(durationMillis = nameAnimationMillis)) +
                    fadeIn(animationSpec = tween(durationMillis = nameAnimationMillis)),
                // Same overlap trick as Compact — see SplashUiCompact for why layout() over offset()
                modifier = Modifier.layout { measurable, constraints ->
                    val overlapPx = nameOverlap.roundToPx()
                    val placeable = measurable.measure(constraints.copy(maxWidth = Constraints.Infinity))
                    layout(
                        width = (placeable.width - overlapPx).coerceAtLeast(0),
                        height = placeable.height
                    ) {
                        placeable.place(x = -overlapPx, y = 0)
                    }
                }
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White,
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.splash_icon_name_gap))
                )
            }
        }
    }
}

package com.example.athanly.ui.subscreens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import com.example.athanly.R

// Shared "shimmer" loading placeholder — a soft highlight band that sweeps left-to-right over
// a low-alpha base color. Any skeleton composable applies this instead of duplicating the
// animation per screen (CLAUDE.md routes reusable loading UI through ui/subscreens/).
// Caller applies shape via .clip(...) before this, same order as a real background() call.
@Composable
fun Modifier.shimmerEffect(): Modifier {
    var sizePx by remember { mutableStateOf(IntSize.Zero) }

    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    
    val translateAnim by transition.animateFloat(
        initialValue = -2f * sizePx.width.coerceAtLeast(1),
        targetValue = 2f * sizePx.width.coerceAtLeast(1),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerTranslate",
    )

    val baseColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    val highlightColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)

    return this
        .onGloballyPositioned { coordinates -> 
            sizePx = coordinates.size 
        }
        .background(
            brush = Brush.linearGradient(
                colors = listOf(baseColor, highlightColor, baseColor),
                start = Offset(translateAnim, 0f),
                end = Offset(translateAnim + sizePx.width, sizePx.height.toFloat()),
            ),
        )
}

// A single shimmering rounded-rect standing in for a line of text or a small shape — the
// smallest reusable skeleton building block, shared by any card that keeps its outer shell
// mounted and only swaps its inner content between loading/real (see SacredCountdownCard,
// PrayerScheduleList)
@Composable
fun SkeletonBar(width: Dp, height: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(dimensionResource(R.dimen.skeleton_bar_corner_radius)))
            .shimmerEffect(),
    )
}
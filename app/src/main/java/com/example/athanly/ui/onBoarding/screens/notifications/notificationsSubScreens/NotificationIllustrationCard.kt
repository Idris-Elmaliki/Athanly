package com.example.athanly.ui.onBoarding.screens.notifications.notificationsSubScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import com.example.athanly.R

// Circular "glassmorphic" illustration for the final onboarding step. Uses alpha-transparency
// only (no Modifier.blur) — matches this codebase's existing glass-card convention, e.g.
// CalculationsUiCompact's HanafiCard. Reused as-is across all 3 window size classes.
@Composable
fun NotificationIllustrationCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(dimensionResource(R.dimen.notifications_illustration_outer_size)),
        contentAlignment = Alignment.Center,
    ) {
        // Outer glow — radial gradient fading to fully transparent, standing in for a blurred glow
        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.notifications_illustration_outer_size))
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f),
                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0f),
                        ),
                    ),
                    shape = CircleShape,
                ),
        )

        // Inner glass circle — semi-transparent surface + low-alpha border, same approximation as HanafiCard
        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.notifications_illustration_inner_size))
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                .border(
                    width = dimensionResource(R.dimen.notifications_illustration_border_width),
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
                    shape = CircleShape,
                ),
        ) {
            Image(
                painter = painterResource(R.drawable.notifications_mosque_illustration),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // Floating notification badge, overlapping the top-right edge of the circle
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(
                    x = dimensionResource(R.dimen.notifications_badge_offset_end),
                    y = dimensionResource(R.dimen.notifications_badge_offset_top),
                )
                .rotate(12f) // matches the slight tilt in the Figma design
                .size(dimensionResource(R.dimen.notifications_badge_size))
                .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                .border(
                    width = dimensionResource(R.dimen.notifications_badge_border_width),
                    color = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier.size(dimensionResource(R.dimen.notifications_badge_icon_size)),
            )
        }
    }
}
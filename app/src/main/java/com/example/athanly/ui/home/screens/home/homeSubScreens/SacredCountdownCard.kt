package com.example.athanly.ui.home.screens.home.homeSubScreens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.athanly.R
import com.example.athanly.athanTime.local.domain.modelData.NextPrayerInfo
import java.time.Duration
import java.time.format.DateTimeFormatter

private val startsAtFormatter = DateTimeFormatter.ofPattern("HH:mm")

// The "Sacred Card" — next-prayer label, live countdown timer, and progress ring, all
// centered over each other since the ring sits behind the text in the design
@Composable
fun SacredCountdownCard(
    nextPrayerInfo: NextPrayerInfo,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensionResource(R.dimen.home_sacred_card_corner_radius)))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(dimensionResource(R.dimen.home_sacred_card_padding)),
        contentAlignment = Alignment.Center
    ) {
        ProgressRing(
            progressFraction = nextPrayerInfo.progressFraction,
            modifier = Modifier.size(dimensionResource(R.dimen.home_sacred_card_ring_size))
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(
                    R.string.home_next_prayer_label,
                    stringResource(nextPrayerInfo.nextPrayer.displayNameRes)
                ),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.home_sacred_card_label_timer_gap)))
            Text(
                text = formatCountdown(nextPrayerInfo.remaining),
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.home_sacred_card_timer_starts_at_gap)))
            Text(
                text = stringResource(
                    R.string.home_starts_at_label,
                    nextPrayerInfo.startsAt.format(startsAtFormatter)
                ),
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = dimensionResource(R.dimen.home_top_bar_title_letter_spacing).value.sp
                ),
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

// Track (full circle) + progress arc (sweeps clockwise from the top) drawn as a single Canvas
@Composable
private fun ProgressRing(progressFraction: Float, modifier: Modifier = Modifier) {
    val trackColor = MaterialTheme.colorScheme.outlineVariant
    val progressColor = MaterialTheme.colorScheme.primary
    val strokeWidthPx = with(LocalDensity.current) {
        dimensionResource(R.dimen.home_sacred_card_ring_stroke_width).toPx()
    }

    Canvas(modifier = modifier) {
        val stroke = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        drawArc(
            color = trackColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = stroke
        )
        drawArc(
            color = progressColor,
            startAngle = -90f,
            sweepAngle = 360f * progressFraction.coerceIn(0f, 1f),
            useCenter = false,
            style = stroke
        )
    }
}

// Formats a Duration as HH:mm:ss for the big timer — e.g. 1h45m0s -> "01:45:00"
private fun formatCountdown(duration: Duration): String {
    val totalSeconds = duration.seconds.coerceAtLeast(0)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}
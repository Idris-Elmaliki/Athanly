package com.example.athanly.ui.home.screens.qibla.qiblaSubScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.athanly.R

// needleRotationDegrees and kaabaMarkerRotationDegrees are deliberately independent —
// the needle is a live compass hand driven purely by device heading (gyroscope/rotation
// sensor), updating continuously. The Kaaba marker is a static angle from the last
// direction calculation (local for now, the API's `direction` field later) and only
// changes when a fresh calculation happens, never in response to the phone turning.
@Composable
fun QiblaCompass(
    needleRotationDegrees: Float,
    kaabaMarkerDegrees: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .size(dimensionResource(R.dimen.qibla_compass_diameter)),
        contentAlignment = Alignment.Center
    ) {
        // Glassmorphism ring — translucent fill + low-alpha border, same convention
        // used for other frosted-glass cards elsewhere in the app.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.background)
                .border(
                    width = dimensionResource(R.dimen.qibla_compass_needle_pivot_size) / 4,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    shape = CircleShape
                )
        )

        CompassCardinalLabel(
            text = stringResource(R.string.qibla_compass_north),
            alignment = Alignment.TopCenter
        )
        CompassCardinalLabel(
            text = stringResource(R.string.qibla_compass_east),
            alignment = Alignment.CenterEnd
        )
        CompassCardinalLabel(
            text = stringResource(R.string.qibla_compass_south),
            alignment = Alignment.BottomCenter
        )
        CompassCardinalLabel(
            text = stringResource(R.string.qibla_compass_west),
            alignment = Alignment.CenterStart
        )

        CompassNeedle(rotationDegrees = needleRotationDegrees)

        CompassKaabaMarker(degrees = kaabaMarkerDegrees)
    }
}

@Composable
private fun CompassCardinalLabel(text: String, alignment: Alignment) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.qibla_compass_cardinal_label_inset)),
        contentAlignment = alignment
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun CompassNeedle(rotationDegrees: Float) {
    // A single Box tinted with a vertical-gradient-like split would need a Brush;
    // kept simple here as one shape in the primary brand color pointing at the Kaaba.
    // Modifier.rotate() rotates around this composable's own center by default, which
    // is exactly the ring's center since the needle is centered in the parent Box.
    Box(
        modifier = Modifier
            .rotate(rotationDegrees)
            .clip(CircleShape)
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize(),
            painter = painterResource(R.drawable.qibla_needle),
            contentDescription = null
        )
    }
}

@Composable
private fun CompassKaabaMarker(degrees: Float) {
    // Orbits the icon around the ring's center: the outer Box rotates to the needle's
    // angle and pushes its child to the top edge via alignment, then the inner Box
    // counter-rotates by the same amount so the Kaaba icon itself stays upright
    // instead of spinning with the orbit.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .rotate(degrees),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .padding(top = dimensionResource(R.dimen.qibla_compass_cardinal_label_inset))
                .rotate(-degrees)
        ) {
            Image(
                painter = painterResource(R.drawable.qibla_kaaba),
                contentDescription = stringResource(R.string.qibla_compass_kaaba_marker_description),
                modifier = Modifier.size(dimensionResource(R.dimen.qibla_compass_kaaba_icon_size))
            )
        }
    }
}
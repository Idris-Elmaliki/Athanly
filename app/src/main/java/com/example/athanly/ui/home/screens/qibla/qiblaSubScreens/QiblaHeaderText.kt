package com.example.athanly.ui.home.screens.qibla.qiblaSubScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.athanly.R

// "Qibla" heading + calibration instruction — same title/subtitle shape as
// WelcomeHeader/GreetingUiCompact. Heading uses tertiaryContainer (MintGlow) for the
// teal tone from the design, rather than introducing a brand-new color for one screen.
@Composable
fun QiblaHeaderText(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.qibla_header_title),
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.tertiaryContainer,
            textAlign = TextAlign.Center
        )
    }
}
package com.example.athanly.ui.onBoarding.screens.greeting.medium

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.athanly.R

// Medium layout (600–839dp) — 12 columns, 12dp min margins
// Content constrained to 75% of available width and centered, matching the Welcome screen's pattern
@Composable
fun GreetingUiMedium(
    salamAlpha: Float,
    subtitleAlpha: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth(fraction = 0.75f)
                .padding(horizontal = dimensionResource(R.dimen.welcome_screen_horizontal_padding)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.greeting_salam),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp
                ),
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(salamAlpha)
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.greeting_text_gap)))

            Text(
                text = stringResource(R.string.greeting_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(subtitleAlpha)
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
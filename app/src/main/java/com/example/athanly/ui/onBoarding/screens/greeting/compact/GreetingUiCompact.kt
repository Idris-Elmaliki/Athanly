package com.example.athanly.ui.onBoarding.screens.greeting.compact

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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

// Compact layout (0–599dp) — 4 columns, 8dp min margins
@Composable
fun GreetingUiCompact(
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
                .padding(horizontal = dimensionResource(R.dimen.welcome_screen_horizontal_padding)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Push the text block toward the vertical center of the available space
            Spacer(modifier = Modifier.weight(1f))

            // "السلام عليكم" — fades in first, large gold heading
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

            // "Your journey starts here." — fades in second, once the heading finishes
            Text(
                text = stringResource(R.string.greeting_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(subtitleAlpha)
            )

            // Mirror the top spacer so the text block sits centered
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
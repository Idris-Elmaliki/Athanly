package com.example.athanly.ui.onBoarding.screens.welcome.compact

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.athanly.language.domain.modelData.Language
import com.example.athanly.language.domain.modelData.WelcomeUiState
import com.example.athanly.R

// Compact layout (0–599dp) — 4 columns, 8dp min margins
@Composable
fun WelcomeUiCompact(
    state: WelcomeUiState,
    onLanguageSelected: (Language) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Subtle green gradient glow bleeding down from the top edge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize(fraction = 0.35f)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(R.dimen.welcome_screen_horizontal_padding)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Push header content toward the vertical center of the available space
                Spacer(modifier = Modifier.weight(1f))

                WelcomeHeader()

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.welcome_header_to_cards_spacing)))

                LanguageCard(
                    label = stringResource(R.string.welcome_language_english),
                    isSelected = state.selectedLanguage is Language.English,
                    onClick = { onLanguageSelected(Language.English) }
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.welcome_card_spacing)))

                LanguageCard(
                    label = stringResource(R.string.welcome_language_arabic),
                    isSelected = state.selectedLanguage is Language.Arabic,
                    onClick = { onLanguageSelected(Language.Arabic) }
                )

                // Mirror the top spacer so the content block sits centered
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun WelcomeHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // "Welcome" — large bold display title in the primary brand green
        Text(
            text = stringResource(R.string.welcome_title),
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 42.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.welcome_text_gap)))

        // Arabic "أهلاً بك" — secondary gold color, slightly smaller
        Text(
            text = stringResource(R.string.welcome_title_arabic),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.welcome_text_gap)))

        // Subtitle body copy — muted color, centered, two lines
        Text(
            text = stringResource(R.string.welcome_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LanguageCard(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Yellow border only appears on the selected card — unselected card has no visible border
    val borderModifier = if (isSelected) {
        Modifier.border(
            width = dimensionResource(R.dimen.welcome_radio_button_border_width),
            color = MaterialTheme.colorScheme.tertiary,
            shape = RoundedCornerShape(dimensionResource(R.dimen.welcome_card_corner_radius))
        )
    } else Modifier

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .then(borderModifier),
        shape = RoundedCornerShape(dimensionResource(R.dimen.welcome_card_corner_radius)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.welcome_radio_button_border_width))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.welcome_card_horizontal_padding),
                    vertical = dimensionResource(R.dimen.welcome_card_vertical_padding)
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                color = MaterialTheme.colorScheme.secondary
            )

            RadioDot(isSelected = isSelected)
        }
    }
}

@Composable
private fun RadioDot(isSelected: Boolean) {
    // Outer ring — always visible in tertiary yellow
    Box(
        modifier = Modifier
            .size(dimensionResource(R.dimen.welcome_radio_button_size))
            .border(
                width = dimensionResource(R.dimen.welcome_radio_button_border_width),
                color = MaterialTheme.colorScheme.tertiary,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        // Inner filled dot — only visible when this card is selected
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.welcome_radio_button_inner_size))
                    .background(
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = CircleShape
                    )
            )
        }
    }
}
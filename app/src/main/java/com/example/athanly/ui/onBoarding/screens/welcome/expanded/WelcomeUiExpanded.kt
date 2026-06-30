package com.example.athanly.ui.onBoarding.screens.welcome.expanded

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

// Expanded layout (840+dp) — 12 columns, 32dp min margins
// Content is constrained to 55% width to keep it readable on large landscape displays
@Composable
fun WelcomeUiExpanded(
    state: WelcomeUiState,
    onLanguageSelected: (Language) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
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

            // Content constrained to 55% width — keeps cards/text from stretching on large displays
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxWidth(fraction = 0.55f)
                    .padding(horizontal = dimensionResource(R.dimen.welcome_screen_horizontal_padding)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))

                WelcomeExpandedHeader()

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.welcome_header_to_cards_spacing)))

                LanguageExpandedCard(
                    label = stringResource(R.string.welcome_language_english),
                    isSelected = state.selectedLanguage is Language.English,
                    onClick = { onLanguageSelected(Language.English) }
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.welcome_card_spacing)))

                LanguageExpandedCard(
                    label = stringResource(R.string.welcome_language_arabic),
                    isSelected = state.selectedLanguage is Language.Arabic,
                    onClick = { onLanguageSelected(Language.Arabic) }
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun WelcomeExpandedHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
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

        Text(
            text = stringResource(R.string.welcome_title_arabic),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.welcome_text_gap)))

        Text(
            text = stringResource(R.string.welcome_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LanguageExpandedCard(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
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

            RadioExpandedDot(isSelected = isSelected)
        }
    }
}

@Composable
private fun RadioExpandedDot(isSelected: Boolean) {
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
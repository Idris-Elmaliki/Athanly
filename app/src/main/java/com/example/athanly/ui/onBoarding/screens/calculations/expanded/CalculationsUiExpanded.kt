package com.example.athanly.ui.onBoarding.screens.calculations.expanded

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.athanly.R
import com.example.athanly.calculations.domain.modelData.CalculationsUiState

// Expanded layout (840dp+) — 12 columns, 32dp min margins
@Composable
fun CalculationsUiExpanded(
    state: CalculationsUiState,
    onHanafiModeToggled: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimensionResource(R.dimen.welcome_screen_horizontal_padding)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Constrain content to 55% width so it doesn't over-stretch on large tablets
            Column(
                modifier = Modifier.fillMaxWidth(0.55f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                PrayerSettingsExpandedHeader()

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.prayer_settings_header_bottom_spacing)))

                HanafiExpandedCard(
                    isHanafiMode = state.isHanafiMode,
                    onToggled = onHanafiModeToggled,
                )
            }
        }
    }
}

@Composable
private fun PrayerSettingsExpandedHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.prayer_settings_title),
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.welcome_text_gap)))

        Text(
            text = stringResource(R.string.prayer_settings_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun HanafiExpandedCard(
    isHanafiMode: Boolean,
    onToggled: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensionResource(R.dimen.prayer_settings_card_corner_radius)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
        ),
        border = BorderStroke(
            width = CardDefaults.outlinedCardBorder().width,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        ),
        elevation = CardDefaults.outlinedCardElevation(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.prayer_settings_card_horizontal_padding),
                    vertical = dimensionResource(R.dimen.prayer_settings_card_vertical_padding),
                ),
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = dimensionResource(R.dimen.prayer_settings_card_description_end_padding)),
            ) {
                Text(
                    text = stringResource(R.string.hanafi_card_title),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.prayer_settings_card_text_gap)))

                Text(
                    text = stringResource(R.string.hanafi_card_description),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            Switch(
                checked = isHanafiMode,
                onCheckedChange = { onToggled() },
            )
        }
    }
}
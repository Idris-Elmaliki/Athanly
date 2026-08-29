package com.example.athanly.ui.home.screens.settings.settingsSubScreens.languagePicker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.example.athanly.R
import com.example.athanly.appData.domain.modelData.Language
import com.example.athanly.ui.subscreens.DetailTopBar

// Compact (< 600dp): single-column, full-bleed — same shell shape as SettingsUiCompact.
// Reuses SettingsViewModel's existing selectedLanguage/onLanguageSelected rather than a
// dedicated ViewModel, since the DataStore-backed preference already lives there.
@Composable
fun LanguagePickerUiCompact(
    modifier: Modifier = Modifier,
    selectedLanguage: Language,
    onLanguageSelected: (Language) -> Unit,
    onBackClick: () -> Unit,
    topBarPadding: Dp
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailTopBar(
                onBackClick = onBackClick,
                modifier = Modifier.padding(top = topBarPadding)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(R.dimen.settings_screen_horizontal_padding))
                    .padding(top = dimensionResource(R.dimen.settings_screen_top_content_padding))
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.settings_card_corner_radius)),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                    ),
                    border = BorderStroke(
                        width = dimensionResource(R.dimen.settings_card_border_width),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column {
                        LanguageOptionRow(
                            title = stringResource(Language.English.displayNameRes),
                            isSelected = selectedLanguage is Language.English,
                            onClick = { onLanguageSelected(Language.English) }
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                        LanguageOptionRow(
                            title = stringResource(Language.Arabic.displayNameRes),
                            isSelected = selectedLanguage is Language.Arabic,
                            onClick = { onLanguageSelected(Language.Arabic) }
                        )
                    }
                }
            }
        }
    }
}

// Screen-local — just a title + a checkmark that only shows up when this row is the current
// selection, unlike SettingsRow's chevron/switch trailing slots which don't fit a picker list.
@Composable
private fun LanguageOptionRow(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = dimensionResource(R.dimen.settings_row_horizontal_padding),
                vertical = dimensionResource(R.dimen.settings_row_vertical_padding)
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.Check,
                // Decorative — the title text next to it already labels the row for TalkBack
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(dimensionResource(R.dimen.settings_row_chevron_size))
            )
        }
    }
}
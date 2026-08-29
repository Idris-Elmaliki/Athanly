package com.example.athanly.ui.home.screens.settings.settingsSubScreens.muadhdhinPicker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.Dp
import com.example.athanly.R
import com.example.athanly.appData.domain.modelData.Muadhdhin
import com.example.athanly.appData.domain.modelData.muadhhinList
import com.example.athanly.ui.subscreens.DetailTopBar

// Expanded (840dp+) — 12 columns, 32dp min margins. Same structure as Compact/Medium, content
// capped at 55% width to keep it readable on large displays, mirroring LanguagePickerUiExpanded's convention.
@Composable
fun MuadhdhinPickerUiExpanded(
    modifier: Modifier = Modifier,
    selectedMuadhdhin: Muadhdhin,
    onMuadhdhinSelected: (Muadhdhin) -> Unit,
    onBackClick: () -> Unit,
    topBarPadding: Dp
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth(fraction = 0.55f)
        ) {
            DetailTopBar(
                onBackClick = onBackClick,
                modifier = Modifier.padding(top = topBarPadding)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(R.dimen.settings_screen_horizontal_padding)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.welcome_card_spacing))
            ) {
                item {
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.settings_screen_top_content_padding)))
                }

                items(muadhhinList) { muadhdhin ->
                    MuadhdhinOptionRow(
                        muadhdhin = muadhdhin,
                        isSelected = muadhdhin == selectedMuadhdhin,
                        onClick = { onMuadhdhinSelected(muadhdhin) }
                    )
                }
            }
        }
    }
}

// Screen-local — mirrors onboarding's MuadhhinCard visuals (gradient avatar + play icon,
// location/sheikh text) but with LanguagePickerUiExpanded's simpler "checkmark only when
// selected" trailing slot instead of a filled-circle radio, to match this picker's sibling screen.
@Composable
private fun MuadhdhinOptionRow(
    muadhdhin: Muadhdhin,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensionResource(R.dimen.settings_card_corner_radius)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        ),
        border = BorderStroke(
            width = dimensionResource(R.dimen.settings_card_border_width),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.athan_selection_avatar_content_gap)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MuadhdhinAvatar(muadhdhin = muadhdhin)

                Column {
                    Text(
                        text = stringResource(muadhdhin.locationNameRes),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.athan_selection_text_gap)))

                    Text(
                        text = stringResource(muadhdhin.sheikhNameRes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    // Decorative — the location/sheikh text next to it already labels the row
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(dimensionResource(R.dimen.settings_row_chevron_size))
                )
            }
        }
    }
}

@Composable
private fun MuadhdhinAvatar(muadhdhin: Muadhdhin) {
    // Gradient colors are domain-layer placeholders — Color() conversion is the accepted
    // exception, same as onboarding's MuadhhinCard.
    Box(
        modifier = Modifier
            .size(dimensionResource(R.dimen.athan_selection_avatar_size))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(muadhdhin.gradientStart),
                        Color(muadhdhin.gradientEnd)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(dimensionResource(R.dimen.athan_selection_play_icon_size))
        )
    }
}
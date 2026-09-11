package com.example.athanly.ui.onBoarding.screens.athanSelection.expanded

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.athanly.R
import com.example.athanly.appData.domain.modelData.AppDataUiState
import com.example.athanly.appData.domain.modelData.Muadhdhin
import com.example.athanly.appData.domain.modelData.enums.MuadhdhinId
import com.example.athanly.appData.domain.modelData.muadhhinList
import com.example.athanly.ui.onBoarding.screens.athanSelection.athanSelectionSubScreens.MuadhhinCard

// Expanded layout (840dp+) — 12 columns, 32dp min margins, 2-column card grid
@Composable
fun AthanSelectionUiExpanded(
    state: AppDataUiState,
    previewingMuadhdhinId: MuadhdhinId?,
    onMuadhhinSelected: (Muadhdhin) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = dimensionResource(R.dimen.welcome_screen_horizontal_padding),
                vertical = dimensionResource(R.dimen.welcome_screen_horizontal_padding),
            ),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.welcome_card_spacing)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.welcome_card_spacing)),
        ) {
            // Header spans both columns
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(
                    modifier = Modifier
                        .padding(bottom = dimensionResource(R.dimen.athan_selection_header_bottom_padding))
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AthanSelectionExpandedHeader()
                }
            }

            items(muadhhinList) { muadhdhin ->
                MuadhhinCard(
                    muadhdhin = muadhdhin,
                    isSelected = state.hasSelectedMuadhdhin && muadhdhin == state.selectedMuadhdhin,
                    isPlaying = muadhdhin.id == previewingMuadhdhinId,
                    onClick = { onMuadhhinSelected(muadhdhin) },
                )
            }

            // Bottom spacer spans both columns to clear the fixed bottom bar
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.athan_selection_scroll_bottom_padding)))
            }
        }
    }
}

@Composable
private fun AthanSelectionExpandedHeader() {
    Text(
        text = stringResource(R.string.athan_selection_title),
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.welcome_text_gap)))

    Text(
        text = stringResource(R.string.athan_selection_subtitle),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
    )
}
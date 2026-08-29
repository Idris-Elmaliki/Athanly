package com.example.athanly.ui.onBoarding.screens.athanSelection.compact

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.athanly.appData.domain.modelData.muadhhinList
import com.example.athanly.ui.onBoarding.screens.athanSelection.athanSelectionSubScreens.MuadhhinCard

// Compact layout (0–599dp) — 4 columns, 8dp min margins
@Composable
fun AthanSelectionUiCompact(
    state: AppDataUiState,
    onMuadhhinSelected: (Muadhdhin) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimensionResource(R.dimen.welcome_screen_horizontal_padding)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                AthanSelectionHeader()
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.athan_selection_header_bottom_padding)))
            }

            itemsIndexed(muadhhinList) { index, muadhdhin ->
                MuadhhinCard(
                    muadhdhin = muadhdhin,
                    isSelected = muadhdhin == state.selectedMuadhdhin,
                    onClick = { onMuadhhinSelected(muadhdhin) },
                )

                // Add spacing after every card except the last
                if (index < muadhhinList.lastIndex) {
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.welcome_card_spacing)))
                }
            }

            item {
                // Clears the fixed bottom bar + its gradient fade since Scaffold padding is suppressed
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.athan_selection_scroll_bottom_padding)))
            }
        }
    }
}

@Composable
private fun AthanSelectionHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.welcome_screen_horizontal_padding)),
    ) {
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
}
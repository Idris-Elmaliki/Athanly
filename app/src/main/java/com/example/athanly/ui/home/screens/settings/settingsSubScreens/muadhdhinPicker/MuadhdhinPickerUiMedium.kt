package com.example.athanly.ui.home.screens.settings.settingsSubScreens.muadhdhinPicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import com.example.athanly.R
import com.example.athanly.appData.domain.modelData.Muadhdhin
import com.example.athanly.appData.domain.modelData.enums.MuadhdhinId
import com.example.athanly.appData.domain.modelData.muadhhinList
import com.example.athanly.ui.home.screens.settings.settingsSubScreens.muadhdhinPicker.subScreens.MuadhdhinOptionRow
import com.example.athanly.ui.subscreens.DetailTopBar

// Medium (600–839dp) — 12 columns, 12dp min margins. Same structure as Compact, content
// capped at 75% width to avoid over-stretching, mirroring LanguagePickerUiMedium's convention.
@Composable
fun MuadhdhinPickerUiMedium(
    modifier: Modifier = Modifier,
    selectedMuadhdhin: Muadhdhin,
    previewingMuadhdhinId: MuadhdhinId?,
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
                .fillMaxWidth(fraction = 0.75f)
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
                        isPlaying = muadhdhin.id == previewingMuadhdhinId,
                        onClick = { onMuadhdhinSelected(muadhdhin) }
                    )
                }
            }
        }
    }
}
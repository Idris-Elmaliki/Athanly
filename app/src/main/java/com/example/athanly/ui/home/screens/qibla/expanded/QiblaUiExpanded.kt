package com.example.athanly.ui.home.screens.qibla.expanded

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import com.example.athanly.R
import com.example.athanly.qibla.domain.local.modelData.QiblaUiState
import com.example.athanly.ui.home.screens.qibla.qiblaSubScreens.DistanceCard
import com.example.athanly.ui.home.screens.qibla.qiblaSubScreens.QiblaCompass
import com.example.athanly.ui.home.screens.qibla.qiblaSubScreens.QiblaHeaderText
import com.example.athanly.ui.home.screens.qibla.qiblaSubScreens.QiblaTopBar
import com.example.athanly.ui.subscreens.AtmosphericBackground

// Expanded (840dp+) — 12 columns, 32dp min margins. Same structure as Compact/Medium, content
// capped at 55% width to keep it readable on large displays, mirroring HomeUiExpanded.
@Composable
fun QiblaUiExpanded(
    state: QiblaUiState,
    onMenuClick: () -> Unit,
    onUnitToggled: () -> Unit,
    topBarPadding: Dp,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier) {
        AtmosphericBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth(fraction = 0.55f)
                .verticalScroll(rememberScrollState())
        ) {
            QiblaTopBar(
                onMenuClick = onMenuClick,
                modifier = Modifier.padding(top = topBarPadding)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.qibla_screen_horizontal_padding))
            ) {
                QiblaHeaderText()

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.qibla_screen_section_spacing)))

                QiblaCompass(
                    needleRotationDegrees = state.needleRotationDegrees,
                    kaabaMarkerDegrees = state.qiblaDegrees.toFloat()
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.qibla_screen_section_spacing)))

                DistanceCard(
                    distanceValue = state.distanceValue,
                    distanceUnit = state.distanceUnit,
                    qiblaDegrees = state.qiblaDegrees,
                    qiblaDirection = state.qiblaDirection,
                    onUnitToggled = onUnitToggled
                )
            }
        }
    }
}
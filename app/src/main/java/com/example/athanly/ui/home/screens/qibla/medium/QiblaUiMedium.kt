package com.example.athanly.ui.home.screens.qibla.medium

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

// Medium (600–839dp) — 12 columns, 12dp min margins. Same structure as Compact, content
// capped at 75% width to avoid over-stretching, mirroring HomeUiMedium's convention.
@Composable
fun QiblaUiMedium(
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
                .fillMaxWidth(fraction = 0.75f)
        ) {
            QiblaTopBar(
                onMenuClick = onMenuClick,
                modifier = Modifier.padding(top = topBarPadding)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
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
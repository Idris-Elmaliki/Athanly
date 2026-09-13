package com.example.athanly.ui.home.screens.qibla.compact

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.example.athanly.R
import com.example.athanly.qibla.local.domain.modelData.QiblaUiState
import com.example.athanly.ui.home.screens.qibla.qiblaSubScreens.DistanceCard
import com.example.athanly.ui.home.screens.qibla.qiblaSubScreens.QiblaCompass

// Compact (< 600dp): single-column, full-bleed — same shell shape as HomeUiCompact.
@Composable
fun QiblaUiCompact(
    state: QiblaUiState,
    onUnitToggled: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimensionResource(R.dimen.qibla_screen_horizontal_padding))
                .verticalScroll(rememberScrollState())
        ) {
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
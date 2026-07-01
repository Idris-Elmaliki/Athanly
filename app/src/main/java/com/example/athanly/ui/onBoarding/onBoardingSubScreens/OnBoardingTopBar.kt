package com.example.athanly.ui.onBoarding.onBoardingSubScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import com.example.athanly.R

// Progress indicator displayed at the top of every onboarding step.
// Lives in the Scaffold topBar so it never recomposes on step navigation.
@Composable
fun OnBoardingTopBar(
    currentStep: Int,  // 0-indexed — drives which dot renders as the active pill
    totalSteps: Int    // determines how many dots to render
) {
    Row(
        modifier = Modifier.padding(dimensionResource(R.dimen.onboarding_top_bar_padding)),
        horizontalArrangement = Arrangement.spacedBy(
            // Gap between each dot as per Figma spec
            dimensionResource(R.dimen.onboarding_dot_spacing)
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalSteps) { index ->
            val isActive = index == currentStep

            Box(
                modifier = Modifier
                    // Active step: elongated pill. Inactive: small square clipped to circle.
                    .width(
                        if (isActive) dimensionResource(R.dimen.onboarding_active_dot_width)
                        else dimensionResource(R.dimen.onboarding_dot_size)
                    )
                    .height(dimensionResource(R.dimen.onboarding_dot_size))
                    .clip(CircleShape)
                    .background(
                        // Active uses primary brand color, inactive uses the muted outlineVariant
                        if (isActive) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant
                    )
            )
        }
    }
}
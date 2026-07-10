package com.example.athanly.ui.home.screens.home.homeSubScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import com.example.athanly.R

// Mirrors OnBoardingTopBar's expanding-pill dot pattern for visual consistency — active
// city renders as an elongated pill, inactive cities as small circles
@Composable
fun CityPaginationDots(
    currentIndex: Int,   // 0-indexed — drives which dot renders as the active pill
    totalCities: Int,    // determines how many dots to render
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        // spacedBy's alignment param centers the whole group, not just the gaps between dots
        horizontalArrangement = Arrangement.spacedBy(
            space = dimensionResource(R.dimen.home_pagination_dot_spacing),
            alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalCities) { index ->
            val isActive = index == currentIndex

            Box(
                modifier = Modifier
                    .width(
                        if (isActive) dimensionResource(R.dimen.home_pagination_active_dot_width)
                        else dimensionResource(R.dimen.home_pagination_dot_size)
                    )
                    .height(dimensionResource(R.dimen.home_pagination_dot_size))
                    .clip(CircleShape)
                    .background(
                        if (isActive) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant
                    )
            )
        }
    }
}
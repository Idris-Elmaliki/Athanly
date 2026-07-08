package com.example.athanly.ui.onBoarding.screens.notifications.compact

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.unit.sp
import com.example.athanly.R
import com.example.athanly.ui.onBoarding.screens.notifications.notificationsSubScreens.NotificationIllustrationCard
import com.example.athanly.ui.onBoarding.screens.notifications.notificationsSubScreens.NotificationsActions

// Compact layout (0–599dp) — 4 columns, 8dp min margins
@Composable
fun NotificationsUiCompact(
    modifier: Modifier = Modifier,
    onEnableClick: () -> Unit,
    onMaybeLaterClick: () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = dimensionResource(R.dimen.welcome_screen_horizontal_padding),
                    vertical = dimensionResource(R.dimen.welcome_screen_vertical_padding),
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Illustration + text float in the vertical center of the remaining space
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                NotificationIllustrationCard()

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.notifications_illustration_bottom_spacing)))

                NotificationsHeaderCompact()
            }

            NotificationsActions(
                onEnableClick = onEnableClick,
                onMaybeLaterClick = onMaybeLaterClick,
            )
        }
    }
}

@Composable
private fun NotificationsHeaderCompact() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.notifications_text_horizontal_padding)),
    ) {
        Text(
            text = stringResource(R.string.notifications_heading),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.notifications_heading_body_gap)))

        Text(
            text = stringResource(R.string.notifications_body),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = dimensionResource(R.dimen.notifications_body_max_width)),
        )
    }
}
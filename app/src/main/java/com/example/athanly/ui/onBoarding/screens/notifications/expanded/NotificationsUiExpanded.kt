package com.example.athanly.ui.onBoarding.screens.notifications.expanded

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

// Expanded layout (840dp+) — 12 columns, 32dp min margins
@Composable
fun NotificationsUiExpanded(
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
            // Constrain content to 55% width so it doesn't over-stretch on large tablets
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(0.55f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                NotificationIllustrationCard()

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.notifications_illustration_bottom_spacing)))

                NotificationsHeaderExpanded()
            }

            NotificationsActions(
                onEnableClick = onEnableClick,
                onMaybeLaterClick = onMaybeLaterClick,
                modifier = Modifier.fillMaxWidth(0.55f),
            )
        }
    }
}

@Composable
private fun NotificationsHeaderExpanded() {
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
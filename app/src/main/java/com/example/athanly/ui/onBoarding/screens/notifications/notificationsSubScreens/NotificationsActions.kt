package com.example.athanly.ui.onBoarding.screens.notifications.notificationsSubScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.athanly.R

// Primary "Enable Notifications" CTA + secondary "Maybe Later" skip action.
// Screen-local — the shared OnBoardingBottomBar is hidden for this route since
// this step needs two distinct actions instead of one Continue button.
@Composable
fun NotificationsActions(
    modifier: Modifier = Modifier,
    onEnableClick: () -> Unit,
    onMaybeLaterClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = dimensionResource(R.dimen.notifications_action_area_bottom_padding)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.notifications_action_area_gap)),
    ) {
        Button(
            onClick = onEnableClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(dimensionResource(R.dimen.notifications_button_corner_radius)),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
            contentPadding = PaddingValues(
                vertical = dimensionResource(R.dimen.notifications_button_vertical_padding),
            ),
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(
                    width = dimensionResource(R.dimen.notifications_button_icon_width),
                    height = dimensionResource(R.dimen.notifications_button_icon_height),
                ),
            )

            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.notifications_button_icon_gap)))

            Text(
                text = stringResource(R.string.notifications_enable_button),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.6.sp,
                ),
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        }

        TextButton(
            onClick = onMaybeLaterClick,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                vertical = dimensionResource(R.dimen.notifications_maybe_later_vertical_padding),
            ),
        ) {
            Text(
                text = stringResource(R.string.notifications_maybe_later),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.6.sp,
                ),
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}
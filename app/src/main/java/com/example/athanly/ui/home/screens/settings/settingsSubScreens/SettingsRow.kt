package com.example.athanly.ui.home.screens.settings.settingsSubScreens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.athanly.R

// Shared row shell for every settings row except Volume Control (which has its own layout
// — a slider needs full width below the text, not a trailing slot). onClick is nullable
// because the row itself isn't always the tap target (a Switch handles its own taps).
@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailingContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .let { rowModifier ->
                if (onClick != null) rowModifier.clickable(onClick = onClick) else rowModifier
            }
            .padding(
                horizontal = dimensionResource(R.dimen.settings_row_horizontal_padding),
                vertical = dimensionResource(R.dimen.settings_row_vertical_padding)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(dimensionResource(R.dimen.settings_row_icon_circle_size))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    // Decorative — the title text next to it already labels the row for TalkBack
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(dimensionResource(R.dimen.settings_row_icon_size))
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = dimensionResource(R.dimen.settings_row_icon_text_gap))
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.size(dimensionResource(R.dimen.settings_row_title_subtitle_gap)))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = dimensionResource(R.dimen.settings_row_subtitle_letter_spacing).value.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        trailingContent()
    }
}

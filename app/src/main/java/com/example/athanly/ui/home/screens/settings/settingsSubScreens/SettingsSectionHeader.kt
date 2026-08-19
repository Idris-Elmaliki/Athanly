package com.example.athanly.ui.home.screens.settings.settingsSubScreens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.athanly.R

// Uppercase label above each settings card (e.g. "GENERAL") — takes the already-resolved
// String rather than a @StringRes id so the same composable serves all three sections
@Composable
fun SettingsSectionHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = dimensionResource(R.dimen.settings_section_header_letter_spacing).value.sp
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = dimensionResource(R.dimen.settings_section_header_start_padding))
    )
}

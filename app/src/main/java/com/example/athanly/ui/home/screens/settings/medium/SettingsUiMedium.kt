package com.example.athanly.ui.home.screens.settings.medium

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.example.athanly.R
import com.example.athanly.appData.domain.modelData.AppDataUiState
import com.example.athanly.ui.home.screens.settings.settingsSubScreens.SettingsRow
import com.example.athanly.ui.home.screens.settings.settingsSubScreens.SettingsSectionHeader
import com.example.athanly.ui.home.screens.settings.settingsSubScreens.VolumeControlRow

// Medium (600–839dp) — 12 columns, 12dp min margins. Same structure as Compact, content
// capped at 75% width to avoid over-stretching, mirroring HomeUiMedium/QiblaUiMedium's convention.
@Composable
fun SettingsUiMedium(
    modifier: Modifier = Modifier,
    state: AppDataUiState,
    onLanguageRowClick: () -> Unit,
    onTimeFormatToggled: () -> Unit,
    onAsrCalculationToggled: () -> Unit,
    onMuadhdhinRowClick: () -> Unit,
    onVolumeChanged: (Float) -> Unit,
    onNotificationSettingsRowClick: () -> Unit,
    topBarPadding: Dp
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth(fraction = 0.75f)
        ) {
            Spacer(
                modifier = Modifier
                    .padding(top = topBarPadding)
                    .height(dimensionResource(R.dimen.settings_top_bar_height))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = dimensionResource(R.dimen.settings_screen_horizontal_padding))
                    .padding(top = dimensionResource(R.dimen.settings_screen_top_content_padding)),
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.settings_screen_section_spacing)
                )
            ) {
                // GENERAL
                Column {
                    SettingsSectionHeader(text = stringResource(R.string.settings_section_general))

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.settings_section_header_bottom_gap)))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(dimensionResource(R.dimen.settings_card_corner_radius)),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                        ),
                        border = BorderStroke(
                            width = dimensionResource(R.dimen.settings_card_border_width),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Column {
                            SettingsRow(
                                icon = Icons.Filled.Language,
                                title = stringResource(R.string.settings_language_title),
                                subtitle = stringResource(state.selectedLanguage.displayNameRes),
                                onClick = onLanguageRowClick,
                                trailingContent = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(dimensionResource(R.dimen.settings_row_chevron_size))
                                    )
                                }
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                            SettingsRow(
                                icon = Icons.Filled.Schedule,
                                title = stringResource(R.string.settings_time_format_title),
                                subtitle = stringResource(R.string.settings_time_format_subtitle),
                                trailingContent = {
                                    Switch(
                                        checked = state.is24HourFormat,
                                        onCheckedChange = { onTimeFormatToggled() }
                                    )
                                }
                            )
                        }
                    }
                }

                // PRAYER SETTINGS
                Column {
                    SettingsSectionHeader(text = stringResource(R.string.settings_section_prayer_settings))

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.settings_section_header_bottom_gap)))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(dimensionResource(R.dimen.settings_card_corner_radius)),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                        ),
                        border = BorderStroke(
                            width = dimensionResource(R.dimen.settings_card_border_width),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        SettingsRow(
                            icon = Icons.Filled.School,
                            title = stringResource(R.string.settings_asr_calculation_title),
                            subtitle = stringResource(R.string.settings_asr_calculation_subtitle),
                            trailingContent = {
                                Switch(
                                    checked = state.isHanafiMode,
                                    onCheckedChange = { onAsrCalculationToggled() }
                                )
                            }
                        )
                    }
                }

                // ATHAN & ALERTS
                Column {
                    SettingsSectionHeader(text = stringResource(R.string.settings_section_athan_alerts))

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.settings_section_header_bottom_gap)))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(dimensionResource(R.dimen.settings_card_corner_radius)),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                        ),
                        border = BorderStroke(
                            width = dimensionResource(R.dimen.settings_card_border_width),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Column {
                            SettingsRow(
                                icon = Icons.Filled.Mic,
                                title = stringResource(R.string.settings_selected_muadhdhin_title),
                                subtitle = stringResource(state.selectedMuadhdhin.locationNameRes),
                                onClick = onMuadhdhinRowClick,
                                trailingContent = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(dimensionResource(R.dimen.settings_row_chevron_size))
                                    )
                                }
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                            VolumeControlRow(
                                volume = state.athanVolume,
                                onVolumeChanged = onVolumeChanged
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                            SettingsRow(
                                icon = Icons.Filled.Notifications,
                                title = stringResource(R.string.settings_notification_settings_title),
                                subtitle = stringResource(R.string.settings_notification_settings_subtitle),
                                onClick = onNotificationSettingsRowClick,
                                trailingContent = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(dimensionResource(R.dimen.settings_row_chevron_size))
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

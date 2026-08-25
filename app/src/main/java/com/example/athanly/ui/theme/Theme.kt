package com.example.athanly.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = DarkGreen,           // brand color — buttons, active indicators
    onPrimary = Color.White,       // text/icons on primary surfaces
    secondary = Gold,              // secondary accent — labels, Arabic text
    onSecondary = Color.White,
    tertiary = Yellow,             // highlights — active card borders, radio buttons
    onTertiary = DarkGreen,
    background = BackgroundWhite,
    onBackground = TextMuted,      // body and subtitle text
    surface = BackgroundWhite,
    onSurface = TextMuted,
    outlineVariant = InactiveDot,          // inactive pagination dots and subtle borders
    outline = InactiveRadioBorder,         // unselected radio button rings
    surfaceContainerHigh = CardActiveBackground,   // selected muadhdhin card background
    surfaceContainerLow = CardInactiveBackground,  // unselected muadhdhin card background
    surfaceVariant = CardInactiveBorder,           // unselected muadhdhin card border
    scrim = AvatarDarkOverlay,                     // dark overlay on avatar gradient circles
    tertiaryContainer = MintGlow,                  // notifications screen — illustration glow
    onTertiaryContainer = CreamGold,                // notifications screen — button label
    primaryContainer = RowIconContainer,           // settings screen — row icon circle background
    onPrimaryContainer = RowIconTint,              // settings screen — row icon tint
    onSurfaceVariant = RowTitle,                   // settings screen — row title text
    secondaryContainer = SwitchActiveBlue,         // settings screen — checked switch track/thumb
)

// Dark scheme mirrors light for now — will be refined when dark mode designs are provided
private val DarkColorScheme = darkColorScheme(
    primary = DarkGreen,
    onPrimary = Color.White,
    secondary = Gold,
    onSecondary = Color.White,
    tertiary = Yellow,
    onTertiary = DarkGreen,
    background = BackgroundWhite,
    onBackground = TextMuted,
    surface = BackgroundWhite,
    onSurface = TextMuted,
    outlineVariant = InactiveDot,
    outline = InactiveRadioBorder,
    surfaceContainerHigh = CardActiveBackground,
    surfaceContainerLow = CardInactiveBackground,
    surfaceVariant = CardInactiveBorder,
    scrim = AvatarDarkOverlay,
    tertiaryContainer = MintGlow,
    onTertiaryContainer = CreamGold,
    primaryContainer = RowIconContainer,
    onPrimaryContainer = RowIconTint,
    onSurfaceVariant = RowTitle,
    secondaryContainer = SwitchActiveBlue,
)

@Composable
fun AthanlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Dynamic color disabled — app uses a custom branded color scheme
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
package com.example.athanly.ui.onBoarding.onBoardingSubScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.athanly.R

// Continue button anchored at the bottom of the onboarding Scaffold.
// Lives in bottomBar so it persists across all steps without recomposing.
@Composable
fun OnBoardingBottomBar(
    onContinueClick: () -> Unit,
    // Location is the only step so far that requires state before advancing — a saved city,
    // either from GPS or the forced preset picker. Defaults to true so every other step's
    // call site is unaffected.
    enabled: Boolean = true,
) {
    // Gradient fade gives the button a floating appearance over scrollable content
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(dimensionResource(R.dimen.onboarding_bottom_bar_padding)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onContinueClick,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            // Full pill shape as per Figma spec
            shape = RoundedCornerShape(9999.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            contentPadding = PaddingValues(
                vertical = dimensionResource(R.dimen.onboarding_button_vertical_padding)
            )
        ) {
            Text(
                text = stringResource(R.string.onboarding_continue),
                style = MaterialTheme.typography.labelMedium.copy(
                    // Uppercase letter-spaced label matches the Figma button spec
                    letterSpacing = 1.2.sp
                ),
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
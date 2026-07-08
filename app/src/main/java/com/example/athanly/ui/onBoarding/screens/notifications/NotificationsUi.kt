package com.example.athanly.ui.onBoarding.screens.notifications

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowSizeClass
import com.example.athanly.ui.onBoarding.screens.notifications.compact.NotificationsUiCompact
import com.example.athanly.ui.onBoarding.screens.notifications.expanded.NotificationsUiExpanded
import com.example.athanly.ui.onBoarding.screens.notifications.medium.NotificationsUiMedium
import com.example.athanly.ui.onBoarding.viewModel.OnBoardingViewModel

@Composable
fun NotificationsUi(
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass,
    onNotificationsStepComplete: () -> Unit,
    viewModel: OnBoardingViewModel = hiltViewModel(),
) {
    // POST_NOTIFICATIONS is only a runtime-requestable permission on API 33+ —
    // below that it's already granted automatically at install time
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        viewModel.onNotificationPermissionResult(isGranted)
        onNotificationsStepComplete()
    }

    val onEnableClick: () -> Unit = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            viewModel.onNotificationPermissionResult(isGranted = true)
            onNotificationsStepComplete()
        }
    }

    val onMaybeLaterClick: () -> Unit = {
        viewModel.onNotificationPermissionResult(isGranted = false)
        onNotificationsStepComplete()
    }

    when {
        // 840dp+ — expanded (large tablets, landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
            NotificationsUiExpanded(
                modifier = modifier,
                onEnableClick = onEnableClick,
                onMaybeLaterClick = onMaybeLaterClick,
            )
        }
        // 600dp+ — medium (small tablets, large phones in landscape)
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
            NotificationsUiMedium(
                modifier = modifier,
                onEnableClick = onEnableClick,
                onMaybeLaterClick = onMaybeLaterClick,
            )
        }
        // < 600dp — compact (standard phones)
        else -> {
            NotificationsUiCompact(
                modifier = modifier,
                onEnableClick = onEnableClick,
                onMaybeLaterClick = onMaybeLaterClick,
            )
        }
    }
}
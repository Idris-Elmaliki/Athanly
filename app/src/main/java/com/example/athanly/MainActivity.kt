package com.example.athanly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.athanly.appData.data.localizedFor
import com.example.athanly.appData.domain.modelData.Language
import com.example.athanly.ui.AppRoute
import com.example.athanly.ui.theme.AthanlyTheme
import com.example.athanly.ui.viewModel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Same instance AppRoute's hiltViewModel() resolves to (both are scoped to this Activity)
    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Must run before super.onCreate — it swaps Theme.Athanly.Starting back to
        // Theme.Athanly. No keep-on-screen condition: the Compose splash does the waiting.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // English until the stored choice arrives (or when none is stored)
            val language by appViewModel.language.collectAsStateWithLifecycle()
            val context = LocalContext.current
            val localizedContext = remember(language) { 
                context.localizedFor(language) 
            }

            // Overriding resources/configuration/direction (not LocalContext) switches strings
            // and RTL while LocalContext stays the real Activity for permissions and intents
            CompositionLocalProvider(
                LocalResources provides localizedContext.resources,
                LocalConfiguration provides localizedContext.resources.configuration,
                LocalLayoutDirection provides when (language) {
                    Language.Arabic -> LayoutDirection.Rtl
                    else -> LayoutDirection.Ltr
                }
            ) {
                AthanlyTheme {
                    // AppRoute owns all navigation — MainActivity holds no routing logic
                    AppRoute(
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
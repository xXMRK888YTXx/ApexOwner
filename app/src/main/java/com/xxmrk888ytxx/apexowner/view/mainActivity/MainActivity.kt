package com.xxmrk888ytxx.apexowner.view.mainActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.xxmrk888ytxx.android.ToastManager
import com.xxmrk888ytxx.android.mvi.UiEvent
import com.xxmrk888ytxx.android.viewModel.stub.Stub
import com.xxmrk888ytxx.apexowner.core.Screen
import com.xxmrk888ytxx.apexowner.core.extension.ScreenContent
import com.xxmrk888ytxx.apexowner.domain.NavigationManager
import com.xxmrk888ytxx.compose.extension.setContentWithThemeAndProviders
import com.xxmrk888ytxx.compose.theme.AppSeedColors.MustardYellow
import com.xxmrk888ytxx.onboarding.OnboardingScreen
import com.xxmrk888ytxx.onboarding.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var toastManager: ToastManager

    @Inject
    lateinit var navigationManager: NavigationManager

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        splashScreen.setKeepOnScreenCondition { viewModel.isScreenLoading.value }
        setContentWithThemeAndProviders(
            navigator = navigationManager,
            toastManager = toastManager,
            themeColor = MutableStateFlow(MustardYellow),
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
            ) { paddingValues ->
                val backStack by navigationManager.backStack.collectAsState()
                NavDisplay(
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    backStack = backStack,
                    onBack = { navigationManager.navigateUp() },
                    predictivePopTransitionSpec = { swipeEdge ->
                        val enterTransition = fadeIn(
                            animationSpec = tween(
                                durationMillis = BACK_PRESS_ANIMATION_DURATION,
                                easing = LinearEasing
                            )
                        ) + scaleIn(
                            initialScale = 0.9f,
                            animationSpec = tween(
                                durationMillis = BACK_PRESS_ANIMATION_DURATION,
                                easing = FastOutSlowInEasing
                            )
                        )

                        val exitTransition = slideOutHorizontally(
                            targetOffsetX = { width -> if (swipeEdge == 0) width else -width },
                            animationSpec = tween(
                                durationMillis = BACK_PRESS_ANIMATION_DURATION,
                                easing = FastOutSlowInEasing
                            )
                        ) + fadeOut(
                            animationSpec = tween(
                                durationMillis = BACK_PRESS_ANIMATION_DURATION,
                                easing = LinearEasing
                            )
                        )

                        enterTransition togetherWith exitTransition
                    },
                    entryProvider = entryProvider {
                        entry<Screen.MainScreen> {
                            Text("Main Screen")
                        }

                        entry<Screen.OnboardingScreen> {
                            ScreenContent<Stub, UiEvent, OnboardingViewModel>(::OnboardingScreen)
                        }

                        entry<Screen.Stub> {
                            // Stub
                        }
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }

    private companion object {
        const val BACK_PRESS_ANIMATION_DURATION = 300
    }
}
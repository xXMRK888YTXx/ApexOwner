package com.xxmrk888ytxx.apexowner.view.mainActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.xxmrk888ytxx.apexowner.core.extension.ScreenContent
import com.xxmrk888ytxx.apexowner.core.navigation.BottomBarScreen
import com.xxmrk888ytxx.apexowner.core.navigation.Screen
import com.xxmrk888ytxx.apexowner.view.mainActivity.model.ApexOwnerBottomBarItem
import com.xxmrk888ytxx.apexowner.view.mainActivity.model.MainActivityEvent
import com.xxmrk888ytxx.apexowner.view.mainActivity.ui.ApexOwnerBottomBar
import com.xxmrk888ytxx.core.android.ToastManager
import com.xxmrk888ytxx.core.compose.extension.setContentWithThemeAndProviders
import com.xxmrk888ytxx.core.compose.theme.AppSeedColors.MustardYellow
import com.xxmrk888ytxx.feature.main.MainScreen
import com.xxmrk888ytxx.feature.main.MainViewModel
import com.xxmrk888ytxx.feature.main.model.MainScreenEvent
import com.xxmrk888ytxx.feature.main.model.ScreenState
import com.xxmrk888ytxx.feature.onboarding.OnboardingScreen
import com.xxmrk888ytxx.feature.onboarding.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import com.xxmrk888ytxx.core.android.mvi.UiEvent
import com.xxmrk888ytxx.core.android.viewModel.stub.Stub

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var toastManager: ToastManager

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        splashScreen.setKeepOnScreenCondition { viewModel.isScreenLoading.value }
        setContentWithThemeAndProviders(
            navigator = viewModel.navigator,
            toastManager = toastManager,
            themeColor = MutableStateFlow(MustardYellow),
        ) {
            val backStack by viewModel.backStack.collectAsState()
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    BottomBar(
                        backStack = backStack,
                        onBottomItemClicked = { viewModel.onEvent(MainActivityEvent.BottomItemClicked(it)) }
                    )
                }
            ) { paddingValues ->
                NavDisplay(
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    backStack = backStack,
                    onBack = { viewModel.onEvent(MainActivityEvent.NavigationUp) },
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
                            ScreenContent<ScreenState, MainScreenEvent, MainViewModel>(::MainScreen)
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

    @Composable
    private fun BottomBar(
        backStack: List<Screen>,
        onBottomItemClicked: (ApexOwnerBottomBarItem) -> Unit
    ) {
        val items = remember {
            ApexOwnerBottomBarItem.itemList
        }
        val bottomBarItemOfCurrentScreen = remember(backStack.lastOrNull()) {
            (backStack.lastOrNull() as? BottomBarScreen)?.itemId
        }
        AnimatedVisibility(bottomBarItemOfCurrentScreen != null) {
            ApexOwnerBottomBar(items, bottomBarItemOfCurrentScreen ?: -1) {
                if (it.id == bottomBarItemOfCurrentScreen) return@ApexOwnerBottomBar
                onBottomItemClicked(it)
            }
        }
    }

    private companion object {
        const val BACK_PRESS_ANIMATION_DURATION = 300
    }
}
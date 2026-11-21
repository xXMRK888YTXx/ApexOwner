package com.xxmrk888ytxx.apexowner.view.mainActivity

import android.os.Bundle
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.xxmrk888ytxx.apexowner.core.extension.ScreenContent
import com.xxmrk888ytxx.apexowner.R
import com.xxmrk888ytxx.apexowner.core.navigation.BottomBarScreen
import com.xxmrk888ytxx.apexowner.core.navigation.Screen
import com.xxmrk888ytxx.apexowner.view.mainActivity.model.ApexOwnerBottomBarItem
import com.xxmrk888ytxx.apexowner.view.mainActivity.model.MainActivityEvent
import com.xxmrk888ytxx.apexowner.view.mainActivity.ui.ApexOwnerBottomBar
import com.xxmrk888ytxx.core.base.android.ToastManager
import com.xxmrk888ytxx.core.base.compose.extension.setContentWithThemeAndProviders
import com.xxmrk888ytxx.core.base.compose.theme.AppSeedColors.MustardYellow
import com.xxmrk888ytxx.core.devicepolicy.ProfileOwnerManager
import com.xxmrk888ytxx.feature.main.MainScreen
import com.xxmrk888ytxx.feature.main.MainViewModel
import com.xxmrk888ytxx.feature.main.model.MainScreenEvent
import com.xxmrk888ytxx.feature.main.model.ScreenState
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.AppRestrictionModuleScreen
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.AppRestrictionModuleViewModel
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.model.AppRestrictionModuleUiEvent
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.GlobalPermissionScreen
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.GlobalPermissionViewModel
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.app.AppPermissionScreen
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.app.AppPermissionViewModel
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.app.model.AppPermissionUiEvent
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.model.GlobalPermissionScreenState
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.model.GlobalPermissionUiEvent
import com.xxmrk888ytxx.feature.managementmodule.workprofile.WorkProfileScreen
import com.xxmrk888ytxx.feature.managementmodule.workprofile.WorkProfileViewModel
import com.xxmrk888ytxx.feature.managementmodule.workprofile.model.WorkProfileScreenState
import com.xxmrk888ytxx.feature.managementmodule.workprofile.model.WorkProfileUiEvent
import com.xxmrk888ytxx.feature.settings.SettingsScreen
import com.xxmrk888ytxx.feature.location.LocationSpoofScreen
import com.xxmrk888ytxx.apexowner.feature.auth.AuthScreen
import com.xxmrk888ytxx.apexowner.feature.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.model.ScreenState as AppRestrictionScreenState

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var toastManager: ToastManager

    @Inject
    lateinit var profileOwnerManager: ProfileOwnerManager

    private val viewModel: MainActivityViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        profileOwnerManager.setupCrossProfileFilters()
        com.xxmrk888ytxx.apexowner.service.ApexPolicyService.start(this)
        splashScreen.setKeepOnScreenCondition { viewModel.isScreenLoading.value }
        if (intent?.getBooleanExtra(EXTRA_OPEN_RESTRICTIONS, false) == true) {
            viewModel.navigator.navigate(Screen.DeviceRestrictionModuleScreen)
        }
        if (intent?.getBooleanExtra(EXTRA_OPEN_PERMISSIONS, false) == true) {
            viewModel.navigator.navigate(Screen.GlobalPermissionModuleScreen)
        }
        setContentWithThemeAndProviders(
            useDarkTheme = true,
            navigator = viewModel.navigator,
            toastManager = toastManager,
            themeColor = MutableStateFlow(MustardYellow),
        ) {
            val authState by authViewModel.state.collectAsState()
            val biometricAvailable = BiometricManager.from(this@MainActivity).canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
            ) == BiometricManager.BIOMETRIC_SUCCESS
            LaunchedEffect(authState.isLocked, authState.biometricEnabled, biometricAvailable) {
                if (authState.isLocked && authState.biometricEnabled && biometricAvailable) {
                    authenticateWithBiometric()
                }
            }
            if (!authState.isReady || authState.isLocked) {
                AuthScreen(
                    state = authState,
                    onUnlock = authViewModel::unlock,
                    onBiometric = ::authenticateWithBiometric,
                    biometricAvailable = biometricAvailable
                )
                return@setContentWithThemeAndProviders
            }
            val backStack by viewModel.backStack.collectAsState()
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    BottomBar(
                        backStack = backStack,
                        onBottomItemClicked = {
                            viewModel.onEvent(
                                MainActivityEvent.BottomItemClicked(it)
                            )
                        }
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
                        entry<Screen.WorkProfileModuleScreen> {
                            ScreenContent<WorkProfileScreenState, WorkProfileUiEvent, WorkProfileViewModel>(
                                ::WorkProfileScreen
                            )
                        }

                        entry<Screen.DeviceRestrictionModuleScreen> {
                            ScreenContent<AppRestrictionScreenState, AppRestrictionModuleUiEvent, AppRestrictionModuleViewModel>(
                                ::AppRestrictionModuleScreen
                            )
                        }

                        entry<Screen.GlobalPermissionModuleScreen> {
                            ScreenContent<GlobalPermissionScreenState, GlobalPermissionUiEvent, GlobalPermissionViewModel>(
                                ::GlobalPermissionScreen
                            )
                        }

                        entry<Screen.LocationSpoofScreen> {
                            LocationSpoofScreen()
                        }

                        entry<Screen.AppPermissionsModuleScreen> { navKey ->
                            val viewModel = hiltViewModel<AppPermissionViewModel>()
                            LaunchedEffect(navKey.packageName) {
                                viewModel.onEvent(
                                    AppPermissionUiEvent.Init(
                                        navKey.packageName,
                                        navKey.appName
                                    )
                                )
                            }
                            val state by viewModel.state.collectAsState()
                            AppPermissionScreen(
                                screenState = state,
                                onEvent = viewModel::onEvent,
                                sideEffect = viewModel.sideEffectFlow
                            )
                        }

                        entry<Screen.MainScreen> {
                            ScreenContent<ScreenState, MainScreenEvent, MainViewModel>(::MainScreen)
                        }

                        entry<Screen.SettingsScreen> {
                            SettingsScreen()
                        }

                        entry<Screen.OnboardingScreen> {
                            // Onboarding is disabled for now. Keep the route as a compatibility
                            // fallback so an old/restored navigation state cannot show it.
                            ScreenContent<ScreenState, MainScreenEvent, MainViewModel>(::MainScreen)
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

    override fun onResume() {
        super.onResume()
        profileOwnerManager.setupCrossProfileFilters()
        viewModel.onEvent(MainActivityEvent.OnResume)
    }

    override fun onStop() {
        super.onStop()
        authViewModel.lock()
    }

    private fun authenticateWithBiometric() {
        val prompt = BiometricPrompt(
            this,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    authViewModel.unlockWithBiometric()
                }
            }
        )
        prompt.authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.app_lock_title))
                .setSubtitle(getString(R.string.app_lock_message))
                .setNegativeButtonText(getString(R.string.app_lock_password))
                .build()
        )
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

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra(EXTRA_OPEN_RESTRICTIONS, false)) {
            viewModel.navigator.navigate(Screen.DeviceRestrictionModuleScreen)
        }
        if (intent.getBooleanExtra(EXTRA_OPEN_PERMISSIONS, false)) {
            viewModel.navigator.navigate(Screen.GlobalPermissionModuleScreen)
        }
    }

    companion object {
        const val ACTION_CLONE_APP = "com.xxmrk888ytxx.apexowner.ACTION_CLONE_APP"
        const val EXTRA_PACKAGE_NAME = "EXTRA_PACKAGE_NAME"
        const val EXTRA_OPEN_RESTRICTIONS = "EXTRA_OPEN_RESTRICTIONS"
        const val EXTRA_OPEN_PERMISSIONS = "EXTRA_OPEN_PERMISSIONS"
        private const val BACK_PRESS_ANIMATION_DURATION = 300
    }
}

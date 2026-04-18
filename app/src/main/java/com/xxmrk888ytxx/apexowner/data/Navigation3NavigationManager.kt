package com.xxmrk888ytxx.apexowner.data

import com.xxmrk888ytxx.apexowner.core.Screen
import com.xxmrk888ytxx.apexowner.domain.NavigationManager
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Navigation3NavigationManager @Inject constructor() : NavigationManager {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val navChannel = Channel<NavCommand>(Channel.BUFFERED)

    private val _backStack = MutableStateFlow<List<Screen>>(listOf(Screen.Stub))
    override val backStack: StateFlow<List<Screen>> = _backStack.asStateFlow()

    private suspend fun processCommand(command: NavCommand) {
        when (command) {
            is NavCommand.Navigate -> {
                val current = _backStack.value
                if (current.lastOrNull() != command.screen) {
                    _backStack.emit(current + command.screen)
                }
            }

            is NavCommand.SetStartDestination -> {
                _backStack.emit(listOf(command.screen))
                command.deferred.complete(Unit)
            }

            is NavCommand.NavigateUp -> {
                val current = _backStack.value
                if (current.size > 1) {
                    _backStack.emit(current.dropLast(1))
                }
            }

            is NavCommand.Replace -> {
                val current = _backStack.value
                if (current.isNotEmpty()) {
                    _backStack.emit(current.dropLast(1) + command.screen)
                }
            }
        }
    }
    override fun navigateUp() {
        navChannel.trySend(NavCommand.NavigateUp)
    }

    override fun navigate(screen: Screen) {
        navChannel.trySend(NavCommand.Navigate(screen))
    }

    override fun replaceCurrentScreenAndNavigate(screen: Screen) {
        navChannel.trySend(NavCommand.Replace(screen))
    }

    override suspend fun setStartDestination(screen: Screen) {
        val deferred = CompletableDeferred<Unit>()
        navChannel.send(NavCommand.SetStartDestination(screen, deferred))
        deferred.await()
    }

    init {
        scope.launch {
            for (command in navChannel) {
                processCommand(command)
            }
        }
    }

    private sealed interface NavCommand {
        data class Navigate(val screen: Screen) : NavCommand
        data class SetStartDestination(
            val screen: Screen,
            val deferred: CompletableDeferred<Unit>
        ) : NavCommand

        data class Replace(val screen: Screen) : NavCommand
        data object NavigateUp : NavCommand
    }
}
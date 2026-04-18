@file:Suppress("DEPRECATION")

package com.xxmrk888ytxx.android.viewModel.stub

import com.xxmrk888ytxx.android.mvi.UiEvent
import com.xxmrk888ytxx.android.viewModel.ApexOwnerViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

/**
 * A temporary ViewModel used for screens without a defined state.
 * Allows development to proceed without overriding specific state logic
 * until the actual requirements are implemented.
 * * Exception: Used for Activities that do not persist state
 * or act as simple containers.
 */
@Deprecated(
    message = "This is a placeholder for early development stages. Replace with a real ViewModel once the screen state is defined.",
    level = DeprecationLevel.WARNING
)
open class StubViewModel : ApexOwnerViewModel<Stub, UiEvent>(Stub) {

    override val state: StateFlow<Stub> = flowOf(Stub).stateWhileSubscribed()

    override fun onEvent(event: UiEvent) = Unit
}
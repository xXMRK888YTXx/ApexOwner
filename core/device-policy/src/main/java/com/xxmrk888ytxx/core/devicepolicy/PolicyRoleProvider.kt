package com.xxmrk888ytxx.core.devicepolicy

import com.xxmrk888ytxx.core.devicepolicy.model.PolicyState
import kotlinx.coroutines.flow.Flow

interface PolicyRoleProvider {
    val policyState: Flow<PolicyState>
    suspend fun updatePolicyState()
}

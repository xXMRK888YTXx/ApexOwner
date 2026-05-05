package com.xxmrk888ytxx.apexowner.featureContract.main

import android.content.Intent
import com.xxmrk888ytxx.core.devicepolicy.WorkProfileOwnerComponentManager
import com.xxmrk888ytxx.feature.main.contract.ProvideIntentForWorkProfileCreationContract
import javax.inject.Inject

class ProvideIntentForWorkProfileCreationContractImpl @Inject constructor(
    private val workProfileOwnerComponentManager: WorkProfileOwnerComponentManager
) : ProvideIntentForWorkProfileCreationContract {
    override val intentForWorkProfileCreation: Intent
        get() = workProfileOwnerComponentManager.workProfileCreateIntent
}
package com.xxmrk888ytxx.core.devicepolicy.exception

import com.xxmrk888ytxx.core.base.android.exception.ApexOwnerException

class AppNotProfileOwnerException(
    message: String = "App is neither Profile Owner nor Device Owner"
) : ApexOwnerException(message)

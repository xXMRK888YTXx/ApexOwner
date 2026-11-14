package com.xxmrk888ytxx.feature.managementmodule.workprofile.model

data class WorkProfileApp(
    val packageName: String,
    val name: String,
    val isSystem: Boolean,
    val isFrozen: Boolean,
    val isInstalledInWorkProfile: Boolean,
    val hasLauncher: Boolean = true,
    val isInstalledInOtherProfile: Boolean = false,
    val isOneShot: Boolean = false,
    val isAutoFreeze: Boolean = false
)

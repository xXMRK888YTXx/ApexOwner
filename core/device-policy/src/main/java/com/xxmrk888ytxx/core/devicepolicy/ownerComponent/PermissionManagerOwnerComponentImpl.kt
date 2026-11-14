package com.xxmrk888ytxx.core.devicepolicy.ownerComponent

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.xxmrk888ytxx.core.base.android.logs.Logger
import com.xxmrk888ytxx.core.devicepolicy.PermissionManagerOwnerComponent
import com.xxmrk888ytxx.core.devicepolicy.exception.AppNotDeviceOwnerException
import com.xxmrk888ytxx.core.devicepolicy.model.AppPermissionPolicy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PermissionManagerOwnerComponentImpl @Inject constructor(
    devicePolicyManager: DevicePolicyManager,
    @ApplicationContext context: Context
) : BaseOwnerComponent(devicePolicyManager, context), PermissionManagerOwnerComponent {

    override val isAppDeviceOwner: Boolean
        get() = checkIsAppDeviceOwner()

    override val isAppProfileOwner: Boolean
        get() = checkIsAppProfileOwner()

    private val prefs by lazy {
        context.getSharedPreferences("global_permissions_prefs", Context.MODE_PRIVATE)
    }

    private val appPrefs by lazy {
        context.getSharedPreferences("app_permission_policies_prefs", Context.MODE_PRIVATE)
    }

    private val _disabledPermissions = MutableStateFlow(loadDisabledPermissions())
    override val disabledPermissions: Flow<Set<String>> = _disabledPermissions.asStateFlow()

    private val _appPolicies = MutableStateFlow(loadAllAppPolicies())

    private fun loadDisabledPermissions(): Set<String> {
        return prefs.getStringSet("disabled_permission_ids", emptySet())?.toSet() ?: emptySet()
    }

    private fun saveDisabledPermissions(set: Set<String>) {
        prefs.edit().putStringSet("disabled_permission_ids", set).apply()
    }

    private fun loadAllAppPolicies(): Map<String, Map<String, AppPermissionPolicy>> {
        val result = mutableMapOf<String, MutableMap<String, AppPermissionPolicy>>()
        val allEntries = appPrefs.all
        for ((key, value) in allEntries) {
            if (value !is String) continue
            val parts = key.split(":", limit = 2)
            if (parts.size == 2) {
                val pkg = parts[0]
                val permId = parts[1]
                val policy = try {
                    AppPermissionPolicy.valueOf(value)
                } catch (_: Exception) {
                    AppPermissionPolicy.DEFAULT
                }
                result.getOrPut(pkg) { mutableMapOf() }[permId] = policy
            }
        }
        return result
    }

    private fun getAppPermissionPolicy(packageName: String, permissionId: String): AppPermissionPolicy {
        return _appPolicies.value[packageName]?.get(permissionId) ?: AppPermissionPolicy.DEFAULT
    }

    override fun getAppPermissionPolicies(packageName: String): Flow<Map<String, AppPermissionPolicy>> {
        return _appPolicies.map { it[packageName] ?: emptyMap() }
    }

    override fun getAppRequestedPermissions(packageName: String): Set<String> {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong())
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            }
            packageInfo.requestedPermissions?.toSet() ?: emptySet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    override suspend fun setAppPermissionPolicy(
        packageName: String,
        permissionId: String,
        policy: AppPermissionPolicy
    ) = withContext(Dispatchers.IO) {
        if (!checkCanManageOwnerPolicies()) throw AppNotDeviceOwnerException()

        val currentMap = _appPolicies.value.toMutableMap()
        val pkgMap = (currentMap[packageName] ?: emptyMap()).toMutableMap()
        if (policy == AppPermissionPolicy.DEFAULT) {
            pkgMap.remove(permissionId)
            appPrefs.edit().remove("$packageName:$permissionId").apply()
        } else {
            pkgMap[permissionId] = policy
            appPrefs.edit().putString("$packageName:$permissionId", policy.name).apply()
        }

        if (pkgMap.isEmpty()) {
            currentMap.remove(packageName)
        } else {
            currentMap[packageName] = pkgMap
        }
        _appPolicies.value = currentMap

        val targetState = when (policy) {
            AppPermissionPolicy.FORCE_GRANTED -> DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED
            AppPermissionPolicy.FORCE_DENIED -> DevicePolicyManager.PERMISSION_GRANT_STATE_DENIED
            AppPermissionPolicy.DEFAULT -> {
                if (permissionId in _disabledPermissions.value) {
                    DevicePolicyManager.PERMISSION_GRANT_STATE_DENIED
                } else {
                    DevicePolicyManager.PERMISSION_GRANT_STATE_DEFAULT
                }
            }
        }

        val permissionsToApply = getManifestPermissions(permissionId)
        val requestedPermissions = getAppRequestedPermissions(packageName)

        for (perm in permissionsToApply) {
            if (requestedPermissions.isEmpty() || requestedPermissions.contains(perm)) {
                try {
                    val res = devicePolicyManager.setPermissionGrantState(
                        deviceOwnerReceiver,
                        packageName,
                        perm,
                        targetState
                    )
                    Logger.writeDebugLog("setAppPermissionPolicy: setPermissionGrantState($packageName, $perm, $targetState) -> $res")
                } catch (e: Exception) {
                    Logger.writeErrorLog("Failed to set $perm state=$targetState for $packageName: ${e.message}")
                }
            }
        }
    }

    override suspend fun setPermissionGloballyDisabled(permissionId: String, isDisable: Boolean) = withContext(Dispatchers.IO) {
        if (!checkCanManageOwnerPolicies()) throw AppNotDeviceOwnerException()

        val currentSet = _disabledPermissions.value.toMutableSet()
        if (isDisable) {
            currentSet.add(permissionId)
        } else {
            currentSet.remove(permissionId)
        }
        _disabledPermissions.value = currentSet
        saveDisabledPermissions(currentSet)

        val permissionsToApply = getManifestPermissions(permissionId)
        if (permissionsToApply.isEmpty()) return@withContext

        val packages = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getInstalledPackages(0)
            }
        } catch (e: Exception) {
            Logger.writeErrorLog("Failed to get installed packages: ${e.message}")
            emptyList()
        }

        val myPackage = context.packageName
        Logger.writeDebugLog("Applying permission $permissionId globally (isDisable=$isDisable) to ${packages.size} packages")

        for (pkg in packages) {
            val pkgName = pkg.packageName
            if (pkgName == myPackage) continue

            val appPolicy = getAppPermissionPolicy(pkgName, permissionId)
            val targetState = when (appPolicy) {
                AppPermissionPolicy.FORCE_GRANTED -> DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED
                AppPermissionPolicy.FORCE_DENIED -> DevicePolicyManager.PERMISSION_GRANT_STATE_DENIED
                AppPermissionPolicy.DEFAULT -> if (isDisable) {
                    DevicePolicyManager.PERMISSION_GRANT_STATE_DENIED
                } else {
                    DevicePolicyManager.PERMISSION_GRANT_STATE_DEFAULT
                }
            }

            val requestedPermissions: Array<out String>? = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.packageManager.getPackageInfo(
                        pkgName,
                        PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong())
                    ).requestedPermissions
                } else {
                    @Suppress("DEPRECATION")
                    context.packageManager.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS).requestedPermissions
                }
            } catch (_: Exception) {
                null
            }

            for (perm in permissionsToApply) {
                if (requestedPermissions == null || requestedPermissions.contains(perm)) {
                    try {
                        val res = devicePolicyManager.setPermissionGrantState(
                            deviceOwnerReceiver,
                            pkgName,
                            perm,
                            targetState
                        )
                        Logger.writeDebugLog("setPermissionGrantState($pkgName, $perm, $targetState) -> $res")
                    } catch (e: Exception) {
                        Logger.writeErrorLog("Failed to set $perm state=$targetState for $pkgName: ${e.message}")
                    }
                }
            }
        }
    }

    override suspend fun applyPermissionsToPackage(packageName: String) = withContext(Dispatchers.IO) {
        if (!checkCanManageOwnerPolicies()) return@withContext
        if (packageName == context.packageName) return@withContext
        val currentDisabled = _disabledPermissions.value
        val requestedPermissions = getAppRequestedPermissions(packageName)

        for (permissionId in ALL_SUPPORTED_PERMISSIONS) {
            val policy = getAppPermissionPolicy(packageName, permissionId)
            val targetState = when (policy) {
                AppPermissionPolicy.FORCE_GRANTED -> DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED
                AppPermissionPolicy.FORCE_DENIED -> DevicePolicyManager.PERMISSION_GRANT_STATE_DENIED
                AppPermissionPolicy.DEFAULT -> {
                    if (permissionId in currentDisabled) {
                        DevicePolicyManager.PERMISSION_GRANT_STATE_DENIED
                    } else {
                        null
                    }
                }
            }

            if (targetState != null) {
                val permissions = getManifestPermissions(permissionId)
                for (perm in permissions) {
                    if (requestedPermissions.isEmpty() || requestedPermissions.contains(perm)) {
                        try {
                            val res = devicePolicyManager.setPermissionGrantState(
                                deviceOwnerReceiver,
                                packageName,
                                perm,
                                targetState
                            )
                            Logger.writeDebugLog("applyPermissionsToPackage: set $perm -> $res (state=$targetState) on $packageName")
                        } catch (e: Exception) {
                            Logger.writeErrorLog("applyPermissionsToPackage failed for $perm on $packageName: ${e.message}")
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun getManifestPermissions(permissionId: String): List<String> = when (permissionId) {
            PERMISSION_POST_NOTIFICATIONS -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) listOf(Manifest.permission.POST_NOTIFICATIONS) else emptyList()
            PERMISSION_CAMERA -> listOf(Manifest.permission.CAMERA)
            PERMISSION_RECORD_AUDIO -> listOf(Manifest.permission.RECORD_AUDIO)
            PERMISSION_ACCESS_FINE_LOCATION -> listOf(Manifest.permission.ACCESS_FINE_LOCATION)
            PERMISSION_ACCESS_COARSE_LOCATION -> listOf(Manifest.permission.ACCESS_COARSE_LOCATION)
            PERMISSION_ACCESS_BACKGROUND_LOCATION -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) listOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION) else emptyList()
            PERMISSION_READ_CONTACTS -> listOf(Manifest.permission.READ_CONTACTS)
            PERMISSION_WRITE_CONTACTS -> listOf(Manifest.permission.WRITE_CONTACTS)
            PERMISSION_GET_ACCOUNTS -> listOf(Manifest.permission.GET_ACCOUNTS)
            PERMISSION_READ_CALENDAR -> listOf(Manifest.permission.READ_CALENDAR)
            PERMISSION_WRITE_CALENDAR -> listOf(Manifest.permission.WRITE_CALENDAR)
            PERMISSION_READ_PHONE_STATE -> listOf(Manifest.permission.READ_PHONE_STATE)
            PERMISSION_CALL_PHONE -> listOf(Manifest.permission.CALL_PHONE)
            PERMISSION_READ_CALL_LOG -> listOf(Manifest.permission.READ_CALL_LOG)
            PERMISSION_WRITE_CALL_LOG -> listOf(Manifest.permission.WRITE_CALL_LOG)
            PERMISSION_READ_PHONE_NUMBERS -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) listOf(Manifest.permission.READ_PHONE_NUMBERS) else emptyList()
            PERMISSION_ANSWER_PHONE_CALLS -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) listOf(Manifest.permission.ANSWER_PHONE_CALLS) else emptyList()
            PERMISSION_SEND_SMS -> listOf(Manifest.permission.SEND_SMS)
            PERMISSION_READ_SMS -> listOf(Manifest.permission.READ_SMS)
            PERMISSION_RECEIVE_SMS -> listOf(Manifest.permission.RECEIVE_SMS)
            PERMISSION_RECEIVE_MMS -> listOf(Manifest.permission.RECEIVE_MMS)
            PERMISSION_READ_EXTERNAL_STORAGE -> listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            PERMISSION_WRITE_EXTERNAL_STORAGE -> listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            PERMISSION_READ_MEDIA_IMAGES -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) listOf(Manifest.permission.READ_MEDIA_IMAGES) else emptyList()
            PERMISSION_READ_MEDIA_VIDEO -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) listOf(Manifest.permission.READ_MEDIA_VIDEO) else emptyList()
            PERMISSION_READ_MEDIA_AUDIO -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) listOf(Manifest.permission.READ_MEDIA_AUDIO) else emptyList()
            PERMISSION_READ_MEDIA_VISUAL_USER_SELECTED -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) listOf(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) else emptyList()
            PERMISSION_ACCESS_MEDIA_LOCATION -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) listOf(Manifest.permission.ACCESS_MEDIA_LOCATION) else emptyList()
            PERMISSION_BLUETOOTH_SCAN -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) listOf(Manifest.permission.BLUETOOTH_SCAN) else emptyList()
            PERMISSION_BLUETOOTH_CONNECT -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) listOf(Manifest.permission.BLUETOOTH_CONNECT) else emptyList()
            PERMISSION_BLUETOOTH_ADVERTISE -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) listOf(Manifest.permission.BLUETOOTH_ADVERTISE) else emptyList()
            PERMISSION_NEARBY_WIFI_DEVICES -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) listOf(Manifest.permission.NEARBY_WIFI_DEVICES) else emptyList()
            PERMISSION_UWB_RANGING -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) listOf(Manifest.permission.UWB_RANGING) else emptyList()
            PERMISSION_BODY_SENSORS -> listOf(Manifest.permission.BODY_SENSORS)
            PERMISSION_BODY_SENSORS_BACKGROUND -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) listOf(Manifest.permission.BODY_SENSORS_BACKGROUND) else emptyList()
            PERMISSION_ACTIVITY_RECOGNITION -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) listOf(Manifest.permission.ACTIVITY_RECOGNITION) else emptyList()
            else -> emptyList()
        }

        const val PERMISSION_POST_NOTIFICATIONS = "POST_NOTIFICATIONS"
        const val PERMISSION_CAMERA = "CAMERA"
        const val PERMISSION_RECORD_AUDIO = "RECORD_AUDIO"
        const val PERMISSION_ACCESS_FINE_LOCATION = "ACCESS_FINE_LOCATION"
        const val PERMISSION_ACCESS_COARSE_LOCATION = "ACCESS_COARSE_LOCATION"
        const val PERMISSION_ACCESS_BACKGROUND_LOCATION = "ACCESS_BACKGROUND_LOCATION"
        const val PERMISSION_READ_CONTACTS = "READ_CONTACTS"
        const val PERMISSION_WRITE_CONTACTS = "WRITE_CONTACTS"
        const val PERMISSION_GET_ACCOUNTS = "GET_ACCOUNTS"
        const val PERMISSION_READ_CALENDAR = "READ_CALENDAR"
        const val PERMISSION_WRITE_CALENDAR = "WRITE_CALENDAR"
        const val PERMISSION_READ_PHONE_STATE = "READ_PHONE_STATE"
        const val PERMISSION_CALL_PHONE = "CALL_PHONE"
        const val PERMISSION_READ_CALL_LOG = "READ_CALL_LOG"
        const val PERMISSION_WRITE_CALL_LOG = "WRITE_CALL_LOG"
        const val PERMISSION_READ_PHONE_NUMBERS = "READ_PHONE_NUMBERS"
        const val PERMISSION_ANSWER_PHONE_CALLS = "ANSWER_PHONE_CALLS"
        const val PERMISSION_SEND_SMS = "SEND_SMS"
        const val PERMISSION_READ_SMS = "READ_SMS"
        const val PERMISSION_RECEIVE_SMS = "RECEIVE_SMS"
        const val PERMISSION_RECEIVE_MMS = "RECEIVE_MMS"
        const val PERMISSION_READ_EXTERNAL_STORAGE = "READ_EXTERNAL_STORAGE"
        const val PERMISSION_WRITE_EXTERNAL_STORAGE = "WRITE_EXTERNAL_STORAGE"
        const val PERMISSION_READ_MEDIA_IMAGES = "READ_MEDIA_IMAGES"
        const val PERMISSION_READ_MEDIA_VIDEO = "READ_MEDIA_VIDEO"
        const val PERMISSION_READ_MEDIA_AUDIO = "READ_MEDIA_AUDIO"
        const val PERMISSION_READ_MEDIA_VISUAL_USER_SELECTED = "READ_MEDIA_VISUAL_USER_SELECTED"
        const val PERMISSION_ACCESS_MEDIA_LOCATION = "ACCESS_MEDIA_LOCATION"
        const val PERMISSION_BLUETOOTH_SCAN = "BLUETOOTH_SCAN"
        const val PERMISSION_BLUETOOTH_CONNECT = "BLUETOOTH_CONNECT"
        const val PERMISSION_BLUETOOTH_ADVERTISE = "BLUETOOTH_ADVERTISE"
        const val PERMISSION_NEARBY_WIFI_DEVICES = "NEARBY_WIFI_DEVICES"
        const val PERMISSION_UWB_RANGING = "UWB_RANGING"
        const val PERMISSION_BODY_SENSORS = "BODY_SENSORS"
        const val PERMISSION_BODY_SENSORS_BACKGROUND = "BODY_SENSORS_BACKGROUND"
        const val PERMISSION_ACTIVITY_RECOGNITION = "ACTIVITY_RECOGNITION"

        val ALL_SUPPORTED_PERMISSIONS = listOf(
            PERMISSION_POST_NOTIFICATIONS,
            PERMISSION_CAMERA,
            PERMISSION_RECORD_AUDIO,
            PERMISSION_ACCESS_FINE_LOCATION,
            PERMISSION_ACCESS_COARSE_LOCATION,
            PERMISSION_ACCESS_BACKGROUND_LOCATION,
            PERMISSION_READ_CONTACTS,
            PERMISSION_WRITE_CONTACTS,
            PERMISSION_GET_ACCOUNTS,
            PERMISSION_READ_CALENDAR,
            PERMISSION_WRITE_CALENDAR,
            PERMISSION_READ_PHONE_STATE,
            PERMISSION_CALL_PHONE,
            PERMISSION_READ_CALL_LOG,
            PERMISSION_WRITE_CALL_LOG,
            PERMISSION_READ_PHONE_NUMBERS,
            PERMISSION_ANSWER_PHONE_CALLS,
            PERMISSION_SEND_SMS,
            PERMISSION_READ_SMS,
            PERMISSION_RECEIVE_SMS,
            PERMISSION_RECEIVE_MMS,
            PERMISSION_READ_EXTERNAL_STORAGE,
            PERMISSION_WRITE_EXTERNAL_STORAGE,
            PERMISSION_READ_MEDIA_IMAGES,
            PERMISSION_READ_MEDIA_VIDEO,
            PERMISSION_READ_MEDIA_AUDIO,
            PERMISSION_READ_MEDIA_VISUAL_USER_SELECTED,
            PERMISSION_ACCESS_MEDIA_LOCATION,
            PERMISSION_BLUETOOTH_SCAN,
            PERMISSION_BLUETOOTH_CONNECT,
            PERMISSION_BLUETOOTH_ADVERTISE,
            PERMISSION_NEARBY_WIFI_DEVICES,
            PERMISSION_UWB_RANGING,
            PERMISSION_BODY_SENSORS,
            PERMISSION_BODY_SENSORS_BACKGROUND,
            PERMISSION_ACTIVITY_RECOGNITION
        )
    }
}

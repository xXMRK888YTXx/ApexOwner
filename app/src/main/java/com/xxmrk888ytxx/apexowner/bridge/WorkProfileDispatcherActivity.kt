package com.xxmrk888ytxx.apexowner.bridge

import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.xxmrk888ytxx.core.base.android.logs.Logger
import com.xxmrk888ytxx.core.devicepolicy.ProfileOwnerManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WorkProfileDispatcherActivity : ComponentActivity() {

    @Inject
    lateinit var profileOwnerManager: ProfileOwnerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileOwnerManager.setupCrossProfileFilters()
        val action = intent?.action
        val packageName = intent?.getStringExtra(EXTRA_PACKAGE_NAME)
        Logger.writeDebugLog("WorkProfileDispatcherActivity: received action $action for package: $packageName")

        if (action == ACTION_INSTALL_CALLBACK) {
            val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)
            when (status) {
                PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                    val confirmIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(Intent.EXTRA_INTENT, Intent::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(Intent.EXTRA_INTENT)
                    }
                    if (confirmIntent != null) {
                        confirmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(confirmIntent)
                    }
                }
                PackageInstaller.STATUS_SUCCESS -> {
                    Toast.makeText(this, "Приложение успешно установлено в рабочий профиль", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val msg = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE) ?: "Ошибка установки"
                    Logger.writeErrorLog("PackageInstaller status $status: $msg")
                }
            }
            finish()
            return
        }

        if (action == ACTION_REMOVE_WORK_PROFILE) {
            CoroutineScope(Dispatchers.Main).launch {
                profileOwnerManager.removeWorkProfile().fold(
                    onSuccess = {
                        Toast.makeText(this@WorkProfileDispatcherActivity, "Рабочий профиль удален", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = {
                        Toast.makeText(this@WorkProfileDispatcherActivity, "Ошибка удаления: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                )
                finish()
            }
            return
        }

        if (packageName.isNullOrBlank()) {
            finish()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            when (action) {
                ACTION_CLONE_APP -> {
                    val apkPath = intent?.getStringExtra(EXTRA_APK_PATH)
                    val splitPaths = intent?.getStringArrayExtra(EXTRA_SPLIT_PATHS)
                    profileOwnerManager.installExistingPackage(packageName, apkPath, splitPaths).fold(
                        onSuccess = {
                            Logger.writeDebugLog("WorkProfileDispatcherActivity: successfully cloned $packageName")
                            Toast.makeText(this@WorkProfileDispatcherActivity, "Клонировано: $packageName", Toast.LENGTH_SHORT).show()
                        },
                        onFailure = {
                            val err = it.message ?: "Неизвестная ошибка"
                            Logger.writeErrorLog("WorkProfileDispatcherActivity: failed to clone $packageName: $err")
                            Toast.makeText(this@WorkProfileDispatcherActivity, "Ошибка: $err", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                ACTION_FREEZE_APP -> {
                    profileOwnerManager.setApplicationHidden(packageName, true).fold(
                        onSuccess = {
                            Toast.makeText(this@WorkProfileDispatcherActivity, "Заморожено: $packageName", Toast.LENGTH_SHORT).show()
                        },
                        onFailure = {
                            Toast.makeText(this@WorkProfileDispatcherActivity, "Ошибка заморозки: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                ACTION_UNFREEZE_APP -> {
                    profileOwnerManager.setApplicationHidden(packageName, false).fold(
                        onSuccess = {
                            Toast.makeText(this@WorkProfileDispatcherActivity, "Разморожено: $packageName", Toast.LENGTH_SHORT).show()
                        },
                        onFailure = {
                            Toast.makeText(this@WorkProfileDispatcherActivity, "Ошибка разморозки: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
            finish()
        }
    }

    companion object {
        const val ACTION_CLONE_APP = "com.xxmrk888ytxx.apexowner.ACTION_CLONE_APP"
        const val ACTION_FREEZE_APP = "com.xxmrk888ytxx.apexowner.ACTION_FREEZE_APP"
        const val ACTION_UNFREEZE_APP = "com.xxmrk888ytxx.apexowner.ACTION_UNFREEZE_APP"
        const val ACTION_REMOVE_WORK_PROFILE = "com.xxmrk888ytxx.apexowner.ACTION_REMOVE_WORK_PROFILE"
        const val ACTION_INSTALL_CALLBACK = "com.xxmrk888ytxx.apexowner.ACTION_INSTALL_CALLBACK"
        const val EXTRA_PACKAGE_NAME = "EXTRA_PACKAGE_NAME"
        const val EXTRA_APK_PATH = "EXTRA_APK_PATH"
        const val EXTRA_SPLIT_PATHS = "EXTRA_SPLIT_PATHS"
    }
}

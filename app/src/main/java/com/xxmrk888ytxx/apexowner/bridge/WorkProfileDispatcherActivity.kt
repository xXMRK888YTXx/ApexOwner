package com.xxmrk888ytxx.apexowner.bridge

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.xxmrk888ytxx.apexowner.view.mainActivity.MainActivity
import com.xxmrk888ytxx.core.base.android.logs.Logger
import com.xxmrk888ytxx.core.devicepolicy.OneShotAppManagerOwnerComponent
import com.xxmrk888ytxx.core.devicepolicy.ProfileOwnerManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class WorkProfileDispatcherActivity : ComponentActivity() {

    @Inject
    lateinit var profileOwnerManager: ProfileOwnerManager

    @Inject
    lateinit var oneShotAppManagerOwnerComponent: OneShotAppManagerOwnerComponent

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
                    Toast.makeText(this, "Приложение успешно установлено", Toast.LENGTH_SHORT).show()
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

        if (action == ACTION_OPEN_RESTRICTIONS) {
            val mainIntent = Intent(this, MainActivity::class.java).apply {
                putExtra(MainActivity.EXTRA_OPEN_RESTRICTIONS, true)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(mainIntent)
            finish()
            return
        }

        if (action == ACTION_OPEN_PERMISSIONS) {
            val mainIntent = Intent(this, MainActivity::class.java).apply {
                putExtra(MainActivity.EXTRA_OPEN_PERMISSIONS, true)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(mainIntent)
            finish()
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

                ACTION_CLONE_TO_PERSONAL -> {
                    val apkPath = intent?.getStringExtra(EXTRA_APK_PATH)
                    val splitPaths = intent?.getStringArrayExtra(EXTRA_SPLIT_PATHS)
                    installInCurrentProfile(packageName, apkPath, splitPaths)
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
                            Toast.makeText(
                                this@WorkProfileDispatcherActivity,
                                "Ошибка: ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }

                ACTION_TOGGLE_ONE_SHOT -> {
                    val isOneShot = intent?.getBooleanExtra(EXTRA_IS_ONE_SHOT, false) ?: false
                    oneShotAppManagerOwnerComponent.setOneShotApp(packageName, isOneShot).fold(
                        onSuccess = {
                            val msg = if (isOneShot) "One-shot режим включен" else "One-shot режим выключен"
                            Toast.makeText(this@WorkProfileDispatcherActivity, msg, Toast.LENGTH_SHORT).show()
                        },
                        onFailure = {
                            Toast.makeText(this@WorkProfileDispatcherActivity, "Ошибка: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                ACTION_RESET_ONE_SHOT -> {
                    oneShotAppManagerOwnerComponent.resetOneShotApp(packageName).fold(
                        onSuccess = {
                            Toast.makeText(this@WorkProfileDispatcherActivity, "Данные сброшены и приложение заморожено", Toast.LENGTH_SHORT).show()
                        },
                        onFailure = {
                            Toast.makeText(this@WorkProfileDispatcherActivity, "Ошибка: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                ACTION_TOGGLE_AUTO_FREEZE -> {
                    val isAutoFreeze = intent?.getBooleanExtra(EXTRA_IS_AUTO_FREEZE, false) ?: false
                    oneShotAppManagerOwnerComponent.setAutoFreezeApp(packageName, isAutoFreeze).fold(
                        onSuccess = {
                            val msg = if (isAutoFreeze) "Режим автозаморозки включен" else "Режим автозаморозки выключен"
                            Toast.makeText(this@WorkProfileDispatcherActivity, msg, Toast.LENGTH_SHORT).show()
                        },
                        onFailure = {
                            Toast.makeText(this@WorkProfileDispatcherActivity, "Ошибка: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                ACTION_LAUNCH_APP -> {
                    if (oneShotAppManagerOwnerComponent.isOneShotApp(packageName) || oneShotAppManagerOwnerComponent.isAutoFreezeApp(packageName)) {
                        oneShotAppManagerOwnerComponent.launchOneShotApp(packageName)
                    } else {
                        if (profileOwnerManager.isApplicationHidden(packageName)) {
                            profileOwnerManager.setApplicationHidden(packageName, false)
                        }
                        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                        if (launchIntent != null) {
                            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(launchIntent)
                        }
                    }
                }
            }
            finish()
        }
    }

    private fun installInCurrentProfile(
        packageName: String,
        apkPath: String?,
        splitPaths: Array<String>?
    ) {
        try {
            val packageInstaller = packageManager.packageInstaller
            val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
            val sessionId = packageInstaller.createSession(params)
            val session = packageInstaller.openSession(sessionId)

            val baseFile = apkPath?.let { File(it) }
            if (baseFile != null && baseFile.exists()) {
                baseFile.inputStream().use { input ->
                    session.openWrite("base.apk", 0, baseFile.length()).use { output ->
                        input.copyTo(output)
                        session.fsync(output)
                    }
                }
            } else {
                val uri =
                    android.net.Uri.parse("content://com.xxmrk888ytxx.apexowner.bridge/apk/$packageName/base.apk")
                contentResolver.openInputStream(uri)?.use { input ->
                    session.openWrite("base.apk", 0, -1).use { output ->
                        input.copyTo(output)
                        session.fsync(output)
                    }
                } ?: run {
                    Toast.makeText(this, "Не удалось прочитать base.apk", Toast.LENGTH_SHORT).show()
                    session.abandon()
                    return
                }
            }

            splitPaths?.forEachIndexed { index, path ->
                val splitFile = File(path)
                if (splitFile.exists()) {
                    splitFile.inputStream().use { input ->
                        session.openWrite("split_$index.apk", 0, splitFile.length()).use { output ->
                            input.copyTo(output)
                            session.fsync(output)
                        }
                    }
                }
            }

            val callbackIntent = Intent(this, WorkProfileDispatcherActivity::class.java).apply {
                action = ACTION_INSTALL_CALLBACK
            }
            val pendingIntent = PendingIntent.getActivity(
                this,
                sessionId,
                callbackIntent,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            session.commit(pendingIntent.intentSender)
            session.close()
            Logger.writeDebugLog("PackageInstaller session committed for $packageName in current profile")
        } catch (e: Exception) {
            Logger.writeErrorLog("PackageInstaller failed for $packageName: ${e.message}")
            Toast.makeText(this, "Ошибка установки: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val ACTION_CLONE_APP = "com.xxmrk888ytxx.apexowner.ACTION_CLONE_APP"
        const val ACTION_CLONE_TO_PERSONAL = "com.xxmrk888ytxx.apexowner.ACTION_CLONE_TO_PERSONAL"
        const val ACTION_FREEZE_APP = "com.xxmrk888ytxx.apexowner.ACTION_FREEZE_APP"
        const val ACTION_UNFREEZE_APP = "com.xxmrk888ytxx.apexowner.ACTION_UNFREEZE_APP"
        const val ACTION_REMOVE_WORK_PROFILE = "com.xxmrk888ytxx.apexowner.ACTION_REMOVE_WORK_PROFILE"
        const val ACTION_OPEN_RESTRICTIONS = "com.xxmrk888ytxx.apexowner.ACTION_OPEN_RESTRICTIONS"
        const val ACTION_OPEN_PERMISSIONS = "com.xxmrk888ytxx.apexowner.ACTION_OPEN_PERMISSIONS"
        const val ACTION_INSTALL_CALLBACK = "com.xxmrk888ytxx.apexowner.ACTION_INSTALL_CALLBACK"
        const val ACTION_TOGGLE_ONE_SHOT = "com.xxmrk888ytxx.apexowner.ACTION_TOGGLE_ONE_SHOT"
        const val ACTION_RESET_ONE_SHOT = "com.xxmrk888ytxx.apexowner.ACTION_RESET_ONE_SHOT"
        const val ACTION_TOGGLE_AUTO_FREEZE = "com.xxmrk888ytxx.apexowner.ACTION_TOGGLE_AUTO_FREEZE"
        const val ACTION_LAUNCH_APP = "com.xxmrk888ytxx.apexowner.ACTION_LAUNCH_APP"
        const val EXTRA_PACKAGE_NAME = "EXTRA_PACKAGE_NAME"
        const val EXTRA_IS_ONE_SHOT = "EXTRA_IS_ONE_SHOT"
        const val EXTRA_IS_AUTO_FREEZE = "EXTRA_IS_AUTO_FREEZE"
        const val EXTRA_APK_PATH = "EXTRA_APK_PATH"
        const val EXTRA_SPLIT_PATHS = "EXTRA_SPLIT_PATHS"
    }
}

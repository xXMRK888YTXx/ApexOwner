package com.xxmrk888ytxx.apexowner.bridge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Build
import android.widget.Toast
import com.xxmrk888ytxx.core.base.android.logs.Logger

class PackageInstallerReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_INSTALL_CALLBACK = "com.xxmrk888ytxx.apexowner.ACTION_INSTALL_CALLBACK"
        const val ACTION_UNINSTALL_CALLBACK = "com.xxmrk888ytxx.apexowner.ACTION_UNINSTALL_CALLBACK"
        const val EXTRA_IS_UNINSTALL = "is_uninstall"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)
        val isUninstall = intent.getBooleanExtra(EXTRA_IS_UNINSTALL, false) ||
            intent.action == ACTION_UNINSTALL_CALLBACK
        Logger.writeDebugLog("PackageInstallerReceiver received status: $status, isUninstall: $isUninstall")
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
                    context.startActivity(confirmIntent)
                }
            }
            PackageInstaller.STATUS_SUCCESS -> {
                val message = if (isUninstall) {
                    "Приложение успешно удалено"
                } else {
                    "Приложение успешно установлено"
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
            else -> {
                val msg = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
                    ?: if (isUninstall) "Ошибка удаления" else "Ошибка установки"
                Logger.writeErrorLog("PackageInstaller operation failed ($status): $msg")
            }
        }
    }
}

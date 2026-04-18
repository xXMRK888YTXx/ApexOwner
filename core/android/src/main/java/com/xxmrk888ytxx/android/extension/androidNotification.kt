package com.xxmrk888ytxx.android.extension

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Context
import androidx.core.content.getSystemService

inline fun Context.buildNotificationChannel(
    id: String,
    name: String,
    configuration: NotificationChannel.() -> Unit = {}
) {
    val channel =
        NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT).apply(configuration)

    val notificationManager = getSystemService<NotificationManager>()

    notificationManager?.createNotificationChannel(channel)
}

fun Context.buildForegroundServiceNotificationChannel(
    id: String,
    name: String,
) = buildNotificationChannel(id, name) {
    importance = IMPORTANCE_LOW
}

inline fun Context.buildNotification(
    channelId: String,
    configuration: Notification.Builder.() -> Unit
): Notification {
    val notificationBuilder = Notification.Builder(this, channelId)

    return notificationBuilder.apply(configuration).build()
}
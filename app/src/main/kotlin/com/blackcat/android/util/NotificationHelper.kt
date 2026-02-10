package com.blackcat.android.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.blackcat.android.MainActivity
import com.blackcat.android.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_STATUS_UPDATE = "status_update"
        const val CHANNEL_DELIVERY_COMPLETE = "delivery_complete"
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val statusChannel = NotificationChannel(
            CHANNEL_STATUS_UPDATE,
            "配達状況更新",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "配達状況が更新された時の通知"
        }

        val completeChannel = NotificationChannel(
            CHANNEL_DELIVERY_COMPLETE,
            "配達完了",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "配達が完了した時の通知"
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(statusChannel)
        manager.createNotificationChannel(completeChannel)
    }

    fun sendStatusUpdateNotification(
        deliveryId: Long,
        trackingNumber: String,
        status: String,
        location: String
    ) {
        if (!hasNotificationPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("deliveryId", deliveryId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, deliveryId.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_STATUS_UPDATE)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("配達状況が更新されました")
            .setContentText("$trackingNumber: $status")
            .setSubText(location)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(deliveryId.toInt(), notification)
    }

    fun sendDeliveryCompletedNotification(
        deliveryId: Long,
        trackingNumber: String,
        location: String
    ) {
        if (!hasNotificationPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("deliveryId", deliveryId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, deliveryId.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_DELIVERY_COMPLETE)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("配達が完了しました")
            .setContentText("$trackingNumber が配達されました")
            .setSubText(location)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(context).notify(deliveryId.toInt() + 10000, notification)
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}

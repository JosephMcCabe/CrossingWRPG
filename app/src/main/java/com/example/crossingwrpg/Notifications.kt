package com.example.crossingwrpg

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.crossingwrpg.R.drawable.pixelpotion


class Notifications(private val context: Context) {
    private val levelUpChannelId = "level_up_channel"
    private val itemDropChannelId = "item_drop_channel"

    private val mainActivityPendingIntent: PendingIntent by lazy {
        val mainActivityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        PendingIntent.getActivity(
            context, 0, mainActivityIntent, PendingIntent.FLAG_IMMUTABLE
        )
    }
    fun initChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val levelUpChannel = NotificationChannel(
                levelUpChannelId, "Level Ups", NotificationManager.IMPORTANCE_HIGH
            )
            val itemDropChannel = NotificationChannel(
                itemDropChannelId, "Item Drops", NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(levelUpChannel)
            notificationManager.createNotificationChannel(itemDropChannel)
        }
    }

    fun postLevelUp(messageText: String) {
        val levelUpNotification = NotificationCompat.Builder(context, levelUpChannelId)
            .setSmallIcon(pixelpotion)
            .setContentTitle("You leveled up!")
            .setContentText(messageText)
            .setAutoCancel(true)
            .setContentIntent(mainActivityPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val permissionNotGranted =
            Build.VERSION.SDK_INT >= 33 &&
                    ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED

        if (permissionNotGranted) return

        NotificationManagerCompat.from(context).notify(1001, levelUpNotification)
    }

    fun showItemNotification(title: String, message: String) {
        val itemDropNotification = NotificationCompat.Builder(context, itemDropChannelId)
            .setSmallIcon(pixelpotion)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(mainActivityPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val permissionNotGranted =
            Build.VERSION.SDK_INT >= 33 &&
                    ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED

        if (permissionNotGranted) return

        NotificationManagerCompat.from(context).notify(2001, itemDropNotification)
    }
}
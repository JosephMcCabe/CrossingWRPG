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

class Notifications(private val context: Context) {

    private val levelUpChannelId = "level_up_channel"

    fun initChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val levelUpChannel = NotificationChannel(
                levelUpChannelId,
                "Level Ups",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(levelUpChannel)
        }
    }
    fun postLevelUp(messageText: String) {
        val mainActivityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val mainActivityPendingIntent = PendingIntent.getActivity(
            context,
            0,
            mainActivityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val levelUpNotification = NotificationCompat.Builder(context, levelUpChannelId)
            .setSmallIcon(R.drawable.purplepotion)
            .setContentTitle("You leveled up!")
            .setContentText(messageText)
            .setAutoCancel(true)
            .setContentIntent(mainActivityPendingIntent)
            .build()

        // On Android 13+ (API 33), posting notifications requires POST_NOTIFICATIONS permission
        val permissionNotGranted =
            Build.VERSION.SDK_INT >= 33 &&
                    ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED

        if (permissionNotGranted) return

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1001, levelUpNotification)
    }
}

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
<<<<<<< HEAD
import com.example.crossingwrpg.R.drawable.pixelpotion

class Notifications(private val context: Context) {
=======

class Notifications(private val context: Context) {

>>>>>>> bbc15b5441f6d138b330746302de7d1007ff1a49
    private val levelUpChannelId = "level_up_channel"

    fun initChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val levelUpChannel = NotificationChannel(
<<<<<<< HEAD
                levelUpChannelId, "Level Ups", NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(levelUpChannel)
        }
    }

=======
                levelUpChannelId,
                "Level Ups",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(levelUpChannel)
        }
    }
>>>>>>> bbc15b5441f6d138b330746302de7d1007ff1a49
    fun postLevelUp(messageText: String) {
        val mainActivityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
<<<<<<< HEAD
        val mainActivityPendingIntent = PendingIntent.getActivity(
            context, 0, mainActivityIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val levelUpNotification = NotificationCompat.Builder(context, levelUpChannelId)
            .setSmallIcon(pixelpotion)
=======

        val mainActivityPendingIntent = PendingIntent.getActivity(
            context,
            0,
            mainActivityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val levelUpNotification = NotificationCompat.Builder(context, levelUpChannelId)
            .setSmallIcon(R.drawable.purplepotion)
>>>>>>> bbc15b5441f6d138b330746302de7d1007ff1a49
            .setContentTitle("You leveled up!")
            .setContentText(messageText)
            .setAutoCancel(true)
            .setContentIntent(mainActivityPendingIntent)
            .build()

<<<<<<< HEAD
        val permissionNotGranted =
            Build.VERSION.SDK_INT >= 33 &&
                    ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.POST_NOTIFICATIONS
=======
        // On Android 13+ (API 33), posting notifications requires POST_NOTIFICATIONS permission
        val permissionNotGranted =
            Build.VERSION.SDK_INT >= 33 &&
                    ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
>>>>>>> bbc15b5441f6d138b330746302de7d1007ff1a49
                    ) != PackageManager.PERMISSION_GRANTED

        if (permissionNotGranted) return

<<<<<<< HEAD
        NotificationManagerCompat.from(context).notify(1001, levelUpNotification)
=======
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1001, levelUpNotification)
>>>>>>> bbc15b5441f6d138b330746302de7d1007ff1a49
    }
}

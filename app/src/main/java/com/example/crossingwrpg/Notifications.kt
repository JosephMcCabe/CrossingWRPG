package com.example.crossingwrpg

import android.app.NotificationManager
import android.app.NotificationChannel
import android.app.PendingIntent
import android.os.Build
import android.util.Log
import android.content.Intent
import android.content.Context
import android.content.Intent.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object Notifier{
    private const val CHANNEL_ID = "battle_events_v2"//change channel_id when changing importance
    private const val NOTIF_ID_VICTORY = 2001

    private fun ensureChannel(context: Context) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val desc = context.getString(R.string.channel_description)
            val channel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH).apply { description = desc }

            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }


fun showVictory(context: Context, message: String) {
    ensureChannel(context)
    //tap notif to return to Main
    val intent = Intent(context, MainActivity::class.java).apply {
        //launch the app fresh from the notification
        flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
    }
    val pending = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.purplepotion)
        .setContentTitle("You gained a Purple Potion!")
        .setContentText(message)
        .setStyle(NotificationCompat.BigTextStyle().bigText(message))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pending)
        .setAutoCancel(true)

    try{
    NotificationManagerCompat.from(context).notify(NOTIF_ID_VICTORY, builder.build())
    } catch (e: SecurityException) {
    Log.w("Notifier", "Notification skipped (SecurityException).", e)
        }
    }

}



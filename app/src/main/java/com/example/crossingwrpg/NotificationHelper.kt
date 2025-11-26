package com.example.crossingwrpg


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlin.random.Random

object NotificationHelper {

    // Single high-priority channel used for all alerts
    private const val CHANNEL_ID = "crossing notifications"
    private const val CHANNEL_NAME = "Crossing Notifications"
    private const val CHANNEL_DESC = "High-priority reminders for achievements and items"

    /**
     * Create (or ensure) the high-priority channel exists.
     * Safe to call multiple times.
     */
    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification_manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val existing = notification_manager.getNotificationChannel(CHANNEL_ID)
            if (existing == null) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val ch = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                    description = CHANNEL_DESC
                    enableVibration(true)
                    enableLights(true)
                }
                notification_manager.createNotificationChannel(ch)
            }
        }
    }

    /**
     * Backward-compatible alias some of your screens call.
     * Keeps older code working.
     */
    fun createHighPriorityChannel(context: Context) = createChannel(context)

    /**
     * Show a notification for the given reminder type.
     *
     * @param useHarsh if true, picks from new item lines (only applies to ITEM).
     * @param persistent if true, makes it ongoing (user can dismiss via action).
     */
    fun show(
        context: Context,
        type: ReminderType,
        useHarsh: Boolean,
        persistent: Boolean
    ) {
        createChannel(context) // ensure channel exists

        val (title, text) = pickContent(type, useHarsh)
        val contentIntent = context.packageManager?.getLaunchIntentForPackage(context.packageName)
        val contentPI = PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    (if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0)
        )



        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // simple built-in icon
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(contentPI)
            .setAutoCancel(!persistent)
            .setOngoing(persistent)

        if (persistent) {
            builder.addAction(
                /* icon = */ 0,
                /* title = */ "Dismiss",
               // /* intent = */ dismissPI
            )
        }

        val id = notificationId(type)
        NotificationManagerCompat.from(context).notify(id, builder.build())
    }

    /**
     * Used by your HomeScreen’s manual trigger.
     */



    // ---- helpers ----

    private fun pickContent(type: ReminderType, useHarsh: Boolean): Pair<String, String> {
        return when (type) {
            ReminderType.ITEM -> {
                val list = if (useHarsh) NotificationTexts.droppedItem else NotificationTexts.itemLogic
                "Crossing" to list.randomSafe()
            }
            ReminderType.ACHIEVEMENT -> {
                "AchievementNotification" to NotificationTexts.achievemenet.randomSafe()
            }

            }
        }
    }

    private fun <T> List<T>.randomSafe(): T =
        if (isEmpty()) throw IllegalStateException("Notification text list is empty")
        else this[Random.nextInt(size)]

    private fun notificationId(type: ReminderType): Int {
        // A simple type-based stable prefix + random tail to avoid collisions in rapid repeats
        val base = when (type) {
            ReminderType.ITEM-> 1000
            ReminderType.ACHIEVEMENT -> 2000
            ReminderType.STEPS -> 3000
        }
        return base + Random.nextInt(500)
    }
}



package com.example.crossingwrpg

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class WalkService: Service() {

    companion object {
        private const val notifId = 100
        private const val channelId = "ForegroundServiceChannel"
        const val ACTION_START = "com.example.crossingwrpg.action.START"
        const val ACTION_PAUSE = "com.example.crossingwrpg.action.PAUSE"
        const val ACTION_RESUME = "com.example.crossingwrpg.action.RESUME"
        const val ACTION_STOP = "com.example.crossingwrpg.action.STOP"
    }
    private lateinit var builder: NotificationCompat.Builder

    private val serviceScope = CoroutineScope(Dispatchers.Default)
    private var tickerJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}
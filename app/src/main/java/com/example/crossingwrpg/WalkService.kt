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

    override fun onCreate() {
        super.onCreate()
        WalkManager.ensureInit(applicationContext)
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Adventuring!")
            .setContentText("You are currently walking")
            .setSmallIcon(R.drawable.pixelsoldier)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
        startForeground(notifId, builder.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> handleStart()
            ACTION_STOP -> handleStop()
            ACTION_PAUSE -> handlePause()
            ACTION_RESUME -> handleResume()
            else -> {}
        }
        return START_STICKY
    }

    private fun startTicker() {
        stopTicker()
        tickerJob = serviceScope.launch {
            combine(
                WalkManager.totalSteps,
                WalkManager.elapsedSeconds
            ) { steps, elapsed ->
                "Walking - Steps: $steps Time: ${elapsed}s"
            }.collect { text ->updateText(text) }
        }
    }

    private fun stopTicker() {
        tickerJob?.cancel()
        tickerJob = null
    }

    private fun updateText(text: String) {
        val notificationManager = NotificationManagerCompat.from(this)
        val notif: Notification = builder.setContentText(text).build()
        notificationManager.notify(notifId, notif)
    }

    private fun handleStart() {
        WalkManager.startSession()
        startTicker()
    }

    private fun handleStop() {
        stopTicker()
        WalkManager.stopSession()
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    private fun handlePause() {
        WalkManager.pauseSession()
    }

    private fun handleResume() {
        WalkManager.resumeSession()
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channelId,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        stopTicker()
        serviceScope.cancel()
        super.onDestroy()
    }
}
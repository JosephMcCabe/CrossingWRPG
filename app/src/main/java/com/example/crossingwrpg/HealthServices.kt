package com.example.crossingwrpg

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import kotlinx.coroutines.*



const val EXTRA_COMMAND = "extra_command"
enum class HealthCommand { START, PAUSE, CONTINUE, STOP, EXIT }

private const val CHANNEL_ID = "crossing_walk"
private const val NOTIF_ID = 1001

class HealthServices : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    //Service owns pedometer + stopwatch
    private lateinit var pedometer: Pedometer
    private lateinit var stopwatch: Stopwatch
    private lateinit var battle: BattleSimulation

    private var tickerJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        battle = BattleSimulation()
        pedometer = Pedometer(applicationContext, battle)
        stopwatch = Stopwatch()
        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val cmd = intent?.getStringExtra(EXTRA_COMMAND)?.let {
            runCatching { HealthCommand.valueOf(it) }.getOrNull()
        } ?: HealthCommand.START

        when (cmd) {
            HealthCommand.START, HealthCommand.CONTINUE -> startOrContinue()
            HealthCommand.PAUSE -> pause()
            HealthCommand.STOP -> stopWalk()
            HealthCommand.EXIT -> { stopWalk(); stopSelf() }
        }

        // Keep running unless explicitly stopped
        return START_STICKY
    }

    private fun startOrContinue() {
        startInForeground(updateText = "Getting ready…")

        pedometer.start()
        stopwatch.start()
        WalkBus.publishActive(true)

        tickerJob?.cancel()
        tickerJob = scope.launch {
            while (isActive) {
                delay(1_000)
                WalkBus.publishElapsed(stopwatch.elapsedTime.value)
                updateNotification(
                    "Steps: ${WalkBus.steps.value} • Time: ${WalkBus.elapsed.value}s"
                )
            }
        }
    }

    private fun pause() {
        pedometer.stop()
        stopwatch.stop()
        WalkBus.publishActive(false)
        updateNotification("Paused • Steps: ${WalkBus.steps.value} • Time: ${WalkBus.elapsed.value}s")
    }

    private fun stopWalk() {
        tickerJob?.cancel()
        pedometer.stop()
        stopwatch.stop()
        stopwatch.reset()
        WalkBus.publishActive(false)
        WalkBus.publishElapsed(0)
        // keep the service in foreground until user exits/stopForeground called
        updateNotification("Stopped")
    }

    private fun startInForeground(updateText: String) {
        val notification = buildNotification(updateText)
        ServiceCompat.startForeground(
            this,
            NOTIF_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
        )
    }

    private fun updateNotification(text: String) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIF_ID, buildNotification(text))
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val ch = NotificationChannel(
                CHANNEL_ID,
                "Crossing",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            ch.setSound(null, null)
            ch.enableVibration(false)
            nm.createNotificationChannel(ch)
        }
    }

    private fun buildNotification(content: String): Notification {
        fun actionIntent(cmd: HealthCommand, reqCode: Int): PendingIntent {
            val i = Intent(this, HealthServices::class.java)
                .putExtra(EXTRA_COMMAND, cmd.name)
            return PendingIntent.getService(
                this, reqCode, i, PendingIntent.FLAG_IMMUTABLE
            )
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.pathimage)
            .setContentTitle("Crossing")
            .setContentText(content)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(actionIntent(HealthCommand.CONTINUE, 200))
            .addAction(0, "CONTINUE", actionIntent(HealthCommand.CONTINUE, 201))
            .addAction(0, "PAUSE", actionIntent(HealthCommand.PAUSE, 202))
            .addAction(0, "STOP", actionIntent(HealthCommand.STOP, 203))

        return builder.build()
    }

    override fun onDestroy() {
        super.onDestroy()
        tickerJob?.cancel()
        pedometer.stop()
        stopwatch.stop()
        scope.cancel()
    }
}

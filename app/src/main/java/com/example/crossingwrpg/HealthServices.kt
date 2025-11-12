package com.example.crossingwrpg

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import android.os.IBinder
import android.util.Log
import android.widget.Toast

/*
To ensure that your app is secure,
 always use an explicit intent when
 starting a Service and do not declare
 intent filters for your services.

* Note: When starting a Service, always specify the component name.
* Otherwise, you cannot be certain what service will respond to the intent,
* and the user cannot see which service starts.
*/

const val INTENT_COMMAND = "Command"
const val INTENT_COMMAND_EXIT = "Exit" //exit deletes notification from drawer(and maybe service)
const val INTENT_COMMAND_PAUSE = "Pause"
const val INTENT_COMMAND_CONTINUE = "Continue"
const val INTENT_COMMAND_STOP = "Stop"

const val INTENT_COMMAND_HEALTH = "Health"

private const val NOTIFICATION_CHANNEL_GENERAL = "Checking"
private const val CODE_FOREGROUND_SERVICE = 1
private const val CODE_CONTINUE_INTENT = 2
private const val CODE_PAUSE_INTENT = 3
private const val CODE_HEALTH_INTENT = 4

private const val CODE_STOP_INTENT = 5



class HealthServices: Service() {

    override fun onBind(p0: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val command = intent?.getStringExtra(INTENT_COMMAND)
        if (command == INTENT_COMMAND_EXIT) {
            stopService()
            return START_NOT_STICKY
        }

        showNotification()

        if (command == INTENT_COMMAND_CONTINUE) {
            Toast.makeText(this, "Returned to Map", Toast.LENGTH_SHORT).show()

        }
        return START_STICKY
    }

    private fun stopService() {
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()

    }

    private fun showNotification() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val continueIntent = Intent(this, HealthServices::class.java).apply {
            putExtra(INTENT_COMMAND, INTENT_COMMAND_CONTINUE)
        }
        val pauseIntent = Intent(this, HealthServices::class.java).apply {
            putExtra(INTENT_COMMAND, INTENT_COMMAND_PAUSE)
        }
        val healthIntent = Intent(this, HealthServices::class.java).apply {
            putExtra(INTENT_COMMAND, INTENT_COMMAND_HEALTH)
        }
        val stopIntent = Intent(this, HealthServices::class.java).apply {
            putExtra(INTENT_COMMAND, INTENT_COMMAND_STOP)
        }


        val continuePendingIntent = PendingIntent.getService(
            this, CODE_CONTINUE_INTENT, continueIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val pausePendingIntent = PendingIntent.getService(
            this, CODE_PAUSE_INTENT, pauseIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val healthPendingIntent = PendingIntent.getService(
            this, CODE_HEALTH_INTENT, healthIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val stopPendingIntent = PendingIntent.getService(
            this, CODE_STOP_INTENT, stopIntent, PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                with(
                    NotificationChannel(
                        NOTIFICATION_CHANNEL_GENERAL,
                        "Crossing",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                ) {
                    enableLights(false)
                    setShowBadge(false)
                    enableVibration(false)
                    setSound(null, null)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    manager.createNotificationChannel(this)
                }
            } catch (e: Exception) {
                Log.d("Error", "showNotification: {e.localizedMessage}")
            }
        }
        with(
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_GENERAL)
        ) {
            setTicker(null)
            setContentTitle("Crossing")
            setContentText("Pedometer")
            setAutoCancel(false)
            setOngoing(true)
            setWhen(System.currentTimeMillis())
            setSmallIcon(R.drawable.pathimage)
            priority = NotificationManager.IMPORTANCE_HIGH
            setContentIntent(continuePendingIntent)
            addAction(
                0, "CONTINUE", continuePendingIntent
            )
            addAction(
                0, "PAUSE", pausePendingIntent
            )
            addAction(
                0, "STOP", stopPendingIntent
            )
            addAction(
                0, "HEALTH", healthPendingIntent
            )


            startForeground(CODE_FOREGROUND_SERVICE, build())
        }
    }
}
/*

    private fun startForeground() {

        val activityPermission =
            PermissionChecker.checkSelfPermission(
                this,
                Manifest.permission.FOREGROUND_SERVICE_HEALTH
            )
        if (activityPermission != PermissionChecker.PERMISSION_GRANTED) {
            stopSelf()
            //actuallly launch the foreground
            return
        }


        val foregroundServicesNotification = NotificationCompat.Builder(this, "HEALTH_ID")
            // Create the notification to display while the service is running
            .setSmallIcon(pixelpotion)
            .setContentTitle("Pedometer Running")
            .setAutoCancel(false)
            .build()
        ServiceCompat.startForeground(this, 100, foregroundServicesNotification, ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH)
        }
    }
*/
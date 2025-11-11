package com.example.crossingwrpg

import android.Manifest
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.pm.ServiceInfo
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.PermissionChecker
import android.os.IBinder
import android.widget.Toast
import com.example.crossingwrpg.R.drawable.pixelpotion


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
const val INTENT_COMMAND_EXIT = "Exit"
const val INTENT_COMMAND_REPLY = "Reply"
const val INTENT_COMMAND_ACHIEVE = "Achieve"

private const val NOTIFICATION_CHANEL_GENERAL = "Checking"
private const val CODE_FOREGROUND_SERVICE = 1
private const val CODE_REPLY_INTENT = 2
private const val CODE_ACHIEVE_INTENT = 3



class HealthServices: Service() {

    override fun onBind(p0: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val command = intent.getStringExtra(INTENT_COMMAND)
        if (command == INTENT_COMMAND_EXIT) {
            return START_NOT_STICKY
        }
        if (command == INTENT_COMMAND_REPLY) {
            Toast.makeText(this, "Clicked in Notification", Toast.LENGTH_SHORT).show()

        }
        return START_STICKY
    }
    //instructs the system to remove the service from its foreground state but without removing the associated notification from the status bar.
    private fun stopService(){
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()

    }

    private fun showNotification() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE)
        val replyIntent = Intent(this, HealthServices)
    }



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

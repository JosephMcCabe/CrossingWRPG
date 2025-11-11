package com.example.crossingwrpg.com.example.crossingwrpg

import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.crossingwrpg.HealthServices
import com.example.crossingwrpg.INTENT_COMMAND

fun Context.startForegroundService(command: String){
    val intent = Intent(this, HealthServices::class.java)
    if (command == "Start") {
        intent.putExtra(INTENT_COMMAND, command)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(intent)

        }
        else {
            this.startService(intent)
        }
    }
    else if (command == "Exit") {
        intent.putExtra(INTENT_COMMAND, command)

        this.stopService(intent)
    }

}
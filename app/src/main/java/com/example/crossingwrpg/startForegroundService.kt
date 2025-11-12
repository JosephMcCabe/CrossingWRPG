package com.example.crossingwrpg

import android.content.Context
import android.content.Intent
import android.os.Build

fun Context.sendHealthCommand(command: HealthCommand) {
    val intent = Intent(this, HealthServices::class.java)
        .putExtra(EXTRA_COMMAND, command.name)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(intent)
    } else {
        startService(intent)
    }
}

package com.example.crossingwrpg

import android.content.Context
import android.os.Build
import androidx.core.content.edit

object NotifPrefs {
    private const val FILE = "notif_prefs"
    private const val KEY_ASKED = "asked_post_notifications"

    fun asked(context: Context): Boolean =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getBoolean(KEY_ASKED, false)
    fun markAsked(context: Context) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit { putBoolean(KEY_ASKED, true)}
    }
    fun needsRuntimePermission(): Boolean = Build.VERSION.SDK_INT >= 33
}
package me.host43.locationnotifier.util

import android.os.Build
import me.host43.locationnotifier.BuildConfig

class Constants {
    companion object{
        val ACTION_START_SERVICE = "${BuildConfig.APPLICATION_ID}.STARTSERVICE"
        val ACTION_STOP_SERVICE = "${BuildConfig.APPLICATION_ID}.STOPSERVICE"

        val NOTIFICATION_CHANNEL_ID = ""
        val NOTIFICATION_CHANNEL_NAME = ""
        val NOTIFICATION_ID = 143
    }
}
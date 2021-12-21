package me.host43.locationnotifier.util

import android.os.Build
import me.host43.locationnotifier.BuildConfig

class Constants {
    companion object{
        const val ACTION_START_SERVICE = "${BuildConfig.APPLICATION_ID}.STARTSERVICE"
        const val ACTION_STOP_SERVICE = "${BuildConfig.APPLICATION_ID}.STOPSERVICE"

        const val NOTIFICATION_CHANNEL_ID = "LiveLocation_channel_id"
        const val NOTIFICATION_CHANNEL_NAME = "LiveLocaiton_channel_name"
        const val NOTIFICATION_ID = 143

        const val LOCATION_RECEIVED = "${BuildConfig.APPLICATION_ID}.locationreceived"
        const val LOCATION_ALARM_FILTER = "${BuildConfig.APPLICATION_ID}.location_alarm_filter"
    }
}
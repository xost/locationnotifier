package me.host43.locationnotifier.locationreceiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.location.Location
import androidx.core.app.NotificationCompat
import me.host43.locationnotifier.AlarmActivity
import me.host43.locationnotifier.MainActivity
import me.host43.locationnotifier.database.Point
import me.host43.locationnotifier.trackpoints.TrackPointsViewModel
import me.host43.locationnotifier.util.Constants
import timber.log.Timber

class LocationBroadcastReceiver : BroadcastReceiver() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var notification: Notification

    override fun onReceive(p0: Context?, p1: Intent?) {
        val alarmPoints = p1?.getStringExtra("alarmPoints")
        Timber.d(alarmPoints)
    }


    private fun createNotification(): Notification {
        notificationChannel = NotificationChannel(
            Constants.ALARM_NOTIFICATION_CHANNEL_ID,
            Constants.ALARM_NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationChannel.description = "alarm notifications"
        notificationChannel.enableLights(false)
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
        notificationChannel.group = Constants.NOTIFICATION_GROUP_ID
        notificationManager.createNotificationChannel(notificationChannel)
    }
}
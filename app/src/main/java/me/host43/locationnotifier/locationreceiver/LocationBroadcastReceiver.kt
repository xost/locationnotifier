package me.host43.locationnotifier.locationreceiver

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import me.host43.locationnotifier.MainActivity
import me.host43.locationnotifier.R
import me.host43.locationnotifier.database.Point
import me.host43.locationnotifier.util.Constants
import timber.log.Timber
import java.lang.StringBuilder

class LocationBroadcastReceiver : BroadcastReceiver() {

    private lateinit var notificationManager: NotificationManager

    override fun onReceive(ctx: Context?, i: Intent?) {
        val alarmPoints = i?.getSerializableExtra("alarmPoints") as List<Point>
        if (alarmPoints.isEmpty()) {
            Timber.d("alarm points is empty")
        } else {
            Timber.d(alarmPoints.toString())
            ctx?.let { notify(it, makeAlarmMessage(alarmPoints)) }
        }
    }

    private fun makeAlarmMessage(alarmPoints: List<Point>): String {
        StringBuilder("You are inside the zone of: ").apply {
            alarmPoints.forEach {
                append(it.name).append("\n")
            }
            return toString()
        }
    }

    private fun notify(ctx: Context, msg: String) {
        ctx.apply {
            notificationManager =
                ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.apply {
                createNotificationChannelGroup(
                    NotificationChannelGroup(
                        Constants.ALARM_NOTIFICATION_GROUP_ID,
                        Constants.ALARM_NOTIFICATION_GROUP_NAME
                    )
                )
            }
            notificationManager.createNotificationChannel(createNotificationChannel())
            notificationManager.notify(
                Constants.ALARM_NOTIFICATION_ID,
                createNotification(
                    this, Constants.ALARM_NOTIFICATION_CHANNEL_ID,
                    msg
                )
            )
        }
    }

    private fun createNotificationChannel(): NotificationChannel {
        val notificationChannel = NotificationChannel(
            Constants.ALARM_NOTIFICATION_CHANNEL_ID,
            Constants.ALARM_NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "alarm notifications"
            enableLights(false)
            lockscreenVisibility = Notification.VISIBILITY_SECRET
            group = Constants.ALARM_NOTIFICATION_GROUP_ID
        }
        return notificationChannel
    }

    private fun createNotification(ctx: Context, channelId: String, message: String): Notification {
        val intent = Intent(ctx, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationBuilder = NotificationCompat.Builder(ctx, channelId).apply {
            setContentTitle("${R.string.app_name} - ${Companion.CONTENT_TITLE_TEXT}")
            setTicker("${R.string.app_name} - ${Companion.CONTENT_TITLE_TEXT}")
            setContentText(message)
            setSmallIcon(android.R.drawable.ic_dialog_alert)
            setAutoCancel(true)
            setOngoing(true)
            setWhen(0)
            setOnlyAlertOnce(true)
            setContentIntent(pendingIntent)
            setFullScreenIntent(pendingIntent,true)
            priority = NotificationCompat.PRIORITY_MAX
        }
        return notificationBuilder.build()
    }

    companion object {
        private const val CONTENT_TITLE_TEXT = "Inside the zone"
    }
}
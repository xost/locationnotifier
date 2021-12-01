package me.host43.locationnotifier.LiveLocation

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import me.host43.locationnotifier.MainActivity
import me.host43.locationnotifier.R

class LiveLocationService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        requestLocationUpdates()
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action != null && intent.action.equals("LiveLocation", ignoreCase = true)) {
            stopForeground(true)
            stopSelf()
        }
        generateForegroundNotification()
        return START_NOT_STICKY
    }

    fun generateForegroundNotification() {
        Log.i("DEBUG","start requset location")
        val intentMainLanding = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intentMainLanding, 0)
        val iconNotification = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannelGroup(
            NotificationChannelGroup("location_group", "location")
        )
        val notificationChannel = NotificationChannel(
            "service_channel",
            "Service Notification",
            NotificationManager.IMPORTANCE_MIN
        )
        notificationChannel.enableLights(false)
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
        notificationManager.createNotificationChannel(notificationChannel)
        val builder = NotificationCompat.Builder(this, "service_channel")
        builder.setContentTitle(
            StringBuilder(resources.getString(R.string.app_name)).append(" service is running")
                .toString()
        )
            .setTicker(
                StringBuilder(resources.getString(R.string.app_name)).append("service is running")
                    .toString()
            )
            .setContentText("Touch to open")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setWhen(0)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
        iconNotification?.let{
            builder.setLargeIcon(Bitmap.createScaledBitmap(iconNotification, 128, 128, false))
        }
        builder.color = resources.getColor(R.color.purple_200)
        val notification = builder.build()
        startForeground(1, notification)
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        val req = LocationRequest.create().apply {
            setInterval(10000)
            setFastestInterval(5000)
            setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        }
        val cli = LocationServices.getFusedLocationProviderClient(this@LiveLocationService)
        cli.requestLocationUpdates(
            req,
            object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    val ll = p0.lastLocation //ll - last location
                    Log.i("DEBUG","latitude: ${ll.latitude}, longtitude: ${ll.longitude }")
                }
            },
            null
        )
    }
}
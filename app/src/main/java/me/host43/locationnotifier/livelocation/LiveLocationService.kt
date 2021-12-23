package me.host43.locationnotifier.livelocation

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.ticker
import me.host43.locationnotifier.MainActivity
import me.host43.locationnotifier.R
import me.host43.locationnotifier.database.Point
import me.host43.locationnotifier.database.PointDatabase
import me.host43.locationnotifier.database.PointDatabaseDao
import me.host43.locationnotifier.locationreceiver.LocationBroadcastReceiver
import me.host43.locationnotifier.util.Constants
import timber.log.Timber
import java.io.Serializable
import java.sql.Time

class LiveLocationService : LifecycleService() {

    private lateinit var notification: Notification
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var notificationManager: NotificationManager
    private lateinit var builder: NotificationCompat.Builder

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private lateinit var alarmBCReceiver: BroadcastReceiver

    private lateinit var points: LiveData<List<Point>>
    private val staticPoints = mutableListOf<Point>()

    override fun onCreate() {
        super.onCreate()
        val ds = PointDatabase.getInstance(this).dao
        points = ds.getAllPoints()
        alarmBCReceiver = LocationBroadcastReceiver()
        registerBCReceiver()

        points.observe(this, Observer {
            staticPoints.removeAll(staticPoints)
            Timber.d("Lenght of staticPoints = ${staticPoints.size}")
            it.forEach {
                if (it.enabled) {
                    staticPoints += it
                }
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.let {
            when (it.action) {
                Constants.ACTION_START_SERVICE -> {
                    if (!isServiceStarted) {
                        Timber.d("START SERVICE")
                        notification = createNotification()
                        initLocationUpdates()
                        registerLocationUpdates()
                        isServiceStarted = true
                        startForeground(Constants.NOTIFICATION_ID, notification)
                    }
                }
                Constants.ACTION_STOP_SERVICE -> {
                    if (isServiceStarted) {
                        Timber.d("STOP SERVICE")
                        notificationManager.cancel(Constants.NOTIFICATION_ID)
                        unregisterLocationUpdates()
                        isServiceStarted = false
                        stopForeground(true)
                        stopSelf()

                    }
                }
            }
        }
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun registerLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper(),
        )
    }

    private fun unregisterLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun createNotification(): Notification {
        val intentMainLanding = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intentMainLanding, 0)
        val iconNotification = BitmapFactory.decodeResource(
            resources,
            android.R.drawable.ic_dialog_map
        )
        notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannelGroup(
            NotificationChannelGroup(
                Constants.NOTIFICATION_GROUP_ID,
                Constants.NOTIFICATION_GROUP_NAME
            )
        )
        notificationChannel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_MIN
        )
        notificationChannel.description = "channel for service's notifications"
        notificationChannel.enableLights(false)
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
        notificationChannel.group = Constants.NOTIFICATION_GROUP_ID
        notificationManager.createNotificationChannel(notificationChannel)

        builder = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
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
        iconNotification?.let {
            builder.setLargeIcon(Bitmap.createScaledBitmap(iconNotification, 128, 128, false))
        }
        builder.color = resources.getColor(R.color.purple_500)
        notification = builder.build()
        return notification
    }

    private fun initLocationUpdates() {
        locationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 500
            maxWaitTime = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                Timber.d("got location update")
                super.onLocationResult(p0)
                val ll = p0.lastLocation //ll - last location
                notification =
                    builder.setContentText("latitude: ${ll.latitude}, longtitude: ${ll.longitude}")
                        .build()
                notificationManager.notify(Constants.NOTIFICATION_ID, notification)

                val intent = Intent(applicationContext, LocationBroadcastReceiver::class.java)
                intent.action = Constants.LOCATION_ALARM_FILTER
                val alarmPoints = mutableListOf<Point>()
                staticPoints.forEach {
                    val pl = Location("").apply {
                        latitude = it.latitude
                        longitude = it.longitude
                    }
                    val distance = ll.distanceTo(pl)
                    Timber.d("distance is: $distance")
                    if (distance <= it.distance) {
                        alarmPoints.add(it)
                    }
                }
                intent.putExtra("alarmPoints", alarmPoints as Serializable)
                applicationContext.sendBroadcast(intent)
            }
            ;
        }
    }

    private fun getAlarmPoints(): List<Point>? {
        val alarmPoints = mutableListOf<Point>()
        staticPoints.forEach {
            alarmPoints.add(it)
        }
        return if (alarmPoints.size > 0) alarmPoints.toList() else null
    }

    private fun registerBCReceiver() {
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(
            alarmBCReceiver,
            IntentFilter(Constants.LOCATION_ALARM_FILTER)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(alarmBCReceiver)
        //notificationManager.cancel(Constants.NOTIFICATION_ID+1)
        Timber.d("DESTROYED")
    }

    companion object {
        var isServiceStarted: Boolean = false
    }
}
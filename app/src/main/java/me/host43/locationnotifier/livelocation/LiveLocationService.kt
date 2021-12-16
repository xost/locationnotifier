package me.host43.locationnotifier.livelocation

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.*
import me.host43.locationnotifier.MainActivity
import me.host43.locationnotifier.R
import me.host43.locationnotifier.database.PointDatabase
import me.host43.locationnotifier.database.PointDatabaseDao
import me.host43.locationnotifier.util.Constants

class LiveLocationService : LifecycleService() {

    private lateinit var notification: Notification
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var notificationManager: NotificationManager
    private lateinit var builder: NotificationCompat.Builder

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private val ds :PointDatabaseDao = PointDatabase.getInstance(this).dao
    private var points = ds.getAllPoints()

    override fun onCreate() {
        super.onCreate()
        Log.d("LiveLocationService",ds.getAllPoints().value.toString())

        points.observe(this, Observer {
            Log.d("LiveLocationsService","Points are changed")
            it.forEach{
                Log.d("LiveLocationService",it.name)
            }
        })
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.let {
            when (it.action) {
                Constants.ACTION_START_SERVICE -> {
                    if (!isServiceStarted) {
                        notification = createNotification()
                        initLocationUpdates()
                        registerLocationUpdates()
                        isServiceStarted = true
                        startForeground(Constants.NOTIFICATION_ID, notification)
                    }
                }
                Constants.ACTION_STOP_SERVICE -> {
                    if (isServiceStarted) {
                        notificationManager.cancel(Constants.NOTIFICATION_ID)
                        unregisterLocationUpdates()
                        stopForeground(true)
                        stopSelf()
                        isServiceStarted = false

                    }
                }
            }
        }
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    fun registerLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper(),
        )
    }

    fun unregisterLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    fun createNotification(): Notification {
        val intentMainLanding = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intentMainLanding, 0)
        val iconNotification = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannelGroup(
            NotificationChannelGroup(
                "notification_group_id",
                "notification_group_name"
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
        notificationChannel.group = "notifications_group_id"
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
        builder.color = resources.getColor(R.color.purple_200)
        notification = builder.build()
        return notification
    }

    private fun initLocationUpdates() {
        locationRequest = LocationRequest.create().apply {
            interval = 2000
            fastestInterval = 1000
            maxWaitTime = 2000 * 60
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                val ll = p0.lastLocation //ll - last location
                notification =
                    builder.setContentText("latitude: ${ll.latitude}, longtitude: ${ll.longitude}")
                        .build()
                notificationManager.notify(Constants.NOTIFICATION_ID, notification)
                //val intent = Intent()
                //intent.action = Constants.LOCATION_RECEIVED
                //intent.putExtra("lastLocation", p0.lastLocation)
                //sendBroadcast(intent)
                points.value?.forEach {
                    Log.d("LiveLocationService: locationCallback","point name: ${it.name}")
                }
            }
        }
    }

    companion object {
        var isServiceStarted: Boolean = false
    }
}
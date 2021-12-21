package me.host43.locationnotifier.livelocation

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import me.host43.locationnotifier.MainActivity
import me.host43.locationnotifier.R
import me.host43.locationnotifier.database.Point
import me.host43.locationnotifier.database.PointDatabase
import me.host43.locationnotifier.database.PointDatabaseDao
import me.host43.locationnotifier.locationreceiver.LocationBroadcastReceiver
import me.host43.locationnotifier.util.Constants
import timber.log.Timber

class LiveLocationService : LifecycleService() {

    private lateinit var notification: Notification
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var notificationManager: NotificationManager
    private lateinit var builder: NotificationCompat.Builder

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private lateinit var alarmBCReceiver: BroadcastReceiver

    private var pi: PendingIntent? = null

    private lateinit var ds: PointDatabaseDao
    private lateinit var points: LiveData<List<me.host43.locationnotifier.database.Point>>

    override fun onCreate() {
        super.onCreate()
        ds = PointDatabase.getInstance(this).dao
        points = ds.getAllPoints()
        alarmBCReceiver = LocationBroadcastReceiver()
        registerBCReceiver()
    }

    @SuppressLint("MissingPermission")
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
                        pi = intent.getParcelableExtra<PendingIntent>("pendingIntent")
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
        //val iconNotification = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
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
        notificationChannel.group = "notification_group_id"
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
        //iconNotification?.let {
        //    builder.setLargeIcon(Bitmap.createScaledBitmap(iconNotification, 128, 128, false))
        //}
        builder.color = resources.getColor(R.color.purple_200)
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
                val alarmPoints = getAlarmPoints()
                val alarmIntent = Intent(Constants.LOCATION_ALARM_FILTER)
                alarmIntent.putExtra("alarmPoints", alarmPoints.?.toTypedArray())
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(alarmIntent)
            }
        }
    }

    private fun getAlarmPoints(): Array<Point> {
        points.value?.forEach {
            Timber.d("point name ${it.name}")
        }
        return points.value.toTypedArray<Point>()
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
        Timber.d("DESTROYED")
    }

    companion object {
        var isServiceStarted: Boolean = false
    }
}
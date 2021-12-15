package me.host43.locationnotifier.livelocation

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import me.host43.locationnotifier.MainActivity
import me.host43.locationnotifier.R
import me.host43.locationnotifier.trackpoints.TrackPointsViewModel
import me.host43.locationnotifier.trackpoints.TrackPointsViewModel.Companion.LOCATION_RECEIVED
import me.host43.locationnotifier.util.Constants

class LiveLocationService : Service() {

    private lateinit var notification: Notification
    private lateinit var notificationManager: NotificationManager
    private lateinit var builder: NotificationCompat.Builder

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onBind(p0: Intent?): IBinder? {
        //initLocationUpdates()
        return null
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                Constants.ACTION_START_SERVICE -> {
                    if (!isServiceStarted) {
                        notification = createNotification()
                        initLocationUpdates()
                        registerLocationUpdates()
                        isServiceStarted = true
                        startForeground(Constants.NOTIFICATION_ID,notification)
                    }
                }
                Constants.ACTION_STOP_SERVICE -> {
                }
                Constants.ACTION_STOP_SERVICE -> {
                    if (isServiceStarted){
                        unregisterLocationUpdates()

                        isServiceStarted = false

                    }
                }
            }
        }
        if (intent?.action != null && intent.action.equals(
                TrackPointsViewModel.ACTION_STOP_FOREGROUND,
                ignoreCase = true
            )
        ) {
            Log.d("LiveLocationService:", "ACTION STOP")
            isServiceStarted = false

            stopForeground(true)
            Log.d("LiveLocationService: isServiceStarted", isServiceStarted.toString())
            //stopSelf() //?????? what does it do ?
        }
        return START_NOT_STICKY // ?????????????
    }

    @SuppressLint("MissingPermission")
    fun registerLocationUpdates(){
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper(),
        )
    }

    fun unregisterLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    fun createNotification(): Notification {
        Log.i("DEBUG", "start requset location")
        val intentMainLanding = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intentMainLanding, 0)
        val iconNotification = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        notificationManager =
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
        builder = NotificationCompat.Builder(this, "service_channel")
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
    }

    //    @SuppressLint("MissingPermission")
    private fun initLocationUpdates() {
        Log.d("initLocationUpdates", "!!!!!!!!!!!!!!")
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
                Log.i("DEBUG", "latitude: ${ll.latitude}, longtitude: ${ll.longitude}")
                notification =
                    builder.setContentText("latitude: ${ll.latitude}, longtitude: ${ll.longitude}")
                        .build()
                notificationManager.notify(1, notification)
                val intent = Intent()
                intent.action = LOCATION_RECEIVED
                intent.putExtra("lastLocation",p0.lastLocation)
                sendBroadcast(intent)
            }
        }

    }

    companion object {
        var isServiceStarted: Boolean = false
    }

}
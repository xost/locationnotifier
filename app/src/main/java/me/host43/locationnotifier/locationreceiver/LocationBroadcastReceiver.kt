package me.host43.locationnotifier.locationreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.location.Location
import me.host43.locationnotifier.MainActivity
import me.host43.locationnotifier.database.Point
import me.host43.locationnotifier.trackpoints.TrackPointsViewModel
import me.host43.locationnotifier.util.Constants
import timber.log.Timber

class LocationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        val alarmPoints = p1?.getStringExtra("alarmPoints")
        Timber.d(alarmPoints)
        val intent = Intent(p0,MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        p0?.startActivity(intent)
    }
}
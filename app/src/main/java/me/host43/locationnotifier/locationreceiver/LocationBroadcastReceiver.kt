package me.host43.locationnotifier.locationreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import me.host43.locationnotifier.database.Point
import me.host43.locationnotifier.trackpoints.TrackPointsViewModel
import me.host43.locationnotifier.util.Constants
import timber.log.Timber

class LocationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        val location = p1?.extras?.get("lastLocation") as Array<Point>
        location.forEach {
            Timber.d(it.name)
        }
    }
}
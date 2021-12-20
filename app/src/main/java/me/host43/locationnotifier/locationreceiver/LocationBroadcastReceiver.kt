package me.host43.locationnotifier.locationreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import me.host43.locationnotifier.trackpoints.TrackPointsViewModel
import me.host43.locationnotifier.util.Constants
import timber.log.Timber

class LocationBroadcastReceiver(private val vm: TrackPointsViewModel) : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        Timber.d(Constants.LOCATION_RECEIVED)
        val extra = p1?.extras?.get("lastLocation") as Location

        Timber.d(
            "altitude: ${extra.altitude.toString()} longtitude: ${extra.longitude.toString()}"
        )
        Timber.d(
            "points: ${vm.points}"
        )

        val points = vm.getAllPoints()
        points?.forEach {
            Timber.d("point name: ${it.name}")
        }
    }
}
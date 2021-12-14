package me.host43.locationnotifier.LocationReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import me.host43.locationnotifier.database.Point
import me.host43.locationnotifier.database.PointDatabaseDao
import me.host43.locationnotifier.trackpoints.TrackPointsViewModel
import me.host43.locationnotifier.trackpoints.TrackPointsViewModel.Companion.LOCATION_RECEIVED

class LocationBroadcastReceiver(private val vm: TrackPointsViewModel): BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.d("LocationBroadcastReceiver",LOCATION_RECEIVED)
        val extra = p1?.extras?.get("lastLocation") as Location

        Log.d("LocationBroadcastReceiver",
            "altitude: ${extra.altitude.toString()} longtitude: ${extra.longitude.toString()}")
        Log.d("LocationBroadcastReceiver",
            "points: ${vm.points}")

        val points = vm.getAllPoints()
        points?.forEach {
            Log.d("LocationBroadcastReceiver",
                "point name: ${it.name}")
        }
        vm.logAllPoints()
    }
}
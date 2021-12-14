package me.host43.locationnotifier.LocationReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import me.host43.locationnotifier.database.Point
import me.host43.locationnotifier.database.PointDatabase
import me.host43.locationnotifier.database.PointDatabaseDao
import me.host43.locationnotifier.trackpoints.TrackPointsViewModel
import me.host43.locationnotifier.trackpoints.TrackPointsViewModel.Companion.LOCATION_RECEIVED

class LocationBroadcastReceiver(private val ctx: Context): BroadcastReceiver() {

    private val db = PointDatabase.getInstance(ctx)

    override fun onReceive(p0: Context?, p1: Intent?) {
        val extra = p1?.extras?.get("lastLocation") as Location
        val db=PointDatabase.getInstance(ctx)

        Log.d("LocationBroadcastReceiver",LOCATION_RECEIVED)
        Log.d("LocationBroadcastReceiver",
            "altitude: ${extra.altitude.toString()} longtitude: ${extra.longitude.toString()}")

        val points = db.dao.getAllPoints()
        Log.d("LocationBroadcastReceiver",points.value.toString())
    }
}
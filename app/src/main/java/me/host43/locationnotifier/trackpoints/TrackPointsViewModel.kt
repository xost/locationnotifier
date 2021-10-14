package me.host43.locationnotifier.trackpoints

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import me.host43.locationnotifier.database.PointDatabaseDao

class TrackPointsViewModel(val db: PointDatabaseDao, app: Application): AndroidViewModel(app) {
    override fun onCleared() {
        super.onCleared()
    }
}
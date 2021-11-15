package me.host43.locationnotifier.map

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.host43.locationnotifier.database.PointDatabaseDao
import java.lang.IllegalArgumentException

class MapViewModelFactory(
    private val ds: PointDatabaseDao,
    private val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(ds, app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
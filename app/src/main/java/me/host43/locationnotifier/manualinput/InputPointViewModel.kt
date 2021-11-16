package me.host43.locationnotifier.manualinput

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.host43.locationnotifier.database.Point
import me.host43.locationnotifier.database.PointDatabaseDao

class InputPointViewModel(val db: PointDatabaseDao, app: Application) : AndroidViewModel(app) {

    private val _navigateToTrackPoints = MutableLiveData<Boolean>()
    val navigateToTrackPoints: LiveData<Boolean>
        get() = _navigateToTrackPoints

    val p = Point()

    fun onAddButton() {
        viewModelScope.launch {
            db.insert(p)
            Log.i("InputPoint", "add point complete")
            _navigateToTrackPoints.value = true
        }
    }

    fun navigateToTrackPointsDone() {
        _navigateToTrackPoints.value = false
    }
}
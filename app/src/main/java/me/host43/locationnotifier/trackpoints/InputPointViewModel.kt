package me.host43.locationnotifier.trackpoints

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

    private val _eventAddPoint = MutableLiveData<Boolean>()
    val eventAddPoint: LiveData<Boolean>
        get() = _eventAddPoint

    private val _eventAddPointDone = MutableLiveData<Boolean>()
    val eventAddPointDone: LiveData<Boolean>
        get() = _eventAddPointDone

    var pointName = "First point"
    var altitude = 0.0
    var latitude = 0.0
    var distance = 0.0
    var enabled = false

    fun onAddButton() {
        viewModelScope.launch {
            val point = Point()
            point.name = pointName
            point.altitude = altitude
            point.latitude = latitude
            point.distance = distance
            point.enabled = enabled
            db.insert(point)
            Log.i("InputPoint","add point complete")
            _eventAddPointDone.value=true
        }
    }

    fun addPointComplete() {
        _eventAddPointDone.value=false
    }
}
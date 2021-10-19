package me.host43.locationnotifier.trackpoints

import android.app.Application
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import me.host43.locationnotifier.BR
import me.host43.locationnotifier.database.Point
import me.host43.locationnotifier.database.PointDatabaseDao

class InputPointViewModel(val db: PointDatabaseDao, app: Application) : AndroidViewModel(app) {

    private val _navigateToTrackPoints = MutableLiveData<Boolean>()
    val navigateToTrackPoints: LiveData<Boolean>
        get() = _navigateToTrackPoints

    var pointName = "First point"

    private val altitudeString = MutableLiveData<String>()
    val altitude:Double = Transformations.map(altitudeString) {
        altitude=it.toDouble()
    }

    var latitudeString = "0.0"
    var distanceString = "0.0"
    var enabled = false


    fun onAddButton() {
        viewModelScope.launch {
            val point = Point()
            point.name = pointName
            //point.altitude = altitude
            //point.latitude = latitude
            //point.distance = distance
            //point.enabled = enabled
            db.insert(point)
            Log.i("InputPoint", "add point complete")
            _navigateToTrackPoints.value = true
        }
    }

    fun navigateToTrackPointsDone() {
        _navigateToTrackPoints.value = false
    }
}
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

    val p = newPoint()

    fun onAddButton() {
        viewModelScope.launch {
            //p.name = p.name
            //p.altitude = 0.0
            //p.latitude = 0.0
            //p.distance = 0.0
            //p.enabled = true
            db.insert(p)
            Log.i("InputPoint", "add point complete")
            _navigateToTrackPoints.value = true
        }
    }

    private fun newPoint(): Point{
        return Point()
    }

    fun navigateToTrackPointsDone() {
        _navigateToTrackPoints.value = false
    }
}
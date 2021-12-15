package me.host43.locationnotifier.trackpoints

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.host43.locationnotifier.BuildConfig
import me.host43.locationnotifier.database.Point
import me.host43.locationnotifier.database.PointDatabaseDao

class TrackPointsViewModel(private val db: PointDatabaseDao, val app: Application) : AndroidViewModel(app) {

    private val _eventAddPoint = MutableLiveData<Boolean>()
    val eventAddPoint: LiveData<Boolean>
        get() = _eventAddPoint

    private val _eventAddPointDone = MutableLiveData<Boolean>()
    val eventAddPointDone: LiveData<Boolean>
        get() = _eventAddPointDone

    private val _eventStartStopService = MutableLiveData<Boolean>(false)
    val eventStartStopService: LiveData<Boolean>
        get() = _eventStartStopService

    var points = db.getAllPoints()

    fun onAddButton() {
        _eventAddPoint.value = true
    }

    fun navigationComplete() {
        _eventAddPoint.value = false
    }

    fun eventServiceStateChanged(state: Boolean){
        _eventStartStopService.value = state
    }

    fun clearAll(){
        Log.i("cleanAll","on Clean all listener")
        viewModelScope.launch{
            db.clear()
        }
    }

    fun getAllPoints() : List<Point>? {
        return db.getAllPoints().value
    }

    fun logAllPoints(){
        points.value?.forEach{
            Log.d("TrackPointsViewModel","${it.name}")
        }
    }
}
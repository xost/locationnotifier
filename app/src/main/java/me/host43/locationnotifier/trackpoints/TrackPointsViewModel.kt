package me.host43.locationnotifier.trackpoints

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.host43.locationnotifier.database.Point
import me.host43.locationnotifier.database.PointDatabaseDao
import me.host43.locationnotifier.livelocation.LiveLocationService
import timber.log.Timber

class TrackPointsViewModel(private val db: PointDatabaseDao, val app: Application) :
    AndroidViewModel(app) {

    var points: LiveData<List<Point>>

    private val _eventAddPoint = MutableLiveData<Boolean>()
    val eventAddPoint: LiveData<Boolean>
        get() = _eventAddPoint

    //private val _serviceState = MutableLiveData<Boolean>()
    //val serviceState: LiveData<Boolean>
    //    get() = _serviceState

    private val _eventStartService = MutableLiveData<Boolean>()
    val eventStartService: LiveData<Boolean>
        get() = _eventStartService

    private val _eventStopService = MutableLiveData<Boolean>()
    val eventStopService: LiveData<Boolean>
        get() = _eventStopService

    init {
        //val prefs = app.getSharedPreferences("TrackPointsFragment", Context.MODE_PRIVATE)
        //val state = prefs.getBoolean("serviceState",false)
        //_serviceState.value = LiveLocationService.isServiceStarted
        points = db.getAllPoints()
    }

    fun onAddButton() {
        _eventAddPoint.value = true
    }

    fun navigationComplete() {
        _eventAddPoint.value = false
    }

    fun clearAll() {
        viewModelScope.launch {
            db.clear()
        }
    }

    fun eventServiceStateChanged(checked: Boolean) {
        if (checked && LiveLocationService.isServiceStarted == false) {
            _eventStartService.value = true
        }
        if (!checked && LiveLocationService.isServiceStarted == true) {
            _eventStopService.value = true
        }
    }

    fun startServiceDone() {
        _eventStartService.value = false
    }

    fun stopServiceDone() {
        _eventStartService.value = false
    }

    fun getAllPoints(): List<Point>? {
        return db.getAllPoints().value
    }
}
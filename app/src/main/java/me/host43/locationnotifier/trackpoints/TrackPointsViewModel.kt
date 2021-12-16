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
import timber.log.Timber

class TrackPointsViewModel(private val db: PointDatabaseDao, val app: Application) :
    AndroidViewModel(app) {

    var points = db.getAllPoints()

    private val _eventAddPoint = MutableLiveData<Boolean>()
    val eventAddPoint: LiveData<Boolean>
        get() = _eventAddPoint

    private val _eventAddPointDone = MutableLiveData<Boolean>()
    val eventAddPointDone: LiveData<Boolean>
        get() = _eventAddPointDone

    private val _serviceState = MutableLiveData<Boolean>(false)
    val serviceState: LiveData<Boolean>
        get() = _serviceState

    private val _eventStartService = MutableLiveData<Boolean>()
    val eventStartService: LiveData<Boolean>
        get() = _eventStartService

    private val _eventStopService = MutableLiveData<Boolean>()
    val eventStopService: LiveData<Boolean>
        get() = _eventStopService

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

    fun eventServiceStateChanged(checked: Boolean){
        if (checked && serviceState.value==false) {
            _eventStartService.value=true
            _serviceState.value=true
        }
        if (!checked && serviceState.value==true){
            _eventStopService.value=true
            _serviceState.value=false
        }
    }

    fun startServiceDone(){
        _eventStartService.value=false
    }

    fun stopServiceDone(){
        _eventStartService.value=false
    }

    fun getAllPoints(): List<Point>? {
        return db.getAllPoints().value
    }
}
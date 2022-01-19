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

    private val _eventStartService = MutableLiveData<Boolean>()
    val eventStartService: LiveData<Boolean>
        get() = _eventStartService

    private val _eventStopService = MutableLiveData<Boolean>()
    val eventStopService: LiveData<Boolean>
        get() = _eventStopService

    private val _switchPointEvent = MutableLiveData<Long>()
    val switchPointEvent: LiveData<Long>
        get() = _switchPointEvent

    private val _point = MutableLiveData<Point>(null)
    val point: LiveData<Point>
        get() = _point

    init {
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
        if (checked && !LiveLocationService.isServiceStarted) {
            _eventStartService.value = true
        }
        if (!checked && LiveLocationService.isServiceStarted) {
            _eventStopService.value = true
        }
    }

    fun startServiceDone() {
        _eventStartService.value = false
    }

    fun stopServiceDone() {
        _eventStartService.value = false
    }

    fun switchPoint(id: Long) {
        viewModelScope.launch {
            val point = db.get(id)
            point.enabled = !point.enabled
            db.update(point)
            Timber.d("point set to: ${point.enabled}")
        }
    }

    fun navigateToMap(id: Long){
        viewModelScope.launch {
            _point.value = db.get(id)
        }
    }

    fun navigateToMapDone(){
        _point.value = null
    }

    fun updatePoint(point: Point) {
        viewModelScope.launch {
            db.update(point)
        }
    }

    fun getAllPoints(): List<Point>? {
        return db.getAllPoints().value
    }

    fun updatedb(point: Point,method: String){
        Timber.d("method==${method} point name == ${point.name}")
        when(method){
            "new" -> viewModelScope.launch {
                db.insert(point)
            }
            "update" -> viewModelScope.launch {
                db.update(point)
            }
        }
    }
}
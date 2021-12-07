package me.host43.locationnotifier.trackpoints

import android.app.Activity
import android.app.Application
import android.app.PendingIntent.getActivity
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.host43.locationnotifier.database.PointDatabaseDao

class TrackPointsViewModel(val db: PointDatabaseDao, app: Application) : AndroidViewModel(app) {

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
}
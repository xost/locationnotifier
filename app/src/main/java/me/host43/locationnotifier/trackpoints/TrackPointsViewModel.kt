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

    private val _eventGoButton = MutableLiveData<Boolean>()
    val eventGoButton: LiveData<Boolean>
        get() = _eventGoButton

    private val _eventStartServiceDone = MutableLiveData<Boolean>()
    val eventStartServiceDone: LiveData<Boolean>
        get() = _eventStartServiceDone


    var points = db.getAllPoints()

    fun onAddButton() {
        _eventAddPoint.value = true
    }

    fun onGoButton(){
        _eventGoButton.value = true
    }

    fun navigationComplete() {
        _eventAddPoint.value = false
    }

    fun startServiceDone(){
        _eventStartServiceDone.value = true
    }

    fun startLiveLocationService(){
    }

    fun clearAll(){
        Log.i("cleanAll","on Clean all listener")
        viewModelScope.launch{
            db.clear()
        }
    }
}
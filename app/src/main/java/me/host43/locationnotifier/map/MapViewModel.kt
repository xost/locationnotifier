package me.host43.locationnotifier.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import me.host43.locationnotifier.database.Point
import me.host43.locationnotifier.database.PointDatabaseDao

class MapViewModel(private val db: PointDatabaseDao, app: Application) : AndroidViewModel(app) {

    private val _navigateToTrackPoints = MutableLiveData<Boolean>()
    val navigateToTrackPoints: LiveData<Boolean>
        get() = _navigateToTrackPoints

    private val _placeIsNotSetNotify = MutableLiveData<Boolean>(false)
    val placeIsNotSetNotify: LiveData<Boolean>
        get() = _placeIsNotSetNotify

    private val _placenameIsNotSetNotify = MutableLiveData<Boolean>(false)
    val placenameIsNotSetNotify: LiveData<Boolean>
        get() = _placenameIsNotSetNotify

    private lateinit var _map: GoogleMap
    val map: GoogleMap
        get() = _map

    private var _marker: Marker? = null
    val marker: Marker?
        get() = _marker

    val p = Point()

    fun setMap(m: GoogleMap) {
        _map = m
    }

    fun onAddButton() {
        if (marker == null){
            _placeIsNotSetNotify.value = true
            return
        }
        if (p.name == ""){
            _placenameIsNotSetNotify.value = true
            return
        }
        viewModelScope.launch {
            db.insert(p)
            _navigateToTrackPoints.value = true
        }
    }

    fun newMarker(ll: LatLng) {
        marker?.remove()
        _marker = map.addMarker(MarkerOptions().position(ll).draggable(true))
        p.latitude=ll.latitude
        p.longitude=ll.longitude
        p.distance = 10000.0
    }

    fun navigateToTrackPointsDone() {
        _navigateToTrackPoints.value = false
    }

    fun placeNotSetNotifyDone(){
        _placeIsNotSetNotify.value=false
    }

    fun placenameNotSetNotifyDone(){
        _placenameIsNotSetNotify.value=false
    }
}
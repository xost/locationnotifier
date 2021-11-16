package me.host43.locationnotifier.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import me.host43.locationnotifier.database.Point
import me.host43.locationnotifier.database.PointDatabaseDao

class MapViewModel(private val db: PointDatabaseDao, app: Application, map: GoogleMap) : AndroidViewModel(app) {

    private val _navigateToTrackPoints = MutableLiveData<Boolean>()
    val navigateToTrackPoints: LiveData<Boolean>
        get() = _navigateToTrackPoints

    var marker = MarkerOptions()
    var map: GoogleMap = map
        get() = field
        set(value) {field=value}

    val p = Point()

    fun onAddButton() {
        viewModelScope.launch {
            db.insert(p)
            _navigateToTrackPoints.value = true
        }
    }

    fun newMarker(ll: LatLng): MarkerOptions{
        marker = MarkerOptions().position(ll)
        marker.
        return marker
    }

    fun navigateToTrackPointsDone() {
        _navigateToTrackPoints.value = false
    }
}
package me.host43.locationnotifier.trackpoints

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import me.host43.locationnotifier.database.Point
import me.host43.locationnotifier.database.PointDatabaseDao

class InputPointViewModel(val db: PointDatabaseDao, app: Application) : AndroidViewModel(app) {

    private val _eventAddPoint = MutableLiveData<Boolean>()
    val eventAddPoint: LiveData<Boolean>
        get() = _eventAddPoint

    fun onAddButton(n: String, a: Double, l: Double, d: Double){
        val point = Point()
        with(point){
            name=n
            altitude=a
            latitude=l
            distance=d
        }
    }
}
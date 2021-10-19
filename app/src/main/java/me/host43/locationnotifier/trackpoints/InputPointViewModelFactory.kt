package me.host43.locationnotifier.trackpoints

import android.app.Application
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.host43.locationnotifier.database.PointDatabaseDao
import java.lang.IllegalArgumentException

class InputPointViewModelFactory(
    private val ds: PointDatabaseDao,
    private val app: Application
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InputPointViewModel::class.java)) {
            return InputPointViewModel(ds, app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
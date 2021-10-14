package me.host43.locationnotifier.trackpoints

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import me.host43.locationnotifier.R
import me.host43.locationnotifier.database.PointDatabase
import me.host43.locationnotifier.databinding.FragmentTrackPointsBinding

class TrackPointsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val b = DataBindingUtil.inflate<FragmentTrackPointsBinding>(
            inflater,
            R.layout.fragment_track_points,
            container,
            false
        )

        val app = requireNotNull(this.activity).application
        val ds = PointDatabase.getInstance(app).dao
        val vmFactory = TrackPointsViewModelFactory(ds, app)
        val vm = ViewModelProvider(this, vmFactory).get(TrackPointsViewModel::class.java)

        b.lifecycleOwner = this
        b.vm = vm

        return b.root
    }
}
package me.host43.locationnotifier.trackpoints

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import me.host43.locationnotifier.livelocation.LiveLocationService
import me.host43.locationnotifier.R
import me.host43.locationnotifier.database.PointDatabase
import me.host43.locationnotifier.databinding.FragmentTrackPointsBinding
import me.host43.locationnotifier.util.Constants

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

        //check is LiveLocationService started ???
        Log.d("isServiceStarted: ", "ACTION_INIT")
        Log.d(
            "isServiceStarted: LiveLocationService.isServiceStarted: ",
            LiveLocationService.isServiceStarted.toString()
        )
        Log.d(
            "isServiceStarted: vm.eventStartStopService.value: ",
            vm.eventStartStopService.value.toString()
        )
        b.goButton.isChecked = LiveLocationService.isServiceStarted

        val adapter = PointAdapter()
        b.pointList.adapter = adapter

        vm.eventAddPoint.observe(viewLifecycleOwner, Observer {
            if (it) {
                this.findNavController().navigate(
                    //TrackPointsFragmentDirections.actionTrackPointsFragmentToInputPointFragment()
                    TrackPointsFragmentDirections.actionTrackPointsFragmentToMapFragment()
                )
                vm.navigationComplete()
            }
        })

        vm.eventStartStopService.observe(viewLifecycleOwner, Observer {
            val intent = Intent(this.context,LiveLocationService::class.java)
            if (it) {
                intent.action = Constants.ACTION_START_SERVICE
            } else {
                intent.action = Constants.ACTION_STOP_SERVICE
            }
            app.startForegroundService(intent)
        })

        vm.points.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })
        return b.root
    }

}
package me.host43.locationnotifier.trackpoints

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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
import timber.log.Timber

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

        Timber.d("vm.serviceState=${vm.serviceState}")
        val prefs=app.getSharedPreferences("TrackPointsFragment",MODE_PRIVATE)
        val state = prefs.getBoolean("serviceState",false)
        b.goButton.isChecked = state

        val adapter = PointAdapter()
        b.pointList.adapter = adapter

        vm.eventAddPoint.observe(viewLifecycleOwner, Observer {
            if (it) {
                this.findNavController().navigate(
                    TrackPointsFragmentDirections.actionTrackPointsFragmentToMapFragment()
                )
                vm.navigationComplete()
            }
        })

        vm.eventStartService.observe(viewLifecycleOwner, Observer {
            if (it) {
                vm.startServiceDone()
                val intent = Intent(context, LiveLocationService::class.java).apply {
                    action = Constants.ACTION_START_SERVICE
                }
                app.startForegroundService(intent)
            }
        })

        vm.eventStopService.observe(viewLifecycleOwner, Observer {
            if (it) {
                vm.stopServiceDone()
                val intent = Intent(context, LiveLocationService::class.java).apply {
                    action = Constants.ACTION_STOP_SERVICE
                }
                app.startForegroundService(intent)
            }
        })

        vm.serviceState.observe(viewLifecycleOwner, Observer {
            val prefs = app.getSharedPreferences("TrackPointsFragment", MODE_PRIVATE).edit()
            prefs.putBoolean("serviceState",it)
            prefs.commit()
        })

        vm.points.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })
        return b.root
    }

}
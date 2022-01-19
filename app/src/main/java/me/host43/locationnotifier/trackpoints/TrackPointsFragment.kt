package me.host43.locationnotifier.trackpoints

import android.app.Application
import android.app.PendingIntent
import android.content.*
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import me.host43.locationnotifier.MainActivity
import me.host43.locationnotifier.livelocation.LiveLocationService
import me.host43.locationnotifier.R
import me.host43.locationnotifier.database.Point
import me.host43.locationnotifier.database.PointDatabase
import me.host43.locationnotifier.databinding.FragmentTrackPointsBinding
import me.host43.locationnotifier.locationreceiver.LocationBroadcastReceiver
import me.host43.locationnotifier.util.Constants
import timber.log.Timber

class TrackPointsFragment : Fragment() {

    private lateinit var adapter: PointAdapter
    private lateinit var app: Application

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

        var point:Point? = null
        var method = ""

        app = requireNotNull(this.activity).application
        val ds = PointDatabase.getInstance(app).dao
        val vmFactory = TrackPointsViewModelFactory(ds, app)
        val vm = ViewModelProvider(this, vmFactory).get(TrackPointsViewModel::class.java)

        arguments?.let {
            point = it.getSerializable("point") as Point?
            method = it.getString("method").toString()
        }

        point?.let {
            if (method.isNotEmpty()) vm.updatedb(it, method)
        }

        b.lifecycleOwner = this
        b.vm = vm

        b.goButton.isChecked = LiveLocationService.isServiceStarted

        adapter = PointAdapter(
            vm,
            PointItemListener {
                vm.navigateToMap(it)
                //Toast.makeText(context, "Clicked on: ${it}", Toast.LENGTH_LONG).show()
            },
            PointItemSwitchListener {
                //vm.switchPoint(it)
            }
        )
        b.pointList.adapter = adapter

        setObservers(vm)
        return b.root
    }

    private fun setObservers(vm: TrackPointsViewModel) {

        vm.eventAddPoint.observe(viewLifecycleOwner, Observer {
            if (it) {
                this.findNavController().navigate(
                    TrackPointsFragmentDirections.actionTrackPointsFragmentToMapFragment(
                        Point(),
                        "new"
                    )
                )
                vm.navigationComplete()
            }
        })

        vm.point.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(
                    TrackPointsFragmentDirections.actionTrackPointsFragmentToMapFragment(
                        it,
                        "update"
                    )
                )
                vm.navigateToMapDone()
            }
        })

        vm.eventStartService.observe(viewLifecycleOwner, Observer {
            if (it) {
                vm.startServiceDone()
                val intent = Intent(app, LiveLocationService::class.java).apply {
                    action = Constants.ACTION_START_SERVICE
                }
                val pi = PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, MainActivity::class.java),
                    0
                )
                intent.putExtra("pendingIntent", pi)
                app.startForegroundService(intent)
            }
        })

        vm.eventStopService.observe(viewLifecycleOwner, Observer {
            if (it) {
                vm.stopServiceDone()
                val intent = Intent(app, LiveLocationService::class.java).apply {
                    action = Constants.ACTION_STOP_SERVICE
                }
                app.startForegroundService(intent)
            }
        })

        vm.points.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })
    }
}
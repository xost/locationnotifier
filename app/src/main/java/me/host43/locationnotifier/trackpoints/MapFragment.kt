package me.host43.locationnotifier.trackpoints

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
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import me.host43.locationnotifier.R
import me.host43.locationnotifier.database.PointDatabase
import me.host43.locationnotifier.databinding.FragmentMapBinding

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var b: FragmentMapBinding
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = DataBindingUtil.inflate<FragmentMapBinding>(
            inflater,
            R.layout.fragment_map,
            container,
            false
        )

        val app = requireNotNull(this.activity).application
        val ds = PointDatabase.getInstance(app).dao
        val vmf = MapViewModelFactory(ds,app)
        val vm = ViewModelProvider(this,vmf).get(MapViewModel::class.java)

        b.lifecycleOwner=this
        b.vm=vm

        vm.navigateToTrackPoints.observe(viewLifecycleOwner, Observer {
            if (it){
                this.findNavController().navigate(
                    MapFragmentDirections.actionMapFragmentToTrackPointsFragment()
                )
                vm.navigateToTrackPointsDone()
            }
        })

        b.mapView.onCreate(savedInstanceState)
        b.mapView.onResume()
        b.mapView.getMapAsync(this)

        return b.root
    }

    override fun onMapReady(map: GoogleMap){
        map?.let{
            googleMap = it
        }
        val geoCoo = LatLng(22.3,33.4)
    }

    override fun onStart() {
        super.onStart()
        b.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        b.mapView.onStop()
    }

    override fun onResume() {
        super.onResume()
        b.mapView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        b.mapView.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        b.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        b.mapView.onLowMemory()
    }
}
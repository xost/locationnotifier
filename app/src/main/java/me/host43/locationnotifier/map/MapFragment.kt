package me.host43.locationnotifier.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import me.host43.locationnotifier.R
import me.host43.locationnotifier.database.Point
import me.host43.locationnotifier.database.PointDatabase
import me.host43.locationnotifier.databinding.FragmentMapBinding
import timber.log.Timber

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var b: FragmentMapBinding
    private lateinit var vm: MapViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var point: Point? = null

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

        val args = arguments
        point = args?.getSerializable("point") as Point?

        val app = requireNotNull(this.activity).application
        val ds = PointDatabase.getInstance(app).dao
        val vmf = MapViewModelFactory(ds, app)
        vm = ViewModelProvider(this, vmf).get(MapViewModel::class.java)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        b.lifecycleOwner = this
        b.vm = vm

        vm.navigateToTrackPoints.observe(viewLifecycleOwner, Observer {
            if (it) {
                this.findNavController().navigate(
                    MapFragmentDirections.actionMapFragmentToTrackPointsFragment()
                )
                vm.navigateToTrackPointsDone()
            }
        })

        vm.placenameIsNotSetNotify.observe(viewLifecycleOwner, Observer {
            if (it) {
                Toast.makeText(context, "please set a placename", Toast.LENGTH_SHORT).show()
                vm.placenameNotSetNotifyDone()
            }
        })

        vm.placeIsNotSetNotify.observe(viewLifecycleOwner, Observer {
            if (it) {
                Toast.makeText(context, "please set a place", Toast.LENGTH_SHORT).show()
                vm.placeNotSetNotifyDone()
            }
        })

        b.cancelButton.setOnClickListener {
            this.findNavController().navigate(
                MapFragmentDirections.actionMapFragmentToTrackPointsFragment(point,method)
            )
        }

        b.mapView.onCreate(savedInstanceState)
        b.mapView.onResume()
        b.mapView.getMapAsync(this)

        point?.let {
            b.placeName.setText(it.name.toString(), TextView.BufferType.EDITABLE)
            Timber.d("Point was passed = ${it.name}")
        }

        return b.root
    }

    override fun onMapReady(map: GoogleMap) {
        vm.setMap(map)
        setOnMapLongTap(map)
        enableMyLocation(map)
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

    private fun setOnMapLongTap(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            vm.newMarker(latLng)
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation(map: GoogleMap) {
        map.isMyLocationEnabled = true
        var ll: LatLng
        point?.let {
            ll = LatLng(it.latitude, it.longitude)
            vm.newMarker(ll)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15.0F))
            b.placeName.setText(it.name)
        } ?: fusedLocationClient.lastLocation.addOnSuccessListener {
            ll = LatLng(it.latitude, it.longitude)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15.0F))
        }
    }
}

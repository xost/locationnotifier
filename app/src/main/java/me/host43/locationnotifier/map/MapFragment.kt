package me.host43.locationnotifier.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import me.host43.locationnotifier.R
import me.host43.locationnotifier.database.PointDatabase
import me.host43.locationnotifier.databinding.FragmentMapBinding

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var b: FragmentMapBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_LOCATION_PERMISSION_CODE = 666

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
        val vmf = MapViewModelFactory(ds, app)
        val vm = ViewModelProvider(this, vmf).get(MapViewModel::class.java)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

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

        b.mapView.onCreate(savedInstanceState)
        b.mapView.onResume()
        b.mapView.getMapAsync(this)

        return b.root
    }


    override fun onMapReady(map: GoogleMap) {
        b.vm?.setMap(map)
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
            b.vm?.let {
                it.newMarker(latLng)
            }
        }
    }

    private fun isPermissionGranted() {

    }

    private fun enableMyLocation(map: GoogleMap) {
        activity?.let {
            if (ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf<String>(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    REQUEST_LOCATION_PERMISSION_CODE
                )
            }
            map.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener {
                val ll = LatLng(it.latitude,it.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(ll,15.0F))
            }
        }
    }
}

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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import me.host43.locationnotifier.R
import me.host43.locationnotifier.database.Point
import me.host43.locationnotifier.database.PointDatabase
import me.host43.locationnotifier.databinding.FragmentMapBinding
import timber.log.Timber

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var b: FragmentMapBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var marker: Marker? = null
    private var point = Point().apply {
        latitude = 0.0
        longitude = 0.0
        distance = 0.0
        enabled = false
    }
    private var method = ""

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

        arguments?.let {
            point = it.getSerializable("point") as Point
            method = it.getString("method").toString()
        }

        b.point = point

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        b.lifecycleOwner = this

        setListeners()

        b.mapView.onCreate(savedInstanceState)
        b.mapView.onResume()
        b.mapView.getMapAsync(this)

        return b.root
    }

    override fun onMapReady(map: GoogleMap) {
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

    private fun setListeners() {
        b.cancelButton.setOnClickListener {
            this.findNavController().navigate(
                MapFragmentDirections.actionMapFragmentToTrackPointsFragment(
                    point,
                    "cancel"
                )
            )
        }

        b.okButton.setOnClickListener {
            if (b.placeName.text.isEmpty()) {
                Toast.makeText(context, "please set a placename", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (b.distanceInput.text.isEmpty()) {
                Toast.makeText(context, "please set a distance", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            marker?.let {
                point = Point().apply {
                    latitude = it.position.latitude
                    longitude = it.position.longitude
                }
                this.findNavController().navigate(
                    MapFragmentDirections.actionMapFragmentToTrackPointsFragment(
                        point,
                        method
                    )
                )
            }
        }
    }

    private fun setOnMapLongTap(map: GoogleMap) {
        map.setOnMapLongClickListener { ll ->
            newMarker(ll, map)
        }
    }

    private fun newMarker(ll: LatLng, map: GoogleMap) {
        marker?.remove()
        marker = map.addMarker(MarkerOptions().position(ll).draggable(true))
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation(map: GoogleMap) {
        map.isMyLocationEnabled = true
        var ll: LatLng
        if (method.equals("new", true)) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                ll = LatLng(it.latitude, it.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15.0F))
            }
        } else {
            ll = LatLng(point.latitude, point.longitude)
            newMarker(ll, map)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15.0F))
            b.placeName.setText(point.name)
        }
    }

    fun onClickCancelButton() {

    }

    fun onClickOkButton() {

    }
}

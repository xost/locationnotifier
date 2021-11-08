package me.host43.locationnotifier.trackpoints

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import me.host43.locationnotifier.R
import me.host43.locationnotifier.database.PointDatabase
import me.host43.locationnotifier.database.PointDatabaseDao
import me.host43.locationnotifier.databinding.FragmentMapBinding
import me.host43.locationnotifier.databinding.FragmentMapBindingImpl

class MapFragment : Fragment(), InputListener {

    private lateinit var b: FragmentMapBinding
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MapKitFactory.setApiKey("dd4e461e-e9f8-4af5-a7dd-34e78e874e22")
        MapKitFactory.initialize(this.context)

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

        mapView = b.mapView

        mapView.map.move(
            CameraPosition(Point(55.751574, 37.573856), 17.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 3.0F), null
        )
        mapView.map.addInputListener(this)

        vm.navigateToTrackPoints.observe(viewLifecycleOwner, Observer {
            if (it){
                this.findNavController().navigate(
                    MapFragmentDirections.actionMapFragmentToTrackPointsFragment()
                )
                vm.navigateToTrackPointsDone()
            }
        })

        return b.root
    }

    override fun onMapTap(p0: Map, p1: Point) {
        Toast.makeText(
            context,
            "${p1.latitude.toString()}:${p1.longitude.toString()}",
            Toast.LENGTH_SHORT
        ).show()
        b.vm?.let{
            it.p.latitude=p1.latitude
            it.p.longitude=p1.longitude
            Log.i("SHORT TAP","GPS coordinates should be upgraded")
        }
    }

    override fun onMapLongTap(p0: Map, p1: Point) {
        Toast.makeText(
            context,
            "${p1.latitude.toString()}:${p1.longitude.toString()}",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onStop() {
        // Вызов onStop нужно передавать инстансам MapView и MapKit.
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        // Вызов onStart нужно передавать инстансам MapView и MapKit.
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }
}
package me.host43.locationnotifier.trackpoints

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import me.host43.locationnotifier.R
import me.host43.locationnotifier.databinding.FragmentMapBinding
import me.host43.locationnotifier.databinding.FragmentMapBindingImpl

class MapFragment : Fragment() {

    private lateinit var mapView:MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MapKitFactory.setApiKey("dd4e461e-e9f8-4af5-a7dd-34e78e874e22")
        MapKitFactory.initialize(this.context)

        val b = DataBindingUtil.inflate<FragmentMapBinding>(inflater,R.layout.fragment_map,container,false)

        mapView = b.mapView

        mapView.map.move(CameraPosition(Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f),
        Animation(Animation.Type.SMOOTH, 0.0F),null
        )

        return b.root
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
    }}
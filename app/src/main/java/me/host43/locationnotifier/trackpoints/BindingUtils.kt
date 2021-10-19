package me.host43.locationnotifier.trackpoints

import android.widget.TextView
import androidx.databinding.BindingAdapter
import me.host43.locationnotifier.database.Point

@BindingAdapter("name")
fun TextView.setName(p: Point){
    text = p.name
}

@BindingAdapter("altitude")
fun TextView.setAltitude(p: Point) {
    text = p.altitude.toString()
}

@BindingAdapter("latitude")
fun TextView.setlatitude(p: Point) {
    text = p.latitude.toString()
}

@BindingAdapter("distance")
fun TextView.setDistance(p: Point){
    text = p.distance.toString()
}
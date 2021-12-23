package me.host43.locationnotifier.database

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "points_table")
class Point: Serializable {
    @PrimaryKey(autoGenerate = true)
    var pointId: Long = 0L

    @ColumnInfo(name = "name")
    var name: String = ""

    @ColumnInfo(name = "latitude")
    var latitude: Double = 0.0

    @ColumnInfo(name = "longitude")
    var longitude: Double = 0.0

    @ColumnInfo(name = "distance")
    var distance: Double = 0.0

    @ColumnInfo(name = "enabled")
    var enabled: Boolean = false

    companion object {

        @BindingAdapter("android:text")
        @JvmStatic
        fun setText(v: TextView, value: String) {
            if (v.text != value) {
                v.text = value
            }
        }

        @InverseBindingAdapter(attribute = "android:text")
        @JvmStatic
        fun getString(v: EditText): String {
            return v.text.toString()
        }

        @BindingAdapter("android:text")
        @JvmStatic
        fun setDouble(v: TextView, value: Double) {
            v.text = value.toString()
        }

        @InverseBindingAdapter(attribute = "android:text")
        @JvmStatic
        fun getDouble(v: EditText): Double {
            return v.text.toString().toDouble()
        }
    }
}
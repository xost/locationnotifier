package me.host43.locationnotifier.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="points_table")
data class Point(
    @PrimaryKey(autoGenerate = true)
    var pointId: Long = 0L,
    @ColumnInfo(name="name")
    var name: String = "",
    @ColumnInfo(name="altitude")
    var altitude: Double = 0.0,
    @ColumnInfo(name="latitude")
    var latitude: Double = 0.0,
    @ColumnInfo(name="distance")
    var distance: Double = 0.0,
    @ColumnInfo(name="enabled")
    var enabled: Boolean = false
)

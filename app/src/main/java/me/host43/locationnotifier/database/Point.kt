package me.host43.locationnotifier.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="points_table")
data class Point(
    @PrimaryKey(autoGenerate = true)
    var pointId: Long,
    @ColumnInfo(name="name")
    val name: String,
    @ColumnInfo(name="altitude")
    val altitude: Double,
    @ColumnInfo(name="latitude")
    val latitude: Double,
    @ColumnInfo(name="distance")
    val distance: Double,
    @ColumnInfo(name="enabled")
    var enabled: Boolean
)

package me.host43.locationnotifier.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PointDatabaseDao {
    @Insert
    suspend fun insert(point: Point)
    @Update
    suspend fun update(point: Point)
    @Delete
    suspend fun delete(key: Long)
    @Query("SELECT * FROM points_table ORDER BY pointId ASC")
    fun getAllPoints(): LiveData<List<Point>>
    @Query("SELECT * FROM points_table WHERE pointId=:key")
    suspend fun get(key: Long): Point?
}
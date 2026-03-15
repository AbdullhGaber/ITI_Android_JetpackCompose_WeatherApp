package com.iti.weatherapp.data.local.db.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.iti.weatherapp.data.local.db.entities.FavoriteLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteLocationsDao {

    @Query("SELECT * FROM favorite_locations")
    fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteLocation(location: FavoriteLocation)

    @Delete
    suspend fun deleteFavoriteLocation(location: FavoriteLocation)

    @Query("SELECT * FROM favorite_locations WHERE latitude = :lat AND longitude = :lon LIMIT 1")
    suspend fun getLocationByCoordinates(lat: Double, lon: Double): FavoriteLocation?
}
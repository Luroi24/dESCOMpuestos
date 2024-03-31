package com.example.descompuestos
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

@Database(entities=[LocationEntity::class], version=1)
abstract class MMDatabase: RoomDatabase() {
    abstract fun locationDao(): ILocationDao
}

@Dao
interface ILocationDao {
    @Insert
    fun insertLocation(locationEntity: LocationEntity)
    @Query("SELECT * FROM LocationEntity")
    fun getAllLocations(): List<LocationEntity>
    @Query("SELECT COUNT(*) FROM LocationEntity")
    fun getCount(): Int
    @Query("DELETE FROM LocationEntity WHERE timestamp = :timestamp")
    fun deleteLocationByTimestamp(timestamp:Long)
}

@Entity
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
)

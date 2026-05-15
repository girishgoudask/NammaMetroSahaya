package com.girish.nammametrosahaya.data

import android.content.Context
import androidx.room.*

@Dao
interface MetroDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStations(stations: List<Station>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConnections(connections: List<RouteConnection>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExitGates(gates: List<ExitGate>)

    @Query("SELECT * FROM stations")
    suspend fun getAllStations(): List<Station>

    @Query("SELECT * FROM connections")
    suspend fun getAllConnections(): List<RouteConnection>

    @Query("SELECT * FROM exit_gates WHERE stationId = :stationId")
    suspend fun getExitGatesForStation(stationId: String): List<ExitGate>

    @Query("SELECT * FROM exit_gates")
    suspend fun getAllExitGates(): List<ExitGate>

    @Query("DELETE FROM stations")
    suspend fun deleteAllStations()

    @Query("DELETE FROM connections")
    suspend fun deleteAllConnections()

    @Query("DELETE FROM exit_gates")
    suspend fun deleteAllExitGates()
}

@Database(entities = [Station::class, RouteConnection::class, ExitGate::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun metroDao(): MetroDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "metro_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

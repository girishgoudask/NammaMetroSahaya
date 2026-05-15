package com.girish.nammametrosahaya.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stations")
data class Station(
    @PrimaryKey val id: String,
    val name: String,
    val line: String, // Purple, Green, or Both
    val latitude: Double,
    val longitude: Double
)

@Entity(tableName = "connections")
data class RouteConnection(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fromStationId: String,
    val toStationId: String,
    val fare: Double,
    val timeInMinutes: Int
)

@Entity(tableName = "exit_gates")
data class ExitGate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val stationId: String,
    val gateName: String,
    val landmark: String
)

data class StationWithExit(
    val station: Station,
    val exits: List<ExitGate>
)

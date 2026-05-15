package com.girish.nammametrosahaya.data

import java.util.*

class MetroRepository(private val metroDao: MetroDao) {
    suspend fun getAllStations() = metroDao.getAllStations()
    suspend fun getAllConnections() = metroDao.getAllConnections()
    suspend fun getExitGates(stationId: String) = metroDao.getExitGatesForStation(stationId)
    suspend fun getAllExitGates() = metroDao.getAllExitGates()
    
    suspend fun insertData(stations: List<Station>, connections: List<RouteConnection>, gates: List<ExitGate>) {
        metroDao.deleteAllStations()
        metroDao.deleteAllConnections()
        metroDao.deleteAllExitGates()

        metroDao.insertStations(stations)
        metroDao.insertConnections(connections)
        metroDao.insertExitGates(gates)
    }

    fun findPath(startId: String, endId: String, connections: List<RouteConnection>): List<String> {
        val adj = mutableMapOf<String, MutableList<String>>()
        connections.forEach {
            adj.getOrPut(it.fromStationId) { mutableListOf() }.add(it.toStationId)
            adj.getOrPut(it.toStationId) { mutableListOf() }.add(it.fromStationId)
        }
        
        val queue: Queue<List<String>> = LinkedList()
        queue.add(listOf(startId))
        val visited = mutableSetOf(startId)
        
        while (queue.isNotEmpty()) {
            val path = queue.poll() ?: break
            val last = path.last()
            if (last == endId) return path
            
            adj[last]?.forEach { neighbor ->
                if (neighbor !in visited) {
                    visited.add(neighbor)
                    val newPath = path.toMutableList()
                    newPath.add(neighbor)
                    queue.add(newPath)
                }
            }
        }
        return emptyList()
    }
}

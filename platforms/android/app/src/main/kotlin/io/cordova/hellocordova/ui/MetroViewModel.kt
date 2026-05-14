package io.cordova.hellocordova.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import io.cordova.hellocordova.data.*
import kotlinx.coroutines.launch

class MetroViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MetroRepository
    val allStations = MutableLiveData<List<Station>>()
    val routeResult = MutableLiveData<List<Station>>()
    val totalFare = MutableLiveData<Double>()
    val totalTime = MutableLiveData<Int>()
    val interchangeDetails = MutableLiveData<String>()
    val selectedDestinationId = MutableLiveData<String?>()
    
    // To persist selections in HomeFragment
    val homeStartStationId = MutableLiveData<String?>()
    val homeEndStationId = MutableLiveData<String?>()

    init {
        val dao = AppDatabase.getDatabase(application).metroDao()
        repository = MetroRepository(dao)
        loadStations()
    }

    private fun loadStations() = viewModelScope.launch {
        val prefs = getApplication<Application>().getSharedPreferences("metro_prefs", Context.MODE_PRIVATE)
        val dataVersion = prefs.getInt("data_version", 0)
        val currentExpectedVersion = 8 // Bumped to 8 to restore and expand exit data

        val existingStations = repository.getAllStations()
        
        if (existingStations.size < 80 || dataVersion < currentExpectedVersion) {
            val initialStations = mutableListOf<Station>()
            
            // --- PURPLE LINE ---
            val purpleData = listOf(
                Triple("Challaghatta", "CHT", Pair(12.9030, 77.4470)),
                Triple("Kengeri", "KNG", Pair(12.9079, 77.4649)),
                Triple("Kengeri Bus Terminal", "KBT", Pair(12.9174, 77.4836)),
                Triple("Pattanagere", "PAT", Pair(12.9238, 77.4965)),
                Triple("Jnanabharathi", "JNB", Pair(12.9304, 77.5094)),
                Triple("Rajarajeshwari Nagar", "RRN", Pair(12.9370, 77.5223)),
                Triple("Nayandahalli", "NYH", Pair(12.9436, 77.5352)),
                Triple("Mysore Road", "MSR", Pair(12.9538, 77.5420)),
                Triple("Deepanjali Nagar", "DPN", Pair(12.9550, 77.5450)),
                Triple("Attiguppe", "ATG", Pair(12.9600, 77.5300)),
                Triple("Vijayanagar", "VJN", Pair(12.9700, 77.5350)),
                Triple("Hosahalli", "HSH", Pair(12.9750, 77.5450)),
                Triple("Magadi Road", "MGRD", Pair(12.9750, 77.5550)),
                Triple("KSR City Railway", "SBC", Pair(12.9750, 77.5650)),
                Triple("Majestic", "MAJ", Pair(12.9756, 77.5728)),
                Triple("Sir M. Visvesvaraya", "SMV", Pair(12.9750, 77.5850)),
                Triple("Cubbon Park", "CBP", Pair(12.9800, 77.5950)),
                Triple("MG Road", "MG", Pair(12.9755, 77.6067)),
                Triple("Trinity", "TRI", Pair(12.9730, 77.6160)),
                Triple("Halasuru", "HAL", Pair(12.9750, 77.6250)),
                Triple("Indiranagar", "IND", Pair(12.9784, 77.6385)),
                Triple("SV Road", "SVR", Pair(12.9840, 77.6490)),
                Triple("Baiyappanahalli", "BYP", Pair(12.9908, 77.6491)),
                Triple("Benniganahalli", "BNH", Pair(12.9930, 77.6610)),
                Triple("K.R. Puram", "KRP", Pair(12.9940, 77.6750)),
                Triple("Mahadevapura", "MDP", Pair(12.9940, 77.6870)),
                Triple("Garudacharpalya", "GCP", Pair(12.9940, 77.7000)),
                Triple("Hoodi", "HDI", Pair(12.9940, 77.7120)),
                Triple("Seetharampalya", "SPY", Pair(12.9940, 77.7240)),
                Triple("Kundalahalli", "KHL", Pair(12.9900, 77.7360)),
                Triple("Nallurhalli", "NLH", Pair(12.9850, 77.7480)),
                Triple("Sri Sathya Sai Hospital", "SSH", Pair(12.9850, 77.7550)),
                Triple("Kadugodi Tree Park", "KTP", Pair(12.9880, 77.7620)),
                Triple("Whitefield Kadugodi", "WFD", Pair(12.9940, 77.7610))
            )
            purpleData.forEach { initialStations.add(Station(it.second, it.first, "Purple", it.third.first, it.third.second)) }

            // --- GREEN LINE ---
            val greenData = listOf(
                Triple("Madavara", "MDV", Pair(13.0600, 77.4850)),
                Triple("Chikkabidarakallu", "CBK", Pair(13.0550, 77.4900)),
                Triple("Manjunathnagar", "MJN", Pair(13.0500, 77.4930)),
                Triple("Nagasandra", "NGS", Pair(13.0480, 77.4950)),
                Triple("Dasarahalli", "DSH", Pair(13.0410, 77.5120)),
                Triple("Jalahalli", "JLH", Pair(13.0380, 77.5250)),
                Triple("Peenya Industry", "PI", Pair(13.0340, 77.5340)),
                Triple("Peenya", "PNY", Pair(13.0310, 77.5340)),
                Triple("Goraguntepalya", "GGP", Pair(13.0280, 77.5400)),
                Triple("Yeshwanthpur", "YPR", Pair(13.0230, 77.5500)),
                Triple("Sandal Soap Factory", "SSF", Pair(13.0130, 77.5540)),
                Triple("Mahalakshmi", "MLM", Pair(13.0070, 77.5480)),
                Triple("Rajajinagar", "RJN", Pair(12.9980, 77.5530)),
                Triple("Kuvempu Road", "KVR", Pair(12.9930, 77.5580)),
                Triple("Srirampura", "SRP", Pair(12.9900, 77.5640)),
                Triple("Sampige Road", "MSSR", Pair(12.9850, 77.5720)),
                Triple("Chickpete", "CPT", Pair(12.9680, 77.5750)),
                Triple("KR Market", "KRM", Pair(12.9600, 77.5750)),
                Triple("National College", "NC", Pair(12.9510, 77.5730)),
                Triple("Lalbagh", "LBG", Pair(12.9460, 77.5800)),
                Triple("South End Circle", "SEC", Pair(12.9380, 77.5800)),
                Triple("Jayanagar", "JYN", Pair(12.9279, 77.5801)),
                Triple("RV Road", "RVR", Pair(12.9210, 77.5830)),
                Triple("Banashankari", "BSK", Pair(12.9150, 77.5730)),
                Triple("JP Nagar", "JPN", Pair(12.9070, 77.5730)),
                Triple("Yelachenahalli", "YCH", Pair(12.8962, 77.5730)),
                Triple("Konanakunte Cross", "KKC", Pair(12.8850, 77.5730)),
                Triple("Doddakallasandra", "DKS", Pair(12.8750, 77.5730)),
                Triple("Vajrahalli", "VJH", Pair(12.8650, 77.5730)),
                Triple("Thalaghattapura", "TGP", Pair(12.8550, 77.5730)),
                Triple("Silk Institute", "SI", Pair(12.8450, 77.5730))
            )
            greenData.forEach { if (it.second != "MAJ") initialStations.add(Station(it.second, it.first, "Green", it.third.first, it.third.second)) }

            // Majestic interchange fix
            initialStations.find { it.id == "MAJ" }?.let { initialStations.remove(it) }
            initialStations.add(Station("MAJ", "Majestic (Interchange)", "Both", 12.9756, 77.5728))

            val initialConnections = mutableListOf<RouteConnection>()
            for (i in 0 until purpleData.size - 1) initialConnections.add(RouteConnection(fromStationId = purpleData[i].second, toStationId = purpleData[i+1].second, fare = 5.0, timeInMinutes = 3))
            val greenIds = greenData.map { it.second }.toMutableList()
            if (!greenIds.contains("MAJ")) greenIds.add(greenIds.indexOf("MSSR") + 1, "MAJ")
            for (i in 0 until greenIds.size - 1) initialConnections.add(RouteConnection(fromStationId = greenIds[i], toStationId = greenIds[i+1], fare = 5.0, timeInMinutes = 3))

            // COMPREHENSIVE EXITS DATA RESTORED
            val initialGates = listOf(
                // MAJESTIC - 4 EXITS
                ExitGate(stationId = "MAJ", gateName = "Gate 1", landmark = "KSR Bengaluru City Railway Station (Railway Station)"),
                ExitGate(stationId = "MAJ", gateName = "Gate 2", landmark = "Upparpet Police Station (Landmark)"),
                ExitGate(stationId = "MAJ", gateName = "Gate 3", landmark = "KBS Majestic Bus Stand (Bus Stand)"),
                ExitGate(stationId = "MAJ", gateName = "Gate 4", landmark = "Gandhinagar / KSRTC side (Bus Stand)"),
                
                // BANASHANKARI - 2 EXITS
                ExitGate(stationId = "BSK", gateName = "Gate A", landmark = "Banashankari TTMC Bus Stand (Bus Stand)"),
                ExitGate(stationId = "BSK", gateName = "Gate B", landmark = "Banashankari Temple / Bus Stop (Landmark)"),
                
                // OTHERS
                ExitGate(stationId = "KRM", gateName = "Gate A", landmark = "Victoria Hospital / Vani Vilas (Hospital)"),
                ExitGate(stationId = "KRM", gateName = "Gate B", landmark = "KR Market (Landmark)"),
                ExitGate(stationId = "MG", gateName = "Gate A", landmark = "Brigade Road / Church Street (Landmark)"),
                ExitGate(stationId = "MG", gateName = "Gate B", landmark = "Manipal Centre (Landmark)"),
                ExitGate(stationId = "SSH", gateName = "Gate 1", landmark = "Sri Sathya Sai Hospital (Hospital)"),
                ExitGate(stationId = "YPR", gateName = "Gate 1", landmark = "Yeshwanthpur Railway Station (Railway Station)"),
                ExitGate(stationId = "YPR", gateName = "Gate 2", landmark = "Yeshwanthpur TTMC (Bus Stand)"),
                ExitGate(stationId = "KRP", gateName = "Gate 1", landmark = "Tin Factory Bus Stop (Bus Stand)"),
                ExitGate(stationId = "KRP", gateName = "Gate 2", landmark = "KR Puram Railway Station (Railway Station)"),
                ExitGate(stationId = "VJN", gateName = "Gate A", landmark = "Vijayanagar TTMC Bus Stand (Bus Stand)"),
                ExitGate(stationId = "PAT", gateName = "Gate 1", landmark = "RV College of Engineering (Landmark)"),
                ExitGate(stationId = "LBG", gateName = "Gate 1", landmark = "Lalbagh West Gate (Landmark)"),
                ExitGate(stationId = "JYN", gateName = "Gate 1", landmark = "Jayanagar 4th Block Market (Landmark)")
            )

            repository.insertData(initialStations, initialConnections, initialGates)
            prefs.edit().putInt("data_version", currentExpectedVersion).apply()
            allStations.postValue(initialStations.sortedBy { it.name })
        } else {
            allStations.postValue(existingStations.sortedBy { it.name })
        }
    }

    fun findRoute(startId: String, endId: String) = viewModelScope.launch {
        val connections = repository.getAllConnections()
        val pathIds = repository.findPath(startId, endId, connections)
        val stations = repository.getAllStations()
        val pathStations = pathIds.mapNotNull { id -> stations.find { it.id == id } }
        routeResult.postValue(pathStations)

        var fareSum = 0.0
        var timeSum = 0
        
        for (i in 0 until pathIds.size - 1) {
            val from = pathIds[i]; val to = pathIds[i+1]
            connections.find { (it.fromStationId == from && it.toStationId == to) || (it.fromStationId == to && it.toStationId == from) }?.let {
                fareSum += it.fare; timeSum += it.timeInMinutes
            }
        }

        val linesInPath = pathStations.map { it.line }.filter { it != "Both" }.distinct()
        val interchange = if (linesInPath.size > 1) {
            "Interchange: Change from ${linesInPath[0]} to ${linesInPath[1]} at Majestic"
        } else {
            "Direct route on ${linesInPath.getOrNull(0) ?: ""} Line"
        }

        totalFare.postValue(if (fareSum > 60) 60.0 else fareSum)
        totalTime.postValue(timeSum)
        interchangeDetails.postValue(interchange)
    }
}

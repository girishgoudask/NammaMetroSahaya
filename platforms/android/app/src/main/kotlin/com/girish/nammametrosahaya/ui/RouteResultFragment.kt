package com.girish.nammametrosahaya.ui

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.girish.nammametrosahaya.R
import com.girish.nammametrosahaya.data.Station
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class RouteResultFragment : Fragment() {
    private lateinit var viewModel: MetroViewModel
    private lateinit var map: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize osmdroid configuration
        val ctx = requireContext().applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        Configuration.getInstance().userAgentValue = ctx.packageName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_route_result, container, false)
        viewModel = ViewModelProvider(requireActivity())[MetroViewModel::class.java]

        map = view.findViewById(R.id.mapview)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val fareText = view.findViewById<TextView>(R.id.res_txt_fare)
        val timeText = view.findViewById<TextView>(R.id.res_txt_time)
        val interchangeText = view.findViewById<TextView>(R.id.res_txt_interchange)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_route_path)
        val btnBack = view.findViewById<Button>(R.id.btn_back_home)
        val btnExits = view.findViewById<Button>(R.id.btn_view_exits)

        viewModel.totalFare.observe(viewLifecycleOwner) { fare ->
            fareText.text = "Total Fare: ₹${fare.toInt()}"
        }

        viewModel.totalTime.observe(viewLifecycleOwner) { time ->
            timeText.text = "Estimated Time: $time mins"
        }

        viewModel.interchangeDetails.observe(viewLifecycleOwner) { details ->
            interchangeText.text = details
        }

        viewModel.routeResult.observe(viewLifecycleOwner) { path ->
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = RoutePathAdapter(path)
            drawRouteOnMap(path)
        }

        btnExits.setOnClickListener {
            findNavController().navigate(R.id.exitFinderFragment)
        }

        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }

    private fun drawRouteOnMap(path: List<Station>) {
        map.overlays.clear()
        if (path.isEmpty()) return

        val geoPoints = path.map { GeoPoint(it.latitude, it.longitude) }

        // Draw Line
        val line = Polyline()
        line.setPoints(geoPoints)
        line.outlinePaint.color = Color.BLUE
        line.outlinePaint.strokeWidth = 10f
        map.overlays.add(line)

        // Add Markers
        val startMarker = Marker(map)
        startMarker.position = geoPoints.first()
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        startMarker.title = "Start: ${path.first().name}"
        map.overlays.add(startMarker)

        val endMarker = Marker(map)
        endMarker.position = geoPoints.last()
        endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        endMarker.title = "End: ${path.last().name}"
        map.overlays.add(endMarker)

        // Zoom to fit
        map.post {
            if (geoPoints.size > 1) {
                val boundingBox = BoundingBox.fromGeoPoints(geoPoints)
                map.zoomToBoundingBox(boundingBox, true, 100)
            } else {
                map.controller.setZoom(15.0)
                map.controller.setCenter(geoPoints.first())
            }
        }
        
        map.invalidate()
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}

class RoutePathAdapter(private val stations: List<Station>) : RecyclerView.Adapter<RoutePathAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(android.R.id.text1)
        val line: TextView = view.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val station = stations[position]
        holder.name.text = "${position + 1}. ${station.name}"
        holder.line.text = "Line: ${station.line}"
    }

    override fun getItemCount() = stations.size
}

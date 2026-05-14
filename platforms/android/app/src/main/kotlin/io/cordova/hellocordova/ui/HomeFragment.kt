package io.cordova.hellocordova.ui

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import io.cordova.hellocordova.R

class HomeFragment : Fragment() {
    private lateinit var viewModel: MetroViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        viewModel = ViewModelProvider(requireActivity())[MetroViewModel::class.java]

        val startAuto = view.findViewById<AutoCompleteTextView>(R.id.auto_start)
        val endAuto = view.findViewById<AutoCompleteTextView>(R.id.auto_end)
        val btnFind = view.findViewById<Button>(R.id.btn_find_route)

        viewModel.allStations.observe(viewLifecycleOwner) { stations ->
            val names = stations.map { it.name }
            val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, names)
            
            startAuto.setAdapter(adapter)
            endAuto.setAdapter(adapter)

            // Restore selections if they exist
            viewModel.homeStartStationId.value?.let { startId ->
                val station = stations.find { it.id == startId }
                station?.let { startAuto.setText(it.name, false) }
            }
            viewModel.homeEndStationId.value?.let { endId ->
                val station = stations.find { it.id == endId }
                station?.let { endAuto.setText(it.name, false) }
            }
        }

        btnFind.setOnClickListener {
            val startName = startAuto.text.toString().trim()
            val endName = endAuto.text.toString().trim()

            if (startName.isEmpty() || endName.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter both starting and destination stations", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val stations = viewModel.allStations.value ?: emptyList()
            val startStation = stations.find { it.name.equals(startName, ignoreCase = true) }
            val endStation = stations.find { it.name.equals(endName, ignoreCase = true) }

            if (startStation != null && endStation != null) {
                if (startStation.id == endStation.id) {
                    Toast.makeText(requireContext(), "Starting and destination stations cannot be the same", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                // Save selections to ViewModel for persistence
                viewModel.homeStartStationId.value = startStation.id
                viewModel.homeEndStationId.value = endStation.id

                viewModel.selectedDestinationId.value = endStation.id
                viewModel.findRoute(startStation.id, endStation.id)
                // Navigate to the result page
                findNavController().navigate(R.id.routeResultFragment)
            } else {
                Toast.makeText(requireContext(), "Please select valid station names from the list", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.btn_visual_guide).setOnClickListener {
            findNavController().navigate(R.id.visualGuideFragment)
        }

        view.findViewById<Button>(R.id.btn_exits_direct).setOnClickListener {
            findNavController().navigate(R.id.exitFinderFragment)
        }

        return view
    }
}

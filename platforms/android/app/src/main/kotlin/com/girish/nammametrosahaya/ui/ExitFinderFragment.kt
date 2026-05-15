package com.girish.nammametrosahaya.ui

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.girish.nammametrosahaya.R
import com.girish.nammametrosahaya.data.AppDatabase
import com.girish.nammametrosahaya.data.ExitGate
import com.girish.nammametrosahaya.data.MetroRepository
import kotlinx.coroutines.*

class ExitFinderFragment : Fragment() {
    private lateinit var viewModel: MetroViewModel
    private var adapter: ExitAdapter? = null
    private var repo: MetroRepository? = null
    private var countMessageText: TextView? = null
    private var searchEdit: EditText? = null
    private var spinner: Spinner? = null
    
    private var allExitsWithStations = mutableListOf<Pair<ExitGate, String>>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_exit, container, false)
        viewModel = ViewModelProvider(requireActivity())[MetroViewModel::class.java]
        
        val dao = AppDatabase.getDatabase(requireContext()).metroDao()
        repo = MetroRepository(dao)

        spinner = view.findViewById(R.id.spinner_station_exit)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_exits)
        searchEdit = view.findViewById(R.id.edit_search_exit)
        countMessageText = view.findViewById(R.id.txt_exit_count_message)
        
        adapter = ExitAdapter(emptyList())
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        viewModel.allStations.observe(viewLifecycleOwner) { stations ->
            if (stations == null || stations.isEmpty()) return@observe

            val names = mutableListOf("Select Station (Show All)")
            names.addAll(stations.map { it.name })
            
            val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner?.adapter = arrayAdapter

            lifecycleScope.launch {
                val gates = repo?.getAllExitGates() ?: emptyList()
                allExitsWithStations.clear()
                
                val stationMap = stations.associateBy { it.id }
                val uniqueGates = gates.distinctBy { it.stationId + it.gateName + it.landmark }
                
                allExitsWithStations.addAll(uniqueGates.mapNotNull { gate ->
                    stationMap[gate.stationId]?.let { station -> gate to station.name }
                })
                
                // Destination sync logic: Consume the value once
                viewModel.selectedDestinationId.value?.let { destId ->
                    val index = stations.indexOfFirst { it.id == destId }
                    if (index != -1) {
                        spinner?.setSelection(index + 1)
                        // Clear the value so it doesn't stay set when returning manually later
                        viewModel.selectedDestinationId.value = null
                        return@launch
                    }
                } ?: run {
                    // Manual opening: Show all
                    applyFilters()
                }
            }
        }

        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                applyFilters()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        searchEdit?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

    private fun applyFilters() {
        val query = searchEdit?.text?.toString()?.trim() ?: ""
        val selectedIndex = spinner?.selectedItemPosition ?: 0
        
        var filteredList = allExitsWithStations.toList()

        if (selectedIndex > 0) {
            val selectedStationName = spinner?.selectedItem?.toString() ?: ""
            filteredList = filteredList.filter { it.second.equals(selectedStationName, ignoreCase = true) }
        }

        if (query.isNotEmpty()) {
            filteredList = filteredList.filter { 
                it.second.contains(query, ignoreCase = true) || 
                it.first.landmark.contains(query, ignoreCase = true)
            }
        }

        updateUI(filteredList, query, selectedIndex)
    }

    private fun updateUI(list: List<Pair<ExitGate, String>>, query: String, selectedIndex: Int) {
        val adapterRef = adapter ?: return
        adapterRef.updateData(list)
        
        val selectedStationName = if (selectedIndex > 0) spinner?.selectedItem?.toString() ?: "" else ""
        val countMsg = countMessageText ?: return

        when {
            list.isEmpty() && query.isNotEmpty() -> {
                countMsg.text = "No results found for '$query'. Please try another landmark."
                countMsg.setBackgroundColor(Color.parseColor("#FFEBEE"))
                countMsg.setTextColor(Color.parseColor("#B71C1C"))
                countMsg.visibility = View.VISIBLE
            }
            
            list.isEmpty() && selectedIndex > 0 -> {
                countMsg.text = "Information: No specific landmarks recorded for $selectedStationName. Please follow the 'EXIT' signs on the platform."
                countMsg.setBackgroundColor(Color.parseColor("#FFF3E0"))
                countMsg.setTextColor(Color.parseColor("#E65100"))
                countMsg.visibility = View.VISIBLE
            }

            list.size == 1 && selectedIndex > 0 -> {
                countMsg.text = "Note: There is only a single exit gate for $selectedStationName station. Follow the instructions below."
                countMsg.setBackgroundColor(Color.parseColor("#E3F2FD"))
                countMsg.setTextColor(Color.parseColor("#1565C0"))
                countMsg.visibility = View.VISIBLE
            }
            
            else -> {
                countMsg.visibility = View.GONE
            }
        }
    }
}

package io.cordova.hellocordova.ui

import android.graphics.Color
import android.graphics.Typeface
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.cordova.hellocordova.data.ExitGate

class ExitAdapter(
    private var exits: List<Pair<ExitGate, String>> // ExitGate and its Station Name
) : RecyclerView.Adapter<ExitAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gateName: TextView = view.findViewById(android.R.id.text1)
        val landmark: TextView = view.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (exit, stationName) = exits[position]
        
        // Show Station Name + Gate
        holder.gateName.text = "${stationName} - ${exit.gateName}"
        holder.gateName.setTextColor(Color.parseColor("#800080")) // Purple
        holder.gateName.setTypeface(null, Typeface.BOLD)
        
        holder.landmark.text = exit.landmark
        
        // Color coding for clear "Sahaya" (Help)
        when {
            exit.landmark.contains("(Hospital)", ignoreCase = true) -> {
                holder.landmark.setTextColor(Color.parseColor("#D32F2F")) // Medical Red
            }
            exit.landmark.contains("(Bus Stand)", ignoreCase = true) || 
            exit.landmark.contains("(Railway Station)", ignoreCase = true) -> {
                holder.landmark.setTextColor(Color.parseColor("#1B5E20")) // Transit Green
            }
            else -> {
                holder.landmark.setTextColor(Color.BLACK)
            }
        }
    }

    override fun getItemCount() = exits.size

    fun updateData(newExits: List<Pair<ExitGate, String>>) {
        this.exits = newExits
        notifyDataSetChanged()
    }
}

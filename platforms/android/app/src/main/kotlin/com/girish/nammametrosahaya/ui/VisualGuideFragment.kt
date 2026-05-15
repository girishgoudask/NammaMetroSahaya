package com.girish.nammametrosahaya.ui

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.girish.nammametrosahaya.R

data class GuideStep(
    val title: String,
    val description: String,
    val imageResId: Int
)

class VisualGuideFragment : Fragment() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var btnNext: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_visual_guide, container, false)

        viewPager = view.findViewById(R.id.guide_view_pager)
        tabLayout = view.findViewById(R.id.guide_tab_layout)
        btnNext = view.findViewById(R.id.btn_next_guide)

        val steps = listOf(
            GuideStep(
                "1. Security Check", 
                "Place your bags on the scanner and walk through the metal detector. Keep your phone and keys in your hand or bag.",
                R.drawable.security
            ),
            GuideStep(
                "2. Buy a Token", 
                "Go to the Ticket Counter or use the Automatic Machine. Tell the destination name and pay the amount to get a Token or QR ticket.",
                R.drawable.token
            ),
            GuideStep(
                "3. Entry Gate", 
                "Touch your Token or QR ticket on the scanner at the gate. The doors will open. Walk through quickly.",
                R.drawable.entry
            ),
            GuideStep(
                "4. Find Platform", 
                "Look at the signs (Purple or Green). Follow the arrows to the correct platform. Platforms are usually upstairs or downstairs.",
                R.drawable.find
            ),
            GuideStep(
                "5. Board the Train", 
                "Stand behind the yellow line. Wait for the train to stop. Let people come out first, then enter the train.",
                R.drawable.board
            ),
            GuideStep(
                "6. Reaching Destination", 
                "Listen to the announcements. When your station comes, get off the train. Use your token again at the exit gate to leave.",
                R.drawable.reach
            )
        )

        viewPager.adapter = GuideAdapter(steps)

        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == steps.size - 1) {
                    btnNext.text = "Start Traveling"
                } else {
                    btnNext.text = "Next Step"
                }
            }
        })

        btnNext.setOnClickListener {
            if (viewPager.currentItem < steps.size - 1) {
                viewPager.currentItem = viewPager.currentItem + 1
            } else {
                findNavController().popBackStack()
            }
        }

        return view
    }
}

class GuideAdapter(private val steps: List<GuideStep>) : RecyclerView.Adapter<GuideAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.txt_guide_title)
        val desc: TextView = view.findViewById(R.id.txt_guide_description)
        val icon: ImageView = view.findViewById(R.id.img_guide_main)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_guide_page, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val step = steps[position]
        holder.title.text = step.title
        holder.desc.text = step.description
        holder.icon.setImageResource(step.imageResId)
    }

    override fun getItemCount() = steps.size
}

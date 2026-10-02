package org.engine.simulogic.android.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import org.engine.simulogic.R
import org.engine.simulogic.android.ui.models.HelpItem
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView
import pl.droidsonroids.gif.GifTextView

class EnvironmentHelpViewPagerAdapter :
    RecyclerView.Adapter<EnvironmentHelpViewPagerAdapter.HelpViewHolder>() {

    private val data = mutableMapOf<Int, MutableList<HelpItem>>()
    var currentPage = 0

    init {
        data[0] = mutableListOf()
        data[1] = mutableListOf()
        data[2] = mutableListOf()
        addItem(
            0, HelpItem(
                "Touch",
                "This mode restricts touch events only to a single item on the screen." +
                    " This aids the user in moving and positioning items more accurately on the environment space.",
                0,
                R.drawable.tutorial_touch_mode
            )
        )
        addItem(
            0,
            HelpItem(
                "Interact",
                "Enables interactions in the environment only for elements with states available e.g POWER ON & POWER OFF.",
                0,
                R.drawable.tutorial_interact_mode
            )
        )
        addItem(
            0,
            HelpItem(
                "Sel-Touch",
                "Enable multi-select mode in the environment. The user can toggle items as selected or not selected by the click of a finger.",
                0,
                R.drawable.tutorial_sel_touch_mode
            )
        )
        addItem(
            0,
            HelpItem(
                "Sel-Range",
                "Enables multi-select but with a range slider instead of selecting individual items naturally.",
                0,
                R.drawable.tutorial_sel_range_mode
            )
        )
        addItem(
            0, HelpItem(
                "Connect-2",
                "Enables connection mode with an upper limit of 2 joints. " +
                    "These joints can be used for proper wire management in the project.",
                0,
                R.drawable.tutorial_connect_2_mode
            )
        )
        addItem(
            0, HelpItem(
                "Connect-4",
                "Enables connection mode with an upper limit of 4 joints. " +
                    "These joints can be used for proper wire management in the project.",
                0,
                R.drawable.tutorial_connect_4_mode
            )
        )
        addItem(
            0, HelpItem(
                "Connect-6",
                "Enables connection mode with an upper limit of 6 joints. " +
                    "These joints can be used for proper wire management in the project.",
                0,
                R.drawable.tutorial_connect_6_mode
            )
        )
        addItem(
            1, HelpItem(
                "Rotate",
                description = "Rotates selected components 90-degrees in a clockwise direction.",
                0,
                layoutIcon = R.drawable.action_rotate
            )
        )
        addItem(
            1, HelpItem(
                "Group With Sel-Touch",
                description = "Groups selected components as one entity.",
                0,
                layoutIcon = R.drawable.action_group_sel_touch
            )
        )
        addItem(
            1, HelpItem(
                "Group With Sel-Range",
                description = "Groups selected components as one entity.",
                0,
                layoutIcon = R.drawable.action_group_sel_range
            )
        )
        addItem(
            1, HelpItem(
                "UnGroup",
                description = "Collapses grouped components as individual entities.",
                0,
                layoutIcon = R.drawable.action_ungroup
            )
        )
        addItem(
            1, HelpItem(
                "Split Node",
                description = "Adds new nodes to a wire connection. For a more controlled wire routing.",
                0,
                layoutIcon = R.drawable.action_split_node
            )
        )
        addItem(
            1, HelpItem(
                "A-Label",
                description = "Anchors a label directly to a component. For better a better labelling experience.",
                0,
                layoutIcon = R.drawable.action_anchor_label
            )
        )

        addItem(
            2, HelpItem(
                "POINT",
                description = "Creates a point that splits a connection into multiple nodes or passes a value from one component to another like a buffer gate.",
                0,
                R.drawable.component_point
            )
        )

        addItem(
            2,
            HelpItem(
                "RANDOM",
                description = "A seeded component that creates a random signal if the incoming signal has changed.",
                0,
                R.drawable.component_random
            )
        )
        addItem(
            2, HelpItem(
                "CHANNEL",
                description = "Carries signals from a source to one or more destinations without wires." +
                    " A channel has an input with a particular ID and one or multiple outputs.",
                0,
                R.drawable.component_random
            )
        )
    }

    private fun addItem(key: Int, item: HelpItem) {
        data[key]?.add(item)
    }

    inner class HelpViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun init(item: HelpItem) {
            itemView.findViewById<GifImageView>(R.id.gif_view).apply {
                setBackgroundResource(item.layoutIcon)
            }
            itemView.findViewById<MaterialTextView>(R.id.title).apply {
                text = item.title
            }
            itemView.findViewById<MaterialTextView>(R.id.description).apply {
                text = item.description
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelpViewHolder {
        return HelpViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.environment_help_tutorial_layout, parent, false)
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItemsAll() {
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data[currentPage]!!.size
    }

    override fun onBindViewHolder(holder: HelpViewHolder, position: Int) {
        holder.init(data[currentPage]!![position])
    }
}

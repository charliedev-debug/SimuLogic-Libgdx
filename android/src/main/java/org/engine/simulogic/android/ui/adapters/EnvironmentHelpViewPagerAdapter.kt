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
                "Undo/Redo",
                description = "Undo/removes the current operation in order. Redo/restores the previous operation in order.",
                0,
                layoutIcon = R.drawable.action_undo_redo
            )
        )
        addItem(
            1,
            HelpItem(
                "Cut",
                description = "Cut items from a certain position.",
                0,
                layoutIcon = R.drawable.action_cut
            )
        )
        addItem(
            1, HelpItem(
                "Copy",
                description = "Duplicates the selected components, this operation can be finalized by clicking the paste button and only connections of selected children or parents will be duplicated.",
                0,
                layoutIcon = R.drawable.action_copy
            )
        )

        addItem(
            1, HelpItem(
                "Remove Connection",
                description = "The delete tool can be used to remove established connections.",
                0,
                layoutIcon = R.drawable.action_remove_line
            )
        )

        addItem(
            1, HelpItem(
                "Delete",
                description = "Deletes components form the environment. Any connection associated with the deleted component will also be deleted.",
                layoutId = 0,
                layoutIcon = R.drawable.action_delete
            )
        )

        addItem(2, HelpItem("AND GATE", description = "Outputs  HIGH(1) if all Inputs are HIGH(1).", 0, R.drawable.component_gate_and))
        addItem(2, HelpItem("OR GATE", description = "Outputs HIGH(1) if one of the Inputs is HIGH(1).", 0, R.drawable.component_gate_or))
        addItem(2, HelpItem("XOR GATE", description = "Outputs HIGH(1) only when the Inputs are different.", 0, R.drawable.component_gate_xor))
        addItem(2, HelpItem("NOR GATE", description = "Negated OR GATE.", 0, R.drawable.component_gate_nor))
        addItem(2, HelpItem("NOT GATE", description = "Outputs HIGH(1) only when the input is LOW(1). It negates the Input.", 0, R.drawable.component_gate_not))
        addItem(2, HelpItem("NAND GATE", description = "Negated AND GATE", 0, R.drawable.component_gate_nand))
        addItem(2, HelpItem("XNOR GATE", description = "Negated XOR GATE.", 0, R.drawable.component_gate_xnor))
        addItem(
            2, HelpItem(
                "CLOCK",
                description = "Periodic signaling component that alternates between 0 (LOW) and 1 (HIGH) at a fixed interval." +
                    " Maximum supported interval is 60Hz.",
                0,
                R.drawable.component_clock
            )
        )
        addItem(
            2,
            HelpItem(
                "D-LATCH",
                description = "Level-sensitive  memory component that stores 1 bit of data.",
                0,
                R.drawable.component_general_d_latch
            )
        )
        addItem(
            2,
            HelpItem(
                "D-FLIP-FLOP",
                description = "Memory component that stores 1 bit of data and can only be updated on clock edge.",
                0,
                R.drawable.component_general_d_flip_flop
            )
        )
        addItem(
            2,
            HelpItem(
                "RANDOM",
                description = "A seeded component that creates a random signal if the incoming signal has changed.",
                0,
                R.drawable.component_general_random
            )
        )
        addItem(
            2, HelpItem(
                "DATA-BUS",
                description = "A group of parallel wires that carry multi-bit data." +
                    " This is useful also for space optimization and organization.",
                0,
                R.drawable.component_general_data_bus
            )
        )
        addItem(
            2, HelpItem(
                "CHANNEL",
                description = "Carries signals from a source to one or more destinations without wires." +
                    " A channel has an input with a particular ID and one or multiple outputs.",
                0,
                R.drawable.component_general_channel
            )
        )
        addItem(
            2,
            HelpItem(
                "BCD-DISPLAY",
                description = "Binary Coded Decimal values from 0 - F.",
                0,
                R.drawable.component_general_bcd_display
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

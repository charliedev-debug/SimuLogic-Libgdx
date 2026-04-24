package org.engine.simulogic.android

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Window
import android.view.WindowInsets
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.runBlocking
import org.engine.simulogic.R
import org.engine.simulogic.android.circuits.storage.UserSettings
import org.engine.simulogic.android.helpers.ActivityHelpers
import org.engine.simulogic.android.ui.adapters.HelpAdapter
import org.engine.simulogic.android.ui.models.HelpItem

class HelpActivity : AppCompatActivity() {

private val userSettings = UserSettings()
    override fun onCreate(savedInstanceState: Bundle?) {
        runBlocking{
            ActivityHelpers.getTheme(userSettings, this@HelpActivity)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(scrim = Color.WHITE),
            navigationBarStyle = SystemBarStyle.dark(scrim = Color.WHITE))
        ActivityHelpers.setStatusBarColor(window, ActivityHelpers.getThemeResourceID(this, com.google.android.material.R.attr.backgroundColor))
        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }

        val helpRecyclerView = findViewById<RecyclerView>(R.id.helpList)
        val helpAdapter = HelpAdapter().apply {

            add(HelpItem("Menu Tools", layoutId = R.layout.help_title_view))

            add(
                HelpItem(
                    "Origin",
                    description = "Repositions the camera to the last saved camera coordinates in the x & y plane",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.origin
                )
            )
            add(
                HelpItem(
                    "Touch",
                    description = "This mode restricts touch events only to a single item on the screen. This aids the user in moving and positioning items more accurately on the environment space.",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.touch,
                    gifView = R.drawable.tutorial_touch_mode
                )
            )
            add(
                HelpItem(
                    "Interact",
                    description = "Enables Interactions in the environment only for elements with states available.",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.interact,
                    gifView = R.drawable.tutorial_interact_mode
                )
            )
            add(
                HelpItem(
                    "Sel-Touch",
                    description = "Enables multi-select mode in the environment. The user can toggle items as selected or not selected by the click of a finger.",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.selection,
                    gifView = R.drawable.action_group_sel_touch
                )
            )
            add(
                HelpItem(
                    "Sel-Range",
                    description = "Enables multi-select but with a range slider instead of selecting individual items manually.",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.select_rect,
                    gifView = R.drawable.action_group_sel_range
                )
            )
            add(
                HelpItem(
                    "Connect-2",
                    description = "Enables connection mode with an upper limit of 2 joints. These joints can be used for proper wire management in the project.",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.connect_2_node,
                    gifView = R.drawable.tutorial_connect_2_mode
                )
            )
            add(
                HelpItem(
                    "Connect-4",
                    description = "Enables connection mode with an upper limit of 4 joints. These joints can be used for proper wire management in the project.",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.connect_4_node,
                    gifView = R.drawable.tutorial_connect_4_mode
                )
            )
            add(
                HelpItem(
                    "Connect-6",
                    description = "Enables connection mode with an upper limit of 6 joints. These joints can be used for proper wire management in the project.",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.connect_6_node,
                    gifView = R.drawable.tutorial_connect_6_mode
                )
            )
            add(
                HelpItem(
                    "Rotate",
                    description = "Rotates selected components 90-degrees in a clockwise direction.",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.rotate_right,
                    gifView = R.drawable.action_rotate
                )
            )
            add(
                HelpItem(
                    "Group",
                    description = "Groups selected components as one entity. Collapses grouped components as individual entities.",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.group,
                    gifView = R.drawable.action_ungroup
                )
            )

            add(
                HelpItem(
                    "Undo & Redo",
                    description = "Undo/removes the current operation in order. Redo/restores the previous operation in order.",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.undo,
                    gifView = R.drawable.action_undo_redo
                )
            )

            add(
                HelpItem(
                    "Copy",
                    description = "Duplicates the selected components, this operation can be finalized by clicking the paste button and only connections of selected children or parents will be duplicated.",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.copy,
                    gifView = R.drawable.action_copy
                )
            )
            add(
                HelpItem(
                    "Cut",
                    description = "Cut items from a certain position.",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.cut,
                    gifView = R.drawable.action_cut
                )
            )
            add(
                HelpItem(
                    "Paste",
                    description = "This operation finalizes the cut and copy operations",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.paste,
                    gifView = R.drawable.action_cut
                )
            )
            add(
                HelpItem(
                    "Delete",
                    description = "Deletes components form the environment. Any connection associated with the deleted component will also be deleted.",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.delete,
                    gifView = R.drawable.action_delete
                )
            )

            add(HelpItem("Components", layoutId = R.layout.help_title_view))
            add(
                HelpItem(
                    "CLOCK",
                    description = "Periodic signaling component that alternates between 0 (LOW) and 1 (HIGH) at a fixed interval. Maximum supported interval is 60Hz.",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.clock_custom,
                    gifView = R.drawable.component_clock
                )
            )
            add(
                HelpItem(
                    "D-LATCH",
                    description = "Level-sensitive  memory component that stores 1 bit of data.",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.d_latch,
                    gifView = R.drawable.component_general_d_latch
                )
            )
            add(
                HelpItem(
                    "D-FLIP-FLOP",
                    description = "Memory component that stores 1 bit of data and can only be updated on clock edge.",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.d_flip_flop,
                    gifView = R.drawable.component_general_d_flip_flop
                )
            )
            add(
                HelpItem(
                    "LED",
                    description = "Output indicator, either on or off.",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.component_led,
                )
            )
            add(
                HelpItem(
                    "POWER OFF & POWER ON",
                    description = "Toggles a signal to HIGH(1). Can also be toggled to OFF in interaction mode. Toggles a signal to HIGH(1). Can also be toggled to ON in interaction mode.",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.power_off,
                    gifView = R.drawable.component_clock
                )
            )
            add(
                HelpItem(
                    "RANDOM",
                    description = "A seeded component that creates a random signal if the incoming signal has changed.",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.random,
                    gifView = R.drawable.component_general_random
                )
            )
            add(
                HelpItem(
                    "TEXT",
                    description = "Display text in the environment.",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.text,
                )
            )
            add(
                HelpItem(
                    "DATA BUS",
                    description = "A group of parallel wires that carry multi-bit data. This is useful also for space optimization and organization.",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.data_bus,
                    gifView = R.drawable.component_general_data_bus
                )
            )
            add(
                HelpItem(
                    "CHANNEL",
                    description = "Carries signals from a source to one or more destinations without wires. A channel has an input with a particular ID and one or multiple outputs.",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.channel,
                    gifView = R.drawable.component_general_channel
                )
            )
            add(
                HelpItem(
                    "SS DISPLAY",
                    description = "Seven segment display screen with a single output.",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.ss_display,
                )
            )
            add(
                HelpItem(
                    "BCD DISPLAY",
                    description = "Binary Coded Decimal values from 0 - F.",
                    layoutId = R.layout.help_item_view,
                    layoutIcon = R.drawable.bcd_display,
                    gifView = R.drawable.component_general_bcd_display
                )
            )
        }

        helpRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(this@HelpActivity, LinearLayoutManager.VERTICAL, false)
            adapter = helpAdapter
        }
    }
}

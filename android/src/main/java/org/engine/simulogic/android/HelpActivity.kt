package org.engine.simulogic.android
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.engine.simulogic.R
import org.engine.simulogic.android.ui.adapters.HelpAdapter
import org.engine.simulogic.android.ui.models.HelpItem

class HelpActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }
        val helpRecyclerView = findViewById<RecyclerView>(R.id.helpList)
        val helpAdapter = HelpAdapter().apply {

            add(HelpItem("Menu Tools", layoutId = R.layout.help_title_view))

            add(HelpItem("Origin", description = "Repositions the camera to the last saved camera coordinates in the x & y plane", layoutId = R.layout.help_item_view, layoutIcon = R.drawable.origin))
            add(HelpItem("Touch", description = "This mode restricts touch events only to a single item on the screen. This aids the user in moving and positioning items more accurately on the environment space.", layoutId = R.layout.help_item_view, layoutIcon = R.drawable.touch))
            add(HelpItem("Interact", description = "Enables Interactions in the environment only for elements with states available.", layoutId = R.layout.help_item_view, layoutIcon = R.drawable.interact))
            add(HelpItem("Sel-Touch", description = "Enables multi-select mode in the environment. The user can toggle items as selected or not selected by the click of a finger.", layoutId = R.layout.help_item_view, layoutIcon = R.drawable.selection))
            add(HelpItem("Sel-Range", description = "Enables multi-select but with a range slider instead of selecting individual items manually.", layoutId = R.layout.help_item_view, layoutIcon = R.drawable.select_rect))
            add(HelpItem("Connect-2", description = "Enables connection mode with an upper limit of 2 joints. These joints can be used for proper wire management in the project.", layoutId = R.layout.help_item_view, layoutIcon = R.drawable.connect_2_node))
            add(HelpItem("Connect-4", description = "Enables connection mode with an upper limit of 4 joints. These joints can be used for proper wire management in the project.", layoutId = R.layout.help_item_view, layoutIcon = R.drawable.connect_4_node))
            add(HelpItem("Connect-6", description = "Enables connection mode with an upper limit of 6 joints. These joints can be used for proper wire management in the project.", layoutId = R.layout.help_item_view, layoutIcon = R.drawable.connect_6_node))
            add(HelpItem("Rotate", description = "Rotates selected components 90-degrees in a clockwise direction.", layoutId = R.layout.help_item_view, layoutIcon = R.drawable.rotate_right))
            add(HelpItem("Group", description = "Groups selected components as one entity.", layoutId = R.layout.help_item_view, layoutIcon = R.drawable.group))
            add(HelpItem("UnGroup", description = "Collapses grouped components as individual entities.", layoutId = R.layout.help_item_view, layoutIcon = R.drawable.ungroup))
            add(HelpItem("Undo", description = "Undo/removes the current operation in order.", layoutId = R.layout.help_item_view, layoutIcon = R.drawable.undo))
            add(HelpItem("Redo", description = "Redo/restores the previous operation in order.", layoutId = R.layout.help_item_view, layoutIcon = R.drawable.redo))
            add(HelpItem("Copy", description = "Duplicates the selected components, this operation can be finalized by clicking the paste button and only connections of selected children or parents will be duplicated.", layoutId = R.layout.help_item_view, layoutIcon = R.drawable.copy))
            add(HelpItem("Cut", description = "Cut items from a certain position.", layoutId = R.layout.help_item_view, layoutIcon = R.drawable.cut))
            add(HelpItem("Paste", description = "This operation finalizes the cut and copy operations", layoutId = R.layout.help_item_view, layoutIcon = R.drawable.paste))
            add(HelpItem("Delete", description = "Deletes components form the environment. Any connection associated with the deleted component will also be deleted.", layoutId = R.layout.help_item_view, layoutIcon = R.drawable.delete))

            add(HelpItem("Components", layoutId = R.layout.help_title_view))
            add(HelpItem("Clock", layoutId = R.layout.help_item_view, layoutIcon = R.drawable.clock_custom))
            add(HelpItem("D-latch", layoutId = R.layout.help_item_view, layoutIcon = R.drawable.d_latch))
        }

        helpRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HelpActivity, LinearLayoutManager.VERTICAL,false)
            adapter = helpAdapter
        }
    }
}

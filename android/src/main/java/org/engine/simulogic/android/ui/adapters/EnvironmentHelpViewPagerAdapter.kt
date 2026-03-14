package org.engine.simulogic.android.ui.adapters

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

class EnvironmentHelpViewPagerAdapter : RecyclerView.Adapter<EnvironmentHelpViewPagerAdapter.HelpViewHolder>() {

    private val data = mutableListOf<HelpItem>()
    init {
        data.add(HelpItem("Touch","This mode restricts touch events only to a single item on the screen." +
            " This aids the user in moving and positioning items more accurately on the environment space.",0,R.drawable.tutorial_touch_components))
        data.add(HelpItem("Interact","Enables interactions in the environment only for elements with states available e.g POWER ON & POWER OFF.",0,R.drawable.tutorial_interact_mode))
        data.add(HelpItem("Sel-Touch","Enable multi-select mode in the environment. The user can toggle items as selected or not selected by the click of a finger.",0,R.drawable.tutorial_sel_touch_mode))
        data.add(HelpItem("Sel-Range","Enables multi-select but with a range slider instead of selecting individual items naturally.",0,R.drawable.tutorial_sel_range_mode))
        data.add(HelpItem("Connect-2","Enables connection mode with an upper limit of 2 joints. " +
            "These joints can be used for proper wire management in the project.",0,R.drawable.tutorial_connect_mode))
    }
    inner class HelpViewHolder(view:View) : RecyclerView.ViewHolder(view) {
        fun init(item:HelpItem){
            itemView.findViewById<GifImageView>(R.id.gif_view).apply {
                setBackgroundResource(item.layoutIcon)
            }
            itemView.findViewById<MaterialTextView>(R.id.title).apply {
                text = item.title
            }
            itemView.findViewById<MaterialTextView>(R.id.description).apply{
                text = item.description
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelpViewHolder {
        return HelpViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.environment_help_tutorial_layout,parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: HelpViewHolder, position: Int) {
         holder.init(data[position])
    }
}

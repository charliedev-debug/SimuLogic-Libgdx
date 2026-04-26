package org.engine.simulogic.android.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import org.engine.simulogic.R
import org.engine.simulogic.android.ui.models.SettingsOption

class SettingsPrefAdapter : RecyclerView.Adapter<SettingsPrefAdapter.PrefViewHolder>() {

    private val data  = mutableListOf<SettingsOption>()

    companion object{
        val HEADER = 0
        val SWITCH_BUTTON = 1
        val BUTTON = 2
    }

    fun insert(option: SettingsOption){
        data.add(option)
    }

    fun insert( viewType:Int,  title: String,  description: String = "",
                drawableLeft: Int = 0,  selected: Boolean = false,listener: OnItemClickListener? = null){
        insert(SettingsOption(viewType,title,description,drawableLeft,selected,listener))
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PrefViewHolder {
        val resource = when(viewType){
            BUTTON -> R.layout.settings_button
            HEADER -> R.layout.settings_item_header
            else -> R.layout.settings_switch_button
        }
        val view =  LayoutInflater.from(parent.context).inflate(resource, parent, false)
        return PrefViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PrefViewHolder,
        position: Int
    ) {
        holder.initView(position)
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].viewType
    }

    override fun getItemCount(): Int {
        return data.size
    }

   inner class PrefViewHolder(view: View) : RecyclerView.ViewHolder(view){

        fun initView(position: Int){
            val model = data[position]
            itemView.setOnClickListener {
                model.listener?.onClick(model)
            }
            if(model.viewType == SWITCH_BUTTON || model.viewType == BUTTON) {
                itemView.findViewById<AppCompatImageView>(R.id.icon)
                    .setImageResource(model.drawableLeft)
            }
            itemView.findViewById<MaterialTextView>(R.id.title).text = model.title
            if(model.viewType == SWITCH_BUTTON) {
                itemView.findViewById<SwitchMaterial>(R.id.active).isChecked = model.selected
            }
        }
    }

    interface OnItemClickListener{
        fun onClick(item: SettingsOption)
    }
}

package org.engine.simulogic.android.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.engine.simulogic.R
import org.engine.simulogic.android.views.interfaces.IComponentAdapterListener


class ComponentViewAdapter : RecyclerView.Adapter<ComponentViewAdapter.ComponentViewHolder>() {

    private val itemList = mutableListOf<ComponentItem>()
    private val listeners:MutableList<IComponentAdapterListener> = mutableListOf()
    inner class ComponentViewHolder( item: View): RecyclerView.ViewHolder(item){
        fun initView(component: ComponentItem){
            itemView.findViewById<ImageView>(R.id.component_icon).setImageResource(component.res)
            itemView.findViewById<TextView>(R.id.component_header).text = component.title
            itemView.setOnClickListener {
                listeners.forEach {it.onClickComponent(component) }
            }
        }
    }


    fun insert(title:String, res:Int){
        itemList.add(ComponentItem(title, res))
    }

    fun addListener(listener: IComponentAdapterListener){
        listeners.add(listener)
    }

    fun removeListener(listener: IComponentAdapterListener){
        listeners.remove(listener)
    }

    fun clearListeners(){
        listeners.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComponentViewHolder {
        val  view = LayoutInflater.from(parent.context).inflate(R.layout.bottom_sheet_item_component,parent,false)
        return ComponentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ComponentViewHolder, position: Int) {
        holder.initView(itemList[position])
    }
}

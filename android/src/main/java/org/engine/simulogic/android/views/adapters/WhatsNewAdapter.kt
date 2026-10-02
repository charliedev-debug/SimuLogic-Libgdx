package org.engine.simulogic.android.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.engine.simulogic.R

class WhatsNewAdapter: RecyclerView.Adapter<WhatsNewAdapter.ComponentViewHolder>() {

    private val itemList = mutableListOf<WhatsNewItem>()

    class ComponentViewHolder( item: View): RecyclerView.ViewHolder(item){
        fun initView(whatsNewItem: WhatsNewItem){
            if(whatsNewItem.isHeader) {
                itemView.findViewById<ImageView>(R.id.res)
                    .setImageResource(whatsNewItem.res)
                itemView.findViewById<TextView>(R.id.title).text = whatsNewItem.title
            }else{
                itemView.findViewById<TextView>(R.id.message).text = whatsNewItem.title
            }
        }
    }

    fun insert(title:String, res:Int,isHeader: Boolean = false){
        itemList.add(WhatsNewItem(title, res,isHeader))
    }

    override fun getItemViewType(position: Int): Int {
        return if(itemList[position].isHeader) 0 else 1
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ComponentViewHolder {
        val  view = if(viewType == 0) {
            LayoutInflater.from(parent.context).inflate(R.layout.whats_new_item_title,
                parent,false)
        }else{
            LayoutInflater.from(parent.context).inflate(R.layout.whats_new_item_message,
                parent,false)
        }
        return ComponentViewHolder(view)
    }


    override fun onBindViewHolder(
        holder: ComponentViewHolder,
        position: Int
    ) {
        holder.initView(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}

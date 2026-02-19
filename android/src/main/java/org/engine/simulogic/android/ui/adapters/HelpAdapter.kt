package org.engine.simulogic.android.ui.adapters
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.engine.simulogic.android.ui.models.HelpItem
import org.engine.simulogic.R
class HelpAdapter : RecyclerView.Adapter<HelpAdapter.HelpViewHolder>() {
 private val dataList = mutableListOf<HelpItem>()
    inner class HelpViewHolder(view: View): RecyclerView.ViewHolder(view){
          fun initView(position:Int){
              dataList[position].also { item->
                  if(item.layoutId == R.layout.help_item_view){
                      itemView.findViewById<ImageView>(R.id.icon).also { imageView ->
                          imageView.setImageResource(item.layoutIcon)
                      }
                      itemView.findViewById<TextView>(R.id.header).text = item.title
                      itemView.findViewById<TextView>(R.id.title).text = item.title
                      itemView.findViewById<TextView>(R.id.description).text = item.description
                  }else{
                      itemView.findViewById<TextView>(R.id.title).text = item.title
                  }
              }
          }
    }

    fun add(item: HelpItem){
        dataList.add(item)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelpViewHolder {
          val view =  LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return HelpViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return dataList[position].layoutId
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: HelpViewHolder, position: Int) {
          holder.initView(position)
    }
}

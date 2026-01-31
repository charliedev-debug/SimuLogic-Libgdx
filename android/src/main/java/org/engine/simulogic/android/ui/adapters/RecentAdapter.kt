package org.engine.simulogic.android.ui.adapters

import android.net.Uri
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import org.engine.simulogic.R
import org.engine.simulogic.android.ui.models.RecentItem
import org.engine.simulogic.android.utilities.ShareFileHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class RecentAdapter : RecyclerView.Adapter<RecentAdapter.RecentViewHolder>() {

    private val dataList = mutableListOf<RecentItem>()
    private val listeners = mutableListOf<OnItemClickListener>()
    private val date = Date()
    inner class RecentViewHolder(view: View) : RecyclerView.ViewHolder(view){
        fun initView(position:Int){
            val item = dataList[position]
            when(item.type){
                RecentItem.VIEW_ITEM->{
                    val extensionStart = item.title.indexOf(".")
                    val title = if(extensionStart != -1) item.title.substring(0, extensionStart) else item.title
                    itemView.findViewById<TextView>(R.id.project_title).text = title
                    itemView.findViewById<TextView>(R.id.project_description).text = item.description
                    itemView.findViewById<TextView>(R.id.date_created).text = SimpleDateFormat.getInstance().format(date.apply { time = File(item.path).lastModified() })
                    itemView.findViewById<ImageView>(R.id.delete).apply {
                        visibility = if(item.enableDelete) View.VISIBLE else View.INVISIBLE
                        setOnClickListener {
                            listeners.forEach { it.onDelete(item,position) }
                        }
                    }
                    itemView.setOnClickListener {
                        listeners.forEach {
                            it.onClick(item)
                        }
                    }
                    itemView.findViewById<ImageView>(R.id.share).setOnClickListener {
                        ShareFileHelper.share(File(item.path), item.title, itemView.context)
                    }
                }
                RecentItem.VIEW_HEADER->{
                    itemView.findViewById<TextView>(R.id.title).text = item.title
                    itemView.findViewById<ImageView>(R.id.premium).apply {
                        if(item.ispremium){
                            visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    fun addListener(listener:OnItemClickListener){
        listeners.add(listener)
    }

    fun add(title:String, path:String,description:String,lastModified:Long, type:Int = RecentItem.VIEW_ITEM,ispremium:Boolean = false, canDelete:Boolean = false){
        dataList.add(RecentItem(title, path,description,lastModified, type,ispremium,canDelete))
    }

    override fun getItemViewType(position: Int): Int {
        return dataList[position].type
    }

    fun remove(position: Int){
        dataList.removeAt(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {

        val view:View = when(viewType){
            RecentItem.VIEW_ITEM-> LayoutInflater.from(parent.context).inflate(R.layout.recent_project_layout,parent,false)


            RecentItem.VIEW_HEADER-> LayoutInflater.from(parent.context).inflate(R.layout.recent_view_type_item,parent,false)

            else -> { LayoutInflater.from(parent.context).inflate(R.layout.recent_project_layout,parent,false) }
        }

        return RecentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        holder.initView(position)
    }

    interface OnItemClickListener{
        fun onClick(item:RecentItem)
        fun onDelete(item:RecentItem, index:Int)
    }
}

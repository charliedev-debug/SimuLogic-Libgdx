package org.engine.simulogic.android.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.engine.simulogic.R
import org.engine.simulogic.android.ui.models.ProjectOption

class ProjectOptionsAdapter: RecyclerView.Adapter<ProjectOptionsAdapter.ProjectOptionsViewHolder>() {

    private val optionsList = mutableListOf<ProjectOption>()
    var listener:OnOptionClickListener? = null
    inner class ProjectOptionsViewHolder(view:View) : RecyclerView.ViewHolder(view){

        fun initView(position: Int) {
            val option = optionsList[position]
            itemView.findViewById<ImageView>(R.id.icon).apply {
                setImageResource(option.img)
            }
            itemView.findViewById<TextView>(R.id.title).apply {
                text = option.title
            }

            itemView.setOnClickListener {
                listener?.onClick(option)
            }
        }
    }

    fun add(title:String,img:Int){
        optionsList.add(ProjectOption(title, img))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectOptionsViewHolder {
        val  view = LayoutInflater.from(parent.context).inflate(R.layout.home_project_operation_layout,parent,false)
        return ProjectOptionsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return optionsList.size
    }

    override fun onBindViewHolder(holder: ProjectOptionsViewHolder, position: Int) {
        holder.initView(position)
    }

    interface OnOptionClickListener{
        fun onClick(option:ProjectOption)
    }
}

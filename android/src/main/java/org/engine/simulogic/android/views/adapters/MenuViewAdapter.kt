package org.engine.simulogic.android.views.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.engine.simulogic.R
import org.engine.simulogic.android.views.interfaces.IMenuAdapterListener

class MenuViewAdapter:RecyclerView.Adapter<MenuViewAdapter.MenuViewHolder>() {
    private val dataList = mutableListOf<MenuItem>()
    var listener:IMenuAdapterListener? = null
    var selectedMode = 0
    inner class MenuViewHolder(item:View):RecyclerView.ViewHolder(item){

        fun initView(position: Int){
            val item = dataList[position]
            itemView.findViewById<ImageView>(R.id.icon).setImageResource(item.res)
            itemView.findViewById<TextView>(R.id.title).text = item.title
            val selected = itemView.findViewById<View>(R.id.selected)
            if(item.isMode){
                selected.visibility = if(selectedMode == position) View.VISIBLE else View.INVISIBLE
            }else{
                selected.visibility = View.INVISIBLE
            }
            itemView.setOnClickListener {
                listener?.onClickListener(item)
                if(item.isMode){
                    val previous = selectedMode
                    selectedMode = position
                    notifyItemChanged(previous)
                    notifyItemChanged(position)
                }
            }
        }
    }

    fun insert(item: MenuItem){
        dataList.add(item)
    }
    fun insert(title:String,isMode:Boolean,res:Int){
        insert(MenuItem(title, isMode, res))
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val  view = LayoutInflater.from(parent.context).inflate(R.layout.menu_item_simulation,parent,false)
        return MenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.initView(position)
    }
}

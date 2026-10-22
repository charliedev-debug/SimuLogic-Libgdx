package org.engine.simulogic.android.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.badlogic.gdx.graphics.Color
import com.google.android.material.textview.MaterialTextView
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import org.engine.simulogic.R
import org.engine.simulogic.android.views.adapters.MenuViewAdapter.MenuViewHolder

class MenuColorPickerAdapter(private val listener: OnColorSelectedListener): RecyclerView.Adapter<MenuColorPickerAdapter.MenuColorViewHolder>() {


    private val dataList = mutableListOf<ColorPickerItem>()
    var selectedItem = 0
    inner class MenuColorViewHolder(item: View):RecyclerView.ViewHolder(item){
        fun initView(position: Int){
            itemView.setOnClickListener {
                selectedItem = position
                listener.onSelected(dataList[position])
            }
            itemView.findViewById<AppCompatImageView>(R.id.delete).setOnClickListener {
                listener.onDelete(dataList[position])
                remove(position)
            }
            itemView.findViewById<MaterialTextView>(R.id.title).also{titleTextView->
                titleTextView.text = dataList[position].title
            }
            itemView.findViewById<View>(R.id.pallete).also { palleteView->
                val color = dataList[position].color
                palleteView.setBackgroundColor(android.graphics.Color.argb((color.a * 255f).toInt(),(color.r * 255f).toInt(),
                    (color.g * 255f).toInt(),(color.b * 255f).toInt()))

            }.setOnClickListener {
                ColorPickerDialog.Builder(itemView.context).also { colorPicker->
                    val item = dataList[position]
                    val color = dataList[position].color
                    colorPicker.colorPickerView.setInitialColor(android.graphics.Color.argb((color.a * 255f).toInt(),(color.r * 255f).toInt(),
                        (color.g * 255f).toInt(),(color.b * 255f).toInt()))
                    colorPicker.setTitle(item.title)
                    colorPicker.setPositiveButton("Accept", object: ColorEnvelopeListener {
                        override fun onColorSelected(
                            colorEvelope: ColorEnvelope,
                            fromUser: Boolean
                        ) {
                            val argb = colorEvelope.argb
                            item.color = Color( argb[1] / 255f, argb[2] / 255f, argb[3] / 255f,argb[0] / 255f)
                            notifyItemChanged(position)
                            // update since we modified it
                            if(selectedItem == position){
                                listener.onSelected(item)
                            }
                        }
                    })
                    colorPicker.setNegativeButton("Cancel"
                    ) { dialogInterface, i -> dialogInterface.dismiss() }
                    colorPicker.attachAlphaSlideBar(true)
                    colorPicker.attachBrightnessSlideBar(true)
                    colorPicker.setBottomSpace(12)
                    colorPicker.show()
                }
            }
        }
    }

    fun isEmpty(): Boolean{
        return dataList.isEmpty()
    }

    operator fun get(index: Int): ColorPickerItem{
        return dataList[index]
    }

    fun insert(title:String,color: Color){
        dataList.add(ColorPickerItem(title,color))
    }

    fun remove(position: Int){
        notifyItemRemoved(position)
        dataList.removeAt(position)
        notifyItemRangeChanged(0,itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuColorViewHolder {
        val  view = LayoutInflater.from(parent.context).inflate(R.layout.custom_color_picker_pallete_layout,parent,false)
        return MenuColorViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MenuColorViewHolder,
        position: Int
    ) {
        holder.initView(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
    interface OnColorSelectedListener{
        fun onSelected(item: ColorPickerItem)
        fun onDelete(item: ColorPickerItem)
    }
}

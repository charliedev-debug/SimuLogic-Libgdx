package org.engine.simulogic.android.views.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badlogic.gdx.graphics.Color
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import org.engine.simulogic.R
import org.engine.simulogic.android.circuits.components.other.Pallete
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.views.adapters.ColorPickerItem
import org.engine.simulogic.android.views.adapters.MenuColorPickerAdapter

class CustomColorPickerDialog(private val connection: Connection,context: Context): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_color_picker_layout)
        val apply = findViewById<MaterialButton>(R.id.apply)
        val cancel = findViewById<MaterialButton>(R.id.cancel)
        val selectedPalleteTitle = findViewById<MaterialTextView>(R.id.selectedPalleteTitle)
        val selectedPallete = findViewById<View>(R.id.selectedPallete)
        val addPallete = findViewById<MaterialButton>(R.id.addPallete)
        val recyclerViewColor = findViewById<RecyclerView>(R.id.colorPalleteList)
        val deleteBuffer = mutableListOf<ColorPickerItem>()
        val colorPickerAdapter = MenuColorPickerAdapter(object : MenuColorPickerAdapter.OnColorSelectedListener{
            override fun onSelected(item: ColorPickerItem) {
                val color = item.color
                selectedPalleteTitle.text = item.title
                selectedPallete.setBackgroundColor(android.graphics.Color.argb((color.a * 255f).toInt(),(color.r * 255f).toInt(),
                    (color.g * 255f).toInt(),(color.b * 255f).toInt()))
            }

            override fun onDelete(item: ColorPickerItem) {
                deleteBuffer.add(item)
            }
        })

        connection.colorPalleteList.onEach {
            colorPickerAdapter.insert(it.key,it.value.color)
        }

        if(colorPickerAdapter.isEmpty()){
           recyclerViewColor.visibility = View.GONE
        }

        recyclerViewColor.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewColor.adapter = colorPickerAdapter

        addPallete.setOnClickListener {
            PalleteTitleDialog(context) { title->
                ColorPickerDialog.Builder(context).also { colorPicker->
                    colorPicker.setTitle(title)
                    colorPicker.setPositiveButton("Accept", object: ColorEnvelopeListener {
                        override fun onColorSelected(
                            colorEvelope: ColorEnvelope,
                            fromUser: Boolean
                        ) {
                            val argb = colorEvelope.argb
                            val color = Color( argb[1] / 255f, argb[2] / 255f, argb[3] / 255f,argb[0] / 255f)
                            colorPickerAdapter.insert(title, color)
                            colorPickerAdapter.notifyItemRangeChanged(0,colorPickerAdapter.itemCount)
                            recyclerViewColor.visibility = View.VISIBLE
                        }
                    })
                    colorPicker.setNegativeButton("Cancel"
                    ) { dialogInterface, i -> dialogInterface.dismiss() }
                    colorPicker.attachAlphaSlideBar(true)
                    colorPicker.attachBrightnessSlideBar(true)
                    colorPicker.setBottomSpace(12)
                    colorPicker.show()
                }
            }.show()
        }
        apply.setOnClickListener {
            for(index in 0 until colorPickerAdapter.itemCount){
                colorPickerAdapter[index].also { item ->
                    connection.colorPalleteList[item.title] = Pallete(item.title,item.color)
                }
            }

            deleteBuffer.onEach {
                connection.colorPalleteList.remove(it.title)
            }
            dismiss()
        }

        cancel.setOnClickListener {
            dismiss()
        }
    }
    override fun onStart() {
        super.onStart()
        val width: Int = context.resources.getDimensionPixelSize(R.dimen.popup_width)
        window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawableResource(R.color.transparent)
    }
}

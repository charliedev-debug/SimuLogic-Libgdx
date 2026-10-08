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
import org.engine.simulogic.android.views.adapters.ColorPickerItem
import org.engine.simulogic.android.views.adapters.MenuColorPickerAdapter

class CustomColorPickerDialog(context: Context): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_color_picker_layout)
        val apply = findViewById<MaterialButton>(R.id.apply)
        val cancel = findViewById<MaterialButton>(R.id.cancel)
        val selectedPalleteTitle = findViewById<MaterialTextView>(R.id.selectedPalleteTitle)
        val selectedPallete = findViewById<View>(R.id.selectedPallete)
        val addPallete = findViewById<MaterialButton>(R.id.addPallete)
        val recyclerViewColor = findViewById<RecyclerView>(R.id.colorPalleteList)
        val colorPickerAdapter = MenuColorPickerAdapter(object : MenuColorPickerAdapter.OnColorSelectedListener{
            override fun onSelected(item: ColorPickerItem) {
                val color = item.color
                selectedPalleteTitle.text = item.title
                selectedPallete.setBackgroundColor(android.graphics.Color.argb((color.a * 255f).toInt(),(color.r * 255f).toInt(),
                    (color.g * 255f).toInt(),(color.b * 255f).toInt()))
            }
        })
        colorPickerAdapter.insert("Pallete",Color.WHITE)
        colorPickerAdapter.insert("Pallete",Color.WHITE)
        colorPickerAdapter.insert("Pallete",Color.WHITE)
        colorPickerAdapter.insert("Pallete",Color.WHITE)
        colorPickerAdapter.insert("Pallete",Color.WHITE)
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
                            colorPickerAdapter.insert(title, Color( argb[1] / 255f, argb[2] / 255f, argb[3] / 255f,argb[0] / 255f))
                            colorPickerAdapter.notifyItemRangeChanged(0,colorPickerAdapter.itemCount)
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

package org.engine.simulogic.android.views.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badlogic.gdx.graphics.Color
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import org.engine.simulogic.R
import org.engine.simulogic.android.circuits.components.CDefaults.Companion.LINE_MARKER_INACTIVE
import org.engine.simulogic.android.circuits.components.CDefaults.Companion.SIGNAL_ACTIVE_COLOR
import org.engine.simulogic.android.circuits.components.other.Pallete
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.views.adapters.ColorPickerItem
import org.engine.simulogic.android.views.adapters.MenuColorPickerAdapter

class CustomColorPickerDialog(private val connection: Connection, private val isLineMarkerSelected: Boolean, context: Context, private val listener: ColorPickerDialogListener): Dialog(context) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_color_picker_layout)
        val apply = findViewById<MaterialButton>(R.id.apply)
        val cancel = findViewById<MaterialButton>(R.id.cancel)
        val selectedPalleteSignalsView = findViewById<View>(R.id.selectedPalleteSignalsView)
        val selectedPalleteSignalOnText = findViewById<MaterialTextView>(R.id.selectedPalleteSignalOnText)
        val selectedPalleteSignalOn = findViewById<View>(R.id.selectedPalleteSignalOn)
        val selectedPalleteSignalOffText = findViewById<MaterialTextView>(R.id.selectedPalleteSignalOffText)
        val selectedPalleteSignalOff = findViewById<View>(R.id.selectedPalleteSignalOff)
        val addPallete = findViewById<MaterialButton>(R.id.addPallete)
        val recyclerViewColor = findViewById<RecyclerView>(R.id.colorPalleteList)
        val signalOn = findViewById<AppCompatCheckBox>(R.id.signalOn)
        val signalOff = findViewById<AppCompatCheckBox>(R.id.signalOff)
        val signalOnDefault = findViewById<AppCompatButton>(R.id.signalOnDefault)
        val signalOffDefault = findViewById<AppCompatButton>(R.id.signalOffDefault)
        val deleteBuffer = mutableListOf<ColorPickerItem>()
        val signalOnColorDefault =  android.graphics.Color.argb(
            (SIGNAL_ACTIVE_COLOR.a * 255f).toInt(), (SIGNAL_ACTIVE_COLOR.r * 255f).toInt(),
            (SIGNAL_ACTIVE_COLOR.g * 255f).toInt(), (SIGNAL_ACTIVE_COLOR.b * 255f).toInt()
        )
        val signalOffColorDefault = android.graphics.Color.argb(
            (LINE_MARKER_INACTIVE.a * 255f).toInt(), (LINE_MARKER_INACTIVE.r * 255f).toInt(),
            (LINE_MARKER_INACTIVE.g * 255f).toInt(), (LINE_MARKER_INACTIVE.b * 255f).toInt()
        )
        val colorPickerAdapter = MenuColorPickerAdapter(object : MenuColorPickerAdapter.OnColorSelectedListener{
            override fun onSelected(item: ColorPickerItem) {
                val color = item.color
                if(signalOn.isChecked) {
                    selectedPalleteSignalOnText.text = item.title
                    selectedPalleteSignalOn.setBackgroundColor(
                        android.graphics.Color.argb(
                            (color.a * 255f).toInt(), (color.r * 255f).toInt(),
                            (color.g * 255f).toInt(), (color.b * 255f).toInt()
                        )
                    )
                }else{
                    selectedPalleteSignalOffText.text = item.title
                    selectedPalleteSignalOff.setBackgroundColor(
                        android.graphics.Color.argb(
                            (color.a * 255f).toInt(), (color.r * 255f).toInt(),
                            (color.g * 255f).toInt(), (color.b * 255f).toInt()
                        )
                    )
                }
            }

            override fun onDelete(item: ColorPickerItem) {
                deleteBuffer.add(item)
            }
        })
        selectedPalleteSignalOn.setBackgroundColor(signalOnColorDefault)
        selectedPalleteSignalOff.setBackgroundColor(signalOffColorDefault)

        selectedPalleteSignalsView.visibility = if(isLineMarkerSelected) View.VISIBLE else View.GONE
        connection.colorPalleteList.onEach {
            colorPickerAdapter.insert(it.key,it.value.color)
        }

        if(colorPickerAdapter.isEmpty()){
           recyclerViewColor.visibility = View.GONE
        }

        recyclerViewColor.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewColor.adapter = colorPickerAdapter

        signalOn.setOnClickListener {
            signalOff.isChecked = !signalOn.isChecked
        }

        signalOff.setOnClickListener {
            signalOn.isChecked = !signalOff.isChecked
        }

        signalOnDefault.setOnClickListener {
            selectedPalleteSignalOn.setBackgroundColor(signalOnColorDefault)
            selectedPalleteSignalOnText.text = "default"
        }

        signalOffDefault.setOnClickListener {
            selectedPalleteSignalOff.setBackgroundColor(signalOffColorDefault)
            selectedPalleteSignalOffText.text = "default"
        }

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
                    if(connection.colorPalleteList.contains(item.title)){
                        connection.colorPalleteList[item.title]!!.also { pallete->
                           pallete.color = item.color
                        }
                    }else {
                        connection.colorPalleteList[item.title] = Pallete(item.title, item.color)
                    }
                }
            }

            deleteBuffer.onEach {
                connection.colorPalleteList[it.title]?.also {pallete ->
                    pallete.isActive = false
                }
                connection.colorPalleteList.remove(it.title)

            }
            val signalOffPallete = connection.colorPalleteList[selectedPalleteSignalOffText.text]
            val signalOnPallete = connection.colorPalleteList[selectedPalleteSignalOnText.text]
            listener.apply(signalOnPallete,signalOffPallete)
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

    interface ColorPickerDialogListener{
        fun apply(colorPalleteOn: Pallete?, colorPalleteOff: Pallete?)
    }
}

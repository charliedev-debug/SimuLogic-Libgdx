package org.engine.simulogic.android.views.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import org.engine.simulogic.R
import org.engine.simulogic.android.views.interfaces.IDialogLabelListener


class LabelDialog(context: Context, private val listener:IDialogLabelListener) : Dialog(context) {


    class CustomFontAdapter(context: Context,items:Array<String>):ArrayAdapter<String>(context, 0, items){

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
           val view =
               convertView ?: LayoutInflater.from(context).inflate(R.layout.font_popup_list_item, parent, false)
            val currentItem = getItem(position)
            view.findViewById<MaterialTextView>(R.id.label).text = currentItem

            return view
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = layoutInflater.inflate(R.layout.dialog_label_name,null)
        val cancel = view.findViewById<AppCompatButton>(R.id.cancel)
        val save = view.findViewById<AppCompatButton>(R.id.save)
        val editTextFileName = view.findViewById<TextInputEditText>(R.id.fileName)
        val fontDropDownEditText = view.findViewById<TextInputEditText>(R.id.fontDropDown)
        val fontPopupButton = view.findViewById<AppCompatImageButton>(R.id.showPopup)
        val fontSizeDefaults = arrayOf("SMALL - 25px", "MEDIUM - 45px", "LARGE - 60px", "LARGER - 75px", "CUSTOM")
        val fontListPopupWindow = ListPopupWindow(context).apply { setBackgroundDrawable(ResourcesCompat.getDrawable(context.resources,R.drawable.popup_bg, null)) }
        val fontAdapter =  CustomFontAdapter(context,fontSizeDefaults)//ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, fontSizeDefaults)
        fontListPopupWindow.setAdapter(fontAdapter)
        fontListPopupWindow.anchorView = fontDropDownEditText
        fontDropDownEditText.isEnabled = false
        fontDropDownEditText.setText("25")

        fontListPopupWindow.setOnItemClickListener { parent, view, position, id ->
            fontSizeDefaults[position].also { value->
                fontDropDownEditText.isEnabled = false
                when(value){
                    "SMALL - 25px" ->{
                        fontDropDownEditText.setText("25")
                    }
                    "MEDIUM - 45px" ->{
                        fontDropDownEditText.setText("45")
                    }
                    "LARGE - 60px" ->{
                        fontDropDownEditText.setText("60")
                    }
                    "LARGER - 75px" ->{
                        fontDropDownEditText.setText("75")
                    }
                    "CUSTOM"->{
                        fontDropDownEditText.isEnabled = true
                        fontDropDownEditText.requestFocus()
                    }
                }
            }
            fontListPopupWindow.dismiss()
        }
        cancel.setOnClickListener {
            dismiss()
            listener.onCancelled()
        }

        fontPopupButton.setOnClickListener {
            fontListPopupWindow.show()
        }
        save.setOnClickListener {
            dismiss()
            listener.onCompleted(editTextFileName.text.toString(),fontDropDownEditText.text.toString().toInt())
        }

        this.setContentView(view)
    }

    override fun onStart() {
        super.onStart()
        val width: Int = context.resources.getDimensionPixelSize(R.dimen.popup_width)
        window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawableResource(R.color.transparent)
        //  window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}

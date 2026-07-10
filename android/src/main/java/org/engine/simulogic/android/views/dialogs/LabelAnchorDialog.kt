package org.engine.simulogic.android.views.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import org.engine.simulogic.R
import org.engine.simulogic.android.circuits.components.other.CAnchor
import org.engine.simulogic.android.views.interfaces.IDialogLabelAnchorListener
import org.engine.simulogic.android.views.interfaces.IDialogLabelListener


class LabelAnchorDialog(context: Context, private val listener: IDialogLabelAnchorListener) : Dialog(context) {


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
        val view = layoutInflater.inflate(R.layout.dialog_anchor_label,null)
        val cancel = view.findViewById<AppCompatButton>(R.id.cancel)
        val save = view.findViewById<AppCompatButton>(R.id.save)
        val editTextFileName = view.findViewById<TextInputEditText>(R.id.fileName)
        val alignmentLeftRightGroup = view.findViewById<RadioGroup>(R.id.alignmentLeftRight)
        val alignmentTopBottomGroup = view.findViewById<RadioGroup>(R.id.alignmentTopBottom)
        val alignmentLeftRightCenterButton = view.findViewById<RadioButton>(R.id.align_lr_center)
        val alignmentTopBottomCenterButton = view.findViewById<RadioButton>(R.id.align_tb_center)
        var alignmentValue = CAnchor.ALIGN_TOP_LEFT
        val fontDropDownEditText = view.findViewById<TextInputEditText>(R.id.fontDropDown)
        val fontPopupButton = view.findViewById<AppCompatImageButton>(R.id.showPopup)
        val fontSizeDefaults = arrayOf("SMALL - 25px", "MEDIUM - 45px", "LARGE - 60px", "LARGER - 75px", "CUSTOM")
        val fontListPopupWindow = ListPopupWindow(context).apply { setBackgroundDrawable(ResourcesCompat.getDrawable(context.resources,R.drawable.popup_bg, null)) }
        val fontAdapter =  CustomFontAdapter(context,fontSizeDefaults)//ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, fontSizeDefaults)
        fontListPopupWindow.setAdapter(fontAdapter)
        fontListPopupWindow.anchorView = fontDropDownEditText
        fontDropDownEditText.isEnabled = false
        fontDropDownEditText.setText(buildString { append("25") })

        fontListPopupWindow.setOnItemClickListener { parent, view, position, id ->
            fontSizeDefaults[position].also { value->
                fontDropDownEditText.isEnabled = false
                when(value){
                    "SMALL - 25px" ->{
                        fontDropDownEditText.setText(buildString { append("25") })
                    }
                    "MEDIUM - 45px" ->{
                        fontDropDownEditText.setText(buildString { append("45") })
                    }
                    "LARGE - 60px" ->{
                        fontDropDownEditText.setText(buildString { append("60") })
                    }
                    "LARGER - 75px" ->{
                        fontDropDownEditText.setText(buildString { append("75") })
                    }
                    "CUSTOM"->{
                        fontDropDownEditText.isEnabled = true
                        fontDropDownEditText.requestFocus()
                    }
                }
            }
            fontListPopupWindow.dismiss()
        }

        alignmentLeftRightGroup.setOnCheckedChangeListener { group, id ->
             when(id){
                 R.id.align_left->{
                    alignmentLeftRightCenterButton.visibility = View.VISIBLE
                     alignmentTopBottomCenterButton.visibility = View.VISIBLE
                     when(alignmentTopBottomGroup.checkedRadioButtonId){
                         R.id.align_top->{
                             alignmentValue = CAnchor.ALIGN_TOP_LEFT
                         }
                         R.id.align_bottom->{
                             alignmentValue = CAnchor.ALIGN_BOTTOM_LEFT
                         }
                         R.id.align_tb_center->{
                             alignmentValue = CAnchor.ALIGN_LEFT_CENTER
                         }
                     }
                 }
                 R.id.align_right ->{
                     alignmentLeftRightCenterButton.visibility = View.VISIBLE
                     alignmentTopBottomCenterButton.visibility = View.VISIBLE
                     when(alignmentTopBottomGroup.checkedRadioButtonId){
                         R.id.align_top->{
                             alignmentValue = CAnchor.ALIGN_TOP_RIGHT
                         }
                         R.id.align_bottom->{
                             alignmentValue = CAnchor.ALIGN_BOTTOM_RIGHT
                         }
                         R.id.align_tb_center->{
                             alignmentValue = CAnchor.ALIGN_RIGHT_CENTER
                         }
                     }
                 }
                 R.id.align_lr_center->{
                     alignmentTopBottomCenterButton.visibility = View.GONE
                     when(alignmentTopBottomGroup.checkedRadioButtonId){
                         R.id.align_top->{
                             alignmentValue = CAnchor.ALIGN_TOP_CENTER
                         }
                         R.id.align_bottom->{
                             alignmentValue = CAnchor.ALIGN_BOTTOM_CENTER
                         }
                     }
                 }
             }
        }

        alignmentTopBottomGroup.setOnCheckedChangeListener { group, id ->
            when(id){
                R.id.align_top->{
                    alignmentTopBottomCenterButton.visibility = View.VISIBLE
                    alignmentLeftRightCenterButton.visibility = View.VISIBLE
                    when(alignmentLeftRightGroup.checkedRadioButtonId){
                        R.id.align_left->{
                            alignmentValue = CAnchor.ALIGN_TOP_LEFT
                        }
                        R.id.align_right ->{
                            alignmentValue = CAnchor.ALIGN_TOP_RIGHT
                        }
                        R.id.align_tb_center->{
                            alignmentValue = CAnchor.ALIGN_TOP_CENTER
                        }
                    }
                }
                R.id.align_bottom->{
                    alignmentTopBottomCenterButton.visibility = View.VISIBLE
                    alignmentLeftRightCenterButton.visibility = View.VISIBLE
                    when(alignmentLeftRightGroup.checkedRadioButtonId){
                        R.id.align_left->{
                            alignmentValue = CAnchor.ALIGN_BOTTOM_LEFT
                        }
                        R.id.align_right ->{
                            alignmentValue = CAnchor.ALIGN_BOTTOM_RIGHT
                        }
                        R.id.align_tb_center->{
                            alignmentValue = CAnchor.ALIGN_BOTTOM_CENTER
                        }
                    }
                }
                R.id.align_tb_center->{
                    alignmentLeftRightCenterButton.visibility = View.GONE
                    when(alignmentLeftRightGroup.checkedRadioButtonId){
                        R.id.align_left->{
                            alignmentValue = CAnchor.ALIGN_LEFT_CENTER
                        }
                        R.id.align_right ->{
                            alignmentValue = CAnchor.ALIGN_RIGHT_CENTER
                        }
                    }
                }
            }
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
            listener.onCompleted(editTextFileName.text.toString(),fontDropDownEditText.text.toString().toInt(),alignmentValue)
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

package org.engine.simulogic.android.views.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import org.engine.simulogic.R

class AlertDialog (context: Context, private val message:String, private val listener:OnAlertListener) : Dialog(context) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = layoutInflater.inflate(R.layout.alert_yes_no_dialog_layout,null)
        val no = view.findViewById<AppCompatButton>(R.id.no)
        val yes = view.findViewById<AppCompatButton>(R.id.yes)
         view.findViewById<TextView>(R.id.message).apply { text = message }
        yes.setOnClickListener {
            listener.accept()
            dismiss()
        }

        no.setOnClickListener {
            listener.cancel()
            dismiss()
        }
        this.setContentView(view)
        this.setCancelable(false)
    }

    override fun onStart() {
        super.onStart()
        val width: Int = context.resources.getDimensionPixelSize(R.dimen.popup_width)
        window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawableResource(R.color.transparent)
    }


    interface OnAlertListener{
        fun accept()
        fun cancel()
    }
}

package org.engine.simulogic.android.views.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import org.engine.simulogic.R

class SuccessDialog(context: Context, private val message:String) : Dialog(context) {


    var listener:OnCloseDialogListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = layoutInflater.inflate(R.layout.success_dialog_layout,null)
        val close = view.findViewById<AppCompatButton>(R.id.close)
        val messageTextView = view.findViewById<TextView>(R.id.message)
        messageTextView.text = message
        close.setOnClickListener {
            dismiss()
            listener?.onClick()
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

    interface OnCloseDialogListener{
        fun onClick()
    }
}



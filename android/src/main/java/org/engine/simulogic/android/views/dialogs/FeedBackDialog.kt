package org.engine.simulogic.android.views.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.engine.simulogic.R

class FeedBackDialog(context: Context,private val listener: FeedBackListener) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feedback_dialog)
        val cancel = findViewById<MaterialButton>(R.id.cancel)
        val send = findViewById<MaterialButton>(R.id.send)
        val feedBackView = findViewById<TextInputEditText>(R.id.feedback)
        send.setOnClickListener {
            listener.onSend(feedBackView.text.toString())
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

    fun interface FeedBackListener{
        fun onSend(feedBack:String)
    }
}




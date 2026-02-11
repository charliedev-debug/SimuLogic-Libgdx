package org.engine.simulogic.android.views.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import org.engine.simulogic.R
import org.engine.simulogic.android.circuits.components.wireless.ChannelBuffer

class ChannelDialog (context: Context, private val listener:OnChannelListener) : Dialog(context) {

    private var type = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = layoutInflater.inflate(R.layout.channel_dialog_layout,null)
        val cancel = view.findViewById<AppCompatButton>(R.id.cancel)
        val accept = view.findViewById<AppCompatButton>(R.id.accept)
        val channelId = view.findViewById<TextInputEditText>(R.id.id)
        val channelType = view.findViewById<RadioGroup>(R.id.type)

        channelType.setOnCheckedChangeListener { _, id ->
            type = if(R.id.type_input == id) 0 else 1
        }
        accept.setOnClickListener {
            if(!ChannelBuffer.isAvailable(channelId.text.toString())) {
                listener.success(channelId.text.toString(), 0)
                dismiss()
            }else{
                channelId.error = "The input channel already exists!"
            }
        }

        cancel.setOnClickListener {
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


    interface OnChannelListener{
        fun success(id:String, type:Int)
        fun failure(msg:String)
        fun cancel()
    }
}


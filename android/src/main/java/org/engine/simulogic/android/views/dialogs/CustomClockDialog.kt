package org.engine.simulogic.android.views.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import org.engine.simulogic.R
import org.engine.simulogic.android.circuits.storage.ProjectOptions

class CustomClockDialog (context: Context, private val projectOptions: ProjectOptions, private val listener:OnEditProjectClickListener) : Dialog(context) {


    class InputFilterMinMax: InputFilter {
        private var min:Int = 0
        private var max:Int = 0
        constructor(min:Int, max:Int) {
            this.min = min
            this.max = max
        }
        constructor(min:String, max:String) {
            this.min = Integer.parseInt(min)
            this.max = Integer.parseInt(max)
        }
        override fun filter(source:CharSequence, start:Int, end:Int, dest: Spanned, dstart:Int, dend:Int): CharSequence? {
            try
            {
                val input = Integer.parseInt(dest.toString() + source.toString())
                if (isInRange(min, max, input))
                    return null
            }
            catch (_:NumberFormatException) {}
            return ""
        }
        private fun isInRange(a:Int, b:Int, c:Int):Boolean {
            return if (b > a) c in a..b else c in b..a
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = layoutInflater.inflate(R.layout.custom_clock_dialog_layout,null)
        val cancel = view.findViewById<AppCompatButton>(R.id.cancel)
        val accept = view.findViewById<AppCompatButton>(R.id.accept)
        val clock = view.findViewById<TextInputEditText>(R.id.clock).apply {
            filters = arrayOf(InputFilterMinMax(1, 60))
        }
        // ignore the extension
        clock.setText(projectOptions.title)
        accept.setOnClickListener {
            listener.success(1f / clock.text.toString().toFloat())
            dismiss()
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


    interface OnEditProjectClickListener{
        fun success(freq:Float)
        fun failure(msg:String)
        fun cancel()
    }
}


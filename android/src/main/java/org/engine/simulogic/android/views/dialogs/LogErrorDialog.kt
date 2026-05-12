package org.engine.simulogic.android.views.dialogs
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import org.engine.simulogic.R
import org.engine.simulogic.android.helpers.ActivityHelpers


class LogErrorDialog (context: Context, private val message:String) : Dialog(context) {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = layoutInflater.inflate(R.layout.log_error_dialog,null)
        view.findViewById<ImageView>(R.id.close).setOnClickListener {
            dismiss()
        }
        view.findViewById<MaterialTextView>(R.id.error_message).text = message
        view.findViewById<MaterialButton>(R.id.feedback).setOnClickListener {
            ActivityHelpers.copyAndShareEmail(context,message, "Error Log")
        }
        this.setContentView(view)
        this.setCancelable(true)
    }

    override fun onStart() {
        super.onStart()
        val width: Int = context.resources.getDimensionPixelSize(R.dimen.popup_width)
        window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawableResource(R.color.transparent)
    }
}


package org.engine.simulogic.android.views.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import org.engine.simulogic.R
import org.engine.simulogic.android.views.interfaces.IDialogLabelListener


class LabelDialog(context: Context, private val listener:IDialogLabelListener) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = layoutInflater.inflate(R.layout.dialog_label_name,null)
        val cancel = view.findViewById<AppCompatButton>(R.id.cancel)
        val save = view.findViewById<AppCompatButton>(R.id.save)
        val editTextFileName = view.findViewById<TextInputEditText>(R.id.fileName)
        cancel.setOnClickListener {
            dismiss()
            listener.onCancelled()
        }
        save.setOnClickListener {
            dismiss()
            listener.onCompleted(editTextFileName.text.toString())
        }
        this.setContentView(view)
    }

    override fun onStart() {
        super.onStart()
        val width: Int = context.resources.getDimensionPixelSize(R.dimen.popup_width)
        window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        //  window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}

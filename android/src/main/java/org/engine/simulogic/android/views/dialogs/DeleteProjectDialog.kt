package org.engine.simulogic.android.views.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import org.engine.simulogic.R
import org.engine.simulogic.android.circuits.storage.ProjectOptions

class DeleteProjectDialog(context:Context, private val fileName:String,private val listener:OnDeleteProjectClickListener) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = layoutInflater.inflate(R.layout.delete_project_dialog_layout,null)
        val cancel = view.findViewById<AppCompatButton>(R.id.cancel)
        val delete = view.findViewById<AppCompatButton>(R.id.delete)
        val fileNameTextView = view.findViewById<TextView>(R.id.fileName)
        fileNameTextView.text = fileName
        delete.setOnClickListener {
            listener.accept()
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


    interface OnDeleteProjectClickListener{
        fun accept()
        fun cancel()
    }
}



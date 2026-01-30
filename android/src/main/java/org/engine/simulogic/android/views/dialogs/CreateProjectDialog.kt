package org.engine.simulogic.android.views.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import org.engine.simulogic.R

class CreateProjectDialog(context:Context,private val listener:OnCreateProjectClickListener) : Dialog(context) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = layoutInflater.inflate(R.layout.create_project_dialog_layout,null)
        val cancel = view.findViewById<AppCompatButton>(R.id.cancel)
        val create = view.findViewById<AppCompatButton>(R.id.create)
        val editTextFileName = view.findViewById<TextInputEditText>(R.id.fileName)
        val editTextDescr = view.findViewById<TextInputEditText>(R.id.description)
        create.setOnClickListener {
            try {
                listener.success(editTextFileName.text.toString(), editTextDescr.text.toString())
                dismiss()
            }catch (exp:FileAlreadyExistsException){
                listener.failure(exp.reason.toString())
                editTextFileName.error = exp.reason
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


    interface OnCreateProjectClickListener{
        fun success(title:String, description:String)
        fun failure(msg:String)
        fun cancel()
    }
}

package org.engine.simulogic.android.views.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.material.button.MaterialButton
import org.engine.simulogic.R

class AboutDialog (context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = layoutInflater.inflate(R.layout.about_project_dialog,null)
        view.findViewById<ImageView>(R.id.close).setOnClickListener {
            dismiss()
        }
        view.findViewById<MaterialButton>(R.id.review).setOnClickListener {

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


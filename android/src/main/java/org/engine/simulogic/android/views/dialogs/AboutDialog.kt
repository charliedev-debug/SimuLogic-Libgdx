package org.engine.simulogic.android.views.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import org.engine.simulogic.R


class AboutDialog (context: Context) : Dialog(context) {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = layoutInflater.inflate(R.layout.about_project_dialog,null)
        view.findViewById<ImageView>(R.id.close).setOnClickListener {
            dismiss()
        }
         try {
            val packageManager = context.packageManager
            val packageName = context.packageName
            val info = packageManager.getPackageInfo(packageName, 0)
            view.findViewById<MaterialTextView>(R.id.versionName).text = "Version ${info.versionName}"
        } catch (e: NameNotFoundException) {
            e.printStackTrace()
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


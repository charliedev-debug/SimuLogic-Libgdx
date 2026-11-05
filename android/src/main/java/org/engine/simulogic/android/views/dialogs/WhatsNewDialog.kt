package org.engine.simulogic.android.views.dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import org.engine.simulogic.R
import org.engine.simulogic.android.PremiumPurchaseActivity
import org.engine.simulogic.android.views.adapters.WhatsNewAdapter

class WhatsNewDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.whats_new_dialog)
        try {
            val packageManager = context.packageManager
            val packageName = context.packageName
            val info = packageManager.getPackageInfo(packageName, 0)
            findViewById<MaterialTextView>(R.id.versionName).text = "Version ${info.versionName}"
        } catch (e: NameNotFoundException) {
            e.printStackTrace()
        }

        findViewById<AppCompatImageButton>(R.id.closeDialog).setOnClickListener {
            dismiss()
        }
        findViewById<MaterialButton>(R.id.unlockPremium).setOnClickListener {
            Intent(context, PremiumPurchaseActivity::class.java).also {
                context.startActivity(it)
            }
            dismiss()
        }
        findViewById<RecyclerView>(R.id.whatsNewRecyclerView).also { recyclerView ->
            recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL,false)
            WhatsNewAdapter().also { adapter ->
                //adapter.insert("User-interface and experience",R.drawable.whats_new_ui, isHeader = true)
                adapter.insert("Improved Simulation environment",R.drawable.whats_new_simulation_icon, isHeader = true)
                adapter.insert("Increased component sizes",R.drawable.text_edit)
                adapter.insert("Better gesture events",R.drawable.text_edit)
                adapter.insert("Better wiring system events",R.drawable.text_edit)
                adapter.insert("Performance & new features",R.drawable.whats_new_performance_icon, isHeader = true)
                adapter.insert("Added Encoders & Decoders",R.drawable.text_edit)
                adapter.insert("Added Wire-Coloring",R.drawable.text_edit)
               // adapter.insert("Solved bugs and issues",R.drawable.whats_new_bug_icon, isHeader = true)
                recyclerView.adapter = adapter
            }
        }
    }
    override fun onStart() {
        super.onStart()
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawableResource(R.color.transparent)
    }
}

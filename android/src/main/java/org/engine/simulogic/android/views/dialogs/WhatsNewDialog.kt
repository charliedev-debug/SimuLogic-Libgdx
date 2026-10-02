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
                adapter.insert("User-interface and experience",R.drawable.whats_new_ui, isHeader = true)
                adapter.insert("Overhauled dialogs, buttons, cards and icons",R.drawable.text_edit)
                adapter.insert("Improved Simulation environment",R.drawable.whats_new_simulation_icon, isHeader = true)
                adapter.insert("Landscape mode is much better in the env",R.drawable.text_edit)
                adapter.insert("Groups are much better with no component misalignment",R.drawable.text_edit)
                adapter.insert("Faster loading for larger project with many connections and components",R.drawable.text_edit)
                adapter.insert("Component UI floats to enable multiple item addition",R.drawable.text_edit)
                adapter.insert("Performance & new features",R.drawable.whats_new_performance_icon, isHeader = true)
                adapter.insert("Added the ability to split nodes for pro users",R.drawable.text_edit)
                adapter.insert("Better performance, low memory usage on mid-low end devices",R.drawable.text_edit)
                adapter.insert("Improved connections for all users, its now easier to edit and manage wires across larger projects",R.drawable.text_edit)
                adapter.insert("Component alignment lines added",R.drawable.text_edit)
                adapter.insert("Solved bugs and issues",R.drawable.whats_new_bug_icon, isHeader = true)
                adapter.insert("Crashes in the premium checkout page solved",R.drawable.text_edit)
                adapter.insert("Crashes in the simulation environment solved",R.drawable.text_edit)
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

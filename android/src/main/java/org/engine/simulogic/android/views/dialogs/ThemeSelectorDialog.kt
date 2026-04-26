package org.engine.simulogic.android.views.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import kotlinx.coroutines.runBlocking
import org.engine.simulogic.R
import org.engine.simulogic.android.circuits.storage.UserSettings
import org.engine.simulogic.android.helpers.ActivityHelpers

class ThemeSelectorDialog(context: Context, private val userSettings: UserSettings, private val listener: OnThemeClickListener):Dialog(context)  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_theme_selector_dialog)
        findViewById<RadioGroup>(R.id.theme_group).also{
            themeGroup->
            var activeTheme = ""
            runBlocking{
                activeTheme = ActivityHelpers.getTheme(userSettings, context)
            }
            when(activeTheme){
                "rosepine"->{
                    findViewById<RadioButton>(R.id.rosepine).isChecked = true
                }
                "kanagawa"->{
                    findViewById<RadioButton>(R.id.kanagawa).isChecked = true
                }
                "gruvbox"->{
                    findViewById<RadioButton>(R.id.gruvbox).isChecked = true
                }
                "tokyonight"->{
                    findViewById<RadioButton>(R.id.tokyonight).isChecked = true
                }
                "catppuccin"->{
                    findViewById<RadioButton>(R.id.catppuccin).isChecked = true
                }
            }
            themeGroup.setOnCheckedChangeListener { group, id ->
                when(id){
                    R.id.rosepine->{
                        listener.onClick("rosepine")
                        dismiss()
                    }

                    R.id.kanagawa->{
                        listener.onClick("kanagawa")
                        dismiss()
                    }
                    R.id.gruvbox->{
                        listener.onClick("gruvbox")
                        dismiss()
                    }

                    R.id.tokyonight->{
                        listener.onClick("tokyonight")
                        dismiss()
                    }

                    R.id.catppuccin->{
                        listener.onClick("catppuccin")
                        dismiss()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val width: Int = context.resources.getDimensionPixelSize(R.dimen.popup_width)
        window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawableResource(R.color.transparent)
        //  window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    interface OnThemeClickListener{
        fun onClick(value:String)
    }
}

package org.engine.simulogic.android

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.engine.simulogic.R
import org.engine.simulogic.android.circuits.storage.UserSettings
import org.engine.simulogic.android.helpers.ActivityHelpers
import org.engine.simulogic.android.ui.adapters.SettingsPrefAdapter
import org.engine.simulogic.android.ui.models.SettingsOption
import org.engine.simulogic.android.views.dialogs.LanguageSelectorDialog
import org.engine.simulogic.android.views.dialogs.ThemeSelectorDialog

class SettingsActivity : AppCompatActivity() {
    private val userSettings = UserSettings()
    override fun onCreate(savedInstanceState: Bundle?) {
        runBlocking{
            ActivityHelpers.getTheme(userSettings, this@SettingsActivity)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(scrim = Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(scrim = Color.TRANSPARENT))
        ActivityHelpers.setStatusBarColor(window, ActivityHelpers.getThemeResourceID(this, com.google.android.material.R.attr.backgroundColor))
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }
        val scope = CoroutineScope(Dispatchers.Default)
        val prefAdapter = SettingsPrefAdapter().apply {
            insert(SettingsPrefAdapter.HEADER,"Language")
            insert(SettingsPrefAdapter.BUTTON,"English", "",R.drawable.language , listener = object :
                SettingsPrefAdapter.OnItemClickListener{
                override fun onClick(item: SettingsOption) {
                    LanguageSelectorDialog(this@SettingsActivity).show()
                }
            })
            insert(SettingsPrefAdapter.HEADER,"Theme")
            var currentTheme = ""
            runBlocking {
                currentTheme = userSettings.getDataString(this@SettingsActivity, UserSettings.THEME_STYLE).first()
            }
            insert(SettingsPrefAdapter.BUTTON,currentTheme, "",R.drawable.theme_palette , listener = object :
                SettingsPrefAdapter.OnItemClickListener{
                override fun onClick(item: SettingsOption) {
                    ThemeSelectorDialog(this@SettingsActivity, userSettings,object:
                        ThemeSelectorDialog.OnThemeClickListener{
                        override fun onClick(value: String) {
                            scope.launch(Dispatchers.Main) {
                                userSettings.saveStringPref(this@SettingsActivity, UserSettings.THEME_STYLE, value)
                                val intent = Intent(this@SettingsActivity, LauncherActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                finishAffinity()
                                Runtime.getRuntime().exit(0)
                            }
                        }
                        }).show()
                }
           })
        }
        findViewById<RecyclerView>(R.id.settingsList).apply {
            layoutManager = LinearLayoutManager(this@SettingsActivity, LinearLayoutManager.VERTICAL, false)
            adapter = prefAdapter
        }

    }
}

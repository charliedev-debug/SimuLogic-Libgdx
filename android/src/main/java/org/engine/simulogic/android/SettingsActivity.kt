package org.engine.simulogic.android

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowInsets
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.runBlocking
import org.engine.simulogic.R
import org.engine.simulogic.android.circuits.storage.UserSettings
import org.engine.simulogic.android.helpers.ActivityHelpers

class SettingsActivity : AppCompatActivity() {
    private val userSettings = UserSettings()
    override fun onCreate(savedInstanceState: Bundle?) {
        runBlocking{
            ActivityHelpers.getTheme(userSettings, this@SettingsActivity)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(scrim = Color.WHITE),
            navigationBarStyle = SystemBarStyle.dark(scrim = Color.WHITE))
        ActivityHelpers.setStatusBarColor(window, ActivityHelpers.getThemeResourceID(this, com.google.android.material.R.attr.backgroundColor))
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings, SettingsFragment())
                    .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}

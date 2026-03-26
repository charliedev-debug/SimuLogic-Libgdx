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
import org.engine.simulogic.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(scrim = Color.WHITE),
            navigationBarStyle = SystemBarStyle.dark(scrim = Color.WHITE))
        setStatusBarColor(window, R.color.background_dark)
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

    fun setStatusBarColor(window: Window, color: Int) {
        window.decorView.setOnApplyWindowInsetsListener { view, insets ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                val statusBarInsets = insets.getInsets(WindowInsets.Type.systemBars())
                view.setBackgroundResource(color)
                view.setPadding(statusBarInsets.left, statusBarInsets.top, statusBarInsets.right, statusBarInsets.bottom)
                insets
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val bars = insets.getInsets(WindowInsets.Type.systemBars())
                    view.setBackgroundResource(color)
                    view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
                    return@setOnApplyWindowInsetsListener insets
                }else{
                    window.statusBarColor = ContextCompat.getColor(this, color)
                    window.navigationBarColor = ContextCompat.getColor(this, color)
                    val insetsController = WindowInsetsControllerCompat(window, window.decorView)
                    insetsController.isAppearanceLightStatusBars = false
                    view.setPadding(insets.systemWindowInsetLeft,insets.systemWindowInsetTop, insets.systemWindowInsetRight, insets.systemWindowInsetBottom)
                    window.decorView.setBackgroundColor(ContextCompat.getColor(this, color))
                    return@setOnApplyWindowInsetsListener insets
                }
            }
        }
    }
}

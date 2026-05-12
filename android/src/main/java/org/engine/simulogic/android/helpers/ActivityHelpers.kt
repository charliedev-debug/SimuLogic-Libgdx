package org.engine.simulogic.android.helpers

import android.content.Context
import android.os.Build
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.widget.Toast
import android.util.TypedValue
import android.view.Window
import android.view.WindowInsets
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.flow.first
import org.engine.simulogic.R
import org.engine.simulogic.android.circuits.storage.UserSettings

class ActivityHelpers {

    companion object{
        fun copyAndShareEmail(context: Context, text: String, subject:String) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("shared_log", text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Log copied to clipboard", Toast.LENGTH_LONG).show()
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_EMAIL,arrayOf("ngaricharlesdev@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, text)
            }
            context.startActivity(Intent.createChooser(emailIntent, "Share via Email"))
        }

        suspend fun getTheme(userSettings: UserSettings, context: Context): String{
            return userSettings.getDataString(context, UserSettings.THEME_STYLE)
                .first().also {
                    when (it) {
                        "rosepine" -> {
                            context.theme.applyStyle(R.style.RosePine, true)
                        }

                        "tokyonight" -> {
                            // setTheme(R.style.TokyoNight)
                            context.theme.applyStyle(R.style.TokyoNight, true)
                        }

                        "kanagawa" -> {
                            context.theme.applyStyle(R.style.Kanagawa, true)
                        }

                        "catppuccin" -> {
                            context.theme.applyStyle(R.style.Catppuccin, true)
                        }

                        "gruvbox" -> {
                            context.theme.applyStyle(R.style.GruvBox, true)
                        }
                    }
                }
        }

        fun getThemeResourceID(context: Context, attr: Int): Int {
            val value = TypedValue()
            context.theme.resolveAttribute(attr, value, true)
            return value.data
        }

        fun setStatusBarColor(window: Window, color: Int) {
            window.decorView.setOnApplyWindowInsetsListener { view, insets ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                    val statusBarInsets = insets.getInsets(WindowInsets.Type.systemBars())
                    view.setBackgroundColor(color)
                    view.setPadding(statusBarInsets.left, statusBarInsets.top, statusBarInsets.right, statusBarInsets.bottom)
                    insets
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val bars = insets.getInsets(WindowInsets.Type.systemBars())
                        view.setBackgroundColor(color)
                        view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
                        return@setOnApplyWindowInsetsListener insets
                    }else{
                        window.statusBarColor = color
                        window.navigationBarColor = color
                        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
                        insetsController.isAppearanceLightStatusBars = false
                        view.setPadding(insets.systemWindowInsetLeft,insets.systemWindowInsetTop, insets.systemWindowInsetRight, insets.systemWindowInsetBottom)
                        window.decorView.setBackgroundColor(color)
                        return@setOnApplyWindowInsetsListener insets
                    }
                }

            }
        }
    }
}

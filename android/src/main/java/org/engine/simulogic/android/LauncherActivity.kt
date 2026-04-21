package org.engine.simulogic.android
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.Menu
import android.view.Window
import android.view.WindowInsets
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import org.engine.simulogic.R
import org.engine.simulogic.android.views.dialogs.AboutDialog
import org.engine.simulogic.databinding.ActivityLauncherBinding


class LauncherActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
private lateinit var binding: ActivityLauncherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

     binding = ActivityLauncherBinding.inflate(layoutInflater)
     setContentView(binding.root)

     enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(scrim = Color.WHITE),
            navigationBarStyle = SystemBarStyle.dark(scrim = Color.WHITE))
        setStatusBarColor(window, getThemeResourceID(this, com.google.android.material.R.attr.backgroundColor))

     setSupportActionBar(binding.appBarLauncher.toolbar)

     val settingsButtonLauncher = findViewById<MaterialButton>(R.id.settings)
     val aboutButtonLauncher = findViewById<MaterialButton>(R.id.about)

     findViewById<Toolbar>(R.id.toolbar).setOnMenuItemClickListener { item ->
            when (item.title) {
                "help" -> {
                    Intent(this@LauncherActivity, HelpActivity::class.java).also { intent ->
                        startActivity(intent)
                    }
                }
            }
            true
        }
        settingsButtonLauncher.setOnClickListener {
            Intent(this@LauncherActivity,SettingsActivity::class.java).also { intent ->
                startActivity(intent)
            }
        }

        aboutButtonLauncher.setOnClickListener {
            AboutDialog(this@LauncherActivity).show()
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_launcher)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.nav_open_project, R.id.nav_manage_projects), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        binding.appBarLauncher.toolbar.setNavigationIcon(R.drawable.menu)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.nav_home) {
                binding.appBarLauncher.toolbar.setNavigationOnClickListener {
                    binding.drawerLayout.openDrawer(Gravity.LEFT)
                }
                binding.appBarLauncher.toolbar.setNavigationIcon(R.drawable.menu)
            }else if(destination.id == R.id.nav_open_project || destination.id == R.id.nav_manage_projects){
                binding.appBarLauncher.toolbar.also{ toolbar->
                    toolbar.setNavigationIcon(R.drawable.back)
                    toolbar.setNavigationOnClickListener {
                        supportFragmentManager.popBackStack()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.launcher, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_launcher)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
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

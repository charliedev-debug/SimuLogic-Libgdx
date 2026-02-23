package org.engine.simulogic.android
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.button.MaterialButton
import org.engine.simulogic.databinding.ActivityLauncherBinding
import org.engine.simulogic.R
import org.engine.simulogic.android.views.adapters.MenuAdapterItem
import org.engine.simulogic.android.views.dialogs.AboutDialog

class LauncherActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
private lateinit var binding: ActivityLauncherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

     binding = ActivityLauncherBinding.inflate(layoutInflater)
     setContentView(binding.root)

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

        binding.appBarLauncher.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
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
}

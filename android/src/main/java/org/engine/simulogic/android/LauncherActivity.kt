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
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.runBlocking
import org.engine.simulogic.R
import org.engine.simulogic.android.circuits.storage.UserSettings
import org.engine.simulogic.android.helpers.ActivityHelpers
import org.engine.simulogic.android.views.dialogs.AboutDialog
import org.engine.simulogic.android.views.dialogs.AlertDialog
import org.engine.simulogic.android.views.dialogs.InfoDialog
import org.engine.simulogic.databinding.ActivityLauncherBinding


class LauncherActivity : AppCompatActivity() {

private lateinit var appBarConfiguration: AppBarConfiguration
private lateinit var binding: ActivityLauncherBinding
private val userSettings = UserSettings()
    override fun onCreate(savedInstanceState: Bundle?) {
        runBlocking{
            ActivityHelpers.getTheme(userSettings, this@LauncherActivity)
        }
        super.onCreate(savedInstanceState)

     binding = ActivityLauncherBinding.inflate(layoutInflater)
     setContentView(binding.root)

     enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(scrim = Color.WHITE),
            navigationBarStyle = SystemBarStyle.dark(scrim = Color.WHITE))
     ActivityHelpers.setStatusBarColor(window, ActivityHelpers.getThemeResourceID(this, com.google.android.material.R.attr.backgroundColor))

     setSupportActionBar(binding.appBarLauncher.toolbar)

     val settingsButtonLauncher = findViewById<MaterialButton>(R.id.settings)
     val aboutButtonLauncher = findViewById<MaterialButton>(R.id.about)
     val updateActivityResult = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
          if(result.resultCode != RESULT_CANCELED){

              Toast.makeText(this@LauncherActivity, "update cancelled!", Toast.LENGTH_LONG).show()

          }
     }

        AppUpdateManagerFactory.create(this@LauncherActivity).also{
                appUpdateManager ->
            appUpdateManager.appUpdateInfo.addOnSuccessListener {appUpdateInfo ->
                if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE){
                    AlertDialog(
                        this@LauncherActivity,
                        "Do you want to update app to latest version? This will improve user experience and performance.",
                        object :
                            AlertDialog.OnAlertListener {
                            override fun accept() {
                                appUpdateManager.startUpdateFlowForResult(
                                    appUpdateInfo, updateActivityResult,
                                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                                )
                            }

                            override fun cancel() {

                            }
                        }).show()
                }
            }}

        findViewById<Toolbar>(R.id.toolbar).setOnMenuItemClickListener { item ->
            when (item.title) {
                "help" -> {
                    Intent(this@LauncherActivity, HelpActivity::class.java).also { intent ->
                        startActivity(intent)
                    }
                }

                "update" ->{
                    //FakeAppUpdateManager(this@LauncherActivity)
                   AppUpdateManagerFactory.create(this@LauncherActivity).also{
                        appUpdateManager ->
                        appUpdateManager.appUpdateInfo.addOnSuccessListener {appUpdateInfo ->
                            when(appUpdateInfo.updateAvailability()){

                                UpdateAvailability.UPDATE_AVAILABLE->{
                                    AlertDialog(this@LauncherActivity,"Do you want to update app to latest version? This will improve user experience and performance.",object :
                                        AlertDialog.OnAlertListener{
                                        override fun accept() {
                                            appUpdateManager.startUpdateFlowForResult(appUpdateInfo,updateActivityResult,
                                                AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build())
                                        }

                                        override fun cancel() {

                                        }
                                    }).show()

                                }

                                UpdateAvailability.UPDATE_NOT_AVAILABLE, UpdateAvailability.UNKNOWN ->{
                                    InfoDialog(this@LauncherActivity,"No updates available!").show()
                                }

                                UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS->{
                                    InfoDialog(this@LauncherActivity,"Update currently in progress!").show()
                                }
                            }

                        }

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
}

package org.engine.simulogic.android

import android.annotation.SuppressLint
import android.os.Build
import org.engine.simulogic.R
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.engine.simulogic.android.circuits.storage.DataTransferObject
import org.engine.simulogic.android.circuits.storage.ProjectOptions
import org.engine.simulogic.android.views.ComponentBottomSheet
import org.engine.simulogic.android.views.SimulationFragment
import org.engine.simulogic.android.views.adapters.MenuViewAdapter
import org.engine.simulogic.android.views.adapters.ComponentItem
import org.engine.simulogic.android.views.adapters.MenuAdapterItem
import org.engine.simulogic.android.views.dialogs.EditProjectDialog
import org.engine.simulogic.android.views.interfaces.IComponentAdapterListener
import org.engine.simulogic.android.views.interfaces.IFpsListener
import org.engine.simulogic.android.views.interfaces.IMenuAdapterListener
import org.engine.simulogic.android.views.models.BottomSheetViewModel
import org.engine.simulogic.android.views.models.MenuViewModel

class SimulationActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {

    private lateinit var textFps: TextView
    private lateinit var textLatency:TextView
    private lateinit var projectTitle:TextView
    private lateinit var projectDescription:TextView
    private val menuViewModel: MenuViewModel by viewModels()
    private val bottomSheetViewModel: BottomSheetViewModel by viewModels()
    private lateinit var jobStateRoutine :Job
    private lateinit var simulationFragment: SimulationFragment
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulation)


        val toolBar = findViewById<Toolbar>(R.id.toolbar)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val bottomSheetButton = findViewById<View>(R.id.component_bottom_sheet)
        val componentCountTextView = findViewById<TextView>(R.id.components_count)
        val connectionCountTextView = findViewById<TextView>(R.id.connection_count)
        val projectMetaDataEditButton = findViewById<AppCompatButton>(R.id.project_meta_data_edit)
        textFps = findViewById(R.id.fps_text)
        textLatency = findViewById(R.id.latency)
        projectTitle = findViewById(R.id.project_title)
        projectDescription = findViewById(R.id.project_description)
        setSupportActionBar(toolBar)


        val projectOptions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("options", ProjectOptions::class.java)
        } else {
            intent.getSerializableExtra("options") as ProjectOptions
        }

        if(projectOptions == null){
            finish()
        }

        simulationFragment =  SimulationFragment(projectOptions!!)
        toolBar.setOnMenuItemClickListener { item ->
            when (item.title) {
                "Save" -> {
                    menuViewModel.onModeChanged(MenuAdapterItem(title = "Save", isMode = false, 0))
                }

                "Drawer" -> {
                    drawerLayout.openDrawer(Gravity.RIGHT)
                }
            }

            true
        }

        val menuRecyclerView = findViewById<RecyclerView>(R.id.menu_list).apply {
            layoutManager =
                LinearLayoutManager(this@SimulationActivity, LinearLayoutManager.HORIZONTAL, false)
        }

        val menuAdapter = MenuViewAdapter().apply {
            insert("Origin", false, R.drawable.origin)
            insert("Touch", true, R.drawable.touch)
            insert("Interact", true, R.drawable.interact)
            insert("Sel-Touch", true, R.drawable.selection)
            insert("Sel-Range", true, R.drawable.select_rect)
            insert("Connect", true, R.drawable.connector)
            insert("Undo", false, R.drawable.undo)
            insert("Redo", false, R.drawable.redo)
            insert("Cut", false, R.drawable.cut)
            insert("Copy", false, R.drawable.copy)
            insert("Paste", false, R.drawable.paste)
            insert("Delete", false, R.drawable.delete)
            selectedMode = 1
        }

        menuRecyclerView.adapter = menuAdapter

        menuAdapter.listener = object : IMenuAdapterListener {
            override fun onClickListener(item: MenuAdapterItem) {
                menuViewModel.onModeChanged(item)
            }
        }
        val bottomSheet = ComponentBottomSheet(object : IComponentAdapterListener {
            override fun onClickComponent(item: ComponentItem) {
                bottomSheetViewModel.onComponentTriggered(item)
            }

        })
        bottomSheetButton.setOnClickListener {
            if (!bottomSheet.isVisible) {
                bottomSheet.show(supportFragmentManager, "COMPONENTS")
            }
        }

        projectMetaDataEditButton.setOnClickListener {
            val oldFile = projectOptions.title
            drawerLayout.closeDrawer(Gravity.RIGHT)
            EditProjectDialog(this, projectOptions,object:EditProjectDialog.OnEditProjectClickListener{
                override fun success(title: String, description: String) {
                    projectOptions.title = "${title}.bin"
                    projectOptions.description = description
                    simulationFragment.simulationLoop.componentManager.saveProject()
                    DataTransferObject.deleteFile(this@SimulationActivity,oldFile)
                }

                override fun failure(msg: String) {

                }

                override fun cancel() {

                }

            }).show()
        }


        projectTitle.text = projectOptions.title
        projectDescription.text = projectOptions.description

       val scope = CoroutineScope(Dispatchers.Default)

       jobStateRoutine = scope.launch {
           while (true){
               launch(Dispatchers.Main) {
                   simulationFragment.simulationLoop.also { simulationLoop ->
                       val fpsCounter = simulationLoop.fpsCounter
                       textFps.text = "FPS: ${fpsCounter.getFps()} fps"
                       textLatency.text = "Latency:${((1f/ fpsCounter.getFps()) * 1000).toInt()} ms"
                       if(simulationLoop.isReady) {
                           simulationLoop.componentManager.also { componentManager ->
                               componentCountTextView.text = "Components: ${componentManager.size()}"
                               connectionCountTextView.text = "Connections: ${componentManager.connectionSize()}"
                           }
                       }
                   }

               }
               delay(5000L)
           }
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.simulation_fragment,simulationFragment).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.simulation, menu)
        return true
    }

    override fun exit() {
        jobStateRoutine.cancel()
    }
}

package org.engine.simulogic.android

import android.annotation.SuppressLint
import android.os.Build
import org.engine.simulogic.R
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.google.android.material.switchmaterial.SwitchMaterial
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
import org.engine.simulogic.android.views.interfaces.IMenuAdapterListener
import org.engine.simulogic.android.views.models.BottomSheetViewModel
import org.engine.simulogic.android.views.models.MenuViewModel

class SimulationActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {

    private lateinit var textFps: TextView
    private lateinit var textLatency: TextView
    private lateinit var projectTitle: TextView
    private lateinit var projectDescription: TextView
    private val menuViewModel: MenuViewModel by viewModels()
    private val bottomSheetViewModel: BottomSheetViewModel by viewModels()
    private lateinit var jobStateRoutine: Job
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
        val gridLabelEnabledSwitch = findViewById<SwitchMaterial>(R.id.label_enabled)
        val gridEnabledSwitch = findViewById<SwitchMaterial>(R.id.grid_enabled)
        val gridStyleRadioButton = findViewById<RadioGroup>(R.id.grid_styles)
        val simulationToggleButton = findViewById<AppCompatToggleButton>(R.id.simulation_toggle)
        val simulationToolbarEnabledSwitch = findViewById<SwitchMaterial>(R.id.top_bar_enabled)
        val simulationMenuBarEnabledSwitch = findViewById<SwitchMaterial>(R.id.menu_bar_enabled)
        val drawerLayoutButtonMinimized = findViewById<AppCompatImageButton>(R.id.drawer_minimized)
        val autoSaveEnabledSwitch = findViewById<SwitchMaterial>(R.id.auto_save_enabled)

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

        if (projectOptions == null) {
            finish()
        }

        simulationFragment = SimulationFragment(projectOptions!!)
        toolBar.setOnMenuItemClickListener { item ->
            when (item.title) {
                "Save" -> {
                    menuViewModel.onModeChanged(MenuAdapterItem(id= "Save",title = "Save", isMode = false, 0))
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
            insert("Origin","Origin", false, R.drawable.origin)
            insert("Touch","Touch", true, R.drawable.touch)
            insert("Interact","Interact", true, R.drawable.interact)
            insert("Sel-Touch","Sel-Touch", true, R.drawable.selection)
            insert("Sel-Range","Sel-Range", true, R.drawable.select_rect)
            insert("Connect2","Connect", true, R.drawable.connect_2_node)
            insert("Connect4","Connect", true, R.drawable.connect_4_node)
            insert("Connect6","Connect", true, R.drawable.connect_6_node)
            insert("Undo","Undo", false, R.drawable.undo)
            insert("Redo","Redo", false, R.drawable.redo)
            insert("Cut","Cut", false, R.drawable.cut)
            insert("Copy","Copy", false, R.drawable.copy)
            insert("Paste","Paste", false, R.drawable.paste)
            insert("Delete","Delete", false, R.drawable.delete)
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

        gridLabelEnabledSwitch.setOnClickListener {
            simulationFragment.simulationLoop.componentManager.hideGridLabels()
        }

        gridEnabledSwitch.setOnClickListener {
            simulationFragment.simulationLoop.componentManager.toggleGrid()
        }

        gridStyleRadioButton.setOnCheckedChangeListener { _, id ->
            if (id == R.id.grid_style_a) {
                simulationFragment.simulationLoop.componentManager.setStyleA()
            } else {
                simulationFragment.simulationLoop.componentManager.setStyleB()
            }
        }
        autoSaveEnabledSwitch.setOnClickListener {
            simulationFragment.simulationLoop.componentManager.toggleAutoSave()
        }

        drawerLayoutButtonMinimized.setOnClickListener {
            drawerLayout.openDrawer(Gravity.RIGHT)
        }

        simulationToggleButton.setOnClickListener {
            simulationFragment.simulationLoop.componentManager.toggleExecutionState()
        }

        simulationToolbarEnabledSwitch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                toolBar.visibility = View.VISIBLE
                drawerLayoutButtonMinimized.visibility = View.GONE
            }else{
                toolBar.visibility = View.GONE
                drawerLayoutButtonMinimized.visibility = View.VISIBLE
            }
        }

        simulationMenuBarEnabledSwitch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                menuRecyclerView.visibility = View.VISIBLE
            }else{
                menuRecyclerView.visibility = View.GONE
            }
        }

        projectMetaDataEditButton.setOnClickListener {
            val oldFile = projectOptions.title
            drawerLayout.closeDrawer(Gravity.RIGHT)
            EditProjectDialog(
                this,
                projectOptions,
                object : EditProjectDialog.OnEditProjectClickListener {
                    override fun success(title: String, description: String) {
                        projectOptions.title = title
                        projectOptions.description = description
                        projectTitle.text = projectOptions.title
                        projectDescription.text = projectOptions.description
                        simulationFragment.simulationLoop.componentManager.saveProject()
                        DataTransferObject.deleteFile(this@SimulationActivity, oldFile)
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
            while (true) {
                launch(Dispatchers.Main) {
                    simulationFragment.simulationLoop.also { simulationLoop ->
                        val fpsCounter = simulationLoop.fpsCounter
                        textFps.text = "FPS: ${fpsCounter.getFps()} fps"
                        textLatency.text =
                            "Latency:${((1f / fpsCounter.getFps()) * 1000).toInt()} ms"
                        if (simulationLoop.isReady) {
                            simulationLoop.componentManager.also { componentManager ->
                                componentCountTextView.text =
                                    "Components: ${componentManager.size()}"
                                connectionCountTextView.text =
                                    "Connections: ${componentManager.connectionSize()}"
                            }
                        }
                    }

                }
                delay(5000L)
            }
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.simulation_fragment, simulationFragment).commit()
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

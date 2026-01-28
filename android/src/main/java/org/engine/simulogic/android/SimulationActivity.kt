package org.engine.simulogic.android
import android.annotation.SuppressLint
import org.engine.simulogic.R
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import org.engine.simulogic.android.views.ComponentBottomSheet
import org.engine.simulogic.android.views.SimulationFragment
import org.engine.simulogic.android.views.adapters.MenuViewAdapter
import org.engine.simulogic.android.views.adapters.ComponentItem
import org.engine.simulogic.android.views.adapters.MenuAdapterItem
import org.engine.simulogic.android.views.interfaces.IComponentAdapterListener
import org.engine.simulogic.android.views.interfaces.IFpsListener
import org.engine.simulogic.android.views.interfaces.IMenuAdapterListener
import org.engine.simulogic.android.views.models.BottomSheetViewModel
import org.engine.simulogic.android.views.models.MenuViewModel

class SimulationActivity : AppCompatActivity(), IFpsListener, AndroidFragmentApplication.Callbacks{

    private lateinit var textFps:TextView
    private val menuViewModel:MenuViewModel by viewModels()
    private val bottomSheetViewModel:BottomSheetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_simulation)

        val toolBar = findViewById<Toolbar>(R.id.toolbar)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val bottomSheetButton = findViewById<View>(R.id.component_bottom_sheet)
            textFps = findViewById(R.id.fps_text)
        setSupportActionBar(toolBar)
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
            layoutManager = LinearLayoutManager(this@SimulationActivity, LinearLayoutManager.HORIZONTAL, false)
        }

        val menuAdapter = MenuViewAdapter().apply {
            insert("Origin", false, R.drawable.origin)
            insert("Touch", true, R.drawable.touch)
            insert("Interact", true, R.drawable.interact)
            insert("Sel-Touch", true, R.drawable.selection)
            insert("Sel-Range",true, R.drawable.select_rect)
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

        menuAdapter.listener = object:IMenuAdapterListener{
            override fun onClickListener(item: MenuAdapterItem) {
                   menuViewModel.onModeChanged(item)
            }
        }
        val bottomSheet = ComponentBottomSheet(object: IComponentAdapterListener {
            override fun onClickComponent(item: ComponentItem) {
                bottomSheetViewModel.onComponentTriggered(item)
            }

        })
        bottomSheetButton.setOnClickListener {
            if(!bottomSheet.isVisible) {
                bottomSheet.show(supportFragmentManager, "COMPONENTS")
            }
        }
        supportFragmentManager.beginTransaction().add(R.id.simulation_fragment,SimulationFragment(this)).commit()
    }

    @SuppressLint("SetTextI18n")
    override fun onFPSUpdate(fps: Int) {
        runOnUiThread{
            textFps.text = "FPS: $fps"
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.simulation, menu)
        return true
    }

    override fun exit() {

    }
}

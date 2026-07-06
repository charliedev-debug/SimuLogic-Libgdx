package org.engine.simulogic.android.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.engine.simulogic.R
import org.engine.simulogic.android.PremiumPurchaseActivity
import org.engine.simulogic.android.SimulationActivity
import org.engine.simulogic.android.circuits.storage.DataTransferObject
import org.engine.simulogic.android.circuits.storage.ProjectOptions
import org.engine.simulogic.android.circuits.storage.UserSettings
import org.engine.simulogic.android.ui.adapters.ProjectOptionsAdapter
import org.engine.simulogic.android.ui.adapters.RecentAdapter
import org.engine.simulogic.android.ui.models.ProjectOption
import org.engine.simulogic.android.ui.models.RecentItem
import org.engine.simulogic.android.views.GridSpacingItemDecoration
import org.engine.simulogic.android.views.dialogs.CreateProjectDialog
import org.engine.simulogic.android.views.dialogs.ErrorDialog
import java.io.File

class HomeFragment : Fragment() {


    private lateinit var recentProjectAdapter: RecentAdapter
    private lateinit var recentProjectAlert:TextView
    private var isPremiumUser = false
    private val userSettings = UserSettings()
    private val MAX_RECENT_ITEMS = 6
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_home_launcher, container, false)
        val projectOptionRecyclerView = root.findViewById<RecyclerView>(R.id.project_operations)
        val recentProjectRecyclerView = root.findViewById<RecyclerView>(R.id.recent_projects)
        val sampleProjectRecyclerView = root.findViewById<RecyclerView>(R.id.sample_projects)
            recentProjectAlert = root.findViewById(R.id.recent_project_alert)

        val projectOptionAdapter = ProjectOptionsAdapter().apply {
            add("Create Project", R.drawable.new_project)
            add("Open Project",R.drawable.open_project)
            add("Import Project", R.drawable.import_project)
            add("Manage Project", R.drawable.manage_projects)
        }
        runBlocking {
            isPremiumUser = userSettings.getDataBoolean(requireContext(), UserSettings.PREMIUM_USER,false).first()
        }
        val importProjectActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result->
            if(result.resultCode == Activity.RESULT_OK && result.data != null){
                result.data?.data?.also { uri->
                    DataTransferObject().importProject(requireContext(),uri).also { projectOptions ->
                        projectOptions?.apply {
                            recentProjectAdapter.add(title, path, description, 0L)
                            recentProjectAdapter.notifyItemInserted(recentProjectAdapter.itemCount + 1)
                        }
                        if(projectOptions == null){
                            ErrorDialog(requireContext(),"Corrupted file or incorrect binary file imported!").show()
                        }
                    }
                }
               // Toast.makeText(context,"Results returned!", Toast.LENGTH_SHORT).show()
            }
        }
        projectOptionAdapter.listener = object : ProjectOptionsAdapter.OnOptionClickListener{
            override fun onClick(option: ProjectOption) {
                when(option.title){
                    "Create Project"->{
                        CreateProjectDialog(context!!,object:CreateProjectDialog.OnCreateProjectClickListener{
                            override fun success(title:String, description:String) {
                                Intent(context,SimulationActivity::class.java).apply {
                                    putExtra("options",ProjectOptions(DataTransferObject.randomFileName(),
                                        title,
                                        description,"",0L,ProjectOptions.CREATE))
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(this)
                                }
                            }

                            override fun failure(msg:String) {
                                Toast.makeText(context,msg, Toast.LENGTH_LONG).show()
                            }

                            override fun cancel() {

                            }
                        }).show()
                    }

                    "Open Project"->{
                        findNavController().navigate(R.id.nav_open_project)
                    }

                    "Import Project"->{
                        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "* / *" // Required for some reason
                            putExtra(
                                Intent.EXTRA_MIME_TYPES,
                                arrayOf("application/octet-stream")
                            )
                            importProjectActivityLauncher.launch(Intent.createChooser(this,"Select Project To Import"))
                        }
                    }

                    "Manage Project"->{
                        findNavController().navigate(R.id.nav_manage_projects)
                    }
                }
            }
        }
        projectOptionRecyclerView.apply {
            layoutManager = GridLayoutManager(this.context,3)
            addItemDecoration(GridSpacingItemDecoration(3, 10, true))
            adapter = projectOptionAdapter
        }

        recentProjectAdapter = RecentAdapter()

        recentProjectAdapter.addListener(object : RecentAdapter.OnItemClickListener{
            override fun onClick(item: RecentItem) {
                Intent(context,SimulationActivity::class.java).apply {
                    putExtra("options",ProjectOptions(File(item.path).name, item.title,item.description,item.path,item.lastModified,ProjectOptions.OPEN))
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(this)
                }
            }
            override fun onDelete(item: RecentItem, index:Int) {

            }
        })

        recentProjectRecyclerView.apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL,false)
            adapter = recentProjectAdapter
            if(recentProjectAdapter.isEmpty()){
                recentProjectAlert.visibility = View.VISIBLE
            }else {
                recentProjectAlert.visibility = View.GONE
            }
        }

        val sampleProjectAdapter = RecentAdapter().apply {
            shareEnabled = false
            dateEnabled = false
            fileLengthEnabled = false
            showPremiumIndicator = !isPremiumUser
            DataTransferObject().also{dto->
                addHeader("Beginner", R.drawable.beginner)
                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_CONNECTIONS.bin").also{
                    add(it.title,it.path, it.description, it.lastModified)
                }
                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_POWER ON & OFF.bin").also{
                    add(it.title,it.path, it.description, it.lastModified)
                }
                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_RANDOM.bin").also{
                    add(it.title,it.path, it.description, it.lastModified)
                }
                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_GROUPS.bin").also{
                    add(it.title,it.path, it.description, it.lastModified)
                }
                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_CHANNEL.bin").also{
                    add(it.title,it.path, it.description, it.lastModified)
                }

                addHeader("Intermediate",R.drawable.intermediate)
                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_LOGIC GATES.bin").also{
                    add(it.title,it.path, it.description, it.lastModified)
                }
                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_Counter.bin").also{
                    add(it.title,it.path, it.description, it.lastModified)
                }
                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_ADDER.bin").also{
                    add(it.title,it.path, it.description, it.lastModified)
                }
                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_Shift Register.bin").also{
                    add(it.title,it.path, it.description, it.lastModified)
                }
                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_BCD DISPLAY (0-9).bin").also{
                    add(it.title,it.path, it.description, it.lastModified)
                }
                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_2x1Multiplexer .bin").also{
                    add(it.title,it.path, it.description, it.lastModified)
                }
                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_4 x 1 Multiplexer .bin").also{
                    add(it.title,it.path, it.description, it.lastModified)
                }
                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_4x1 Multiplexer .bin").also{
                    add(it.title,it.path, it.description, it.lastModified)
                }
                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_8 x 1 Multiplexer .bin").also{
                    add(it.title,it.path, it.description, it.lastModified)
                }
                addHeader("Advanced", R.drawable.advanced,)

                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_2-Bit Counter.bin").also{
                    add(it.title,it.path, it.description, it.lastModified, ispremium = true)
                }
                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_2-Bit Counter D.bin").also{
                    add(it.title,it.path, it.description, it.lastModified, ispremium = true)
                }
                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_3-Bit Counter.bin").also{
                    add(it.title,it.path, it.description, it.lastModified, ispremium = true)
                }
                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_3-Bit Counter .bin").also{
                    add(it.title,it.path, it.description, it.lastModified, ispremium = true)
                }
                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_4-Bit Counter .bin").also{
                    add(it.title,it.path, it.description, it.lastModified, ispremium = true)
                }
                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_4 Bit Program Counter.bin").also{
                    add(it.title,it.path, it.description, it.lastModified, ispremium = true)
                }
                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_4 Bit Program Counter 2.bin").also{
                    add(it.title,it.path, it.description, it.lastModified, ispremium = true)
                }
                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_8 Bit Program Counter.bin").also{
                    add(it.title,it.path, it.description, it.lastModified, ispremium = true)
                }
                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_8 Bit Program Counter 2.bin").also{
                    add(it.title,it.path, it.description, it.lastModified, ispremium = true)
                }
                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_8 Bit Program Counter 3.bin").also{
                    add(it.title,it.path, it.description, it.lastModified, ispremium = true)
                }
                dto.getSampleProjectDetails(requireContext(),"sample_projects/simulogic_examples_16 Bit Program Counter.bin").also{
                    add(it.title,it.path, it.description, it.lastModified, ispremium = true)
                }
            }
            /*DataTransferObject().listSampleProjects(requireContext()).onEach {
                add(it.title,it.path, it.description, it.lastModified)
                println(it.path)
            }*/
        }

        sampleProjectAdapter.addListener(object : RecentAdapter.OnItemClickListener{
            override fun onClick(item: RecentItem) {
                if(item.ispremium && !isPremiumUser){
                    // open payment activity
                    Intent(this@HomeFragment.activity, PremiumPurchaseActivity::class.java).also{ intent->
                        startActivity(intent)
                    }
                    return
                }
                DataTransferObject().fetchSampleProject(requireContext(),
                    ProjectOptions(File(item.path).name,item.title, item.description,
                        item.path, item.lastModified, ProjectOptions.OPEN)).also {projectOptions ->
                        Intent(context,SimulationActivity::class.java).apply {
                            putExtra("options",projectOptions)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(this)
                        }
                    }

            }
            override fun onDelete(item: RecentItem, index:Int) {

            }
        })
        sampleProjectRecyclerView.apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL,false)
            adapter = sampleProjectAdapter
        }

        return root
    }


    override fun onResume() {
        super.onResume()
        recentProjectAdapter.apply {
            clear()
            DataTransferObject().listProjects(requireContext()).also { data->
                data.subList(0, minOf(data.size, MAX_RECENT_ITEMS)).onEach {
                    add(it.title,it.path, it.description, it.lastModified)
                }
            }
            if(recentProjectAdapter.isEmpty()){
                recentProjectAlert.visibility = View.VISIBLE
            }else {
                recentProjectAlert.visibility = View.GONE
            }
            notifyItemRangeChanged(0, recentProjectAdapter.itemCount)
        }
    }
}

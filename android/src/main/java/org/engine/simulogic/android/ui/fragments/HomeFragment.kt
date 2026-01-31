package org.engine.simulogic.android.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
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
import org.engine.simulogic.R
import org.engine.simulogic.android.SimulationActivity
import org.engine.simulogic.android.circuits.storage.DataTransferObject
import org.engine.simulogic.android.circuits.storage.ProjectOptions
import org.engine.simulogic.android.ui.adapters.ProjectOptionsAdapter
import org.engine.simulogic.android.ui.adapters.RecentAdapter
import org.engine.simulogic.android.ui.models.ProjectOption
import org.engine.simulogic.android.ui.models.RecentItem
import org.engine.simulogic.android.views.GridSpacingItemDecoration
import org.engine.simulogic.android.views.dialogs.CreateProjectDialog
import java.io.File

class HomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_home_launcher, container, false)
        val projectOptionRecyclerView = root.findViewById<RecyclerView>(R.id.project_operations)
        val recentProjectRecyclerView = root.findViewById<RecyclerView>(R.id.recent_projects)
        val sampleProjectRecyclerView = root.findViewById<RecyclerView>(R.id.sample_projects)
        val recentProjectAlert = root.findViewById<TextView>(R.id.recent_project_alert)
        val projectOptionAdapter = ProjectOptionsAdapter().apply {
            add("Create Project", R.drawable.new_project)
            add("Open Project",R.drawable.open_project)
            add("Import Project", R.drawable.import_project)
            add("Manage Project", R.drawable.manage_projects)
        }
        val importProjectActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result->
            if(result.resultCode == Activity.RESULT_OK && result.data != null){

                result.data?.data?.apply {
                    val name: String = File(this.path).name
                    val outDir = File(requireContext().
                    getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),"SimuLogic")
                  //  FileUtility.importProject(context,this, outDir, name)
                }
                //copy content to working folder
                Toast.makeText(context,"Results returned!", Toast.LENGTH_SHORT).show()
            }

        }
        projectOptionAdapter.listener = object : ProjectOptionsAdapter.OnOptionClickListener{
            override fun onClick(option: ProjectOption) {
                when(option.title){
                    "Create Project"->{
                        CreateProjectDialog(context!!,object:CreateProjectDialog.OnCreateProjectClickListener{
                            override fun success(title:String, description:String) {
                                Intent(context,SimulationActivity::class.java).apply {
                                    putExtra("options",ProjectOptions(title,description,"",0L,ProjectOptions.CREATE))
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
                       // findNavController().navigate(R.id.nav_open_project)
                    }

                    "Import Project"->{
                        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "* / *" // Required for some reason
                            putExtra(
                                Intent.EXTRA_MIME_TYPES,
                                arrayOf("application/x-zip-compressed", "application/octet-stream", "application/zip")
                            )
                            importProjectActivityLauncher.launch(Intent.createChooser(this,"Select Project To Import"))
                        }
                    }

                    "Manage Project"->{
                        findNavController().navigate(R.id.nav_slideshow)
                    }
                }
            }
        }
        projectOptionRecyclerView.apply {
            layoutManager = GridLayoutManager(this.context,3)
            addItemDecoration(GridSpacingItemDecoration(3, 10, true))
            adapter = projectOptionAdapter
        }

        val recentProjectAdapter = RecentAdapter().apply {
          DataTransferObject().listProjects(requireContext()).forEach {
                add(it.title,it.description,it.path, it.lastModified)
            }
        }

        recentProjectAdapter.addListener(object : RecentAdapter.OnItemClickListener{
            override fun onClick(item: RecentItem) {
                Intent(context,SimulationActivity::class.java).apply {
                    putExtra("options",ProjectOptions(item.title,item.description,item.path,item.lastModified,ProjectOptions.OPEN))
                    startActivity(this)
                }
            }
        })

        recentProjectRecyclerView.apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL,false)
            adapter = recentProjectAdapter
            recentProjectAlert.visibility = View.GONE
        }

        val sampleProjectAdapter = RecentAdapter().apply {
            add("Computation","","",0,RecentItem.VIEW_HEADER,ispremium = true)
            add("4bitAdder","system/io/files","This is an implementation of a 4bit adder",0L)
            add("8bitSubtractor","system/io/files", "This is an implementation of a 8bit Subtractor",0L)
            add("Multiplexing","","", 0L,RecentItem.VIEW_HEADER)
            add("Multiplexer","system/io/files", "This is an implementation of a multiplexer",0L)
        }
        sampleProjectRecyclerView.apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL,false)
            adapter = sampleProjectAdapter
        }

        return root
    }

}

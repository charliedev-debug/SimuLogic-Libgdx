package org.engine.simulogic.android.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.engine.simulogic.R
import org.engine.simulogic.android.SimulationActivity
import org.engine.simulogic.android.circuits.storage.DataTransferObject
import org.engine.simulogic.android.circuits.storage.ProjectOptions
import org.engine.simulogic.android.ui.adapters.RecentAdapter
import org.engine.simulogic.android.ui.models.RecentItem
import java.io.File

class OpenProjectFragment : Fragment() {

  @SuppressLint("SetTextI18n")
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {

      val root = inflater.inflate(R.layout.fragment_open_project, container, false)
      val projectListRecyclerView = root.findViewById<RecyclerView>(R.id.project_list)
      val emptyProjectListAlert = root.findViewById<TextView>(R.id.empty_project_list_alert)
      val projectItemCount = root.findViewById<TextView>(R.id.project_count)
      val projectListAdapter = RecentAdapter().apply {

          DataTransferObject().listProjects(requireContext()).forEach {
              add(it.title,it.path, it.description, it.lastModified)
          }
      }

      projectListAdapter.addListener(object : RecentAdapter.OnItemClickListener{
          override fun onClick(item: RecentItem) {
              Intent(context, SimulationActivity::class.java).apply {
                  putExtra("options",
                      ProjectOptions(File(item.path).name, item.title,item.description,item.path,item.lastModified,
                          ProjectOptions.OPEN)
                  )
                  startActivity(this)
              }
          }

          override fun onDelete(item: RecentItem, index:Int) {
              //unused
          }
      })

      projectListRecyclerView.apply {
          layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL,false)
          adapter = projectListAdapter
          emptyProjectListAlert.visibility = View.GONE
          projectItemCount.text = "Projects(${projectListAdapter.itemCount})"
      }
    return root
  }

}

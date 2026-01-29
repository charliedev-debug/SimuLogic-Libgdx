package org.engine.simulogic.android.ui.slideshow

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.engine.simulogic.R
import org.engine.simulogic.android.ui.adapters.RecentAdapter

class SlideshowFragment : Fragment() {

  @SuppressLint("SetTextI18n")
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
      val root  = inflater.inflate(R.layout.fragment_manage_projects,container, false)
      val projectListRecyclerView = root.findViewById<RecyclerView>(R.id.project_list)
      val emptyProjectListAlert = root.findViewById<TextView>(R.id.empty_project_list_alert)
      val projectItemCount = root.findViewById<TextView>(R.id.project_count)
      val projectListAdapter = RecentAdapter().apply {
          add("Adder","system/io/files","This is an implementation of a 4bit adder", canDelete = true)
          add("Subtractor","system/io/files", "This is an implementation of a 8bit Subtractor", canDelete = true)
          add("Divider","system/io/files","This is an implementation of a 4bit Divider", canDelete = true)
          add("SegmentDisplay","system/io/files","This is a seven segment display using BCD", canDelete = true)
          add("Multiplier","system/io/files", "This is an implementation of a  16bit multiplier", canDelete = true)
          add("Multiplexer","system/io/files", "This is an implementation of a multiplexer", canDelete = true)
      }

      projectListRecyclerView.apply {
          layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL,false)
          adapter = projectListAdapter
          emptyProjectListAlert.visibility = View.GONE
          projectItemCount.text = "Projects(${projectListAdapter.itemCount})"
      }
    return root
  }

override fun onDestroyView() {
        super.onDestroyView()
    }
}

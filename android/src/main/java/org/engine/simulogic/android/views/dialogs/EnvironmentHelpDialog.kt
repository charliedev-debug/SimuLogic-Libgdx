package org.engine.simulogic.android.views.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.textview.MaterialTextView
import com.technifysoft.viewpager2_transformers.ViewPagerCubeInDepthTransformer
import com.technifysoft.viewpager2_transformers.ViewPagerZoomOutSlideTransformer
import org.engine.simulogic.R
import org.engine.simulogic.android.ui.adapters.EnvironmentHelpViewPagerAdapter

class EnvironmentHelpDialog(context:Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.environment_help_dialog_layout)
        val next = findViewById<AppCompatImageButton>(R.id.next)
        val prev = findViewById<AppCompatImageButton>(R.id.prev)
        val currentPageView = findViewById<MaterialTextView>(R.id.currentPage)
        val groupEnvironmentTutorials = findViewById<RadioGroup>(R.id.groupEnvironmentTutorial)
        findViewById<AppCompatImageButton>(R.id.close).apply {
            setOnClickListener {
                dismiss()
            }
        }
        val pager = findViewById<ViewPager2>(R.id.pager).apply {
            val environmentHelpViewPagerAdapter = EnvironmentHelpViewPagerAdapter().also {
                adapter = it
            }

            setPageTransformer(ViewPagerCubeInDepthTransformer())

            groupEnvironmentTutorials.setOnCheckedChangeListener { _, id ->
                when(id){
                    R.id.modes->{
                        environmentHelpViewPagerAdapter.currentPage = 0
                        environmentHelpViewPagerAdapter.updateItemsAll()
                    }
                    R.id.actions->{
                        environmentHelpViewPagerAdapter.currentPage = 1
                        environmentHelpViewPagerAdapter.updateItemsAll()
                    }
                    R.id.components->{
                        environmentHelpViewPagerAdapter.currentPage = 2
                        environmentHelpViewPagerAdapter.updateItemsAll()
                    }
                }
            }
            registerOnPageChangeCallback(object :OnPageChangeCallback(){
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    val max = environmentHelpViewPagerAdapter.itemCount
                    val textCurrentPage = "${position + 1} / $max"
                    next.visibility = if(position >= max - 1) View.INVISIBLE else View.VISIBLE
                    prev.visibility = if(position == 0) View.INVISIBLE else View.VISIBLE
                    currentPageView.text = textCurrentPage
                }
            })
        }
        next.setOnClickListener {
            val size = pager.adapter?.itemCount?:0
            val moveTo = pager.currentItem + 1
            if(size > moveTo) {
                pager.setCurrentItem(moveTo, true)
            }
        }
        prev.setOnClickListener {
            val moveTo = pager.currentItem - 1
            if(moveTo >= 0){
                pager.setCurrentItem(moveTo, true)
            }
        }

    }

    override fun onStart() {
        super.onStart()
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawableResource(R.color.transparent)
    }
}

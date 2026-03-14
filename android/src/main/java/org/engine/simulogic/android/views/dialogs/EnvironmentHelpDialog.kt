package org.engine.simulogic.android.views.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.size
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import org.engine.simulogic.R
import org.engine.simulogic.android.ui.adapters.EnvironmentHelpViewPagerAdapter

class EnvironmentHelpDialog(context:Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.environment_help_dialog_layout)
        val next = findViewById<AppCompatImageButton>(R.id.next)
        val prev = findViewById<AppCompatImageButton>(R.id.prev)
        val pager = findViewById<ViewPager2>(R.id.pager).apply {
            adapter = EnvironmentHelpViewPagerAdapter()

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
                    val max = adapter?.itemCount?:0
                    next.visibility = if(position >= max - 1) View.INVISIBLE else View.VISIBLE
                    prev.visibility = if(position == 0) View.INVISIBLE else View.VISIBLE
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

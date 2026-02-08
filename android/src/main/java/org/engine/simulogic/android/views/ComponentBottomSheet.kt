package org.engine.simulogic.android.views

import org.engine.simulogic.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.engine.simulogic.android.views.adapters.ComponentItem
import org.engine.simulogic.android.views.adapters.ComponentViewAdapter
import org.engine.simulogic.android.views.interfaces.IComponentAdapterListener


class ComponentBottomSheet(private val listener: IComponentAdapterListener? = null) : BottomSheetDialogFragment() {
    companion object {
         const val AND_COMPONENT = "AND"
         const val OR_COMPONENT = "OR"
         const val XOR_COMPONENT = "XOR"
         const val NOR_COMPONENT = "NOR"
         const val NOT_COMPONENT = "NOT"
         const val NAND_COMPONENT = "NAND"
         const val XNOR_COMPONENT = "XNOR"
         const val CLOCK_COMPONENT_1HZ = "1Hz"
         const val CLOCK_COMPONENT_5HZ = "5Hz"
         const val CLOCK_COMPONENT_10HZ = "10Hz"
         const val CLOCK_COMPONENT_20HZ = "20Hz"
         const val CLOCK_COMPONENT_30HZ = "30Hz"
         const val CLOCK_COMPONENT_40HZ = "40Hz"
         const val CLOCK_COMPONENT_60HZ = "60Hz"
         const val CLOCK_COMPONENT_CUSTOM = "CUSTOM"
         const val D_LATCH_COMPONENT = "D-LATCH"
         const val LED_COMPONENT = "LED"
         const val POWER_ON_COMPONENT = "POWER ON"
         const val POWER_OFF_COMPONENT = "POWER OFF"
         const val RANDOM_COMPONENT = "RANDOM"
         const val TEXT_COMPONENT = "TEXT"
         const val SS_DISPLAY_COMPONENT = "SS-DISPLAY"
         const val CUSTOM_COMPONENT = "CUSTOM"
         const val BCD_DISPLAY_COMPONENT = "BCD-DISPLAY"
         const val DATA_BUS_COMPONENT = "DATA-BUS"
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomBottomSheetDialogTheme)
        val mainView: View = inflater.inflate(R.layout.bottom_sheet_simulation, container, false)
        val gatesRecyclerview = mainView.findViewById<RecyclerView>(R.id.gates_component_list)
        val clockRecyclerview = mainView.findViewById<RecyclerView>(R.id.clock_component_list)
        val generalRecyclerview = mainView.findViewById<RecyclerView>(R.id.general_component_list)
        val templateRecyclerview = mainView.findViewById<RecyclerView>(R.id.template_component_list)
        val spanCount = 4
        val spacing = 10
        val includeEdge = false
        gatesRecyclerview.layoutManager = GridLayoutManager(this.context,spanCount)
        clockRecyclerview.layoutManager = GridLayoutManager(this.context,spanCount)
        generalRecyclerview.layoutManager = GridLayoutManager(this.context,spanCount)
        templateRecyclerview.layoutManager = GridLayoutManager(this.context,spanCount)
        val gatesAdapter = ComponentViewAdapter()
        gatesAdapter.insert(AND_COMPONENT, R.drawable.gate_and)
        gatesAdapter.insert(OR_COMPONENT, R.drawable.gate_or)
        gatesAdapter.insert(XOR_COMPONENT, R.drawable.gate_xor)
        gatesAdapter.insert(NOR_COMPONENT, R.drawable.gate_nor)
        gatesAdapter.insert(NOT_COMPONENT, R.drawable.gate_not)
        gatesAdapter.insert(NAND_COMPONENT, R.drawable.gate_nand)
        gatesAdapter.insert(XNOR_COMPONENT, R.drawable.gate_xnor)
        val clockAdapter = ComponentViewAdapter()
        clockAdapter.insert(CLOCK_COMPONENT_1HZ, R.drawable.clock_1hz)
        clockAdapter.insert(CLOCK_COMPONENT_5HZ, R.drawable.clock_5hz)
        clockAdapter.insert(CLOCK_COMPONENT_10HZ, R.drawable.clock_10hz)
        clockAdapter.insert(CLOCK_COMPONENT_20HZ, R.drawable.clock_20hz)
        clockAdapter.insert(CLOCK_COMPONENT_30HZ, R.drawable.clock_30hz)
        clockAdapter.insert(CLOCK_COMPONENT_40HZ, R.drawable.clock_40hz)
        clockAdapter.insert(CLOCK_COMPONENT_60HZ, R.drawable.clock_60hz)
        clockAdapter.insert(CLOCK_COMPONENT_CUSTOM, R.drawable.clock_custom)
        val generalAdapter = ComponentViewAdapter()
        generalAdapter.insert(D_LATCH_COMPONENT, R.drawable.d_latch)
        generalAdapter.insert(LED_COMPONENT, R.drawable.component_led)
        generalAdapter.insert(POWER_ON_COMPONENT, R.drawable.power_on)
        generalAdapter.insert(POWER_OFF_COMPONENT, R.drawable.power_off)
        generalAdapter.insert(RANDOM_COMPONENT, R.drawable.random)
        generalAdapter.insert(TEXT_COMPONENT, R.drawable.text)
        generalAdapter.insert(DATA_BUS_COMPONENT, R.drawable.data_bus)
        generalAdapter.insert(SS_DISPLAY_COMPONENT, R.drawable.ss_display)
        val templateAdapter = ComponentViewAdapter()
        templateAdapter.insert(CUSTOM_COMPONENT, R.drawable.template)
        templateAdapter.insert(BCD_DISPLAY_COMPONENT, R.drawable.bcd_display)
        // dismisses the dialog once a component item has been clicked
        val onDismissListener = object :IComponentAdapterListener{
            override fun onClickComponent(item: ComponentItem) {
                dismiss()
            }
        }
        listener?.also {
            gatesAdapter.addListener(listener)
            gatesAdapter.addListener(onDismissListener)
        }
        listener?.also {
            generalAdapter.addListener(listener)
            generalAdapter.addListener(onDismissListener)
        }
        listener?.also {
            clockAdapter.addListener(listener)
            clockAdapter.addListener(onDismissListener)
        }
        listener?.also {
            templateAdapter.addListener(listener)
            templateAdapter.addListener(onDismissListener)
        }

        generalRecyclerview.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
        clockRecyclerview.addItemDecoration(GridSpacingItemDecoration(spanCount,spacing, includeEdge))
        gatesRecyclerview.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
        templateRecyclerview.addItemDecoration(GridSpacingItemDecoration(spanCount,spacing, includeEdge))
        generalRecyclerview.adapter = generalAdapter
        clockRecyclerview.adapter = clockAdapter
        gatesRecyclerview.adapter = gatesAdapter
        templateRecyclerview.adapter = templateAdapter
        return mainView
    }
}

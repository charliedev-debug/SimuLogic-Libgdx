package org.engine.simulogic.android.views

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.engine.simulogic.android.PremiumPurchaseActivity
import org.engine.simulogic.android.SimulationLoop
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.storage.ProjectOptions
import org.engine.simulogic.android.circuits.storage.UserSettings
import org.engine.simulogic.android.events.MotionGestureListener
import org.engine.simulogic.android.helpers.ErrorLogs
import org.engine.simulogic.android.options.SimulationOptions
import org.engine.simulogic.android.views.dialogs.AutoSaveDialog
import org.engine.simulogic.android.views.dialogs.ChannelDialog
import org.engine.simulogic.android.views.dialogs.CustomBroadCastBusDialog
import org.engine.simulogic.android.views.dialogs.CustomClockDialog
import org.engine.simulogic.android.views.dialogs.CustomDataBusDialog
import org.engine.simulogic.android.views.dialogs.LabelAnchorDialog
import org.engine.simulogic.android.views.dialogs.LabelDialog
import org.engine.simulogic.android.views.dialogs.LoadingDialog
import org.engine.simulogic.android.views.dialogs.LogErrorDialog
import org.engine.simulogic.android.views.interfaces.IDialogLabelAnchorListener
import org.engine.simulogic.android.views.interfaces.IDialogLabelListener
import org.engine.simulogic.android.views.interfaces.ISimulationListener
import org.engine.simulogic.android.views.models.BottomSheetViewModel
import org.engine.simulogic.android.views.models.MenuViewModel

class SimulationFragment : AndroidFragmentApplication() {
    private lateinit var projectOptions: ProjectOptions
    private lateinit var simulationOptions: SimulationOptions
    private val menuViewModel: MenuViewModel by activityViewModels()
    private val bottomSheetViewModel: BottomSheetViewModel by activityViewModels()
    private val userSettings = UserSettings()
    lateinit var simulationLoop: SimulationLoop

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val configuration = AndroidApplicationConfiguration()
        configuration.useImmersiveMode = false // Recommended, but not required.
        configuration.useGL30 = true
        projectOptions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("projectOptions", ProjectOptions::class.java)!!
        }else{
            arguments?.getSerializable("projectOptions") as ProjectOptions
        }
        simulationOptions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("simulationOptions", SimulationOptions::class.java)!!
        }else{
            arguments?.getSerializable("simulationOptions", ) as SimulationOptions
        }
        var isPremiumUser = true
        runBlocking {
            isPremiumUser = userSettings.getDataBoolean(requireContext(), UserSettings.PREMIUM_USER,false).first()
        }
        simulationLoop = SimulationLoop(projectOptions, simulationOptions, object : ISimulationListener {
                override fun onCreate() {
                    runOnUiThread {
                        // show a loading dialog
                        LoadingDialog(requireContext(), "Loading...",
                            object : LoadingDialog.IDialogLoadingListener {
                                override fun onLoad() {
                                    ErrorLogs.reset()
                                    simulationLoop.componentManager.loadProject()
                                }
                                override fun onCancelled() {
                                    Gdx.app.exit()
                                    requireActivity().finish()
                                }

                                override fun onFinished() {
                                    simulationLoop.isReady = true
                                    if(ErrorLogs.isNotEmpty()){
                                        LogErrorDialog(requireContext(), ErrorLogs.last()).show()
                                    }
                                }
                            }).show()
                    }
                }
            })

        menuViewModel.message.observe(viewLifecycleOwner) { item ->
            if(item.isPremium && !isPremiumUser){
                // open payment activity
                Intent(this.activity, PremiumPurchaseActivity::class.java).also{ intent->
                    startActivity(intent)
                }
                return@observe
            }
            if (simulationLoop.isReady) {
                when (item.id) {
                    "Origin" -> {
                        simulationLoop.gestureListener.origin()
                    }

                    "Touch" -> {
                        simulationLoop.componentManager.setMode(MotionGestureListener.TOUCH_MODE)
                    }

                    "Interact" -> {
                        simulationLoop.componentManager.setMode(MotionGestureListener.INTERACT_MODE)
                    }

                    "Sel-Touch" -> {
                        simulationLoop.componentManager.setMode(MotionGestureListener.SELECTION_MODE)
                    }

                    "Sel-Range" -> {
                        simulationLoop.componentManager.setMode(MotionGestureListener.RANGED_SELECTION_MODE)
                    }

                    "Connect2" -> {
                        CDefaults.setLinePointCount(1, 1)
                        simulationLoop.componentManager.setMode(MotionGestureListener.CONNECTION_MODE)
                    }

                    "Connect4" -> {
                        CDefaults.setLinePointCount(2, 2)
                        simulationLoop.componentManager.setMode(MotionGestureListener.CONNECTION_MODE)
                    }

                    "Connect6" -> {
                        CDefaults.setLinePointCount(3, 3)
                        simulationLoop.componentManager.setMode(MotionGestureListener.CONNECTION_MODE)
                    }

                    "Rotate" -> {
                        simulationLoop.componentManager.rotateRight()
                    }

                    "A-Label"->{
                        LabelAnchorDialog(requireContext(), object : IDialogLabelAnchorListener {
                            override fun onCompleted(text: String, fontSize:Int,alignment:Int) {
                                simulationLoop.componentManager.insertCAnchorLabel(text,fontSize,alignment)
                            }

                            override fun onCancelled() {

                            }

                        }).show()
                    }

                    "Group" ->{
                        simulationLoop.componentManager.insertGroup()
                    }

                    "UnGroup" ->{
                        simulationLoop.componentManager.removeGroup()
                    }

                    "Redo" -> {
                        simulationLoop.componentManager.redo()
                    }

                    "Undo" -> {
                        simulationLoop.componentManager.undo()
                    }

                    "Cut" -> {
                        simulationLoop.componentManager.cut()
                    }

                    "Paste" -> {
                        simulationLoop.componentManager.paste()
                    }

                    "Copy" -> {
                        simulationLoop.componentManager.copy()
                    }

                    "Delete" -> {
                        simulationLoop.componentManager.delete()
                    }

                    "Save" -> {
                        AutoSaveDialog(
                            requireContext(),
                            "SAVING DATA",
                            object : AutoSaveDialog.IDialogLoadingListener {
                                override fun onCancelled() {
                                    //unused
                                }
                            }).show()
                        //  simulationLoop.componentManager.saveProject()
                    }
                }
            }
        }

        bottomSheetViewModel.message.observe(viewLifecycleOwner) { item ->

            if(item.isPremium && !isPremiumUser){
                // open payment activity
                Intent(this.activity, PremiumPurchaseActivity::class.java).also{ intent->
                    startActivity(intent)
                }
                return@observe
            }
            when (item.title) {
                ComponentBottomSheet.AND_COMPONENT -> {
                    simulationLoop.componentManager.insertAND()
                }

                ComponentBottomSheet.OR_COMPONENT -> {
                    simulationLoop.componentManager.insertOR()
                }

                ComponentBottomSheet.NAND_COMPONENT -> {
                    simulationLoop.componentManager.insertNAND()
                }

                ComponentBottomSheet.XOR_COMPONENT -> {
                    simulationLoop.componentManager.insertXOR()
                }

                ComponentBottomSheet.XNOR_COMPONENT -> {
                    simulationLoop.componentManager.insertXNOR()
                }

                ComponentBottomSheet.NOR_COMPONENT -> {
                    simulationLoop.componentManager.insertNOR()
                }

                ComponentBottomSheet.NOT_COMPONENT -> {
                    simulationLoop.componentManager.insertNOT()
                }

                ComponentBottomSheet.AND_THREE_INPUT_COMPONENT->{
                    simulationLoop.componentManager.insertANDThreeInput()
                }

                ComponentBottomSheet.NAND_THREE_INPUT_COMPONENT->{
                    simulationLoop.componentManager.insertNandThreeInput()
                }

                ComponentBottomSheet.OR_THREE_INPUT_COMPONENT->{
                    simulationLoop.componentManager.insertOrThreeInput()
                }

                ComponentBottomSheet.NOR_THREE_INPUT_COMPONENT->{
                    simulationLoop.componentManager.insertNorThreeInput()
                }

                ComponentBottomSheet.CLOCK_COMPONENT_1HZ -> {
                    simulationLoop.componentManager.insertCClock(1f)
                }

                ComponentBottomSheet.CLOCK_COMPONENT_5HZ -> {
                    simulationLoop.componentManager.insertCClock(1f / 5f)
                }

                ComponentBottomSheet.CLOCK_COMPONENT_10HZ -> {
                    simulationLoop.componentManager.insertCClock(1f / 10f)
                }

                ComponentBottomSheet.CLOCK_COMPONENT_20HZ -> {
                    simulationLoop.componentManager.insertCClock(1f / 20f)
                }

                ComponentBottomSheet.CLOCK_COMPONENT_30HZ -> {
                    simulationLoop.componentManager.insertCClock(1f / 30f)
                }

                ComponentBottomSheet.CLOCK_COMPONENT_40HZ -> {
                    simulationLoop.componentManager.insertCClock(1f / 40f)
                }

                ComponentBottomSheet.CLOCK_COMPONENT_60HZ -> {
                    simulationLoop.componentManager.insertCClock(1f / 60f)
                }

                ComponentBottomSheet.CLOCK_COMPONENT_CUSTOM -> {
                    CustomClockDialog(
                        requireContext(),
                        projectOptions,
                        object : CustomClockDialog.OnEditProjectClickListener {
                            override fun success(freq: Float) {
                                simulationLoop.componentManager.insertCClock(freq)
                            }

                            override fun failure(msg: String) {
                                //unused
                            }

                            override fun cancel() {
                                //ignore
                            }

                        }).show()

                }

                ComponentBottomSheet.D_LATCH_COMPONENT -> {
                    simulationLoop.componentManager.insertCLatch()
                }

                ComponentBottomSheet.D_FLIP_FLOP_COMPONENT -> {
                    simulationLoop.componentManager.insertCDFlipFlop()
                }

                ComponentBottomSheet.SR_LATCH_COMPONENT -> {
                    simulationLoop.componentManager.insertCSRLatch()
                }

                ComponentBottomSheet.JK_FLIP_FLOP_COMPONENT -> {
                    simulationLoop.componentManager.insertCJKFlipFlop()
                }

                ComponentBottomSheet.T_FLIP_FLOP_COMPONENT -> {
                    simulationLoop.componentManager.insertCTFlipFlop()
                }

                ComponentBottomSheet.POINT_COMPONENT -> {
                    simulationLoop.componentManager.insertPoint()
                }

                ComponentBottomSheet.LED_COMPONENT -> {
                    simulationLoop.componentManager.insertCLed()
                }

                ComponentBottomSheet.POWER_ON_COMPONENT -> {
                    simulationLoop.componentManager.insertCPower(CNode.SIGNAL_ACTIVE)
                }

                ComponentBottomSheet.POWER_OFF_COMPONENT -> {
                    simulationLoop.componentManager.insertCPower(CNode.SIGNAL_INACTIVE)
                }

                ComponentBottomSheet.PULSE_BUTTON_COMPONENT ->{
                    simulationLoop.componentManager.insertPulseButton()
                }

                ComponentBottomSheet.RANDOM_COMPONENT -> {
                    simulationLoop.componentManager.insertCRandom()
                }

                ComponentBottomSheet.DATA_BUS_COMPONENT ->{
                    CustomDataBusDialog(requireContext(),object:CustomDataBusDialog.OnEditProjectClickListener{
                        override fun success(size: Int) {
                            simulationLoop.componentManager.insertCDataBus(size)
                        }
                        override fun failure(msg: String) {}
                        override fun cancel() {}
                    }).show()
                }

                ComponentBottomSheet.DATA_BUS_FAN_OUT_COMPONENT ->{
                    CustomBroadCastBusDialog(requireContext(),object :CustomBroadCastBusDialog.OnCreateBusListener{
                        override fun success(inputs: Int, segments: Int) {
                            simulationLoop.componentManager.insertCFanOutBus(inputs,segments)
                        }

                        override fun failure(msg: String) {

                        }

                        override fun cancel() {

                        }

                    }).show()
                }

                ComponentBottomSheet.CHANNEL_COMPONENT ->{
                    ChannelDialog(requireContext(), object :ChannelDialog.OnChannelListener{
                        override fun success(id: String, type: Int) {
                            simulationLoop.componentManager.insertChannel(id, type)
                        }

                        override fun failure(msg: String) {

                        }

                        override fun cancel() {

                        }

                    }).show()
                }

                ComponentBottomSheet.SS_DISPLAY_COMPONENT -> {
                    simulationLoop.componentManager.insertSevenSegmentDisplay()
                }

                ComponentBottomSheet.BCD_DISPLAY_COMPONENT->{
                    simulationLoop.componentManager.insertBCDDisplay()
                }

                ComponentBottomSheet.TEXT_COMPONENT -> {
                    LabelDialog(requireContext(), object : IDialogLabelListener {
                        override fun onCompleted(text: String, fontSize:Int) {
                            simulationLoop.componentManager.insertCLabel(text,fontSize)
                        }

                        override fun onCancelled() {

                        }

                    }).show()
                }
            }
        }
        return initializeForView(simulationLoop, configuration)
    }
    override fun onPause() {
        super.onPause()
    }
}

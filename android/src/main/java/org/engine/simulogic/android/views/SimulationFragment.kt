package org.engine.simulogic.android.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import org.engine.simulogic.android.SimulationLoop
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.storage.ProjectOptions
import org.engine.simulogic.android.events.MotionGestureListener
import org.engine.simulogic.android.options.SimulationOptions
import org.engine.simulogic.android.views.dialogs.AutoSaveDialog
import org.engine.simulogic.android.views.dialogs.CustomClockDialog
import org.engine.simulogic.android.views.dialogs.LabelDialog
import org.engine.simulogic.android.views.dialogs.LoadingDialog
import org.engine.simulogic.android.views.interfaces.IDialogLabelListener
import org.engine.simulogic.android.views.interfaces.ISimulationListener
import org.engine.simulogic.android.views.models.BottomSheetViewModel
import org.engine.simulogic.android.views.models.MenuViewModel

class SimulationFragment(
    private val projectOptions: ProjectOptions,
    private val simulationOptions: SimulationOptions
) : AndroidFragmentApplication() {
    private val menuViewModel: MenuViewModel by activityViewModels()
    private val bottomSheetViewModel: BottomSheetViewModel by activityViewModels()
    lateinit var simulationLoop: SimulationLoop
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val configuration = AndroidApplicationConfiguration()
        configuration.useImmersiveMode = true // Recommended, but not required.
        configuration.useGL30 = true
        simulationLoop = SimulationLoop(projectOptions, simulationOptions, object : ISimulationListener {
                override fun onCreate() {
                    runOnUiThread {
                        // show a loading dialog
                        LoadingDialog(requireContext(), "Loading...",
                            object : LoadingDialog.IDialogLoadingListener {
                                override fun onLoad() {
                                    simulationLoop.componentManager.loadProject()

                                }
                                override fun onCancelled() {
                                    Gdx.app.exit()
                                    requireActivity().finish()
                                }
                            }).show()
                    }
                }
            })

        menuViewModel.message.observe(viewLifecycleOwner) { item ->
            if (simulationLoop.isReady) {
                when (item.id) {
                    "Origin" -> {
                        simulationLoop.gestureListener.origin()
                    }

                    "Touch" -> {
                        simulationLoop.gestureListener.setMode(MotionGestureListener.TOUCH_MODE)
                    }

                    "Interact" -> {
                        simulationLoop.gestureListener.setMode(MotionGestureListener.INTERACT_MODE)
                    }

                    "Sel-Touch" -> {
                        simulationLoop.gestureListener.setMode(MotionGestureListener.SELECTION_MODE)
                    }

                    "Sel-Range" -> {
                        simulationLoop.gestureListener.setMode(MotionGestureListener.RANGED_SELECTION_MODE)
                    }

                    "Connect2" -> {
                        CDefaults.setLinePointCount(1, 1)
                        simulationLoop.gestureListener.setMode(MotionGestureListener.CONNECTION_MODE)
                    }

                    "Connect4" -> {
                        CDefaults.setLinePointCount(2, 2)
                        simulationLoop.gestureListener.setMode(MotionGestureListener.CONNECTION_MODE)
                    }

                    "Connect6" -> {
                        CDefaults.setLinePointCount(3, 3)
                        simulationLoop.gestureListener.setMode(MotionGestureListener.CONNECTION_MODE)
                    }

                    "Rotate" -> {
                        simulationLoop.gestureListener.rotateRight()
                    }

                    "Redo" -> {
                        simulationLoop.gestureListener.redo()
                    }

                    "Undo" -> {
                        simulationLoop.gestureListener.undo()
                    }

                    "Cut" -> {
                        simulationLoop.gestureListener.cut()
                    }

                    "Paste" -> {
                        simulationLoop.gestureListener.paste()
                    }

                    "Copy" -> {
                        simulationLoop.gestureListener.copy()
                    }

                    "Delete" -> {
                        simulationLoop.gestureListener.delete()
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
                                simulationLoop.componentManager.insertCClock(1f / 60f)
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

                ComponentBottomSheet.LED_COMPONENT -> {
                    simulationLoop.componentManager.insertCLed()
                }

                ComponentBottomSheet.POWER_ON_COMPONENT -> {
                    simulationLoop.componentManager.insertCPower(CNode.SIGNAL_ACTIVE)
                }

                ComponentBottomSheet.POWER_OFF_COMPONENT -> {
                    simulationLoop.componentManager.insertCPower(CNode.SIGNAL_INACTIVE)
                }

                ComponentBottomSheet.RANDOM_COMPONENT -> {
                    simulationLoop.componentManager.insertCRandom()
                }

                ComponentBottomSheet.SS_DISPLAY_COMPONENT -> {
                    simulationLoop.componentManager.insertSevenSegmentDisplay()
                }

                ComponentBottomSheet.TEXT_COMPONENT -> {
                    LabelDialog(requireContext(), object : IDialogLabelListener {
                        override fun onCompleted(text: String) {
                            simulationLoop.componentManager.insertCLabel(text)
                        }

                        override fun onCancelled() {

                        }

                    }).show()
                }
            }
        }




        return initializeForView(simulationLoop, configuration)
    }

}

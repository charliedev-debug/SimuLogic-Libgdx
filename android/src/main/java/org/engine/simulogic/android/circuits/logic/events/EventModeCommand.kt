package org.engine.simulogic.android.circuits.logic.events

import org.engine.simulogic.android.circuits.tools.Command
import org.engine.simulogic.android.events.MotionGestureListener
import org.engine.simulogic.android.events.MotionGestureListener.Companion.RANGED_SELECTION_MODE

class EventModeCommand(private val mode:Int, private val gestureListener: MotionGestureListener) : Command(){

    override fun execute() {
        gestureListener.setMode(mode)
    }
}

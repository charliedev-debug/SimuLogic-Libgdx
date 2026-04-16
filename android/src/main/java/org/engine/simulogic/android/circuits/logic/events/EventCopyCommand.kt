package org.engine.simulogic.android.circuits.logic.events
import org.engine.simulogic.android.circuits.tools.Command
import org.engine.simulogic.android.events.MotionGestureListener
class EventCopyCommand(private val gestureListener: MotionGestureListener): Command() {

    override fun execute() {
       gestureListener.copy()
    }
}

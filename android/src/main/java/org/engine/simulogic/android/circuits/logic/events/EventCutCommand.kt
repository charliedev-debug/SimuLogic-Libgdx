package org.engine.simulogic.android.circuits.logic.events
import org.engine.simulogic.android.circuits.tools.Command
import org.engine.simulogic.android.events.MotionGestureListener
class EventCutCommand(private val gestureListener: MotionGestureListener): Command() {

    override fun execute() {
       gestureListener.cut()
    }
}

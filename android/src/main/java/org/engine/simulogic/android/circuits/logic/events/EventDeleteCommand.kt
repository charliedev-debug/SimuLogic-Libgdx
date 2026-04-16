package org.engine.simulogic.android.circuits.logic.events
import org.engine.simulogic.android.circuits.tools.Command
import org.engine.simulogic.android.events.MotionGestureListener

class EventDeleteCommand(private val gestureListener: MotionGestureListener): Command() {

    override fun execute() {
       gestureListener.delete()
    }
}

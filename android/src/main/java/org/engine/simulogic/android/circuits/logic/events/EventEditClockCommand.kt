package org.engine.simulogic.android.circuits.logic.events
import org.engine.simulogic.android.circuits.components.generators.CClock
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.circuits.tools.Command
import org.engine.simulogic.android.circuits.tools.EditClockCommand
import org.engine.simulogic.android.events.MotionGestureListener

class EventEditClockCommand (private val freq: Float,
                            private val gestureListener: MotionGestureListener) : Command(){

    override fun execute() {
        gestureListener.collisionDetector.also{collisionDetector ->
            collisionDetector.selectedItems.forEach { item ->
                if(item.subject is CClock){
                    gestureListener.commandHistory.execute(EditClockCommand(ListNode(item.subject),item.subject.freq,freq))
                    item.subject.setFrequency(freq)
                }
            }
            collisionDetector.reset()
        }

    }
}

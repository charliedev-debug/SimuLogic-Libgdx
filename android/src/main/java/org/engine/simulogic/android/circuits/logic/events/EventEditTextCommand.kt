package org.engine.simulogic.android.circuits.logic.events
import org.engine.simulogic.android.circuits.components.other.CLabel
import org.engine.simulogic.android.circuits.tools.Command
import org.engine.simulogic.android.events.MotionGestureListener

class EventEditTextCommand (private val text: String,
                            private val fontSize: Float,
                            private val gestureListener: MotionGestureListener) : Command(){

    override fun execute() {

        gestureListener.collisionDetector.also{collisionDetector ->
            collisionDetector.selectedItems.forEach { item ->
                if(item.subject is CLabel){
                    item.subject.text = text
                    item.subject.fontSize = fontSize
                }
            }
            collisionDetector.reset()
        }
       // TODO: add this to command history
    }
}

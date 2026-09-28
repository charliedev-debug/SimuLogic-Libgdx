package org.engine.simulogic.android.circuits.logic.events

import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.lines.LineMarker
import org.engine.simulogic.android.circuits.components.other.CRangeLine
import org.engine.simulogic.android.circuits.tools.Command
import org.engine.simulogic.android.events.MotionGestureListener

class EventSplitConnectionCommand (private val gestureListener: MotionGestureListener) : Command() {

    override fun execute() {
        gestureListener.collisionDetector.selectedItems.forEach { item ->
            if(item.subject is CSignal && item.subject.parent is LineMarker){
                (item.subject.parent as LineMarker).also { lineMarker ->
                    lineMarker.splitNode(item.subject.signalIndex)
                }
            }else if(item.subject is CRangeLine){

            }else{
                println("range line selected ${item.subject}")
            }
        }
    }
}

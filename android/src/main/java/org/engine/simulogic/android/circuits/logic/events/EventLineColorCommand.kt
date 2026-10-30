package org.engine.simulogic.android.circuits.logic.events

import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.lines.LineMarker
import org.engine.simulogic.android.circuits.components.other.CRangeLine
import org.engine.simulogic.android.circuits.components.other.Pallete
import org.engine.simulogic.android.circuits.tools.Command
import org.engine.simulogic.android.circuits.tools.LineColorCommand
import org.engine.simulogic.android.circuits.tools.LineColorCommandList
import org.engine.simulogic.android.events.MotionGestureListener

class EventLineColorCommand(private val gestureListener: MotionGestureListener,
                            private val colorOn: Pallete?,
                            private val colorOff: Pallete? ) : Command() {

    override fun execute() {
        // this lineCommandList class prevents us from filling the undo/redo buffer
        val lineColorCommandList = LineColorCommandList()
        gestureListener.collisionDetector.selectedItems.onEach {
            if(it.subject is CSignal && it.subject.parent is LineMarker){
                (it.subject.parent as LineMarker).also { marker ->
                    marker.getNodeOriginFrom(marker.from).from.getLineMarkerChildren().onEach {childMarker->
                        if(childMarker.markerActive && childMarker != marker){
                            lineColorCommandList.insert(LineColorCommand(childMarker,
                                childMarker.colorSignalOnPallete,
                                childMarker.colorSignalOffPallete,colorOn,colorOff))
                            childMarker.colorSignalOnPallete = colorOn
                            childMarker.colorSignalOffPallete = colorOff
                        }
                    }
                    lineColorCommandList.insert(LineColorCommand(marker,
                        marker.colorSignalOnPallete,
                        marker.colorSignalOffPallete,colorOn,colorOff))
                    marker.colorSignalOnPallete = colorOn
                    marker.colorSignalOffPallete = colorOff
                }

            }else if(it.subject is CRangeLine){
                it.subject.parentLine.also{marker ->
                    marker.getNodeOriginFrom(marker.from).from.getLineMarkerChildren().onEach {childMarker->
                        if(childMarker.markerActive && childMarker != marker){
                            lineColorCommandList.insert(LineColorCommand(childMarker,
                                childMarker.colorSignalOnPallete,
                                childMarker.colorSignalOffPallete,colorOn,colorOff))
                            childMarker.colorSignalOnPallete = colorOn
                            childMarker.colorSignalOffPallete = colorOff
                        }
                    }
                    lineColorCommandList.insert(LineColorCommand(marker,
                        marker.colorSignalOnPallete,
                        marker.colorSignalOffPallete,colorOn,colorOff))
                    marker.colorSignalOnPallete = colorOn
                    marker.colorSignalOffPallete = colorOff
                }
            }
        }
        if(lineColorCommandList.isNotEmpty()) {
            gestureListener.commandHistory.execute(lineColorCommandList)
        }
        gestureListener.collisionDetector.reset()
    }
}


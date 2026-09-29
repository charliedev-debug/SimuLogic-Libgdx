package org.engine.simulogic.android.circuits.logic.events

import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.lines.LineMarker
import org.engine.simulogic.android.circuits.components.other.CRangeLine
import org.engine.simulogic.android.circuits.tools.Command
import org.engine.simulogic.android.circuits.tools.SplitNodeCommand
import org.engine.simulogic.android.events.MotionGestureListener

class EventSplitConnectionCommand (private val gestureListener: MotionGestureListener) : Command() {

    override fun execute() {
        val commands = mutableListOf<SplitNodeCommand>()
        gestureListener.collisionDetector.selectedItems.forEach { item ->
            when (item.subject) {
                is CSignal if item.subject.parent is LineMarker -> {
                    (item.subject.parent as LineMarker).also { lineMarker ->
                        SplitNodeCommand(lineMarker).also { command ->
                            command.fetchPrevious()
                            lineMarker.splitNode(item.subject.signalIndex)
                            command.fetchCurrent()
                            commands.add(command)
                        }
                    }
                }

                is CRangeLine -> {
                    item.subject.parentLine.also { lineMarker ->
                        if (item.subject.start is CSignal) {
                            SplitNodeCommand(lineMarker).also { command ->
                                command.fetchPrevious()
                                lineMarker.splitNode(item.subject.start.signalIndex)
                                command.fetchCurrent()
                                commands.add(command)
                            }
                        }
                    }
                }

                else -> {
                   // ignore
                }
            }
        }

        commands.onEach { command ->
            gestureListener.commandHistory.execute(command)
        }
    }
}

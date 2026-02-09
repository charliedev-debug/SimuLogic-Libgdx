package org.engine.simulogic.android.circuits.logic

import org.engine.simulogic.android.circuits.components.CTypes
import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.lines.LineMarker
import org.engine.simulogic.android.circuits.tools.CommandHistory
import org.engine.simulogic.android.circuits.tools.ConnectionCommand
import org.engine.simulogic.android.events.CollisionDetector
import org.engine.simulogic.android.events.MotionGestureListener
import org.engine.simulogic.android.scene.PlayGroundScene

class ConnectionManager(
    private val connection: Connection,
    private val collisionDetector: CollisionDetector,
    private val commandHistory: CommandHistory,
    private val scene: PlayGroundScene
) {
    private val validationMap = mutableMapOf<CTypes, List<CTypes>>()

    init {
        validationMap[CTypes.SIGNAL_IN] = listOf(CTypes.SIGNAL_OUT, CTypes.Q_SIGNAL_OUT)
        validationMap[CTypes.SIGNAL_OUT] =
            listOf(CTypes.SIGNAL_IN, CTypes.D_SIGNAL_IN, CTypes.E_SIGNAL_IN)
        validationMap[CTypes.E_SIGNAL_IN] = listOf(CTypes.SIGNAL_OUT, CTypes.Q_SIGNAL_OUT)
        validationMap[CTypes.D_SIGNAL_IN] = listOf(CTypes.SIGNAL_OUT, CTypes.Q_SIGNAL_OUT)
        validationMap[CTypes.Q_SIGNAL_OUT] =
            listOf(CTypes.SIGNAL_IN, CTypes.D_SIGNAL_IN, CTypes.E_SIGNAL_IN)
    }

    fun resolveConnection() {
        if (collisionDetector.mode == MotionGestureListener.CONNECTION_MODE && collisionDetector.size() >= 2) {
            val a = collisionDetector[0]
            val b = collisionDetector[1]
            // only signals can make connections
            if (!(a.subject is CSignal && b.subject is CSignal)){
                collisionDetector.reset()
                return
            }

            // ignore lineMarker signals
            if (a.subject.parent is LineMarker || b.subject.parent is LineMarker){
                collisionDetector.reset()
                return
            }

            if (validationMap[a.subject.type]?.contains(b.subject.type) == false){
                collisionDetector.reset()
                return
            }

            if (a.subject.type == CTypes.SIGNAL_OUT || a.subject.type == CTypes.Q_SIGNAL_OUT) {
                connection.insertConnection(
                    a.caller,
                    b.caller,
                    a.subject.signalIndex,
                    b.subject.signalIndex,
                    scene
                ).also { marker->
                    commandHistory.execute(ConnectionCommand(marker))
                }
            } else {
                connection.insertConnection(
                    b.caller,
                    a.caller,
                    b.subject.signalIndex,
                    a.subject.signalIndex,
                    scene
                ).also { marker->
                    commandHistory.execute(ConnectionCommand(marker))
                }
            }

            collisionDetector.selectedItems.forEach {
                it.subject.apply {
                    selected = false
                }
            }
            collisionDetector.reset()
        }
    }

}

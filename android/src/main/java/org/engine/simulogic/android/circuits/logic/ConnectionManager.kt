package org.engine.simulogic.android.circuits.logic

import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.lines.LineMarker
import org.engine.simulogic.android.events.CollisionDetector
import org.engine.simulogic.android.events.MotionGestureListener
import org.engine.simulogic.android.scene.PlayGroundScene

class ConnectionManager(private val connection: Connection,private val collisionDetector: CollisionDetector,private val scene: PlayGroundScene) {
    fun resolveConnection(){
        if(collisionDetector.mode == MotionGestureListener.CONNECTION_MODE && collisionDetector.size() >= 2){
            val a = collisionDetector[0]
            val b = collisionDetector[1]
            if(!(a.subject is CSignal && b.subject is CSignal)) return
            val signalA = a.subject
            val signalB = b.subject

           connection.insertConnection(a.caller, b.caller, signalA.signalIndex, signalB.signalIndex,scene)
            collisionDetector.selectedItems.forEach {
                it.subject.apply {
                    selected = false
                }
            }
            collisionDetector.reset()
        }
    }

}

package org.engine.simulogic.android.circuits.logic

import org.engine.simulogic.android.circuits.components.buttons.CPower
import org.engine.simulogic.android.circuits.components.gates.CAnd
import org.engine.simulogic.android.circuits.components.gates.CNand
import org.engine.simulogic.android.circuits.components.gates.CNor
import org.engine.simulogic.android.circuits.components.gates.CNot
import org.engine.simulogic.android.circuits.components.gates.COr
import org.engine.simulogic.android.circuits.components.gates.CXnor
import org.engine.simulogic.android.circuits.components.gates.CXor
import org.engine.simulogic.android.circuits.components.latches.CLatch
import org.engine.simulogic.android.circuits.components.visuals.CLed
import org.engine.simulogic.android.circuits.components.generators.CClock
import org.engine.simulogic.android.circuits.components.generators.CRandom
import org.engine.simulogic.android.circuits.components.visuals.CSevenSegmentDisplay
import org.engine.simulogic.android.events.MotionGestureListener
import org.engine.simulogic.android.scene.PlayGroundScene

class ComponentManager(private val connection:Connection, private  val scene: PlayGroundScene, private val gestureListener: MotionGestureListener) {

    fun insertAND(){
       gestureListener.rectPointer.getPosition().also {position->
            connection.insertNode(ListNode(CAnd(position.x,position.y,scene)))
        }
    }
    fun insertOR(){
        gestureListener.rectPointer.getPosition().also {position->
            connection.insertNode(ListNode(COr(position.x,position.y,scene)))
        }
    }
    fun insertXOR(){
        gestureListener.rectPointer.getPosition().also {position->
            connection.insertNode(ListNode(CXor(position.x,position.y,scene)))
        }
    }
    fun insertNOR(){
        gestureListener.rectPointer.getPosition().also {position->
            connection.insertNode(ListNode(CNor(position.x,position.y,scene)))
        }
    }
    fun insertNOT(){
        gestureListener.rectPointer.getPosition().also {position->
            connection.insertNode(ListNode(CNot(position.x,position.y,scene)))
        }
    }
    fun insertNAND(){
        gestureListener.rectPointer.getPosition().also {position->
            connection.insertNode(ListNode(CNand(position.x,position.y,scene)))
        }
    }
    fun insertXNOR(){
        gestureListener.rectPointer.getPosition().also {position->
            connection.insertNode(ListNode(CXnor(position.x,position.y,scene)))
        }
    }
    fun insertCClock(){
        gestureListener.rectPointer.getPosition().also {position->
            connection.insertNode(ListNode(CClock(position.x,position.y,scene)))
        }
    }
    fun insertCLatch(){
        gestureListener.rectPointer.getPosition().also {position->
            connection.insertNode(ListNode(CLatch(position.x,position.y,scene)))
        }
    }
    fun insertCLed(){
        gestureListener.rectPointer.getPosition().also {position->
            connection.insertNode(ListNode(CLed(position.x,position.y,scene)))
        }
    }
    fun insertCPower(){
        gestureListener.rectPointer.getPosition().also {position->
            connection.insertNode(ListNode(CPower(position.x,position.y,scene)))
        }
    }
    fun insertCRandom(){
        gestureListener.rectPointer.getPosition().also {position->
            connection.insertNode(ListNode(CRandom(position.x,position.y,scene)))
        }
    }
    fun insertSevenSegmentDisplay(){
        gestureListener.rectPointer.getPosition().also {position->
            connection.insertNode(ListNode(CSevenSegmentDisplay(position.x,position.y,scene)))
        }
    }
}

package org.engine.simulogic.android.events

import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.other.CRangePoint
import org.engine.simulogic.android.circuits.components.other.CRangeSelect
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.scene.Entity

class CollisionDetector(private val connection: Connection) {

    var selectedItems = mutableListOf<CollisionItem>()
    var mode = MotionGestureListener.TOUCH_MODE
    fun isNotEmpty():Boolean{
        return selectedItems.isNotEmpty()
    }

    fun isEmpty():Boolean{
        return selectedItems.isEmpty()
    }

    fun size():Int{
        return selectedItems.size
    }

   private fun clear(){
        selectedItems.clear()
    }

    fun reset(){
        selectedItems.forEach {
            it.subject.selected = false
            it.caller.value.selected = false
        }
        clear()
    }

    operator  fun get (index:Int):CollisionItem{
        return selectedItems[index]
    }

    fun containsRanged(rangeSelect:CRangeSelect){
        rangeSelect.rangeItems.forEach {
            it.subject.selected = false
        }
       rangeSelect.rangeItems.clear()
        connection.forEach { node->
            val collidedObject = node.contains(rangeSelect)
            if(collidedObject != null && collidedObject !is CRangePoint && collidedObject != rangeSelect){
                node.value.selected = true
                rangeSelect.rangeItems.add(CollisionItem(node,node.value))
            }
        }
    }

    fun contains(entity: CNode):CollisionItem?{
        //remove the previous items and add the new item if Touch or interact mode
        if(mode == MotionGestureListener.TOUCH_MODE || mode == MotionGestureListener.INTERACT_MODE|| mode == MotionGestureListener.RANGED_SELECTION_MODE) {
            selectedItems.forEach {
                it.subject.selected = false
            }
            reset()
        }
        connection.forEach {node->
            val collidedObject = node.contains(entity)
            if(collidedObject != null && node.value.isVisible){
                // for connections only return touch events for input and output signals
                if(mode == MotionGestureListener.CONNECTION_MODE && collidedObject is CSignal) {
                    return CollisionItem(node, collidedObject).also {item->
                        selectedItems.add(item)
                    }
                }else if(mode == MotionGestureListener.INTERACT_MODE || mode == MotionGestureListener.TOUCH_MODE ) {
                    return CollisionItem(node, collidedObject).also { item ->
                        selectedItems.add(item)
                    }
                }else if(mode == MotionGestureListener.SELECTION_MODE && collidedObject !is CSignal){
                    return CollisionItem(node, collidedObject).also {item->
                        selectedItems.add(item)
                    }
                }else if (mode == MotionGestureListener.RANGED_SELECTION_MODE){
                    return CollisionItem(node, collidedObject).also {item->
                        selectedItems.add(item)
                    }
                }
            }
        }
        return null
    }

    data class CollisionItem(val caller: ListNode, val subject: CNode)
}

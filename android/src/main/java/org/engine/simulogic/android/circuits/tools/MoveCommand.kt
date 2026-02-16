package org.engine.simulogic.android.circuits.tools

import com.badlogic.gdx.math.Vector2
import org.engine.simulogic.android.circuits.components.other.CGroup
import org.engine.simulogic.android.circuits.logic.ListNode

class MoveCommand : Command() {
    val oldPosition = Vector2(0f,0f)
    val newPosition = Vector2(0f,0f)
    var node: ListNode? = null
    override fun undo() {
        if (node?.value is CGroup ){
            (node?.value as CGroup).apply {
                val offsetX = oldPosition.x - newPosition.x
                val offsetY =  oldPosition.y - newPosition.y
                resetPositionBuffers()
                translate(offsetX,  offsetY)
                resetPositionBuffers()
            }
        }else {
            node?.value?.updatePosition(oldPosition.x, oldPosition.y)
        }
    }

    override fun redo() {
        if (node?.value is CGroup){
            (node?.value as CGroup).apply {
                val offsetX = newPosition.x - oldPosition.x
                val offsetY = newPosition.y - oldPosition.y
                resetPositionBuffers()
                translate(offsetX,  offsetY)
                resetPositionBuffers()
            }
        }else {
            node?.value?.updatePosition(newPosition.x,newPosition.y)
        }
    }

}

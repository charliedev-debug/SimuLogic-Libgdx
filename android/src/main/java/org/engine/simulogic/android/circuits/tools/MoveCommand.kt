package org.engine.simulogic.android.circuits.tools

import com.badlogic.gdx.math.Vector2
import org.engine.simulogic.android.circuits.logic.ListNode

class MoveCommand : Command() {
    val oldPosition = Vector2(0f,0f)
    val newPosition = Vector2(0f,0f)
    var node: ListNode? = null
    override fun undo() {
        node?.value?.updatePosition(oldPosition.x,oldPosition.y)
    }

    override fun redo() {
        node?.value?.updatePosition(newPosition.x,newPosition.y)
    }

}

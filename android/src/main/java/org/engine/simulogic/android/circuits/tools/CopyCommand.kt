package org.engine.simulogic.android.circuits.tools

import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.scene.PlayGroundScene

class CopyCommand(private val scene: PlayGroundScene, private val connection: Connection):Command() {

    private val data = mutableListOf<ListNode>()

    fun insert(node:ListNode):CopyCommand{
        data.add(node)
        return this
    }

    override fun undo() {
        data.forEach {
            connection.removeNode(it)
           // it.detachSelf()
        }
    }

    override fun redo() {
        data.forEach {
            connection.insertNode(it)
          //  it.attachTo(scene)
        }
    }
}

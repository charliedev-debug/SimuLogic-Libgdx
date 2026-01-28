package org.engine.simulogic.android.circuits.tools

import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.scene.PlayGroundScene

class DeleteCommand(private val scene: PlayGroundScene, private val connection: Connection) : Command(){

    private var data = mutableListOf<ListNode>()

    fun insert(node:ListNode):DeleteCommand{
        data.add(node)
        return this
    }

    override fun undo() {
        data.forEach { node->
            node.apply {
                node.value.attachSelf()
                connection.insertNode(node)
            }

        }
    }

    override fun redo() {
        data.forEach { node->
            node.apply {
                node.value.detachSelf()
                connection.removeNode(this)
            }
        }
    }
}

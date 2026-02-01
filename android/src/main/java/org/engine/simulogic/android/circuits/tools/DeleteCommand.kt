package org.engine.simulogic.android.circuits.tools

import org.engine.simulogic.android.circuits.components.lines.LineMarker
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.scene.Entity
import org.engine.simulogic.android.scene.PlayGroundScene

class DeleteCommand(private val scene: PlayGroundScene, private val connection: Connection) : Command(){

    private var data = mutableListOf<DeleteItem>()
    class DeleteItem(val node: ListNode, val children:MutableList<LineMarker> = mutableListOf())
    fun insert(item:DeleteItem):DeleteCommand{
        item.node.getLineMarkerChildren().forEach {
            item.children.add(it)
        }
        item.node.detachSelf()
        data.add(item)
        return this
    }

    override fun undo() {
        data.forEach { item->
            item.node.attachSelf()
            item.children.forEach {
                it.attachSelf()
            }
            connection.insertNode(item.node)
        }
    }

    override fun redo() {
        data.forEach { item->
            item.node.detachSelf()
            item.children.forEach {
                it.detachSelf()
            }
            connection.removeNode(item.node)
        }
    }
}

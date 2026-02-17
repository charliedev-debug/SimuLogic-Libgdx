package org.engine.simulogic.android.circuits.tools

import org.engine.simulogic.android.circuits.components.buttons.CPower
import org.engine.simulogic.android.circuits.components.generators.CClock
import org.engine.simulogic.android.circuits.components.lines.LineMarker
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.scene.PlayGroundScene

class CopyCommand(private val scene: PlayGroundScene, private val connection: Connection):Command() {

    private val data = mutableListOf<CopyItem>()
    class CopyItem(val node: ListNode, val children:MutableList<LineMarker> = mutableListOf())
    fun insert(node:ListNode):CopyCommand{
        val item = CopyItem(node)
        item.node.getLineMarkerChildren().forEach {
            item.children.add(it)
        }
        data.add(item)
        return this
    }

    override fun undo() {
        data.forEach { item->
            item.node.detachSelf()
            item.children.forEach {
                it.removeSelf()
            }
            connection.removeNode(item.node)
        }
    }

    override fun redo() {
        data.forEach { item->
            item.node.attachSelf()
            item.children.forEach {
                it.attachSelf()
            }
            when(item.node.value){
                is CClock -> connection.insertExecutionPoint(item.node)
                is CPower -> connection.insertExecutionPoint(item.node)
                else -> connection.insertNode(item.node)
            }
        }
    }
}

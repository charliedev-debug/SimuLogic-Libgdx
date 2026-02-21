package org.engine.simulogic.android.circuits.tools

import org.engine.simulogic.android.circuits.components.lines.LineMarker
import org.engine.simulogic.android.circuits.components.other.CGroup
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.scene.Entity
import org.engine.simulogic.android.scene.PlayGroundScene

class DeleteCommand(private val scene: PlayGroundScene, private val connection: Connection) : Command(){

    private var data = mutableListOf<DeleteItem>()
    class DeleteItem(val node: ListNode, val children:MutableList<LineMarker> = mutableListOf(), val parent:MutableList<LineMarker> = mutableListOf())
    fun insert(item:DeleteItem):DeleteCommand{
        item.node.getLineMarkerChildren().forEach {
            item.children.add(it)
        }
        val iterator = item.node.parent.listIterator()
        while (iterator.hasNext()){
            iterator.next().removeMarker(item.node)?.also { parentMarker->
                item.parent.add(parentMarker)
            }
            iterator.remove()
        }
        // if it's a group delete all the children
        if(item.node.value is CGroup){
            item.node.value.deleteChildrenOnDetach = true
        }
        item.node.detachSelf()
        connection.removeNode(item.node)
        data.add(item)
        return this
    }

    override fun undo() {
        data.forEach { item->
            item.node.attachSelf()
            item.children.forEach {
                it.attachSelf()
            }
            item.parent.forEach {
                it.attachSelf()
            }
            connection.insertNode(item.node)
        }
    }

    override fun redo() {
        data.forEach { item->
            // if it's a group delete all the children
            if(item.node.value is CGroup){
                item.node.value.deleteChildrenOnDetach = true
            }
            item.node.detachSelf()
            item.children.forEach {
                it.detachSelf()
            }
            item.parent.forEach {
                it.detachSelf()
            }
            connection.removeNode(item.node)
        }
    }

    fun reset(){
        data.clear()
    }
}

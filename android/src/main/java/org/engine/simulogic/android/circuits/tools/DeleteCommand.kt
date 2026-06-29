package org.engine.simulogic.android.circuits.tools

import org.engine.simulogic.android.circuits.components.buttons.CPower
import org.engine.simulogic.android.circuits.components.buttons.CPulseButton
import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.generators.CClock
import org.engine.simulogic.android.circuits.components.lines.LineMarker
import org.engine.simulogic.android.circuits.components.other.CGroup
import org.engine.simulogic.android.circuits.components.wireless.CChannel
import org.engine.simulogic.android.circuits.components.wireless.ChannelBuffer
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.scene.Entity
import org.engine.simulogic.android.scene.PlayGroundScene

class DeleteCommand(private val scene: PlayGroundScene, private val connection: Connection) : Command(){

    private var data = mutableListOf<DeleteItem>()
    class DeleteItem(val node: ListNode, val children:MutableList<LineMarker> = mutableListOf(), val parent:MutableList<LineMarker> = mutableListOf())
    fun insert(item:DeleteItem):DeleteCommand{
        // this only works for node without a hierarchy
        item.node.getLineMarkerChildren().forEach {
            item.children.add(it)
        }
        /* Test if it's a nested marker and remove all nodes in the hierarchy*/
        val associatedMarkers = mutableListOf<LineMarker>()
        item.node.parent.onEach {
            // get the parent node and all it's children
            val nestedMarkerOriginNode = LineMarker.getNodeOriginFromStatic(it)
                // if the marker is a nested marker or has a hierarchy -> One to Many
                nestedMarkerOriginNode?.also { parentConnector ->
                    parentConnector.getLineMarkerChildren().onEach { marker ->
                        if (marker.to == item.node) {
                            associatedMarkers.add(marker)
                            item.children.add(marker)
                        }
                    }

                    do {
                        associatedMarkers.onEach { marker ->
                            marker.removeSelf()
                        }
                        associatedMarkers.clear()
                        parentConnector.getLineMarkerChildren().onEach { marker ->
                            if (marker.from.value is CSignal) {
                                if (marker.from.value.parent != null && marker.from.value.parent is LineMarker) {
                                    if ((marker.from.value.parent as LineMarker).isRemoved) {
                                        if (!marker.isRemoved) {
                                            associatedMarkers.add(marker)
                                            item.children.add(marker)
                                        }
                                    }
                                }
                            }
                        }

                    } while (associatedMarkers.isNotEmpty())

                }
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
            when(item.node.value){
                is CClock -> connection.insertExecutionPoint(item.node)
                is CPower -> connection.insertExecutionPoint(item.node)
                is CPulseButton-> connection.insertExecutionPoint(item.node)
                is CChannel ->{
                    if(item.node.value.channelType == ChannelBuffer.CHANNEL_OUTPUT){
                        connection.insertExecutionPoint(item.node)
                    }else{
                        connection.insertNode(item.node)
                    }
                }
                else -> connection.insertNode(item.node)
            }
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

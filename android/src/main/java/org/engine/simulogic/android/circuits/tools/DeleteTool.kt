package org.engine.simulogic.android.circuits.tools

import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.lines.LineMarker
import org.engine.simulogic.android.circuits.components.other.CGroup
import org.engine.simulogic.android.circuits.components.other.CRangeLine
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.scene.PlayGroundScene

class DeleteTool (private val dataContainer: DataContainer, private val connection: Connection,
                  private val scene: PlayGroundScene,
                  private val commandHistory: CommandHistory) {

    fun execute(){
        val deleteComponent = DeleteCommand(scene, connection)
        val deleteLine = DeleteLineCommand(scene, connection)
        dataContainer.forEach {node->
            // it must be a line
            if(node.value is CSignal && node.value.parent is LineMarker){
                val selectedMarker = node.value.parent as LineMarker
                val parentComponent = selectedMarker.getNodeOriginFrom(selectedMarker.from)
                    //Delete the items in a separate list, this prevents concurrent modification exceptions
                    val markedLines = mutableListOf<LineMarker>()
                    parentComponent.from.getLineMarkerChildren().onEach {lineMarker ->
                        if(lineMarker.markerActive){
                            markedLines.add(lineMarker)
                        }
                    }
                    markedLines.onEach { lineMarker ->
                        if(lineMarker.markerActive){
                           lineMarker.removeSelf()
                            deleteLine.insert(lineMarker)
                        }
                    }
                commandHistory.execute(deleteLine)
            }
            // it must be a line range highlight marker
            else if(node.value is CRangeLine && node.value.start.parent is LineMarker){
                val selectedMarker = node.value.start.parent as LineMarker
                val parentComponent = selectedMarker.getNodeOriginFrom(selectedMarker.from)
                //Delete the items in a separate list, this prevents concurrent modification exceptions
                val markedLines = mutableListOf<LineMarker>()
                parentComponent.from.getLineMarkerChildren().onEach {lineMarker ->
                    if(lineMarker.markerActive){
                        markedLines.add(lineMarker)
                    }
                }
                markedLines.onEach { lineMarker ->
                    if(lineMarker.markerActive){
                        lineMarker.removeSelf()
                        deleteLine.insert(lineMarker)
                    }
                }
                commandHistory.execute(deleteLine)
            }
            // it must be a component
            else{
                connection.removeNode(node)
                deleteComponent.insert( DeleteCommand.DeleteItem(node))
                commandHistory.execute(deleteComponent)
            }
        }
        dataContainer.clear()
    }
}

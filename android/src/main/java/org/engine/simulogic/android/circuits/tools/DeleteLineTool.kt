package org.engine.simulogic.android.circuits.tools

import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.lines.LineMarker
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.scene.PlayGroundScene

class DeleteLineTool (private val dataContainer: DataContainer, private val connection: Connection,
                      private val scene: PlayGroundScene,
                      private val commandHistory: CommandHistory) {

    fun execute(){
        val deleteComponent = DeleteCommand(scene, connection)
        val deleteLine = DeleteLineCommand(scene, connection)
        dataContainer.forEach {node->
            // it must be a line
            if(node.value is CSignal && node.value.parent is LineMarker){
                val lineMarker = node.value.parent as LineMarker
                lineMarker.detachSelf()
                deleteLine.insert(lineMarker)
            }
            // it must be a component
            else{
                connection.removeNode(node)
                deleteComponent.insert( DeleteCommand.DeleteItem(node))
            }

        }
        commandHistory.execute(deleteComponent)
        commandHistory.execute(deleteLine)
        dataContainer.clear()
    }
}

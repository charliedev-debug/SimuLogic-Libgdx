package org.engine.simulogic.android.circuits.tools

import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.scene.PlayGroundScene

class DeleteTool(private val dataContainer: DataContainer, private val connection: Connection,
                 private val scene: PlayGroundScene,
                 private val commandHistory: CommandHistory) {

    fun execute(){
        DeleteCommand(scene, connection).also { command->
            dataContainer.forEach {node->
                connection.removeNode(node)
                node.value.detachSelf()
                command.insert(node)
            }
            commandHistory.execute(command)
        }
        dataContainer.clear()
    }
}

package org.engine.simulogic.android.circuits.logic.events

import com.badlogic.gdx.math.Vector2
import org.engine.simulogic.android.circuits.components.buttons.CPower
import org.engine.simulogic.android.circuits.components.buttons.CPulseButton
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.circuits.tools.Command
import org.engine.simulogic.android.circuits.tools.CommandHistory
import org.engine.simulogic.android.circuits.tools.InsertCommand
import org.engine.simulogic.android.scene.Entity
import org.engine.simulogic.android.scene.PlayGroundScene

class EventPulseButtonCommand(private val position: Vector2,
                              private val connection: Connection,
                              private val commandHistory: CommandHistory,
                              private val scene: PlayGroundScene) : Command(){
    override fun execute() {
        ListNode(
            CPulseButton(
                position.x,
                position.y,
                rotationDirection = Entity.ROTATE_RIGHT,
                scene
            )
        ).also { node->
            connection.insertExecutionPoint(node)
            commandHistory.execute(InsertCommand(node, connection))
        }
    }
}

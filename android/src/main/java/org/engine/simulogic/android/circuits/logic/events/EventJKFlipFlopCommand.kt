package org.engine.simulogic.android.circuits.logic.events

import com.badlogic.gdx.math.Vector2
import org.engine.simulogic.android.circuits.components.flipflops.CJKFlipFlop
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.circuits.tools.Command
import org.engine.simulogic.android.circuits.tools.CommandHistory
import org.engine.simulogic.android.circuits.tools.InsertCommand
import org.engine.simulogic.android.scene.PlayGroundScene

class EventJKFlipFlopCommand (private val position: Vector2,
                              private val connection: Connection,
                              private val commandHistory: CommandHistory,
                              private val scene: PlayGroundScene) : Command(){

    override fun execute() {
        ListNode(CJKFlipFlop(position.x, position.y, scene)).also{ node->
            connection.insertNode(node)
            commandHistory.execute(InsertCommand(node, connection))
        }
    }
}

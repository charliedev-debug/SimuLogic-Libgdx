package org.engine.simulogic.android.circuits.tools

import org.engine.simulogic.android.circuits.components.lines.LineMarker
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.scene.PlayGroundScene

class DeleteLineCommand (private val scene: PlayGroundScene, private val connection: Connection) : Command(){

    private var data = mutableListOf<LineMarker>()
    fun insert(node: LineMarker):DeleteLineCommand{
        data.add(node)
        return this
    }

    override fun undo() {
        data.forEach { node->
            node.attachSelf()
        }

    }

    override fun redo() {
        data.forEach { node->
            node.detachSelf()
        }
    }
}


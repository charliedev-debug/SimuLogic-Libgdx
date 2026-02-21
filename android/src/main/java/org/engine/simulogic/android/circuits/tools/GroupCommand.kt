package org.engine.simulogic.android.circuits.tools

import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.other.CGroup

class GroupCommand(private val group:CGroup) : Command() {

    override fun undo() {
        super.undo()
        group.detachSelf()
    }

    override fun redo() {
        super.redo()
        group.attachSelf()
    }
}

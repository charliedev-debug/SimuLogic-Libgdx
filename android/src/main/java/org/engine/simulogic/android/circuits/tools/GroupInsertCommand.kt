package org.engine.simulogic.android.circuits.tools

import org.engine.simulogic.android.circuits.components.other.CGroup
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ListNode

class GroupInsertCommand(private val group:ListNode, private val connection:Connection, private val detachChildrenOnDelete:Boolean = false) : Command() {

    override fun undo() {
        super.undo()
        if(group.value is CGroup) {
            group.value.deleteChildrenOnDetach = detachChildrenOnDelete
            group.detachSelf()
            connection.removeNode(group)
        }
    }

    override fun redo() {
        super.redo()
        if(group.value is CGroup) {
            group.value.attachSelf()
            connection.insertNode(group)
        }
    }
}

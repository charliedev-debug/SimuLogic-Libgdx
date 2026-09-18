package org.engine.simulogic.android.circuits.tools
import org.engine.simulogic.android.circuits.components.generators.CClock
import org.engine.simulogic.android.circuits.logic.ListNode

class EditClockCommand(private val node: ListNode, private val previousFreq: Float, private val currentFreq: Float) : Command() {

    override fun undo() {
        if (node.value is CClock) {
            node.value.setFrequency(previousFreq)
            node.value.reset()
        }
    }

    override fun redo() {
        if (node.value is CClock) {
            node.value.setFrequency(currentFreq)
            node.value.reset()
        }
    }
}

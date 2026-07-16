package org.engine.simulogic.android.circuits.tools

import org.engine.simulogic.android.circuits.components.other.CLabel
import org.engine.simulogic.android.circuits.logic.ListNode

class EditLabelCommand (private val node:ListNode, private val previousText:String, private val previousFontSize: Float,
                        private val currentText: String, private val currentFontSize: Float) : Command(){

    override fun undo() {
        if(node.value is CLabel){
            node.value.text = previousText
            node.value.fontSize = previousFontSize
        }
    }

    override fun redo() {
        if(node.value is CLabel){
            node.value.text = currentText
            node.value.fontSize = currentFontSize
        }
    }
}

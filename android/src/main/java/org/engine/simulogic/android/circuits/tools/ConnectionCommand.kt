package org.engine.simulogic.android.circuits.tools

import org.engine.simulogic.android.circuits.components.lines.LineMarker

class ConnectionCommand(private val lineMarker: LineMarker) : Command() {

    override fun undo() {
        lineMarker.removeSelf()
    }

    override fun redo() {
        lineMarker.attachSelf()
    }

}

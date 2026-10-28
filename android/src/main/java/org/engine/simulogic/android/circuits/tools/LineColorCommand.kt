package org.engine.simulogic.android.circuits.tools
import org.engine.simulogic.android.circuits.components.lines.LineMarker
import org.engine.simulogic.android.circuits.components.other.Pallete

class LineColorCommand(private val lineMarker: LineMarker,
                       private val pColorOn: Pallete?,
                       private val pColorOff: Pallete?,
                       private val nColorOn: Pallete?,
                       private val nColorOff: Pallete?) : Command() {

    override fun undo() {
        lineMarker.colorSignalOnPallete = pColorOn
        lineMarker.colorSignalOffPallete = pColorOff
    }

    override fun redo() {
        lineMarker.colorSignalOnPallete = nColorOn
        lineMarker.colorSignalOffPallete = nColorOff
    }
}

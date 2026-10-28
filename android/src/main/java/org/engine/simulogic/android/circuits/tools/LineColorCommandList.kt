package org.engine.simulogic.android.circuits.tools

class LineColorCommandList : Command() {

    private val lineMarkers = mutableListOf<LineColorCommand>()

    fun insert(lineColorCommand: LineColorCommand){
        lineMarkers.add(lineColorCommand)
    }

    fun isNotEmpty(): Boolean{
        return lineMarkers.isNotEmpty()
    }

    override fun undo() {
        lineMarkers.onEach { lineMarker->
            lineMarker.undo()
        }
    }

    override fun redo() {
        lineMarkers.onEach { lineMarker->
            lineMarker.redo()
        }
    }
}

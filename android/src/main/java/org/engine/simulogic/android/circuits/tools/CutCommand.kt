package org.engine.simulogic.android.circuits.tools

class CutCommand:Command() {
    private val data = mutableListOf<MoveCommand>()

    fun insert(command:MoveCommand){
        data.add(command)
    }

    override fun undo() {
        data.forEach {
            it.undo()
        }
    }

    override fun redo() {
        data.forEach {
            it.redo()
        }
    }
}

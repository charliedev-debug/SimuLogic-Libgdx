package org.engine.simulogic.android.circuits.tools

class CommandHistory :Command() {
    private val undoQueue = ArrayDeque<Command>()
    private val redoQueue = ArrayDeque<Command>()
    private val MAX_BUFFER = 1000
    override fun execute(command: Command){
        // remove the oldest
        if(isOverflow()){
            undoQueue.removeFirstOrNull()
        }
        undoQueue.add(command)
    }

    override fun undo() {
        undoQueue.removeLastOrNull()?.also { command ->
            command.undo()
            redoQueue.add(0,command)
        }
    }

    override fun redo() {
        redoQueue.removeFirstOrNull()?.also { command ->
            command.redo()
            undoQueue.add(command)
        }
    }

    private fun isOverflow():Boolean{
        return undoQueue.size >= MAX_BUFFER
    }
}

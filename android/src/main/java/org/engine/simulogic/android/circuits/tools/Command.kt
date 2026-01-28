package org.engine.simulogic.android.circuits.tools

abstract class Command {

    open fun execute(command: Command){

    }
    open fun undo(){

    }
    open fun redo(){

    }
}

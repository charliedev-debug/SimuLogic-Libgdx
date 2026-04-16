package org.engine.simulogic.android.circuits.logic.events
import org.engine.simulogic.android.circuits.tools.Command
import java.util.Collections

class EventBridge {

    private val data: MutableList<Command> = Collections.synchronizedList(mutableListOf<Command>())

    fun insertCommand(command: Command){
        data.add(command)
    }

    fun evaluate(){
        synchronized(data){
            data.listIterator().also { iterator->
                while (iterator.hasNext()){
                    iterator.next().execute()
                    iterator.remove()
                }
            }
        }
    }
}

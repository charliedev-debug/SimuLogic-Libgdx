package org.engine.simulogic.android.circuits.logic

import org.engine.simulogic.android.circuits.components.interfaces.IExecutable
import java.util.LinkedList
import java.util.Queue

class Executor(private val connection:Connection):IExecutable {
    var isActive = true
    override fun execute(){
        if(!isActive) return
        synchronized(connection.executionPoints) {
            if(connection.executionPoints.isEmpty()) return
            val executableNodes: Queue<ListNode> = LinkedList()
            connection.executionPoints.forEach {
                executableNodes.add(it)
            }
            while (executableNodes.isNotEmpty()) {
                executableNodes.poll()?.also { node ->
                    node.value.execute()
                    synchronized(node.getLineMarkerChildren()) {
                        node.getLineMarkerChildren().forEach { marker ->
                            marker.to.value.signals[marker.signalTo].value =
                                node.value.signals[marker.signalFrom].value
                            executableNodes.offer(marker.to)
                        }
                    }
                }
            }
        }

    }


}

package org.engine.simulogic.android.circuits.logic

import org.engine.simulogic.android.circuits.components.interfaces.IExecutable
import org.engine.simulogic.android.circuits.components.lines.LineMarker
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
            val visitedNodes = mutableListOf<ListNode>()
            val feedForward = mutableMapOf<LineMarker, Int>()
            while (executableNodes.isNotEmpty()) {
                executableNodes.poll()?.also { node ->
                    node.value.execute()
                        synchronized(node.getLineMarkerChildren()) {
                            node.getLineMarkerChildren().forEach { marker ->
                                if(!marker.to.visited) {
                                    marker.to.value.signals[marker.signalTo].value =
                                        node.value.signals[marker.signalFrom].value
                                    executableNodes.offer(marker.to)
                                }else{
                                    feedForward[marker] = node.value.signals[marker.signalFrom].value
                                    // marker.to.value.execute()
                                }
                        }
                    }
                    visitedNodes.add(node.apply { visited = true })
                }
            }

            feedForward.forEach {
                it.key.also { marker->
                    marker.to.value.signals[marker.signalTo].value = it.value
                    marker.to.value.execute()
                }
            }
            visitedNodes.forEach {
                it.visited = false
            }

        }

    }


}

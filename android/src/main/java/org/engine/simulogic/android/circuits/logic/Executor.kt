package org.engine.simulogic.android.circuits.logic

import com.badlogic.gdx.Gdx
import org.engine.simulogic.android.circuits.components.arithmetic.CHalfAdder
import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.generators.CClock
import org.engine.simulogic.android.circuits.components.interfaces.IExecutable
import org.engine.simulogic.android.circuits.components.visuals.CLed
import org.engine.simulogic.android.utilities.TimerManager
import java.util.LinkedList
import java.util.Queue
import kotlin.concurrent.timer
import kotlin.math.abs

class Executor(private val connection: Connection) : IExecutable {
    var isActive = true
    override fun execute() {
        if (connection.executionPoints.isEmpty() || !isActive) {
            return
        }
        val executableNodes: Queue<ListNode> = LinkedList()
        val timerManager = TimerManager.getInstance()
        val accumulator = Gdx.graphics.deltaTime
        val visitedNodes = mutableListOf<ListNode>()

        connection.executionPoints.forEach {
            executableNodes.add(it)
        }
        while (executableNodes.isNotEmpty()){
            val propagationQueue: Queue<ListNode> = LinkedList()
            val propagationVisited = mutableListOf<ListNode>()
            executableNodes.poll()?.also {node ->
                propagationQueue.offer(node)
                while (propagationQueue.isNotEmpty()){
                    propagationQueue.poll()?.also { propNode->
                        if(propNode.value is CClock){
                            propNode.value.timer.dt = accumulator
                            timerManager.update()
                            propNode.value.execute()
                            propNode.value.timer.dt = 0f
                        }else {
                            propNode.value.execute()
                        }
                        propNode.getLineMarkerChildren().forEach { marker ->
                            if (marker.from.value !is CSignal && marker.to.value !is CSignal) {
                                marker.to.value.signals[marker.signalTo].value =
                                    propNode.value.signals[marker.signalFrom].value
                                if (!marker.to.visited) {
                                    propagationQueue.offer(marker.to)
                                } else {
                                    marker.to.value.execute()
                                }
                            } else {
                                marker.getNodeOriginFrom(marker.from).also { originFrom ->
                                    marker.to.value.signals[marker.signalTo].value =
                                        originFrom.from.value.signals[originFrom.signalFrom].value
                                    if (!marker.to.visited) {
                                        propagationQueue.offer(marker.to)
                                    } else {
                                        marker.to.value.execute()
                                    }
                                }
                            }
                        }
                        propagationVisited.add(propNode.apply { visited = true })
                        visitedNodes.add(propNode)

                    }
                }
                val propNode = propagationVisited.first()
                if(propNode.value is CClock){
                    var timeElapsed = accumulator
                    var iterValue = ((accumulator - propNode.value.freq) * 1000).toInt()
                    if(iterValue > 0f){
                        while (iterValue > 0f){
                            propagationVisited.forEach {
                                it.value.execute()
                            }
                            iterValue--
                            propNode.value.timer.dt = timeElapsed
                            timerManager.update()
                            propNode.value.timer.dt = 0f
                            timeElapsed += timerManager.STEP
                        }
                    }else{
                      /*  propagationVisited.forEach {
                            it.value.execute()
                        }
                        propNode.value.timer.dt = timeElapsed
                        timerManager.update()
                        propNode.value.timer.dt = 0f*/
                    }
                }

              //  visitedNodes.add(node.apply { visited = true })
            }

        }
       /* while (executableNodes.isNotEmpty()){

            executableNodes.poll()?.also {node ->
                node.getLineMarkerChildren().forEach { marker ->
                    if (marker.from.value !is CSignal && marker.to.value !is CSignal) {
                        marker.to.value.signals[marker.signalTo].value =
                            node.value.signals[marker.signalFrom].value
                        if (!marker.to.visited) {
                            executableNodes.offer(marker.to)
                        } else {
                            marker.to.value.execute()
                        }
                    } else {
                        marker.getNodeOriginFrom(marker.from).also { originFrom ->
                            marker.to.value.signals[marker.signalTo].value =
                                originFrom.from.value.signals[originFrom.signalFrom].value

                            if (!marker.to.visited) {
                                executableNodes.offer(marker.to)
                            } else {
                                marker.to.value.execute()
                            }
                        }
                    }
                }
                if(node.value is CClock){
                    var timeElapsed = accumulator
                    var iterValue = accumulator - node.value.freq
                    if(iterValue > 0f){
                        while (iterValue > 0f){
                            node.value.execute()
                            iterValue--
                            node.value.timer.dt = timeElapsed
                            timerManager.update()
                            node.value.timer.dt = 0f
                            timeElapsed += timerManager.STEP
                        }
                    }else{
                        node.value.execute()
                        node.value.timer.dt = timeElapsed
                        timerManager.update()
                        node.value.timer.dt = 0f
                    }


                }else {
                    node.value.execute()
                }
                visitedNodes.add(node.apply { visited = true })
            }

        }*/
        visitedNodes.forEach {
            it.visited = false
        }
        visitedNodes.clear()

    }


}

package org.engine.simulogic.android.circuits.tools

import org.engine.simulogic.android.circuits.components.buttons.CPower
import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.generators.CClock
import org.engine.simulogic.android.circuits.components.generators.CRandom
import org.engine.simulogic.android.circuits.components.lines.LineMarker
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.scene.PlayGroundScene

class CopyTool(
    private val dataContainer: DataContainer,
    private val connection: Connection,
    private val commandHistory: CommandHistory
) {

    fun execute(
        originX: Float,
        originY: Float,
        scene: PlayGroundScene
    ) {
        if (dataContainer.isEmpty()) return
        dataContainer.sort(originX, originY)
        val firstItem = dataContainer[0]
        val ox = firstItem.value.getPosition().x
        val oy = firstItem.value.getPosition().y
        val copyCommand = CopyCommand(scene, connection)
        val cloneList = mutableListOf<ListNode>()
        val cloneOriginMap = mutableMapOf<ListNode,ListNode>()
        // process all possible clones then create lines for them
        for ( i in 0 until dataContainer.size()){
            val data = dataContainer[i]
             data.clone().also { clone->
                 clone.value.updatePosition(clone.value.getPosition().x - ox + originX,
                     clone.value.getPosition().y - oy + originY)
                 when(clone.value){
                     is CClock-> connection.insertExecutionPoint(clone)
                     is CPower-> connection.insertExecutionPoint(clone)
                     else -> connection.insertNode(clone)
                 }
                 cloneOriginMap[data] = clone
                 cloneList.add(clone)
             }
        }

        // clone all possible signals that can be created
        for (i in 0 until dataContainer.size()){
            val origin = dataContainer[i]
            origin.getLineMarkerChildren().forEach {lineMarker ->
                 cloneOriginMap[lineMarker.to]?.also { cloneChild->
                     val clone = cloneOriginMap[lineMarker.from]!!
                    lineMarker.clone(clone,cloneChild,lineMarker.signalFrom,lineMarker.signalTo, scene).also { lineMarkerClone->
                        lineMarker.signals.forEachIndexed{indexPoint,originPoint->
                            val px = originPoint.getPosition().x - ox
                            val py = originPoint.getPosition().y - oy
                            lineMarkerClone.signals[indexPoint].updatePosition(originX+px, originY+py)
                        }
                        clone.insertChildUnmarked(cloneChild,lineMarkerClone)
                    }
                }
            }
        }

        commandHistory.execute(copyCommand)

        dataContainer.clear()
    }
}

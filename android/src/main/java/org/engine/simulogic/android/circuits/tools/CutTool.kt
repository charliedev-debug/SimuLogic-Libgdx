package org.engine.simulogic.android.circuits.tools

import org.engine.simulogic.android.circuits.components.other.CGroup
import org.engine.simulogic.android.circuits.logic.ListNode

class CutTool(private val dataContainer: DataContainer, private val commandHistory: CommandHistory) {

    fun execute(originX:Float,originY: Float){
        if (dataContainer.isEmpty()) return
        if (dataContainer.size() == 1) {
            dataContainer[0].also {item->
                val ox = item.value.getPosition().x
                val oy = item.value.getPosition().y
                commandHistory.execute(MoveCommand().apply {
                    node = item
                    oldPosition.set(item.value.getPosition())
                    newPosition.set(originX,originY)
                })
                if(item.value is CGroup){
                    item.value.resetPositionBuffers()
                    item.value.translate(originX - ox, originY - oy)
                    item.value.resetPositionBuffers()
                }else{
                    item.value.updatePosition(originX,originY)
                }
            }
        } else {
            //move all object based on the closest element to the paste origin
            dataContainer.sort(originX, originY)
            val cutCommand = CutCommand()
            val firstItem = dataContainer[0]
            val ox = firstItem.value.getPosition().x
            val oy = firstItem.value.getPosition().y
            cutCommand.insert(MoveCommand().apply {
                node = firstItem
                oldPosition.set(ox,oy)
                newPosition.set(originX,originY)
            })

            if(firstItem.value is CGroup){
                firstItem.value.resetPositionBuffers()
                firstItem.value.translate(originX - ox, originY - oy)
                firstItem.value.resetPositionBuffers()
            }else{
                firstItem.value.updatePosition(originX,originY)
            }
            for (i in 1 until dataContainer.size()) {
                val n = dataContainer[i]
                val p = dataContainer[i].value
                val ix = p.getPosition().x
                val iy = p.getPosition().y

                if(p is CGroup){
                   p.resetPositionBuffers()
                   p.translate(originX - ox, originY - oy)
                   p.translate(originX - ix, originY - iy)
                   p.resetPositionBuffers()
                }else{
                    p.updatePosition(originX + ix - ox, originY + iy - oy)
                }
                cutCommand.insert(MoveCommand().apply {
                    node = n
                    oldPosition.set(ix,iy)
                    newPosition.set(p.getPosition())
                })
            }
            dataContainer.forEach { data->
                data.getLineMarkerChildren().forEach { marker->
                    marker.signals.forEach { point ->
                        val nx = originX + point.getPosition().x - ox
                        val ny = originY + point.getPosition().y - oy
                        cutCommand.insert(MoveCommand().apply {
                            node = ListNode(point)
                            oldPosition.set(point.getPosition())
                            newPosition.set(nx, ny)
                            point.updatePosition(nx, ny)
                        })
                    }
                }
            }
            commandHistory.execute(cutCommand)
        }
    }
}

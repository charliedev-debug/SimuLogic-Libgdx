package org.engine.simulogic.android.circuits.components.lines

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Rectangle
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.CDefaults.Companion.LINE_MARKER_ACTIVE
import org.engine.simulogic.android.circuits.components.CDefaults.Companion.LINE_MARKER_INACTIVE
import org.engine.simulogic.android.circuits.components.CDefaults.Companion.SIGNAL_ACTIVE_COLOR
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.CTypes
import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.interfaces.ICollidable
import org.engine.simulogic.android.circuits.components.interfaces.IUpdate
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.scene.Entity
import org.engine.simulogic.android.scene.LayerEnums
import org.engine.simulogic.android.scene.PlayGroundScene
import kotlin.math.abs

class LineMarker(val scene: PlayGroundScene,
                 val from: ListNode, val to: ListNode,
                 var signalFrom: Int, val signalTo: Int, var index: Int = 0,
                 val linePointCountX: Int = CDefaults.linePointCountX, val linePointCountY: Int = CDefaults.linePointCountY
) : Entity(), ICollidable,
    IUpdate {
    private val lines = mutableListOf<CLine>()

    fun initialize(scene: PlayGroundScene) {
        val signalFrom = from.value.signals[signalFrom]
        val signalTo = to.value.signals[signalTo]
        val pFrom = signalFrom.getPosition()
        val pTo = signalTo.getPosition()
        val distanceX = pTo.x - pFrom.x
        val distanceY = pTo.y - pFrom.y
        val maxDistanceBetweenX = distanceX / (linePointCountX + 1 )
        val maxDistanceBetweenY = distanceY / (linePointCountY + 1)
        var signalIndex = 0
        for (i in 0 ..  linePointCountX) {
            val x = pFrom.x + maxDistanceBetweenX * i
            val y = pFrom.y
            signals.add(CSignal(x, y, CTypes.SIGNAL_IN, signalIndex++, scene).apply {
                parent = this@LineMarker
            })
        }

        for (i in  0  ..  linePointCountY ) {
            val y = pFrom.y + maxDistanceBetweenY * i
            val x = pTo.x
            signals.add(CSignal(x, y, CTypes.SIGNAL_IN, signalIndex++, scene).apply {
                parent = this@LineMarker
            })
        }

        createMarker(scene)
    }

     override fun detachSelf(){
         lines.forEach {
             it.detachSelf()
         }
         signals.forEach {
             it.detachSelf()
         }
    }

    // removes marker for the parent node
    fun removeSelf(){
        detachSelf()
        from.removeMarker(this)
    }

    override fun attachSelf() {
        scene.getLayerById(LayerEnums.CONNECTION_LAYER.name).also { layer ->
            lines.forEach {
                it.isRemoved = false
                layer.attachChild(it)
            }
        }
        scene.getLayerById(LayerEnums.CONNECTION_LAYER_INPUTS.name).also { layer ->
            signals.forEach {
                it.isRemoved = false
                layer.attachChild(it)
            }
        }

        from.insertChildUnmarked(to,this)
    }

    private fun createMarker(scene: PlayGroundScene) {
        scene.getLayerById(LayerEnums.CONNECTION_LAYER.name).also { layer ->
            for (i in 0 until signals.size - 1) {
                val prev = signals[i].getPosition()
                val next = signals[i + 1].getPosition()
                lines.add(
                    CLine(
                        prev.x,
                        prev.y,
                        next.x,
                        next.y,
                        CDefaults.lineWeight
                    ).also { line ->
                        layer.attachChild(line)
                    })
            }
        }
        scene.getLayerById(LayerEnums.CONNECTION_LAYER_INPUTS.name).also { layer ->
            // to prevent collisions during touch don't attach the first and the last point
            for (i in 1 until signals.size - 1) {
                layer.attachChild(signals[i])
            }
        }
    }

    override fun update() {
        val signalFrom = from.value.signals[signalFrom]
        val signalTo = to.value.signals[signalTo]
        val pFrom = signalFrom.getPosition()
        val pTo = signalTo.getPosition()
        // the first and last marker come from the origin node
        if (signals.isNotEmpty()) {
            signals[0].updatePosition(pFrom.x, pFrom.y)
            signals[signals.size - 1].updatePosition(pTo.x, pTo.y)
        }
        for (i in 1 until signals.size - 1) {
            signals[i].update()
        }

        //snap align body
        var index = 1
        while(index < signals.size - 2){
            val prevSignal = signals[index]
            val nextSignal = signals[index + 1]
            val prev = prevSignal.getPosition()
            val next = nextSignal.getPosition()
            val offsetX = prev.x - next.x
            val offsetY = prev.y - next.y
            val distanceFromPrevX = abs(pFrom.x - prev.x)
            val distanceFromPrevY = abs(pFrom.y - prev.y)
            val distanceToPrevX = abs(pTo.x - prev.x)
            val distanceToPrevY = abs(pTo.y - prev.y)
            // ignore the first and the last elements since we can't modify them directly since it's the source
            if(distanceFromPrevX < distanceToPrevX || index == 0){
                if (abs(offsetX) <= CDefaults.GRID_WIDTH) {
                    nextSignal.updatePosition(prev.x, next.y)
                }
            }else if(distanceFromPrevX > distanceToPrevX){
                if (abs(offsetX) <= CDefaults.GRID_WIDTH) {
                    prevSignal.updatePosition(next.x, prev.y)
                }
            }

            if(distanceFromPrevY < distanceToPrevY || (index+1) == signals.size - 1){
                if (abs(offsetY) <= CDefaults.GRID_HEIGHT) {
                    nextSignal.updatePosition(next.x, prev.y)
                }
            }else if(distanceFromPrevY > distanceToPrevY){
                if (abs(offsetY) <= CDefaults.GRID_HEIGHT) {
                    prevSignal.updatePosition(prev.x, next.y)
                }
            }
            index++
        }

        // snap align start and end points
        val startFrom = signals[0]
        val startSnapFrom = signals[1]
        val endTo = signals[signals.size - 1]
        val endSnapTo = signals[signals.size - 2]
        val offsetFromX = abs(startFrom.getPosition().x - startSnapFrom.getPosition().x)
        val offsetFromY = abs(startFrom.getPosition().y - startSnapFrom.getPosition().y)
        val offsetToX = abs(endTo.getPosition().x - endSnapTo.getPosition().x)
        val offsetToY = abs(endTo.getPosition().y - endSnapTo.getPosition().y)

        if(offsetFromX <= CDefaults.GRID_WIDTH){
            startSnapFrom.updatePosition(startFrom.getPosition().x, startSnapFrom.getPosition().y)
        }
        if(offsetFromY <= CDefaults.GRID_HEIGHT){
            startSnapFrom.updatePosition(startSnapFrom.getPosition().x, startFrom.getPosition().y)
        }
        if(offsetToX <= CDefaults.GRID_WIDTH){
            endSnapTo.updatePosition(endTo.getPosition().x, endSnapTo.getPosition().y)
        }
        if(offsetToY <= CDefaults.GRID_HEIGHT){
            endSnapTo.updatePosition(endSnapTo.getPosition().x, endTo.getPosition().y)
        }


        // mark lines and set coordinates
        var markerActive = false
        for (i in 0 until signals.size - 1) {
            val prevSignal = signals[i]
            val nextSignal = signals[i + 1]
            val prev = prevSignal.getPosition()
            val next = nextSignal.getPosition()
            lines[i].also { line->
                line.color = if(signalFrom.value == CNode.SIGNAL_ACTIVE) SIGNAL_ACTIVE_COLOR else LINE_MARKER_INACTIVE
                line.updatePosition(prev.x, prev.y, next.x, next.y)
            }
            markerActive = markerActive || nextSignal.selected || prevSignal.selected
        }
        if (markerActive) {
            updateColor(LINE_MARKER_ACTIVE)
        }
    }

    override fun updateColor(color: Color) {
        lines.forEach {
            it.color = color
        }
    }

    override fun contains(x: Float, y: Float): CNode? {
        return null
    }

    override fun contains(entity: CNode): CNode? {
        for (i in 1 until signals.size - 1) {
            val obj = signals[i].contains(entity)
            if (obj != null) {
                return obj
            }
        }
        return null
    }

    override fun contains(rect: Rectangle): CNode? {
        for (i in 1 until signals.size - 1) {
            val obj = signals[i].contains(rect)
            if (obj != null) {
                return obj
            }
        }
        return null
    }

    fun clone(
        from: ListNode,
        to: ListNode,
        signalFrom: Int,
        signalTo: Int,
        scene: PlayGroundScene
    ): LineMarker {
        return LineMarker(scene,from, to, signalFrom, signalTo, index, linePointCountX, linePointCountY).also { it.initialize(scene) }
    }

}

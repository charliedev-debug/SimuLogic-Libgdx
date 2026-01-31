package org.engine.simulogic.android.circuits.components.lines

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Rectangle
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.CDefaults.Companion.LINE_MARKER_ACTIVE
import org.engine.simulogic.android.circuits.components.CDefaults.Companion.LINE_MARKER_INACTIVE
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

class LineMarker(val from: ListNode, val to: ListNode,
                 var signalFrom: Int, val signalTo:Int, val index:Int = 0):Entity(), ICollidable,
    IUpdate {
    private val lines = mutableListOf<CLine>()
    fun initialize(scene: PlayGroundScene) {
        val signalFrom = from.value.signals[signalFrom]
        val signalTo = to.value.signals[signalTo]
        val pFrom = signalFrom.getPosition()
        val pTo = signalTo.getPosition()
        val distanceX = pTo.x - pFrom.x
        val distanceY = pTo.y - pFrom.y
        val maxDistanceBetweenX = distanceX / CDefaults.linePointCountX
        val maxDistanceBetweenY = distanceY / CDefaults.linePointCountY
        var lastX = pFrom.x
        var signalIndex = 0
        for (i in 0..CDefaults.linePointCountX) {
            val x = pFrom.x + maxDistanceBetweenX * i
            val y = pFrom.y
            lastX = x
            signals.add(CSignal(x, y,CTypes.SIGNAL_IN, signalIndex++, scene).apply { parent = this@LineMarker})
        }

        for (i in CDefaults.linePointCountY downTo 0) {
            val y = pFrom.y + maxDistanceBetweenY * i
            val x = lastX
            signals.add(CSignal(x, y, CTypes.SIGNAL_IN,signalIndex++, scene).apply { parent = this@LineMarker })
        }

        createMarker(scene)
    }
    private fun createMarker(scene: PlayGroundScene) {
        scene.getLayerById(LayerEnums.CONNECTION_LAYER.name).also { layer->
            for (i in 0 until signals.size - 1) {
                val prev = signals[i].getPosition()
                val next = signals[i + 1].getPosition()
                lines.add(
                    CLine(
                        prev.x,
                        prev.y,
                        next.x,
                        next.y,
                        CDefaults.lineWeight).also { line ->
                        layer.attachChild(line)
                    })
            }
        }
        scene.getLayerById(LayerEnums.CONNECTION_LAYER_INPUTS.name).also { layer->
            // to prevent collisions during touch don't attach the first and the last point
            for(i in 1 until signals.size - 1){
                layer.attachChild(signals[i])
            }
        }
    }

    override fun update() {
        val signalFrom = from.value.signals[signalFrom]
        val signalTo = to.value.signals[signalTo]
        val pFrom = signalFrom.getPosition()
        val pTo =   signalTo.getPosition()
        // the first and last marker come from the origin node
        if (signals.isNotEmpty()) {
            signals[0].updatePosition(pFrom.x, pFrom.y)
            signals[signals.size - 1].updatePosition(pTo.x, pTo.y)
        }
        for (i in 1 until signals.size - 1){
            signals[i].update()
        }
        for (i in 0 until signals.size - 1) {
            val prevSignal = signals[i]
            val nextSignal = signals[i + 1]
            val prev = prevSignal.getPosition()
            val next = nextSignal.getPosition()
            val offsetX = prev.x - next.x
            val offsetY = prev.y - next.y
            // ignore the first and the last elements since we can't modify them directly since it's the source
            if(i != 0 ) {
                if (abs(offsetX) <= 10f) {
                    prevSignal.updatePosition(next.x, prev.y)
                } else if (abs(offsetY) <= 10f) {
                    prevSignal.updatePosition(prev.x, next.y)
                }
            }
            // snap to the parent source node
            else{
                if (abs(offsetX) <= 10f) {
                    nextSignal.updatePosition(prev.x, next.y)
                } else if (abs(offsetY) <= 10f) {
                    nextSignal.updatePosition(next.x, prev.y)
                }
            }
            lines[i].color = (if(nextSignal.selected || prevSignal.selected) LINE_MARKER_ACTIVE else LINE_MARKER_INACTIVE)
            lines[i].updatePosition(prev.x,prev.y,next.x,next.y)
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
        for(i in 1 until signals.size - 1){
            val obj = signals[i].contains(entity)
            if(obj != null){
                return obj
            }
        }
        return null
    }

    override fun contains(rect: Rectangle): CNode? {
        for(i in 1 until signals.size - 1){
            val obj = signals[i].contains(rect)
            if(obj != null){
                return obj
            }
        }
        return null
    }


    fun clone(from: ListNode, to: ListNode, signalFrom: Int, signalTo: Int, scene: PlayGroundScene):LineMarker{
        return LineMarker(from, to, signalFrom, signalTo,index).also {it.initialize(scene)}
    }

}

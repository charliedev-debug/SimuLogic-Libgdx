package org.engine.simulogic.android.circuits.logic

import com.badlogic.gdx.math.Rectangle
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.interfaces.ICollidable
import org.engine.simulogic.android.circuits.components.interfaces.IUpdate
import org.engine.simulogic.android.circuits.components.lines.LineMarker
import org.engine.simulogic.android.scene.Entity
import org.engine.simulogic.android.scene.PlayGroundScene

class ListNode(val value : CNode,
               val next: MutableList<ListNode> = mutableListOf(),
               val parent: MutableList<ListNode> = mutableListOf()): ICollidable, IUpdate{
    private val lineMarkersChildren = mutableListOf<LineMarker>()
    fun insertChild(child: ListNode, signalFrom: Int, signalTo: Int, scene: PlayGroundScene) {
        next.add(child)
        child.parent.add(this)
        lineMarkersChildren.add(LineMarker(this, child,signalFrom, signalTo, index = next.size - 1).apply { initialize(scene) })
    }

    fun insertChildUnmarked(child: ListNode, marker: LineMarker){
        next.add(child)
        child.parent.add(this)
        lineMarkersChildren.add(marker)
    }

    override fun contains(x: Float, y: Float): CNode? {
        val parenNodeCollision = value.contains(x, y)
        if(parenNodeCollision != null){
            return parenNodeCollision
        }
        lineMarkersChildren.forEach {lineMarker ->
            val lineMarkerChild = lineMarker.contains(x, y)
            if(lineMarkerChild != null){
                return lineMarkerChild
            }
        }
        return null
    }

    override fun contains(entity: CNode): CNode? {
        val parenNodeCollision = value.contains(entity)
        if(parenNodeCollision != null){
            return parenNodeCollision
        }
        lineMarkersChildren.forEach {lineMarker ->
            val lineMarkerChild = lineMarker.contains(entity)
            if(lineMarkerChild != null){
                return lineMarkerChild
            }
        }
        return null
    }

    override fun contains(rect: Rectangle): CNode? {
        val parenNodeCollision = value.contains(rect)
        if(parenNodeCollision != null){
            return parenNodeCollision
        }
        lineMarkersChildren.forEach {lineMarker ->
            val lineMarkerChild = lineMarker.contains(rect)
            if(lineMarkerChild != null){
                return lineMarkerChild
            }
        }
        return null
    }

    override fun update() {
         value.update()
        lineMarkersChildren.forEach {lineMarker ->
            lineMarker.update()
        }
    }

    fun getLineMarkerChildren():List<LineMarker>{
        return lineMarkersChildren
    }

    fun getLastMarkerChild():LineMarker{
        return lineMarkersChildren[lineMarkersChildren.size - 1]
    }

    fun clone():ListNode{
        return ListNode(value.clone() as CNode)
    }
}

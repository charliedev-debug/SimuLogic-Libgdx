package org.engine.simulogic.android.circuits.logic

import com.badlogic.gdx.math.Rectangle
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.interfaces.ICollidable
import org.engine.simulogic.android.circuits.components.interfaces.IUpdate
import org.engine.simulogic.android.circuits.components.lines.LineMarker
import org.engine.simulogic.android.circuits.storage.AutoSave
import org.engine.simulogic.android.scene.Entity
import org.engine.simulogic.android.scene.PlayGroundScene
import java.util.Collections

class ListNode(val value : CNode,
               val next: MutableList<ListNode> = mutableListOf(),
               val parent: MutableList<ListNode> = mutableListOf()): ICollidable, IUpdate{
    private val lineMarkersChildren:MutableList<LineMarker> = Collections.synchronizedList(mutableListOf<LineMarker>())
    fun insertChild(child: ListNode, signalFrom: Int, signalTo: Int, scene: PlayGroundScene) {
        next.add(child)
        child.parent.add(this)
        lineMarkersChildren.add(LineMarker(scene,this, child,signalFrom, signalTo, index = next.size - 1).apply { initialize(scene) })
        AutoSave.dataChanged = true
    }

    fun insertChildUnmarked(child: ListNode, marker: LineMarker){
        next.add(child)
        child.parent.add(this)
        lineMarkersChildren.add(marker)
        AutoSave.dataChanged = true
    }

    fun removeMarker(marker: LineMarker){
         next.removeIf { it == marker.from }
         lineMarkersChildren.remove(marker)
        AutoSave.dataChanged = true
    }

    fun insertMarker(marker: LineMarker){
        next.add(marker.from)
        lineMarkersChildren.add(marker.apply { index = next.size - 1 })
        AutoSave.dataChanged = true
    }

    fun detachSelf(){
        synchronized(lineMarkersChildren){
            // use an iterator to prevent concurrent exceptions since we still need to modify this list on another thread
            lineMarkersChildren.listIterator().also { iterator->
               while (iterator.hasNext()){
                   iterator.next().detachSelf()
                   iterator.remove()
               }
           }
        }
        next.clear()
        value.detachSelf()
    }

    fun attachSelf(){
        value.attachSelf()
    }

    fun clearConnections(){
        synchronized(lineMarkersChildren){
            lineMarkersChildren.listIterator().also { iterator->
                while (iterator.hasNext()){
                    iterator.next().detachSelf()
                    iterator.remove()
                }
            }
            next.clear()
        }
    }

    override fun contains(x: Float, y: Float): CNode? {
        val parenNodeCollision = value.contains(x, y)
        if(parenNodeCollision != null){
            return parenNodeCollision
        }
            lineMarkersChildren.forEach { lineMarker ->
                val lineMarkerChild = lineMarker.contains(x, y)
                if (lineMarkerChild != null) {
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
            lineMarkersChildren.forEach { lineMarker ->
                val lineMarkerChild = lineMarker.contains(entity)
                if (lineMarkerChild != null) {
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
            lineMarkersChildren.forEach { lineMarker ->
                val lineMarkerChild = lineMarker.contains(rect)
                if (lineMarkerChild != null) {
                    return lineMarkerChild
                }
            }
        return null
    }

    override fun update() {
         value.update()
            lineMarkersChildren.forEach { lineMarker ->
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

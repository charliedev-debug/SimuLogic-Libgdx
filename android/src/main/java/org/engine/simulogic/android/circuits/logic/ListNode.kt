package org.engine.simulogic.android.circuits.logic

import com.badlogic.gdx.math.Rectangle
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.interfaces.ICollidable
import org.engine.simulogic.android.circuits.components.interfaces.IUpdate
import org.engine.simulogic.android.circuits.components.lines.LineMarker
import org.engine.simulogic.android.circuits.storage.AutoSave
import org.engine.simulogic.android.scene.PlayGroundScene
import java.util.Collections

class ListNode(val value : CNode,
               val parent: MutableList<ListNode> = mutableListOf()): ICollidable, IUpdate{
    private val lineMarkersChildren:MutableList<LineMarker> = Collections.synchronizedList(mutableListOf<LineMarker>())
    var visited = false
    var callingRef = this
    fun insertChild(child: ListNode, signalFrom: Int, signalTo: Int,connection:Connection, scene: PlayGroundScene):LineMarker {
        child.parent.add(this)
        val marker = LineMarker(scene,this, child,signalFrom, signalTo, index = lineMarkersChildren.size ).apply { initialize(scene, connection) }
        lineMarkersChildren.add(marker)
        AutoSave.dataChanged = true
        lineMarkersChildren.forEachIndexed { index, marker ->
            marker.index = index
        }
        return marker
    }

    fun insertChild(parent:ListNode, child: ListNode, signalFrom: Int, signalTo: Int,connection:Connection, scene: PlayGroundScene):LineMarker {
        child.parent.add(parent)
        val marker = LineMarker(scene,parent, child,signalFrom, signalTo, index = lineMarkersChildren.size).apply { initialize(scene, connection) }
        lineMarkersChildren.add(marker)
        AutoSave.dataChanged = true
        lineMarkersChildren.forEachIndexed { index, marker ->
            marker.index = index
        }
        return marker
    }

    fun insertChildUnmarked(child: ListNode, marker: LineMarker){
        child.parent.add(this)
        lineMarkersChildren.add(marker)
        AutoSave.dataChanged = true
    }

    fun removeMarker(marker: LineMarker){
         marker.from.value.reset()
         marker.to.value.reset()
         lineMarkersChildren.remove(marker)
        AutoSave.dataChanged = true
    }

    fun removeMarker(node:ListNode):LineMarker?{
        lineMarkersChildren.listIterator().also { iterator->
            while (iterator.hasNext()){
                val marker = iterator.next()
                if(marker.to == node){
                    marker.detachSelf()
                    iterator.remove()
                    return marker
                }
            }
        }
        return null
    }

    fun detachSelf(){
            // use an iterator to prevent concurrent exceptions since we still need to modify this list on another thread
            lineMarkersChildren.listIterator().also { iterator->
               while (iterator.hasNext()){
                   iterator.next().detachSelf()
                   iterator.remove()
               }
           }
        value.detachSelf()
    }

    fun attachSelf(){
        value.attachSelf()
    }

    override fun contains(x: Float, y: Float): CNode? {
        if(!value.collidable) return null
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
        if(!value.collidable) return null
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
        if(!value.collidable) return null
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
            val iterator = lineMarkersChildren.listIterator()
            while (iterator.hasNext()) {
                iterator.next().update()
            }
    }

    fun getLineMarkerChildren():List<LineMarker>{
        return lineMarkersChildren
    }

    fun sortLinMarkersByDepth(){
        lineMarkersChildren.sortBy { it.getNodeOriginDepth() }
    }

    fun clone():ListNode{
        return ListNode(value.clone() as CNode)
    }
}

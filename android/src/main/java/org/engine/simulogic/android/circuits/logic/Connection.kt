package org.engine.simulogic.android.circuits.logic

import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.interfaces.IUpdate
import org.engine.simulogic.android.circuits.storage.AutoSave
import org.engine.simulogic.android.scene.PlayGroundScene
import java.util.Collections
import java.util.LinkedList

class Connection : Iterable<ListNode>, IUpdate {

    private var nodes: MutableList<ListNode> =Collections.synchronizedList(LinkedList())
    var executionPoints:MutableList<ListNode> = Collections.synchronizedList(LinkedList())
    fun insertExecutionPoint(node:ListNode){
        synchronized(nodes) {
            nodes.add(node)
            executionPoints.add(node)
            AutoSave.dataChanged = true
        }
    }

    operator fun get(index:Int):ListNode{
        return nodes[index]
    }

    fun insertNode(node:ListNode){
        synchronized(nodes) {
            nodes.add(node)
            AutoSave.dataChanged = true
        }
    }

    fun removeNode(node:ListNode){
        synchronized(nodes){
            nodes.remove(node)
            AutoSave.dataChanged = true
        }
    }

    fun insertConnection(parent:ListNode, child:ListNode, signalFrom: Int, signalTo: Int, scene:PlayGroundScene){
        parent.insertChild(child, signalFrom, signalTo, scene)
    }

    override fun update() {
        synchronized(nodes) {
            forEach { it.update() }
        }
    }

    override fun iterator(): Iterator<ListNode> {
        return nodes.iterator()
    }

    fun size():Int{
        return nodes.size
    }


}

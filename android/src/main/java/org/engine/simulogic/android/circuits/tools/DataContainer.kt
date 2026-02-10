package org.engine.simulogic.android.circuits.tools

import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.logic.ListNode
import kotlin.math.abs

class DataContainer:Iterable<ListNode>{
    private val elements = mutableListOf<ListNode>()
    var mode = -1
    companion object{
        const val NONE = -1
        const val CUT = 0
        const val COPY = 1
        const val DELETE = 2
        const val PASTE = 3
    }
    private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return abs(x1 - x2) + abs(y1 - y2)
    }

    private fun distance(node:CNode):Float{
        return node.getPosition().x + node.getPosition().y
    }

    fun insertTo(dataContainer: DataContainer){
        elements.forEach {
            dataContainer.insert(it)
        }
    }
    fun insert(element: ListNode){
        elements.add(element)
    }

    fun remove(index:Int){
        elements.removeAt(index)
    }

    fun remove(list:List<ListNode>){
        elements.removeAll(list)
    }

    operator fun get(index: Int): ListNode {
        return elements[index]
    }

    fun size():Int{
        return elements.size
    }

    fun isEmpty():Boolean{
        return elements.isEmpty()
    }

    fun isNotEmpty():Boolean{
        return elements.isNotEmpty()
    }

    fun first():ListNode{
        return elements[0]
    }

    fun removeFirst(){
        elements.removeAt(0)
    }

    fun last():ListNode{
        return elements[elements.size - 1]
    }

    fun clear(){
        elements.clear()
    }

    fun sort(x : Float, y : Float){
        elements.sortBy { distance(x,y,it.value.getPosition().x,it.value.getPosition().y) }
    }

    fun sortX(){
        elements.sortBy { it.value.getPosition().x }
    }
    fun sortY(){
        elements.sortBy { it.value.getPosition().y }
    }

    override fun iterator(): Iterator<ListNode> {
        return object : Iterator<ListNode> {
            private var index = 0

            override fun hasNext(): Boolean {
                return index < elements.size
            }

            override fun next(): ListNode {
                if (!hasNext()) {
                    throw NoSuchElementException()
                }
                return elements[index++]
            }
        }
    }
}

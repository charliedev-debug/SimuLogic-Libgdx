package org.engine.simulogic.android.circuits.algorithms

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.lines.CLine
import org.engine.simulogic.android.circuits.components.other.CGroup
import org.engine.simulogic.android.circuits.components.other.CRangeSelect
import org.engine.simulogic.android.circuits.components.other.CRect
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.scene.LayerEnums
import org.engine.simulogic.android.scene.PlayGroundScene

class QuadTree(center:Vector2, private val width:Float, private val height:Float, private val scene: PlayGroundScene) {
    data class QNode(var data:ListNode)
    val rect = CRect(center.x, center.y, width, height, Color(0.3f,0f,0f,0.5f), scene)
    private var topLeftQuadTree:QuadTree? = null
    private var topRightQuadTree:QuadTree? = null
    private var bottomLeftQuadTree:QuadTree? = null
    private var bottomRightQuadTree:QuadTree? = null
    private var qItems= mutableListOf<QNode>()
    private var depth = 0
    var itemsTotal = 0
    var lines = mutableListOf<CLine>()
    init {
        scene.getLayerById(LayerEnums.DEBUG_LAYER.name).also { layer ->
            layer.attachChild(CLine(center.x - width/2f, center.y + height / 2f, center.x + width / 2, center.y + height / 2f, 1f).apply { lines.add(this) })
            layer.attachChild(CLine(center.x - width/2f, center.y - height / 2f, center.x + width / 2, center.y - height / 2f, 1f).apply { lines.add(this) })

            layer.attachChild(CLine(center.x - width/2f, center.y - height / 2f, center.x - width / 2, center.y + height / 2f, 1f).apply { lines.add(this) })
            layer.attachChild(CLine(center.x + width/2f, center.y - height / 2f, center.x + width / 2, center.y + height / 2f, 1f).apply { lines.add(this) })
        }
    }

    fun release(){
        topRightQuadTree?.release()
        topLeftQuadTree?.release()
        bottomLeftQuadTree?.release()
        bottomRightQuadTree?.release()
        lines.forEach {
            it.detachSelf()
        }
    }
    companion object{
        fun build(connection:Connection, scene: PlayGroundScene):QuadTree{
            val values = mutableListOf<ListNode>()
            connection.forEach { node->
                if(node.value !is CRangeSelect) {
                    values.add(node)
                }else if(node.value is CGroup){
                    values.add(node)
                }
                node.getLineMarkerChildren().forEach {
                    for( i in 1 until  it.signals.size - 1){
                        values.add(ListNode(it.signals[i]).apply { callingRef = node })
                    }
                }
                node.value.signals.forEach {
                    values.add(ListNode(it).apply { callingRef = node })
                }

            }
            values.sortBy { it.value.getPosition().x + it.value.getWidth() / 2f}
            val farX = values.last()
            val nearX = values.first()
            values.sortBy { it.value.getPosition().y - it.value.getHeight()/2f }
            val farY = values.last()
            val nearY = values.first()

            val maxWidth = (farX.value.getPosition().x) - (nearX.value.getPosition().x ) + farX.value.getWidth() + nearX.value.getWidth()
            val maxHeight = (farY.value.getPosition().y - nearY.value.getPosition().y) + farY.value.getHeight()  + nearY.value.getHeight()

            val centerX = nearX.value.getPosition().x + maxWidth / 2f - nearX.value.getWidth() / 2f
            val centerY = nearY.value.getPosition().y + maxHeight / 2f - nearY.value.getHeight() / 2f
            // all groups should be at the bottom of the collision tree
            values.sortBy { it.value is CGroup }
            return QuadTree(Vector2(centerX, centerY),maxWidth, maxHeight, scene).also {
                    values.forEach {value->
                        it.insert(value)
                    }
                    it.itemsTotal = values.size
            }
        }
    }

    fun insert(data:ListNode){
        if(!contains(data.value)){
            return
        }
        if(rect.getWidth() <= 100f || rect.getHeight() <= 100f){
            qItems.add(QNode(data))
            return
        }
        val halfWidth = width / 2f
        val halfHeight = height / 2f
        if (topLeftQuadTree == null) {
            topLeftQuadTree = QuadTree(
                Vector2(
                    rect.getPosition().x - halfWidth / 2f,
                    rect.getPosition().y + halfHeight / 2f
                ),
                halfWidth,
                halfHeight,
                scene
            )
        }
        topLeftQuadTree!!.depth  = depth + 1
        if(topRightQuadTree == null) {
            topRightQuadTree = QuadTree(
                Vector2(
                    rect.getPosition().x + halfWidth / 2f,
                    rect.getPosition().y + halfHeight / 2f
                ),
                halfWidth,
                halfHeight,
                scene
            )
        }
        topRightQuadTree!!.depth  = depth + 1
        if(bottomLeftQuadTree == null) {
            bottomLeftQuadTree = QuadTree(
                Vector2(
                    rect.getPosition().x - halfWidth / 2f,
                    rect.getPosition().y - halfHeight / 2f
                ),
                halfWidth,
                halfHeight,
                scene
            )
        }
        bottomLeftQuadTree!!.depth  = depth + 1
        if(bottomRightQuadTree == null) {
            bottomRightQuadTree = QuadTree(
                Vector2(
                    rect.getPosition().x + halfWidth / 2f,
                    rect.getPosition().y - halfHeight / 2f
                ),
                halfWidth,
                halfHeight,
                scene
            )
        }
        bottomRightQuadTree!!.depth  = depth + 1
        if(topLeftQuadTree!!.contains(data.value.getBoundingBox())){
            topLeftQuadTree!!.insert(data)
        }

        if(topRightQuadTree!!.contains(data.value.getBoundingBox())){
            topRightQuadTree!!.insert(data)
        }

        if(bottomLeftQuadTree!!.contains(data.value.getBoundingBox())){
            bottomLeftQuadTree!!.insert(data)
        }

        if(bottomRightQuadTree!!.contains(data.value.getBoundingBox())){
            bottomRightQuadTree!!.insert(data)
        }

    }

    fun searchMultiple(box: Rectangle, data:MutableList<ListNode>){
        if(qItems.isNotEmpty()){
            qItems.forEach { item->
                 if(item.data.value.getBoundingBox().overlaps(box)) {
                     data.add(item.data)
                 }
            }
        }
        if(bottomLeftQuadTree!= null && bottomLeftQuadTree!!.contains(box)){
            bottomLeftQuadTree!!.searchMultiple(box, data)
        }
        if(topLeftQuadTree!= null && topLeftQuadTree!!.contains(box)){
            topLeftQuadTree!!.searchMultiple(box, data)
        }
        if(topRightQuadTree!= null && topRightQuadTree!!.contains(box)){
            topRightQuadTree!!.searchMultiple(box, data)
        }
        if(bottomRightQuadTree!= null && bottomRightQuadTree!!.contains(box)){
            bottomRightQuadTree!!.searchMultiple(box, data)
        }
    }

    fun searchSingle(box: Rectangle):ListNode?{
        if(!contains(box)){
            return null
        }
        if(qItems.isNotEmpty()){
            return qItems.find { it.data.value.getBoundingBox().overlaps(box) }?.data
        }
        if(bottomLeftQuadTree!= null && bottomLeftQuadTree!!.contains(box)){
            val item = bottomLeftQuadTree!!.searchSingle(box)
            if(item != null) {
                return item
            }
        }
        if(topLeftQuadTree!= null && topLeftQuadTree!!.contains(box)){
            val item = topLeftQuadTree!!.searchSingle(box)
            if(item != null) {
                return item
            }
        }
        if(topRightQuadTree!= null && topRightQuadTree!!.contains(box)){
            val item = topRightQuadTree!!.searchSingle(box)
            if(item != null) {
                return item
            }
        }

        if(bottomRightQuadTree!= null && bottomRightQuadTree!!.contains(box)){
            val item = bottomRightQuadTree!!.searchSingle(box)
            if(item != null) {
                return item
            }
        }
        return null
    }
    fun contains(data: CNode):Boolean{
        return data.getBoundingBox().overlaps(rect.getBoundingBox())
    }
    fun contains(box: Rectangle):Boolean{
        return box.overlaps(rect.getBoundingBox())
    }
}

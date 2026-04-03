package org.engine.simulogic.android.circuits.algorithms

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Tree
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.lines.CLine
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.scene.Entity
import org.engine.simulogic.android.scene.LayerEnums
import org.engine.simulogic.android.scene.PlayGroundScene
import kotlin.math.abs
import kotlin.math.sign

class WirePathRouter(private val connection: Connection, private val scene: PlayGroundScene) {

    private var gCost = 0f
    private var hCost = 0f

    data class PathNode(
        val i: Int,
        val j: Int,
        var visited: Boolean = false,
        var distance: Int = 0,
        var valid: Boolean = true
    )

    class TreeNode(var x: Float, var y: Float) {
        var child: TreeNode? = null

        companion object {
            fun create(x: Float, y: Float, parent: TreeNode): TreeNode {
                return TreeNode(x, y).also {
                    parent.child = it
                }
            }
        }
    }

    fun route(startNode: CNode, endNode: CNode): ArrayDeque<TreeNode> {
        val start = startNode.getPosition()
        val end = endNode.getPosition()
        val quadTree = QuadTree.build(connection, scene)
        val step = 30
        val width = quadTree.rect.getWidth()
        val height = quadTree.rect.getHeight()
        val x = quadTree.rect.getPosition().x
        val y = quadTree.rect.getPosition().y
        val originX = start.x
        val originY = start.y
        var startNodeTreeA: TreeNode?
        var startNodeTreeB: TreeNode? = null
        /*Algorithm
        * 1. Create a tree data structure that moves in 4  directions either top, bottom || left , right.
        * 2. Propagate in either directions but in a straight 90-degree angle.
        *    - Move points towards the end point.
        * 3. Test if the next cell is a valid position if not split the node into a joint and change direction.
        *    - The direction split should be in two directions either top , bottom || left, right.
        *    - Let these new nodes be the children of the parent node.
        * 4. Repeat 2 & 3 until we reach the end position.
        * 5. After reaching the end position, propagate through the children from the end point
        *  and save valid line segments as valid routes.*/


        var currentNode = TreeNode(originX , originY)
             startNodeTreeA = currentNode
            currentNode = TreeNode.create(currentNode.x, currentNode.y, currentNode)
        val directionX = if(end.x > currentNode.x) 1f else -1f
        val directionY = if(end.y > currentNode.y) 1f else -1f

        val distanceY = abs(currentNode.y - end.y)
        val stepCountY = distanceY.toInt() / step
        currentNode = rayCastYAxis(currentNode, quadTree, startNode, endNode, stepCountY, step, directionX, directionY)

        val distanceX = abs(currentNode.x - end.x)
        val stepCountX = distanceX.toInt() / step

        currentNode = TreeNode.create(currentNode.x, currentNode.y, currentNode)

        currentNode = rayCastXAxis(currentNode, quadTree, startNode, endNode, stepCountX, step, directionX, directionY)

        // set the end node to the tree
        TreeNode.create(end.x, end.y, currentNode)

        var currentNodeB = TreeNode(originX , originY)
        startNodeTreeB = currentNodeB
        currentNodeB = TreeNode.create(currentNodeB.x, currentNodeB.y, currentNodeB)
        val directionXB = if(end.x > currentNodeB.x ) 1f else -1f
        val directionYB = if(end.y > currentNodeB.y) 1f else -1f

        val distanceXB = abs(currentNodeB.x - end.x)
        val stepCountXB = distanceXB.toInt() / step

        currentNodeB = rayCastXAxis(currentNodeB, quadTree, startNode, endNode, stepCountXB, step, directionXB, directionYB)

        currentNodeB = TreeNode.create(currentNodeB.x, currentNodeB.y, currentNodeB)
        val distanceYB = abs(currentNodeB.y - end.y)
        val stepCountYB = distanceYB.toInt() / step
        currentNodeB = rayCastYAxis(currentNodeB, quadTree, startNode, endNode, stepCountYB, step, directionXB, directionYB)

        // set the end node to the tree
        TreeNode.create(end.x, end.y, currentNodeB)

       val directionAList = ArrayDeque<TreeNode>()
       val directionBList = ArrayDeque<TreeNode>()
       while (startNodeTreeA != null) {
          //  println("X= ${startNodeTree.x} Y= ${startNodeTree.y}")
            directionAList.add(startNodeTreeA)
            startNodeTreeA = startNodeTreeA.child
        }

        while (startNodeTreeB != null) {
            //  println("X= ${startNodeTree.x} Y= ${startNodeTree.y}")
            directionBList.add(startNodeTreeB)
            startNodeTreeB = startNodeTreeB.child
        }

        if(directionAList.size < directionBList.size){
           /* directionAList.onEach {
                drawPathHighlight(Vector2(it.x , it.y ), 30f, 30f)
            }*/
            return directionAList
        }else{
           /* directionBList.onEach {
                drawPathHighlight(Vector2(it.x , it.y ), 30f, 30f)
            }*/
            return  directionBList
        }
    }

    fun rayCastXAxis(currentNodeInput: TreeNode, quadTree: QuadTree, startNode: CNode, endNode: CNode, stepCountX: Int, step:Int, directionX: Float, directionY: Float ): TreeNode{
        var currentStepX = 0
        val collider = Rectangle(0f, 0f, 30f, 30f)
        var currentNode = currentNodeInput
        // println("Step count $stepCountX DistanceX $distanceX DirectionX $directionX")
        while (currentStepX < stepCountX  ){
            val offsetX = currentNode.x + step * directionX
            collider.setCenter(offsetX, currentNode.y)
            val hasQuad = quadTree.searchSingle(collider)
            if(hasQuad == null || (hasQuad.value == endNode|| hasQuad.value == startNode)){
                currentStepX++
                if(currentStepX == stepCountX){
                    currentNode.x = endNode.getPosition().x
                }else {
                    currentNode.x = offsetX
                }
            }else{
                currentNode = TreeNode.create(currentNode.x, currentNode.y, currentNode)
                currentNode = TreeNode.create(currentNode.x, currentNode.y + hasQuad.value.getHeight() * directionY, currentNode)
                currentNode = TreeNode.create(currentNode.x, currentNode.y , currentNode)
            }
        }
        return currentNode
    }

    fun rayCastYAxis(currentNodeInput: TreeNode, quadTree: QuadTree, startNode: CNode, endNode: CNode, stepCountY:Int, step:Int, directionX: Float, directionY: Float): TreeNode{
        var currentStepY = 0
        var currentNode = currentNodeInput
        val collider = Rectangle(0f, 0f, 30f, 30f)
        while(currentStepY < stepCountY){
            val offsetY = currentNode.y + step * directionY
            collider.setPosition(currentNode.x, offsetY)
            val hasQuad = quadTree.searchSingle(collider)
            if(hasQuad == null || (hasQuad.value == endNode || hasQuad.value == startNode)){
                currentStepY++
                if(currentStepY >= stepCountY){
                    currentNode.y = endNode.getPosition().y
                }else {
                    currentNode.y = offsetY
                }
            }else{
                currentNode = TreeNode.create(currentNode.x , currentNode.y, currentNode)
                currentNode = TreeNode.create(currentNode.x + hasQuad.value.getWidth() * directionX, currentNode.y, currentNode)
                currentNode = TreeNode.create(currentNode.x , currentNode.y, currentNode)
            }
        }
        return currentNode
    }

    fun drawPathNode(originX: Float, originY: Float, node: PathNode, width: Float, height: Float) {
        val y = node.i * width
        val x = node.j * height
        drawPathHighlight(
            Vector2(originX + x + width / 2f, originY + y + height / 2f),
            width,
            height
        )
    }

    fun drawPathHighlight(center: Vector2, width: Float, height: Float) {
        scene.getLayerById(LayerEnums.DEBUG_LAYER.name).also { layer ->
            layer.attachChild(
                CLine(
                    center.x - width / 2f,
                    center.y + height / 2f,
                    center.x + width / 2,
                    center.y + height / 2f,
                    1f
                )
            )
            layer.attachChild(
                CLine(
                    center.x - width / 2f,
                    center.y - height / 2f,
                    center.x + width / 2,
                    center.y - height / 2f,
                    1f
                )
            )

            layer.attachChild(
                CLine(
                    center.x - width / 2f,
                    center.y - height / 2f,
                    center.x - width / 2,
                    center.y + height / 2f,
                    1f
                )
            )
            layer.attachChild(
                CLine(
                    center.x + width / 2f,
                    center.y - height / 2f,
                    center.x + width / 2,
                    center.y + height / 2f,
                    1f
                )
            )
        }
    }
}

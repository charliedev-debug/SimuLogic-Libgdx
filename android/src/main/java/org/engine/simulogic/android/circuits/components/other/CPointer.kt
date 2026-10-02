package org.engine.simulogic.android.circuits.components.other

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.engine.simulogic.android.circuits.algorithms.QuadTree
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.lines.CLine
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.circuits.theme.EnvironmentTheme
import org.engine.simulogic.android.events.MotionGestureListener
import org.engine.simulogic.android.scene.LayerEnums
import org.engine.simulogic.android.scene.PlayGroundScene
import kotlin.math.abs
import kotlin.math.sign

class CPointer (x:Float, y:Float, val camera: OrthographicCamera,val connection: Connection,val gestureListener: MotionGestureListener,val scene: PlayGroundScene) : CNode() {

    private val linesVertical = mutableListOf<CLine>()
    private val linesHorizontal = mutableListOf<CLine>()
    private val collidedItems = mutableListOf<ListNode>()
    private var collideRangeCenterY: CRect
    private var collideRangeCenterX: CRect
    private var collideRangeViewPort: CRect
    private var previousX = 0f
    private var previousY = 0f
    private var pSelectedNode: CNode? = null
    private var quadTreeInstance: QuadTree? = null
    var selectedNode: CNode? = null

    init {

        val textureAtlas = scene.assetManager.get("${EnvironmentTheme.name}.atlas", TextureAtlas::class.java)
        val spriteRegion = textureAtlas.findRegion("POINTER")
        val width = 40f
        val height = 40f
        sprite = Sprite(spriteRegion).apply {
            setOrigin(x, y)
            setSize(width, height)
            setOriginCenter()
            rotation = 0f
            setPosition(x - width / 2f, y - height / 2f)
        }

        collideRangeCenterY = CRect(0f,0f,0f,0f, Color(1f,1f,1f,0.4f),scene)
        collideRangeCenterX = CRect(0f,0f,0f,0f, Color(1f,1f,1f,0.4f),scene)
        collideRangeViewPort= CRect(0f,0f,0f,0f, Color(0f,1f,0f,0.2f),scene)
        scene.getLayerById(LayerEnums.SCREEN_LAYER.name).also { layer ->
            layer.attachChild(this)
           //@debug layer.attachChild(collideRangeViewPort)
          //@debug layer.attachChild(collideRangeCenterY)
          //@debug layer.attachChild(collideRangeCenterX)
        }

    }

    private fun createLineVertical(): CLine{
      return  CLine(0f,0f,0f,0f,1f).also { it.color = Color.RED }
    }

    override fun execute() {

        selectedNode?.also { node ->
            if (previousX != getPosition().x || previousY != getPosition().y || pSelectedNode != selectedNode) {
                linesVertical.onEach {
                    it.isVisible = false
                }
                linesHorizontal.onEach {
                    it.isVisible = false
                }

                collideRangeCenterX.setSize(camera.viewportWidth * camera.zoom, node.getHeight() )
                collideRangeCenterX.updatePosition(camera.position.x,node.getPosition().y)

                collideRangeCenterY.setSize(node.getWidth() , camera.viewportHeight * camera.zoom)
                collideRangeCenterY.updatePosition(node.getPosition().x, camera.position.y)

                collideRangeViewPort.setSize(camera.viewportWidth * camera.zoom, camera.viewportHeight * camera.zoom)
                collideRangeViewPort.updatePosition(camera.position)

                if(quadTreeInstance == null || pSelectedNode != selectedNode){
                    quadTreeInstance = QuadTree.build(connection, scene, includeSignals = false)
                }

                quadTreeInstance?.also { tree ->
                    // perform acrossY
                    tree.searchMultiple(collideRangeCenterY.getBoundingBox(), collidedItems)
                    // filter out components that are off-screen & exclude origin
                    collidedItems.removeIf { it.value.contains(collideRangeViewPort) == null}
                    if(collidedItems.isNotEmpty()){
                        collidedItems.sortBy {  it.value.getPosition().y}
                        scene.getLayerById(LayerEnums.CONNECTION_LAYER.name).also { connectionLayer ->
                            for (index in 0 until collidedItems.size - 1) {
                                val first = collidedItems[index].value
                                val second = collidedItems[index + 1].value
                                val dirY = sign(first.getPosition().y - second.getPosition().y)
                                val markerOffset = 30f
                                if (linesVertical.size <= index) {
                                    createLineVertical().also { line ->
                                        connectionLayer.attachChild(line)
                                        line.isVisible = false
                                        linesVertical.add(line)
                                    }
                                }
                                linesVertical[index].also { line ->
                                    // test center
                                    line.updatePosition(
                                        first.getPosition().x,
                                        second.getPosition().y + dirY * second.getHeight() / 2f + markerOffset * dirY,
                                        first.getPosition().x,
                                        first.getPosition().y + dirY * first.getHeight() / 2f * -1f + dirY * -markerOffset
                                    )
                                    // it must be in relation to the selected node
                                    line.isVisible =
                                        abs(node.getPosition().x - second.getPosition().x) <= 2f &&
                                            abs(node.getPosition().x - first.getPosition().x) <= 2f
                                }
                            }
                        }
                    }else{
                        linesVertical.onEach { line ->
                            line.isVisible = false
                        }
                    }

                    collidedItems.clear()
                      // perform acrossX
                   tree.searchMultiple(collideRangeCenterX.getBoundingBox(), collidedItems)
                    // filter out components that are off-screen & exclude origin
                    collidedItems.removeIf { it.value.contains(collideRangeViewPort) == null}
                    if(collidedItems.isNotEmpty()){
                        collidedItems.sortBy { it.value.getPosition().x }
                        scene.getLayerById(LayerEnums.CONNECTION_LAYER.name).also { connectionLayer ->
                        for(index in 0 until collidedItems.size -1){
                            val first = collidedItems[index].value
                            val second = collidedItems[index + 1].value
                            val dirX = sign(second.getPosition().x - first.getPosition().x)
                            val markerOffset = 30f
                            if(linesHorizontal.size <= index){
                                createLineVertical().also {line->
                                     connectionLayer.attachChild(line)
                                    line.isVisible = false
                                    linesHorizontal.add(line)
                                }
                            }
                            linesHorizontal[index].also { line ->
                                line.updatePosition(
                                    first.getPosition().x + dirX * first.getWidth() / 2f + markerOffset * dirX,
                                    first.getPosition().y,
                                    second.getPosition().x +  dirX * first.getWidth() / 2f * -1f + dirX * -markerOffset,
                                    first.getPosition().y
                                )
                                // it must be in relation to the selected node
                                line.isVisible = abs(node.getPosition().y - second.getPosition().y) <= 2f &&
                                    abs(node.getPosition().y - first.getPosition().y) <= 2f
                            }
                        }
                            }
                    }else{
                        linesHorizontal.onEach { line ->
                            line.isVisible = false
                        }
                    }

                    collidedItems.clear()
                }
            }
        }

        if(selectedNode== null|| selectedNode?.selected == false || selectedNode?.isRemoved == true
            || selectedNode is CSignal || selectedNode is CRangeLine){
            linesVertical.onEach {
                it.isVisible = false
            }
            linesHorizontal.onEach {
                it.isVisible = false
            }
        }
        previousX = getPosition().x
        previousY = getPosition().y
        pSelectedNode = selectedNode
    }
}

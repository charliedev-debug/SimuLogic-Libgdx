package org.engine.simulogic.android.circuits.components.other

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.CTypes
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.SnapAlign
import org.engine.simulogic.android.circuits.tools.DataContainer
import org.engine.simulogic.android.scene.LayerEnums
import org.engine.simulogic.android.scene.PlayGroundScene

class CGroup(x:Float, y:Float,  scene: PlayGroundScene): CNode() {
    val dataContainer = DataContainer()
    private var previousPosition = Vector2(x ,y)
    private var previousSnapPosition = Vector2()
    var collidableChildren = true
    private val snapAlign = SnapAlign()
    val componentGroupIds = mutableListOf<Int>()

     init {
         val textureAtlas = scene.assetManager.get("component.atlas", TextureAtlas::class.java)
         val spriteRegion = textureAtlas.findRegion("TRANSPARENT")
         type = CTypes.GROUP
         this.rotationDirection = ROTATE_RIGHT
         sprite = Sprite(spriteRegion).apply {
             setOrigin(x , y)
             setSize(CDefaults.gateWidth, CDefaults.gateHeight)
             setOriginCenter()
             color = CDefaults.GROUP_SELECTED_COLOR
             setPosition(x ,y)
         }

         previousPosition.set(getPosition().x - x, getPosition().y - y)

         scene.getLayerById(LayerEnums.GATE_LAYER.name).also { layer ->
             layer.attachChild(this)
         }
     }

    fun insert(inputContainer: DataContainer, range: CRangeSelect){
        inputContainer.insertTo(dataContainer)
        dataContainer.forEach {node->
            node.value.collidable = false
        }
        setSize(range.getWidth(),range.getHeight())
        updatePosition(range.getPosition().x ,range.getPosition().y)
    }

    fun loadFromIds(connection:Connection){
        componentGroupIds.forEach {index->
            dataContainer.insert(connection[index].also { node->
                node.value.collidable = false
            })
        }
        updatePosition(getPosition().x - getWidth() / 2f,getPosition().y - getHeight() / 2f)
    }

    override fun translate(offsetX: Float, offsetY: Float) {
        updatePosition(getPosition().x - previousSnapPosition.x, getPosition().y - previousSnapPosition.y)
        updateGroup(-previousSnapPosition.x, -previousSnapPosition.y)
         previousPosition.add(offsetX, offsetY)
        snapAlign.getSnapCoordinates(previousPosition.x,  previousPosition.y).also { position->
            updatePosition(getPosition().x + position.x,getPosition().y + position.y)
            previousSnapPosition.set(position)
            updateGroup(position.x, position.y)
        }
    }

    private fun updateGroup(offsetX: Float, offsetY: Float){
        if(dataContainer.isNotEmpty()) {
            for (i in 0 until dataContainer.size()) {
                val p = dataContainer[i].value
                val ix = p.getPosition().x + offsetX
                val iy = p.getPosition().y + offsetY
                p.updatePosition(ix, iy)
            }
            dataContainer.forEach { data ->
                data.getLineMarkerChildren().forEach { marker ->
                    marker.signals.forEach { point ->
                        val nx = point.getPosition().x + offsetX
                        val ny = point.getPosition().y + offsetY
                        point.updatePosition(nx, ny)
                    }
                    marker.update()
                }
            }
            previousPosition.set(0f, 0f)
        }

    }

    fun resetPositionBuffers(){
        previousSnapPosition.setZero()
        previousPosition.setZero()
    }

    override fun update() {
         updateColor(if(selected) CDefaults.GROUP_SELECTED_COLOR else CDefaults.GROUP_UNSELECTED_COLOR)
    }

    override fun contains(entity: CNode): CNode? {
        if(collidableChildren) {
            dataContainer.forEach {
                it.value.collidable = true
                val childCollides = it.contains(entity)
                it.value.collidable = false
                if (childCollides != null) {
                    return childCollides
                }
            }
        }
        val parentCollides = super.contains(entity)
        if(parentCollides != null){
            return parentCollides
        }

        return null
    }

    override fun contains(rect: Rectangle): CNode? {
        if(collidableChildren) {
            dataContainer.forEach {
                it.value.collidable = true
                val childCollides = it.contains(rect)
                it.value.collidable = false
                if (childCollides != null) {
                    return childCollides
                }
            }
        }
        val parentCollides = super.contains(rect)
        if(parentCollides != null){
            return parentCollides
        }

        return null
    }

}

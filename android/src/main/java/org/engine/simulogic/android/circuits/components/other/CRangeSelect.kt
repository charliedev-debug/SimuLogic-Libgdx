package org.engine.simulogic.android.circuits.components.other

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Rectangle
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.CTypes
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.theme.EnvironmentTheme
import org.engine.simulogic.android.events.CollisionDetector
import org.engine.simulogic.android.scene.LayerEnums
import org.engine.simulogic.android.scene.PlayGroundScene
import kotlin.math.abs

open class CRangeSelect(initialX: Float, initialY: Float,protected val camera: OrthographicCamera? = null, val connection: Connection, private val scene: PlayGroundScene, protected val layerId:String = LayerEnums.SCREEN_LAYER.name)  : CNode() {

    protected var pointSize = 30f
    protected var previousZoom = 1f
    var rangeItems = mutableListOf<CollisionDetector.CollisionItem>()
    var collisionDetector = CollisionDetector(connection)
    var enableDragMotion = false

    init {
        val textureAtlas = scene.assetManager.get("${EnvironmentTheme.name}.atlas", TextureAtlas::class.java)
        val spriteRegion = textureAtlas.findRegion("TRANSPARENT")
        sprite = Sprite(spriteRegion).apply {
            setOrigin(initialX , initialY)
            setSize(200f, 200f)
            setOriginCenter()
            rotation = 0f
            setPosition(initialX - 100f,initialY - 100f)
        }

        sprite.color = CDefaults.GROUP_SELECTED_COLOR // Color(1f,0f,0f,0.27f)

        signals.add(CRangePoint(0f ,0f, CTypes.SIGNAL_RANGE_POINT,0, scene).also { point->
               point.setSize(pointSize,pointSize)
               point.updatePosition(getPosition().x - sprite.width / 2 , getPosition().y + sprite.height / 2f )
        })
        signals.add(CRangePoint(0f , 0f, CTypes.SIGNAL_RANGE_POINT,0, scene).also {point->
            point.setSize(pointSize,pointSize)
            point.updatePosition(getPosition().x + sprite.width / 2 , getPosition().y + sprite.height / 2f )
        })
        signals.add(CRangePoint(0f ,0f, CTypes.SIGNAL_RANGE_POINT,0, scene).also {point->
            point.setSize(pointSize,pointSize)
            point.updatePosition(getPosition().x - sprite.width / 2 , getPosition().y - sprite.height / 2f )
        })
        signals.add(CRangePoint(0f , 0f, CTypes.SIGNAL_RANGE_POINT,0, scene).also {point->
            point.setSize(pointSize,pointSize)
            point.updatePosition(getPosition().x + sprite.width / 2 , getPosition().y - sprite.height / 2f )
        })

        val signalTopLeft = signals[0] as CRangePoint
        val signalTopRight = signals[1] as CRangePoint
        val signalBottomLeft = signals[2] as CRangePoint
        val signalBottomRight = signals[3] as CRangePoint
        // all the points must be axis aligned
        signalTopLeft.apply {
            childX = this@CRangeSelect.signals[1] as CRangePoint
            childY = this@CRangeSelect.signals[2] as CRangePoint
        }
        signalTopRight.apply {
            childX = signalTopLeft
            childY = this@CRangeSelect.signals[3] as CRangePoint
        }
        signalBottomLeft.apply {
            childX = signalBottomRight
            childY = signalTopLeft
        }
        signalBottomRight.apply {
            childX = signalBottomLeft
            childY = signalTopRight
        }

        signals.forEach {
            attachChild(it)
        }

        scene.getLayerById(layerId).also { layer ->
            layer.attachChild(this)
        }
        isVisible = false
        previousZoom = camera?.zoom?:1f
    }

    fun adjustView(){
        signals[0].also {
            (it as CRangePoint).also { point->
                point.setSize(pointSize,pointSize)
                point.updatePosition(getPosition().x - sprite.width / 2 , getPosition().y + sprite.height / 2f )
            }
        }
        signals[1].also {
            (it as CRangePoint).also { point->
                point.setSize(pointSize,pointSize)
                point.updatePosition(getPosition().x + sprite.width / 2 , getPosition().y + sprite.height / 2f )
            }
        }
        signals[2].also {
            (it as CRangePoint).also { point->
                point.setSize(pointSize,pointSize)
                point.updatePosition(getPosition().x - sprite.width / 2 , getPosition().y - sprite.height / 2f )
            }
        }
        signals[3].also {
            (it as CRangePoint).also { point->
                point.setSize(pointSize,pointSize)
                point.updatePosition(getPosition().x + sprite.width / 2 , getPosition().y - sprite.height / 2f )
            }
        }
    }

    override fun reset(){
        rangeItems.forEach {
            it.subject.selected = false
            it.caller.value.selected = false
        }
        sprite.setSize(200f,200f)
        adjustView()
    }

    override fun update() {
        val signalTopLeft = signals[0] as CRangePoint
        val signalTopRight = signals[1] as CRangePoint
        val signalBottomLeft = signals[2] as CRangePoint
        var updated = false
        signals.forEach {
            (it as CRangePoint).also { point->
                updated= point.isUpdated || updated
                if(point.isUpdated) {
                    point.childX?.also { child ->
                        child.updatePosition(child.getPosition().x, point.getPosition().y)
                        child.isUpdated = false
                    }
                    point.childY?.also { child ->
                        child.updatePosition(point.getPosition().x, child.getPosition().y)
                        child.isUpdated = false
                    }
                    point.isUpdated = false
                }

            }
        }

        signals.forEach {
            (it as CRangePoint).also { point ->
                camera?.also { value ->
                    point.setWidth(pointSize * value.zoom)
                    point.setHeight(pointSize * value.zoom)
                    point.updatePosition(point.getPosition())
                    point.isUpdated = false
                }
            }
        }

        if(updated || previousZoom != camera?.zoom) {
            // update the range background size and position
            val width = (signalTopRight.getPosition().x - signalTopLeft.getPosition().x)
            val height = (signalTopLeft.getPosition().y - signalBottomLeft.getPosition().y)
            sprite.setSize(abs(width), abs(height))
            updatePosition(
                signalTopLeft.getPosition().x + width / 2f,
                signalTopLeft.getPosition().y - height / 2f
            )
        }
        previousZoom = camera?.zoom?:1f
    }

    override fun draw(spriteBatch: SpriteBatch) {
            sprite.draw(spriteBatch)
            data.forEach {
                it.draw(spriteBatch)
            }
    }

    override fun contains(entity: CNode): CNode? {
        data.forEach {
            if(it is CNode){
                val childCollides = it.contains(entity)
                if(childCollides != null){
                    return childCollides
                }
            }
        }
        if(enableDragMotion) {
            val parentCollides = super.contains(entity)
            if (parentCollides != null) {
                return parentCollides
            }
        }

        return null
    }

    override fun contains(rect: Rectangle): CNode? {
        data.forEach {
            if(it is CNode){
                val childCollides = it.contains(rect)
                if(childCollides != null){
                    return childCollides
                }
            }
        }
        if(enableDragMotion) {
            val parentCollides = super.contains(rect)
            if (parentCollides != null) {
                return parentCollides
            }
        }

        return null
    }


}

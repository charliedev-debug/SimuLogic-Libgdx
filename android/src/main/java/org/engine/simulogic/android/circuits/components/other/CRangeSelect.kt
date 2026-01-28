package org.engine.simulogic.android.circuits.components.other

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.CTypes
import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.events.CollisionDetector
import org.engine.simulogic.android.scene.Entity
import org.engine.simulogic.android.scene.LayerEnums
import org.engine.simulogic.android.scene.PlayGroundScene

class CRangeSelect(x:Float, y:Float,connection: Connection, private val scene: PlayGroundScene)  : CNode() {

    private val pointSize = 30f
    var rangeItems = mutableListOf<CollisionDetector.CollisionItem>()
    init {
        val textureAtlas = scene.assetManager.get("component.atlas", TextureAtlas::class.java)
        val spriteRegion = textureAtlas.findRegion("TRANSPARENT")
        sprite = Sprite(spriteRegion).apply {
            setOrigin(x , y)
            setSize(200f, 200f)
            setOriginCenter()
            rotation = 0f
            setPosition(x - CDefaults.gateWidth / 2f,y - CDefaults.gateHeight / 2f)
        }

        sprite.color = Color(1f,0f,0f,0.27f)

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
            connection.insertNode(ListNode(it))
        }

        scene.getLayerById(LayerEnums.SCREEN_LAYER.name).also { layer ->
            layer.attachChild(this)
        }

        connection.insertNode(ListNode(this))
        isVisible = false
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

    override fun update() {
        val signalTopLeft = signals[0] as CRangePoint
        val signalTopRight = signals[1] as CRangePoint
        val signalBottomLeft = signals[2] as CRangePoint
        signals.forEach {
            (it as CRangePoint).apply {
                if(isUpdated) {
                    childX?.also { child ->
                        child.updatePosition(child.getPosition().x, getPosition().y)
                        child.isUpdated = false
                    }
                    childY?.also { child ->
                        child.updatePosition(getPosition().x, child.getPosition().y)
                        child.isUpdated = false
                    }
                    isUpdated = false
                }
            }
        }
        // update the range background size and position
        val width = signalTopLeft.getPosition().x - signalTopRight.getPosition().x
        val height = signalTopLeft.getPosition().y - signalBottomLeft.getPosition().y
        sprite.setSize(width, height)
        updatePosition(signalTopLeft.getPosition().x - width/2f, signalTopLeft.getPosition().y - height / 2f)
    }

    override fun draw(spriteBatch: SpriteBatch) {
            sprite.draw(spriteBatch)
            data.forEach {
                it.draw(spriteBatch)
            }
    }

}

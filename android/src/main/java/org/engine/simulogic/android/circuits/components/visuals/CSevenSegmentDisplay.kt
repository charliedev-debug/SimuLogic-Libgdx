package org.engine.simulogic.android.circuits.components.visuals

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.CTypes
import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.other.CRect
import org.engine.simulogic.android.scene.Entity
import org.engine.simulogic.android.scene.LayerEnums
import org.engine.simulogic.android.scene.PlayGroundScene
import kotlin.math.sign

class CSevenSegmentDisplay(x:Float, y:Float, private val scene: PlayGroundScene) : CNode(){
    private val segmentList = mutableListOf<CRect>()
    private val segColorOff = Color(51/255f, 51/255f, 51/255f, 1f)
      init {
          val textureAtlas = scene.assetManager.get("component.atlas", TextureAtlas::class.java)
          val spriteRegion = textureAtlas.findRegion("SEVEN-SEGMENT-BACKGROUND")
          val width = CDefaults.segmentDisplayWidth * 7f
          val height = CDefaults.segmentDisplayHeight * 7f
          type = CTypes.SEVEN_SEGMENT_DISPLAY
          sprite = Sprite(spriteRegion).apply {
              setOrigin(x , y)
              setSize(width, height)
              setOriginCenter()
              rotation = 0f
              setPosition(x - width / 2f,y - height / 2f)
          }


          segmentList.add(CRect(x, y,width - CDefaults.segmentDisplayWidth * 3f, CDefaults.segmentDisplayWidth, segColorOff, scene))
          segmentList.add(CRect(x, y,width - CDefaults.segmentDisplayWidth * 3f, CDefaults.segmentDisplayWidth, segColorOff, scene))
          segmentList.add(CRect(x, y,width - CDefaults.segmentDisplayWidth * 3f, CDefaults.segmentDisplayWidth, segColorOff, scene))

          segmentList.add(CRect(x, y,CDefaults.segmentDisplayWidth, width - CDefaults.segmentDisplayWidth * 2f, segColorOff, scene))
          segmentList.add(CRect(x, y,CDefaults.segmentDisplayWidth, width - CDefaults.segmentDisplayWidth * 2f, segColorOff, scene))
          segmentList.add(CRect(x, y,CDefaults.segmentDisplayWidth, width - CDefaults.segmentDisplayWidth * 2f, segColorOff, scene))
          segmentList.add(CRect(x, y,CDefaults.segmentDisplayWidth, width - CDefaults.segmentDisplayWidth * 2f, segColorOff, scene))

          segmentList.forEachIndexed { index, it->
              signals.add(CSignal(it.getPosition().x, it.getPosition().y, CTypes.SIGNAL_IN, index, scene))
          }

          signals.forEach {
              attachChild(it)
          }

         // segmentList.add(CRect(x, y,width, height, scene))
          scene.getLayerById(LayerEnums.GATE_LAYER.name).also { layer ->
              layer.attachChild(this)
              segmentList.forEach {
                  layer.attachChild(it)
              }
          }

      }
    override fun attachSelf() {
        super.attachSelf()
        scene.getLayerById(LayerEnums.GATE_LAYER.name).also { layer ->
            signals.forEach {
                it.isRemoved = false
                layer.attachChild(it)
            }
            layer.attachChild(this)
        }
    }

    override fun detachSelf() {
        super.detachSelf()
        signals.forEach { it.detachSelf() }
    }

    override fun update() {
        updateColor(if(selected) CDefaults.GATE_SELECTED_COLOR else CDefaults.GATE_UNSELECTED_COLOR)
        data.forEach {
            it.update()
        }
    }

    override fun draw(spriteBatch: SpriteBatch) {
        super.draw(spriteBatch)
        val x = getPosition().x
        val y = getPosition().y
        val width = sprite.width
        val height = sprite.height
        val segWidth = CDefaults.segmentDisplayWidth
        val segHeight = CDefaults.segmentDisplayHeight
        val offset = CDefaults.segmentDisplayWidth / 2f
        // top center segment
         segmentList[0].also { segment->
             segment.updatePosition(x  , y + height / 2f - segHeight / 2f )
             signals[0].updatePosition(x, y + height/2f + segHeight)
             segment.updateColor(if(signals[0].value == SIGNAL_ACTIVE) CDefaults.SIGNAL_ACTIVE_COLOR else segColorOff)
         }

        // center segment
        segmentList[1].also { segment->
            segment.updatePosition(x  , y - segHeight / 2f + segHeight / 2f)
            signals[1].updatePosition(x - width / 2f - segWidth, y - segHeight / 2f + segHeight / 2f)
            segment.updateColor(if(signals[1].value == SIGNAL_ACTIVE) CDefaults.SIGNAL_ACTIVE_COLOR else segColorOff)
        }

        // bottom segment
        segmentList[2].also { segment->
            segment.updatePosition(x  , y - height / 2f + segHeight / 2f)
            signals[2].updatePosition(x, y - height / 2f - segHeight )
            segment.updateColor(if(signals[2].value == SIGNAL_ACTIVE) CDefaults.SIGNAL_ACTIVE_COLOR else segColorOff)
        }

        // top left segment
        segmentList[3].also { segment->
            segment.updatePosition(x - width /2f + segWidth /2f + offset , y + width / 2f - segHeight / 2f)
            signals[3].updatePosition(x - width /2f - segWidth  , y + width / 2f - segHeight / 2f)
            segment.updateColor(if(signals[3].value == SIGNAL_ACTIVE) CDefaults.SIGNAL_ACTIVE_COLOR else segColorOff)
        }

        // top right segment
        segmentList[4].also { segment->
            segment.updatePosition(x + width /2f - segWidth /2f - offset , y + width / 2f - segHeight / 2f)
            signals[4].updatePosition(x + width /2f + segWidth  , y + width / 2f - segHeight / 2f)
            segment.updateColor(if(signals[4].value == SIGNAL_ACTIVE) CDefaults.SIGNAL_ACTIVE_COLOR else segColorOff)
        }

        // bottom left segment
        segmentList[5].also { segment->
            segment.updatePosition(x - width /2f + segWidth /2f  + offset, y - width / 2f + segHeight / 2f)
            signals[5].updatePosition(x - width /2f - segWidth, y - width / 2f + segHeight / 2f)
            segment.updateColor(if(signals[5].value == SIGNAL_ACTIVE) CDefaults.SIGNAL_ACTIVE_COLOR else segColorOff)
        }

        // bottom right segment
        segmentList[6].also { segment->
            segment.updatePosition(x + width /2f - segWidth /2f - offset , y - width / 2f + segHeight / 2f)
            signals[6].updatePosition(x + width /2f + segWidth , y - width / 2f + segHeight / 2f)
            segment.updateColor(if(signals[6].value == SIGNAL_ACTIVE) CDefaults.SIGNAL_ACTIVE_COLOR else segColorOff)
        }
    }

    override fun contains(entity: CNode): CNode? {
        val parentCollides = super.contains(entity)
        if(parentCollides != null){
            return parentCollides
        }
        data.forEach {
            if(it is CNode){
                val childCollides = it.contains(entity)
                if(childCollides != null){
                    return childCollides
                }
            }
        }
        return null
    }

    override fun contains(rect: Rectangle): CNode? {
        val parentCollides = super.contains(rect)
        if(parentCollides != null){
            return parentCollides
        }
        data.forEach {
            if(it is CNode){
                val childCollides = it.contains(rect)
                if(childCollides != null){
                    return childCollides
                }
            }
        }
        return null
    }

    override fun clone(): CSevenSegmentDisplay {
        return CSevenSegmentDisplay(getPosition().x, getPosition().y , scene)
    }
}

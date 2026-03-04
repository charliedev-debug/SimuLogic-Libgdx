package org.engine.simulogic.android.circuits.components.visuals

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Rectangle
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.CTypes
import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.other.CRect
import org.engine.simulogic.android.scene.LayerEnums
import org.engine.simulogic.android.scene.PlayGroundScene

class CBCDDisplay (x:Float, y:Float, private val scene: PlayGroundScene) : CNode(){
    private val segmentList = mutableListOf<CRect>()
    private val segmentSignalList = mutableListOf<Int>()
    private val segColorOff = Color(51/255f, 51/255f, 51/255f, 1f)
    init {
        val textureAtlas = scene.assetManager.get("component.atlas", TextureAtlas::class.java)
        val spriteRegion = textureAtlas.findRegion("SEVEN-SEGMENT-BACKGROUND")
        val width = CDefaults.segmentDisplayWidth * 7f
        val height = CDefaults.segmentDisplayHeight * 7f
        type = CTypes.BCD_SEVEN_SEGMENT_DISPLAY
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

        segmentList.add(
            CRect(x, y,
                CDefaults.segmentDisplayWidth, width - CDefaults.segmentDisplayWidth * 2f, segColorOff, scene)
        )
        segmentList.add(
            CRect(x, y,
                CDefaults.segmentDisplayWidth, width - CDefaults.segmentDisplayWidth * 2f, segColorOff, scene)
        )
        segmentList.add(
            CRect(x, y,
                CDefaults.segmentDisplayWidth, width - CDefaults.segmentDisplayWidth * 2f, segColorOff, scene)
        )
        segmentList.add(
            CRect(x, y,
                CDefaults.segmentDisplayWidth, width - CDefaults.segmentDisplayWidth * 2f, segColorOff, scene)
        )

        for(i in 0 until segmentSignalList.size ){
            segmentSignalList.add(0)
        }

        for(i in 0 until 4 ){
            signals.add(CSignal(0f, 0f, CTypes.SIGNAL_IN, i, scene).apply { value = 0 })
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

        var currentBCDStringValue = ""
        signals.forEachIndexed { index, cSignal ->
            val ratio = index.toFloat() / 4f
            cSignal.updatePosition(x - width/2f  + width * ratio + cSignal.getWidth(), y - height * 0.6f)
            currentBCDStringValue += "${cSignal.value}"
        }

        val currentBCDIntValue: Int = Integer.parseInt(currentBCDStringValue, 2)

        // top center segment
        segmentList[0].also { segment->
            segment.updatePosition(x  , y + height / 2f - segHeight / 2f )
           segment.updateColor(if(currentBCDIntValue == 0 || currentBCDIntValue == 2 ||
               currentBCDIntValue == 3 ||currentBCDIntValue == 5 ||
               currentBCDIntValue == 6 || currentBCDIntValue == 7 ||
               currentBCDIntValue == 8 || currentBCDIntValue == 9 ||
               currentBCDIntValue == 10 || currentBCDIntValue == 12 ||
               currentBCDIntValue == 14 || currentBCDIntValue == 15) CDefaults.SIGNAL_ACTIVE_COLOR else segColorOff)

        }

        // center segment
        segmentList[1].also { segment->
            segment.updatePosition(x  , y - segHeight / 2f + segHeight / 2f)
            segment.updateColor(if(currentBCDIntValue == 2 || currentBCDIntValue == 3 ||currentBCDIntValue == 5 ||
                currentBCDIntValue == 4 || currentBCDIntValue == 6 || currentBCDIntValue == 8 ||
                currentBCDIntValue == 9 || currentBCDIntValue == 10 ||
                currentBCDIntValue == 11 || currentBCDIntValue == 13 ||
                currentBCDIntValue == 14 || currentBCDIntValue == 15) CDefaults.SIGNAL_ACTIVE_COLOR else segColorOff)
        }

        // bottom segment
        segmentList[2].also { segment->
            segment.updatePosition(x  , y - height / 2f + segHeight / 2f)
            segment.updateColor(if(currentBCDIntValue == 0 || currentBCDIntValue == 2 ||currentBCDIntValue == 3 ||
                currentBCDIntValue == 5 || currentBCDIntValue == 6 ||
                currentBCDIntValue == 8 || currentBCDIntValue == 11 ||
                currentBCDIntValue == 12 || currentBCDIntValue == 13 || currentBCDIntValue == 14) CDefaults.SIGNAL_ACTIVE_COLOR else segColorOff)
        }

        // top left segment
        segmentList[3].also { segment->
            segment.updatePosition(x - width /2f + segWidth /2f + offset , y + width / 2f - segHeight / 2f)
            segment.updateColor(if(currentBCDIntValue == 0 || currentBCDIntValue == 4 ||currentBCDIntValue == 5 ||
                currentBCDIntValue == 6 || currentBCDIntValue == 8 ||
                currentBCDIntValue == 9 || currentBCDIntValue == 10 ||
                currentBCDIntValue == 11 || currentBCDIntValue == 12 ||
                currentBCDIntValue == 14 || currentBCDIntValue == 15) CDefaults.SIGNAL_ACTIVE_COLOR else segColorOff)
        }

        // top right segment
        segmentList[4].also { segment->
            segment.updatePosition(x + width /2f - segWidth /2f - offset , y + width / 2f - segHeight / 2f)
            segment.updateColor(if(currentBCDIntValue == 0 || currentBCDIntValue == 1 ||currentBCDIntValue == 2 ||
                currentBCDIntValue == 3 || currentBCDIntValue == 4 ||
                currentBCDIntValue == 7 ||
                currentBCDIntValue == 8 || currentBCDIntValue == 9 ||
                currentBCDIntValue == 10 || currentBCDIntValue == 13 ) CDefaults.SIGNAL_ACTIVE_COLOR else segColorOff)
        }

        // bottom left segment
        segmentList[5].also { segment->
            segment.updatePosition(x - width /2f + segWidth /2f  + offset, y - width / 2f + segHeight / 2f)
            segment.updateColor(if(currentBCDIntValue == 0 || currentBCDIntValue == 2 ||
                currentBCDIntValue == 6 || currentBCDIntValue == 8 ||
                currentBCDIntValue == 10 || currentBCDIntValue == 11 ||
                currentBCDIntValue == 12 || currentBCDIntValue == 13 ||
                currentBCDIntValue == 14 || currentBCDIntValue == 15) CDefaults.SIGNAL_ACTIVE_COLOR else segColorOff)
        }

        // bottom right segment
        segmentList[6].also { segment->
            segment.updatePosition(x + width /2f - segWidth /2f - offset , y - width / 2f + segHeight / 2f)
            segment.updateColor(if(currentBCDIntValue == 0 || currentBCDIntValue == 1 ||currentBCDIntValue == 3 ||
                currentBCDIntValue == 4 || currentBCDIntValue == 5 ||
                currentBCDIntValue == 6 || currentBCDIntValue == 7 ||
                currentBCDIntValue == 8 || currentBCDIntValue == 9 ||
                currentBCDIntValue == 10 || currentBCDIntValue == 11 || currentBCDIntValue == 13 ) CDefaults.SIGNAL_ACTIVE_COLOR else segColorOff)
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

    override fun clone(): CBCDDisplay {
        return CBCDDisplay(getPosition().x, getPosition().y , scene)
    }
}


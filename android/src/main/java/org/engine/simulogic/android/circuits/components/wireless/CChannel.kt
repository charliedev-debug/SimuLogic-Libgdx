package org.engine.simulogic.android.circuits.components.wireless

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Rectangle
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.CTypes
import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.lines.CLine
import org.engine.simulogic.android.scene.LayerEnums
import org.engine.simulogic.android.scene.PlayGroundScene

class CChannel (x:Float, y:Float, val channelId:String, val channelType:Int,rotationDirection:Int = ROTATE_RIGHT, private val scene: PlayGroundScene) : CNode(){
    private val lines = mutableListOf<CLine>()
    init {
        val textureAtlas = scene.assetManager.get("component.atlas", TextureAtlas::class.java)
        val spriteRegion = textureAtlas.findRegion("CHANNEL")
        type = CTypes.CHANNEL
        this.rotationDirection = rotationDirection
        sprite = Sprite(spriteRegion).apply {
            setOrigin(x , y)
            setSize(CDefaults.ledWidth, CDefaults.ledHeight)
            setOriginCenter()
            when(rotationDirection){
                ROTATE_BOTTOM ->{
                    rotation = 270f
                }
                ROTATE_TOP ->{
                    rotation = 90f
                }
                ROTATE_LEFT ->{
                    rotation = 180f
                }
                ROTATE_RIGHT ->{
                    rotation = 0f
                }
            }
            setPosition(x - CDefaults.ledWidth / 2f,y - CDefaults.ledHeight / 2f)
        }

        signals.add(CSignal(x + sprite.width * 0.8125f, y , CTypes.SIGNAL_IN,0, scene))
        signals.forEach {
            attachChild(it)
        }
        scene.getLayerById(LayerEnums.GATE_LAYER.name).also { layer ->
            layer.attachChild(this)
        }

        scene.getLayerById(LayerEnums.CONNECTION_LAYER.name).also { layer->
            val lineWidth = 2f
            //output line segment
            getChildAt(0).getPosition()?.also { outputPosition ->
                lines.add(CLine(outputPosition.x,outputPosition.y,getPosition().x,getPosition().y,lineWidth))
            }
            lines.forEach {
                layer.attachChild(it)
            }
        }
    }

    override fun execute() {
        //unused
    }


    override fun attachSelf() {
        super.attachSelf()
        scene.getLayerById(LayerEnums.CONNECTION_LAYER.name).also { layer ->
            lines.forEach {
                it.isRemoved = false
                layer.attachChild(it)
            }
        }
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
        lines.forEach { it.detachSelf() }
        signals.forEach { it.detachSelf() }
    }

    override fun update() {
        if(selected){
            updateColor(CDefaults.GATE_SELECTED_COLOR)
        }else{
            updateColor(if(signals[0].value == SIGNAL_ACTIVE) CDefaults.SIGNAL_ACTIVE_COLOR else  CDefaults.LED_INACTIVE_COLOR)
        }
        when(rotationDirection){
            ROTATE_RIGHT ->{
                signals[0].updatePosition(getPosition().x + sprite.width * 0.8125f, getPosition().y)
                getChildAt(0).getPosition()?.also { outputPosition ->
                    lines[0].updatePosition(outputPosition.x,outputPosition.y,getPosition().x,getPosition().y)
                }
            }

            ROTATE_LEFT ->{
                signals[0].updatePosition(getPosition().x - sprite.width * 0.8125f, getPosition().y)
                getChildAt(0).getPosition()?.also { outputPosition ->
                    lines[0].updatePosition(outputPosition.x,outputPosition.y,getPosition().x,getPosition().y)
                }
            }

            ROTATE_TOP ->{
                signals[0].updatePosition(getPosition().x , getPosition().y + sprite.width * 0.8125f)
                getChildAt(0).getPosition()?.also { outputPosition ->
                    lines[0].updatePosition(outputPosition.x,outputPosition.y,getPosition().x,getPosition().y)
                }
            }

            ROTATE_BOTTOM ->{
                signals[0].updatePosition(getPosition().x , getPosition().y - sprite.width * 0.8125f)
                getChildAt(0).getPosition()?.also { outputPosition ->
                    lines[0].updatePosition(outputPosition.x,outputPosition.y,getPosition().x,getPosition().y)
                }
            }
        }
        data.forEach {
            it.update()
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

    override fun contains(x: Float, y: Float): CNode? {
        return super.contains(x, y)
    }

    override fun clone(): CNode {
        return CChannel(getPosition().x,getPosition().y, channelId, channelType, rotationDirection, scene )
    }

}

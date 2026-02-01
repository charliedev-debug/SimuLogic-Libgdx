package org.engine.simulogic.android.circuits.components.generators

import org.engine.simulogic.android.circuits.components.gates.CAnd
import org.engine.simulogic.android.circuits.components.gates.CSignal
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Rectangle
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.CTypes
import org.engine.simulogic.android.circuits.components.lines.CLine
import org.engine.simulogic.android.scene.LayerEnums
import org.engine.simulogic.android.scene.PlayGroundScene

class CRandom(x:Float, y:Float, private val scene: PlayGroundScene) :CNode(){

    private val lines = mutableListOf<CLine>()
    init {

        val textureAtlas = scene.assetManager.get("component.atlas", TextureAtlas::class.java)
        val spriteRegion = textureAtlas.findRegion("RANDOM")
        type = CTypes.RANDOM
        sprite = Sprite(spriteRegion).apply {
            setOrigin(x , y)
            setSize(CDefaults.randomWidth, CDefaults.randomHeight)
            setOriginCenter()
            rotation = 0f
            setPosition(x - CDefaults.randomWidth / 2f,y - CDefaults.randomHeight / 2f)
        }

        signals.add(CSignal(x + sprite.width * 0.8125f, y ,CTypes.SIGNAL_OUT,0, scene))
        signals.add(CSignal(x - sprite.width * 0.8125f, y,CTypes.SIGNAL_IN, 1, scene))
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
            //input line segments
            getChildAt(1).getPosition()?.also { outputPosition ->
                lines.add(CLine(outputPosition.x,outputPosition.y,getPosition().x,outputPosition.y,lineWidth))
            }
            lines.forEach {
                layer.attachChild(it)
            }
        }
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

    override fun execute() {
        signals[0].value = 0
    }

    override fun update() {
        if(selected){
            updateColor(CDefaults.GATE_SELECTED_COLOR)
        }else{
            updateColor(if(signals[0].value == SIGNAL_ACTIVE) CDefaults.SIGNAL_ACTIVE_COLOR else  CDefaults.GATE_UNSELECTED_COLOR)
        }
        signals[0].updatePosition(getPosition().x + sprite.width * 0.8125f, getPosition().y)
        signals[1].updatePosition(getPosition().x - sprite.width * 0.8125f, getPosition().y)
        getChildAt(0).getPosition()?.also { outputPosition ->
            lines[0].updatePosition(outputPosition.x,outputPosition.y,getPosition().x,getPosition().y)
        }
        getChildAt(1).getPosition()?.also { outputPosition ->
            lines[1].updatePosition(outputPosition.x,outputPosition.y,getPosition().x,outputPosition.y)
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

    override fun clone():CNode {
        return CAnd(getPosition().x,getPosition().y, scene )
    }

}


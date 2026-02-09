package org.engine.simulogic.android.circuits.components.buses

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

class CDataBus (x:Float, y:Float, val size:Int, rotationDirection:Int, private val scene: PlayGroundScene) :
    CNode(){
    private val MAX_POINTS = size * 2
    private val lines = mutableListOf<CLine>()
    constructor(x:Float, y:Float,size:Int, scene: PlayGroundScene):this(x, y,size, ROTATE_RIGHT, scene)
    constructor(x:Float, y:Float, scene: PlayGroundScene):this(x, y,4, ROTATE_RIGHT, scene)
    init {

        val textureAtlas = scene.assetManager.get("component.atlas", TextureAtlas::class.java)
        val spriteRegion = textureAtlas.findRegion("DATA-BUS")
        type = CTypes.DATA_BUS
        this.rotationDirection = rotationDirection
        this.cameraClippingEnabled = false
        sprite = Sprite(spriteRegion).apply {
            setOrigin(x , y)
            setSize(CDefaults.randomWidth, CDefaults.randomHeight)
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
            setPosition(x - CDefaults.randomWidth / 2f,y - CDefaults.randomHeight / 2f)
        }
        for(i in 0 until MAX_POINTS step  2) {
            signals.add(CSignal(x + sprite.width * 0.8125f, y + sprite.height * (i - MAX_POINTS / 4) , CTypes.SIGNAL_OUT, i, scene))
            signals.add(CSignal(x - sprite.width * 0.8125f, y + sprite.height * (i - MAX_POINTS / 4), CTypes.SIGNAL_IN, i + 1, scene))
        }
        signals.forEach {
            attachChild(it)
        }

        scene.getLayerById(LayerEnums.GATE_LAYER.name).also { layer ->
            layer.attachChild(this)
        }

        scene.getLayerById(LayerEnums.CONNECTION_LAYER.name).also { layer->
            val lineWidth = 2f
            for(i in 0 until MAX_POINTS step 2) {
                val a = getChildAt(i)
                val b = getChildAt(i+1)
                lines.add(CLine(a.getPosition()!!.x,a.getPosition()!!.y,b.getPosition()!!.x, b.getPosition()!!.y, lineWidth))
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
        for(i in 0 until MAX_POINTS / 2 step  2){
            val output = signals[i]
            val input =  signals[i+ 1]
            output.value = input.value
        }
    }

    override fun update() {
        for((counter, i) in (0 until MAX_POINTS step 2).withIndex()) {
            val a = signals[i]
            val b = signals[i+1]
            updateColor(if(a.value == SIGNAL_ACTIVE) CDefaults.SIGNAL_ACTIVE_COLOR else  CDefaults.GATE_UNSELECTED_COLOR)
            updateColor(if(b.value == SIGNAL_ACTIVE) CDefaults.SIGNAL_ACTIVE_COLOR else  CDefaults.GATE_UNSELECTED_COLOR)
            when(rotationDirection){
                ROTATE_RIGHT->{
                    a.updatePosition(getPosition().x + sprite.width, getPosition().y + sprite.height * (counter - MAX_POINTS/4) + (sprite.height) / 2 )
                    b.updatePosition(getPosition().x - sprite.width, getPosition().y + sprite.height * (counter - MAX_POINTS/4) + (sprite.height) / 2)
                }
                ROTATE_LEFT->{
                    a.updatePosition(getPosition().x - sprite.width , getPosition().y + sprite.height * (counter - MAX_POINTS/4) + (sprite.height) / 2 )
                    b.updatePosition(getPosition().x + sprite.width, getPosition().y + sprite.height * (counter - MAX_POINTS/4) + (sprite.height) / 2)
                }
                ROTATE_BOTTOM->{
                    a.updatePosition(getPosition().x + sprite.width * (counter - MAX_POINTS/4) + (sprite.width) / 2 , getPosition().y + sprite.height )
                    b.updatePosition(getPosition().x + sprite.width * (counter - MAX_POINTS/4) + (sprite.width) / 2, getPosition().y - sprite.height )
                }
                ROTATE_TOP->{
                    a.updatePosition(getPosition().x + sprite.width * (counter - MAX_POINTS/4) + (sprite.width) / 2 , getPosition().y - sprite.height)
                    b.updatePosition(getPosition().x + sprite.width * (counter - MAX_POINTS/4) + (sprite.width) / 2, getPosition().y + sprite.height)
                }
            }

            lines[counter].updatePosition(a.getPosition().x,a.getPosition().y, b.getPosition().x, b.getPosition().y)
        }

        if(selected){
            updateColor(CDefaults.GATE_SELECTED_COLOR)
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
        return CDataBus(getPosition().x,getPosition().y,size, rotationDirection, scene )
    }

}

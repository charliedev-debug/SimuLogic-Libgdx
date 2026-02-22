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

class CDataBus (x:Float, y:Float, val DATA_SIZE:Int, rotationDirection:Int, private val scene: PlayGroundScene) :
    CNode(){
    private val MAX_POINTS = DATA_SIZE * 2
    private val lines = mutableListOf<CLine>()
    constructor(x:Float, y:Float,size:Int, scene: PlayGroundScene):this(x, y,size, ROTATE_RIGHT, scene)
    constructor(x:Float, y:Float, scene: PlayGroundScene):this(x, y,4, ROTATE_RIGHT, scene)
    init {

        val textureAtlas = scene.assetManager.get("component.atlas", TextureAtlas::class.java)
        val spriteRegion = textureAtlas.findRegion("DATA-BUS")
        type = CTypes.DATA_BUS
        this.rotationDirection = rotationDirection
        this.cameraClippingEnabled = false
        val spacingX = CDefaults.GRID_WIDTH * 2
        val spacingY = CDefaults.GRID_HEIGHT * 2
        sprite = Sprite(spriteRegion).apply {
            setOrigin(x , y)
            setSize(spacingX , spacingY)
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
            setPosition(x - spacingX/ 2f,y - spacingY/ 2f)
        }

        for(i in 0 until MAX_POINTS step  2) {
            when(rotationDirection){
                ROTATE_RIGHT->{
                    signals.add(CSignal(x + spacingX, y + spacingY * (i - MAX_POINTS/4) + (spacingY) / 2, CTypes.SIGNAL_OUT, i, scene ))
                    signals.add(CSignal(x - spacingX, y + spacingY * (i - MAX_POINTS/4) + (spacingY) / 2, CTypes.SIGNAL_IN, i + 1, scene))
                }
                ROTATE_LEFT->{
                    signals.add(CSignal(x - spacingX, y + spacingY * (i - MAX_POINTS/4) + (spacingY) / 2, CTypes.SIGNAL_OUT, i, scene  ))
                    signals.add(CSignal(x + spacingX, y + spacingY * (i - MAX_POINTS/4) + (spacingY) / 2, CTypes.SIGNAL_IN, i + 1, scene))
                }
                ROTATE_BOTTOM->{
                    signals.add(CSignal(x + spacingX * (i - MAX_POINTS/4) + (spacingX) / 2 , getPosition().y + spacingY, CTypes.SIGNAL_OUT, i, scene))
                    signals.add(CSignal(x + spacingX * (i - MAX_POINTS/4) + (spacingX) / 2, getPosition().y - spacingY, CTypes.SIGNAL_IN, i + 1, scene))
                }
                ROTATE_TOP->{
                    signals.add(CSignal(x + spacingX * (i - MAX_POINTS/4) + (spacingX) / 2 , getPosition().y - spacingY, CTypes.SIGNAL_OUT, i, scene ))
                    signals.add(CSignal(x + spacingX * (i - MAX_POINTS/4) + (spacingX) / 2, getPosition().y + spacingY, CTypes.SIGNAL_IN, i + 1, scene))
                }
            }
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
        for(i in 0 until MAX_POINTS step  2){
            val output = signals[i]
            val input =  signals[i+ 1]
            output.value = input.value
        }
    }

    override fun update() {
        val spacingX = CDefaults.GRID_WIDTH * 2
        val spacingY = CDefaults.GRID_HEIGHT * 2
        for((counter, i) in (0 until MAX_POINTS step 2).withIndex()) {
            val a = signals[i]
            val b = signals[i+1]
            updateColor(if(a.value == SIGNAL_ACTIVE) CDefaults.SIGNAL_ACTIVE_COLOR else  CDefaults.GATE_UNSELECTED_COLOR)
            updateColor(if(b.value == SIGNAL_ACTIVE) CDefaults.SIGNAL_ACTIVE_COLOR else  CDefaults.GATE_UNSELECTED_COLOR)
            when(rotationDirection){
                ROTATE_RIGHT->{
                    a.updatePosition(getPosition().x + spacingX, getPosition().y + spacingY * (counter - MAX_POINTS/4) + (spacingY) / 2 )
                    b.updatePosition(getPosition().x - spacingX, getPosition().y + spacingY * (counter - MAX_POINTS/4) + (spacingY) / 2)
                }
                ROTATE_LEFT->{
                    a.updatePosition(getPosition().x - spacingX, getPosition().y + spacingY * (counter - MAX_POINTS/4) + (spacingY) / 2 )
                    b.updatePosition(getPosition().x + spacingX, getPosition().y + spacingY * (counter - MAX_POINTS/4) + (spacingY) / 2)
                }
                ROTATE_BOTTOM->{
                    a.updatePosition(getPosition().x + spacingX * (counter - MAX_POINTS/4) + (spacingX) / 2 , getPosition().y + spacingY )
                    b.updatePosition(getPosition().x + spacingX * (counter - MAX_POINTS/4) + (spacingX) / 2, getPosition().y - spacingY )
                }
                ROTATE_TOP->{
                    a.updatePosition(getPosition().x + spacingX * (counter - MAX_POINTS/4) + (spacingX) / 2 , getPosition().y - spacingY)
                    b.updatePosition(getPosition().x + spacingX * (counter - MAX_POINTS/4) + (spacingX) / 2, getPosition().y + spacingY)
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
        return CDataBus(getPosition().x,getPosition().y,DATA_SIZE, rotationDirection, scene )
    }

}

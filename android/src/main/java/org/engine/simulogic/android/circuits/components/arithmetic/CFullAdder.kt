package org.engine.simulogic.android.circuits.components.arithmetic
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Rectangle
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.CTypes
import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.lines.CLine
import org.engine.simulogic.android.circuits.theme.EnvironmentTheme
import org.engine.simulogic.android.scene.LayerEnums
import org.engine.simulogic.android.scene.PlayGroundScene
class CFullAdder(x:Float, y:Float, rotationDirection:Int, private val scene: PlayGroundScene) :CNode(){

    private val lines = mutableListOf<CLine>()
    constructor(x:Float, y:Float, scene: PlayGroundScene):this(x, y, ROTATE_RIGHT, scene)
    init {

        val textureAtlas = scene.assetManager.get("${EnvironmentTheme.name}.atlas", TextureAtlas::class.java)
        val spriteRegion = textureAtlas.findRegion("FULL-ADDER")
        type = CTypes.ARITHMETIC_FULL_ADDER
        this.rotationDirection = rotationDirection
        sprite = Sprite(spriteRegion).apply {
            setOrigin(x , y)
            setSize(CDefaults.latchWidth* 1.5f , CDefaults.latchHeight* 1.5f )
            setOriginCenter()
            type = CTypes.ARITHMETIC_FULL_ADDER
            when(rotationDirection){
                ROTATE_BOTTOM->{
                    rotation = 270f
                }
                ROTATE_TOP->{
                    rotation = 90f
                }
                ROTATE_LEFT->{
                    rotation = 180f
                }
                ROTATE_RIGHT->{
                    rotation = 0f
                }
            }
            setPosition(x - CDefaults.latchWidth * 1.5f / 2f,y - CDefaults.latchHeight * 1.5f / 2f)
        }

        signals.add(CSignal(x + sprite.width * 0.8125f, y + CDefaults.gateHeight / 2f - CDefaults.gateHeight * 0.1875f ,CTypes.S_SIGNAL_OUT,0, scene))
        signals.add(CSignal(x + sprite.width * 0.8125f, y - CDefaults.gateHeight / 2f + CDefaults.gateHeight * 0.1875f ,CTypes.C_SIGNAL_OUT,1, scene))
        signals.add(CSignal(x - sprite.width * 0.8125f, y + sprite.height/ 2f - sprite.height * 0.1875f ,CTypes.SIGNAL_IN, 2, scene))
        signals.add(CSignal(x - sprite.width * 0.8125f, y - sprite.height / 2f +  sprite.height * 0.1875f ,CTypes.SIGNAL_IN, 3, scene))
        signals.add(CSignal(x - sprite.width * 0.8125f, y ,CTypes.SIGNAL_IN, 4, scene))
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
            getChildAt(1).getPosition()?.also { outputPosition ->
                lines.add(CLine(outputPosition.x,outputPosition.y,getPosition().x,outputPosition.y,lineWidth))
            }
            //input line segments
            getChildAt(2).getPosition()?.also { outputPosition ->
                lines.add(CLine(outputPosition.x,outputPosition.y,getPosition().x,outputPosition.y,lineWidth))
            }
            getChildAt(3).getPosition()?.also { outputPosition ->
                lines.add(CLine(outputPosition.x,outputPosition.y,getPosition().x,outputPosition.y,lineWidth))
            }
            getChildAt(4).getPosition()?.also { outputPosition ->
                lines.add(CLine(outputPosition.x,outputPosition.y,getPosition().x,outputPosition.y,lineWidth))
            }
            lines.forEach {
                layer.attachChild(it)
            }
        }
    }

    override fun execute() {
        val outputS = signals[0]
        val outputC = signals[1]
        val inputA = signals[2]
        val inputB = signals[3]
        val inputC = signals[4]
        val va = inputA.value
        val vb = inputB.value
        val vc = inputC.value
        outputS.value = va.xor(vb).xor(vc)
        outputC.value = va.xor(vb).and(vc).or(va.and(vb))
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
            updateColor(if(signals[2].value == SIGNAL_ACTIVE || signals[3].value == SIGNAL_ACTIVE || signals[4].value == SIGNAL_ACTIVE) CDefaults.SIGNAL_ACTIVE_COLOR else  CDefaults.GATE_UNSELECTED_COLOR)
        }
        when(rotationDirection){
            ROTATE_RIGHT->{
                signals[0].updatePosition(getPosition().x + sprite.width * 0.8125f, getPosition().y  + sprite.height / 2f - CDefaults.gateHeight * 0.1875f )
                signals[1].updatePosition(getPosition().x + sprite.width * 0.8125f, getPosition().y  - sprite.height/ 2f + CDefaults.gateHeight * 0.1875f )
                signals[2].updatePosition(getPosition().x - sprite.width * 0.8125f, getPosition().y + sprite.height / 2f - sprite.height * 0.1f)
                signals[3].updatePosition(getPosition().x - sprite.width * 0.8125f, getPosition().y - sprite.height / 2f +  sprite.height * 0.1f)
                signals[4].updatePosition(getPosition().x - sprite.width * 0.8125f, getPosition().y)
                getChildAt(0).getPosition()?.also { outputPosition ->
                    lines[0].updatePosition(outputPosition.x,outputPosition.y,getPosition().x,outputPosition.y)
                }
                getChildAt(1).getPosition()?.also { outputPosition ->
                    lines[1].updatePosition(outputPosition.x,outputPosition.y,getPosition().x,outputPosition.y)
                }
                getChildAt(2).getPosition()?.also { outputPosition ->
                    lines[2].updatePosition(outputPosition.x,outputPosition.y,getPosition().x,outputPosition.y)
                }
                getChildAt(3).getPosition()?.also { outputPosition ->
                    lines[3].updatePosition(outputPosition.x,outputPosition.y,getPosition().x,outputPosition.y)
                }
                getChildAt(4).getPosition()?.also { outputPosition ->
                    lines[4].updatePosition(outputPosition.x,outputPosition.y,getPosition().x,outputPosition.y)
                }
            }

            ROTATE_LEFT->{
                signals[0].updatePosition(getPosition().x - sprite.width * 0.8125f, getPosition().y  + sprite.height / 2f - CDefaults.gateHeight * 0.1875f )
                signals[1].updatePosition(getPosition().x - sprite.width * 0.8125f, getPosition().y  - sprite.height/ 2f + CDefaults.gateHeight * 0.1875f )
                signals[2].updatePosition(getPosition().x + sprite.width * 0.8125f, getPosition().y + sprite.height / 2f - sprite.height * 0.1f)
                signals[3].updatePosition(getPosition().x + sprite.width * 0.8125f, getPosition().y - sprite.height / 2f + sprite.height * 0.1f)
                signals[4].updatePosition(getPosition().x + sprite.width * 0.8125f, getPosition().y)
                getChildAt(0).getPosition()?.also { outputPosition ->
                    lines[0].updatePosition(outputPosition.x,outputPosition.y,getPosition().x,outputPosition.y)
                }
                getChildAt(1).getPosition()?.also { outputPosition ->
                    lines[1].updatePosition(outputPosition.x,outputPosition.y,getPosition().x,outputPosition.y)
                }
                getChildAt(2).getPosition()?.also { outputPosition ->
                    lines[2].updatePosition(outputPosition.x,outputPosition.y,getPosition().x,outputPosition.y)
                }
                getChildAt(3).getPosition()?.also { outputPosition ->
                    lines[3].updatePosition(outputPosition.x,outputPosition.y,getPosition().x,outputPosition.y)
                }
                getChildAt(4).getPosition()?.also { outputPosition ->
                    lines[4].updatePosition(outputPosition.x,outputPosition.y,getPosition().x,outputPosition.y)
                }
            }

            ROTATE_TOP->{
                signals[0].updatePosition(getPosition().x + sprite.width / 2f - sprite.width * 0.1875f , getPosition().y + sprite.width * 0.8125f)
                signals[1].updatePosition(getPosition().x - sprite.width / 2f + sprite.width * 0.1875f , getPosition().y + sprite.width * 0.8125f)
                signals[2].updatePosition(getPosition().x + sprite.height / 2f - sprite.height * 0.1f , getPosition().y - sprite.width * 0.8125f)
                signals[3].updatePosition(getPosition().x - sprite.height / 2f + sprite.height * 0.1f , getPosition().y - sprite.width * 0.8125f)
                signals[4].updatePosition(getPosition().x, getPosition().y - sprite.width * 0.8125f)
                getChildAt(0).getPosition()?.also { outputPosition ->
                    lines[0].updatePosition(outputPosition.x,outputPosition.y,outputPosition.x,getPosition().y)
                }
                getChildAt(1).getPosition()?.also { outputPosition ->
                    lines[1].updatePosition(outputPosition.x,outputPosition.y,outputPosition.x,getPosition().y)
                }
                getChildAt(2).getPosition()?.also { outputPosition ->
                    lines[2].updatePosition(outputPosition.x,outputPosition.y,outputPosition.x,getPosition().y)
                }
                getChildAt(3).getPosition()?.also { outputPosition ->
                    lines[3].updatePosition(outputPosition.x,outputPosition.y,outputPosition.x,getPosition().y)
                }
                getChildAt(4).getPosition()?.also { outputPosition ->
                    lines[4].updatePosition(outputPosition.x,outputPosition.y,outputPosition.x,getPosition().y)
                }
            }

            ROTATE_BOTTOM->{
                signals[0].updatePosition(getPosition().x + sprite.width / 2f - sprite.width * 0.1875f , getPosition().y - sprite.width * 0.8125f)
                signals[1].updatePosition(getPosition().x - sprite.width / 2f + sprite.width * 0.1875f , getPosition().y - sprite.width * 0.8125f)
                signals[2].updatePosition(getPosition().x + sprite.height / 2f - sprite.height * 0.1f , getPosition().y + sprite.width * 0.8125f)
                signals[3].updatePosition(getPosition().x - sprite.height / 2f + sprite.height * 0.1f , getPosition().y + sprite.width * 0.8125f)
                signals[4].updatePosition(getPosition().x, getPosition().y + sprite.width * 0.8125f)
                getChildAt(0).getPosition()?.also { outputPosition ->
                    lines[0].updatePosition(outputPosition.x,outputPosition.y,outputPosition.x,getPosition().y)
                }
                getChildAt(1).getPosition()?.also { outputPosition ->
                    lines[1].updatePosition(outputPosition.x,outputPosition.y,outputPosition.x,getPosition().y)
                }
                getChildAt(2).getPosition()?.also { outputPosition ->
                    lines[2].updatePosition(outputPosition.x,outputPosition.y,outputPosition.x,getPosition().y)
                }
                getChildAt(3).getPosition()?.also { outputPosition ->
                    lines[3].updatePosition(outputPosition.x,outputPosition.y,outputPosition.x,getPosition().y)
                }
                getChildAt(4).getPosition()?.also { outputPosition ->
                    lines[4].updatePosition(outputPosition.x,outputPosition.y,outputPosition.x,getPosition().y)
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

    override fun clone():CNode {
        return CFullAdder(getPosition().x,getPosition().y, rotationDirection, scene )
    }

}


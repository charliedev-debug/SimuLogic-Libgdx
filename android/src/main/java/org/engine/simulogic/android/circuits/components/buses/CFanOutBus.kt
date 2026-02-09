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

class CFanOutBus(
    x: Float,
    y: Float,
    val inputSize: Int,
    val segments: Int,
    rotationDirection: Int,
    private val scene: PlayGroundScene
) :
    CNode() {
    private val MAX_POINTS = inputSize * segments
    private val lines = mutableListOf<CLine>()
    private val inputSignals = mutableListOf<CSignal>()
    private val outputSignals = mutableListOf<CSignal>()

    constructor(x: Float, y: Float, inputSize: Int, segments: Int, scene: PlayGroundScene) : this(
        x,
        y,
        inputSize,
        segments,
        ROTATE_RIGHT,
        scene
    )

    init {

        val textureAtlas = scene.assetManager.get("component.atlas", TextureAtlas::class.java)
        val spriteRegion = textureAtlas.findRegion("DATA-BUS")
        type = CTypes.DATA_BUS_FAN_OUT
        this.rotationDirection = rotationDirection
        this.cameraClippingEnabled = false
        sprite = Sprite(spriteRegion).apply {
            setOrigin(x, y)
            setSize(CDefaults.GRID_WIDTH, CDefaults.GRID_HEIGHT)
            setOriginCenter()
            when (rotationDirection) {
                ROTATE_BOTTOM -> {
                    rotation = 270f
                }

                ROTATE_TOP -> {
                    rotation = 90f
                }

                ROTATE_LEFT -> {
                    rotation = 180f
                }

                ROTATE_RIGHT -> {
                    rotation = 0f
                }
            }
            setPosition(x - CDefaults.randomWidth / 2f, y - CDefaults.randomHeight / 2f)
        }

        for (i in 0 until inputSize) {
            CSignal(
                x - sprite.width,
                y + sprite.height * (i - inputSize / 2) + sprite.height / 2,
                CTypes.SIGNAL_IN,
                i,
                scene
            ).also { signal ->
                signals.add(signal)
                inputSignals.add(signal)
            }

        }
        for (i in 0 until MAX_POINTS) {
            CSignal(
                x + sprite.width ,
                y + sprite.height * (i - MAX_POINTS / 2),
                CTypes.SIGNAL_OUT,
                i + inputSize,
                scene
            ).also { signal ->
                signals.add(signal)
                outputSignals.add(signal)
            }
        }

        signals.forEach {
            attachChild(it)
        }

        scene.getLayerById(LayerEnums.GATE_LAYER.name).also { layer ->
            layer.attachChild(this)
        }

        scene.getLayerById(LayerEnums.CONNECTION_LAYER.name).also { layer ->
            val lineWidth = 2f
            for (i in 0 until inputSignals.size - 1) {
                val a = inputSignals[i]
                val b = inputSignals[i + 1]
                lines.add(
                    CLine(
                        a.getPosition().x, a.getPosition().y,
                        b.getPosition().x, b.getPosition().y, lineWidth
                    )
                )
            }
            for (i in 0 until outputSignals.size - 1) {
                val a = outputSignals[i]
                val b = outputSignals[i + 1]
                lines.add(
                    CLine(
                        a.getPosition().x,
                        a.getPosition().y, b.getPosition().x, b.getPosition().y, lineWidth
                    )
                )
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
        for (i in 0 until MAX_POINTS  step inputSize){
            for(j in 0 until  inputSize){
                val output = outputSignals[i + j]
                val input = inputSignals[j]
                output.value = input.value
            }
        }
    }

    override fun update() {

        for (i in 0 until inputSignals.size) {
            inputSignals[i].also { signal ->
                updateColor(if (signal.value == SIGNAL_ACTIVE) CDefaults.SIGNAL_ACTIVE_COLOR else CDefaults.GATE_UNSELECTED_COLOR)
                signal.updatePosition(
                    getPosition().x - sprite.width,
                    getPosition().y + sprite.height * (i - inputSize / 2) + sprite.height / 2
                )
            }
        }

        for (i in 0 until outputSignals.size) {
            outputSignals[i].also { signal ->
                updateColor(if (signal.value == SIGNAL_ACTIVE) CDefaults.SIGNAL_ACTIVE_COLOR else CDefaults.GATE_UNSELECTED_COLOR)
                signal.updatePosition(
                    getPosition().x + sprite.width ,
                    getPosition().y + sprite.height * (i - MAX_POINTS / 2)
                )
            }
        }
        for ((counter, i) in (0 until inputSignals.size - 1).withIndex()) {
            val a = inputSignals[i]
            val b = inputSignals[i + 1]
            lines[counter].updatePosition(
                a.getPosition().x,
                a.getPosition().y,
                b.getPosition().x,
                b.getPosition().y
            )
        }
        for ((counter, i) in (0 until outputSignals.size - 1).withIndex()) {
            val a = outputSignals[i]
            val b = outputSignals[i + 1]
            lines[counter + inputSignals.size - 1].updatePosition(
                a.getPosition().x,
                a.getPosition().y,
                b.getPosition().x,
                b.getPosition().y
            )
        }
        /*for((counter, i) in (0 until MAX_POINTS step 2).withIndex()) {
          val a = signals[i]
          val b = signals[i+1]
          updateColor(if(a.value == SIGNAL_ACTIVE) CDefaults.SIGNAL_ACTIVE_COLOR else  CDefaults.GATE_UNSELECTED_COLOR)
          updateColor(if(b.value == SIGNAL_ACTIVE) CDefaults.SIGNAL_ACTIVE_COLOR else  CDefaults.GATE_UNSELECTED_COLOR)
        when(rotationDirection){
              ROTATE_RIGHT ->{
                  a.updatePosition(getPosition().x + sprite.width * 0.8125f, getPosition().y + sprite.height * 0.8f * (counter - MAX_POINTS/4) + (sprite.height * 0.8f) / 2 )
                  b.updatePosition(getPosition().x - sprite.width * 0.8125f, getPosition().y + sprite.height * 0.8f  * (counter - MAX_POINTS/4) + (sprite.height * 0.8f) / 2)
              }
              ROTATE_LEFT ->{
                  a.updatePosition(getPosition().x - sprite.width * 0.8125f, getPosition().y + sprite.height * 0.8f * (counter - MAX_POINTS/4) + (sprite.height * 0.8f) / 2 )
                  b.updatePosition(getPosition().x + sprite.width * 0.8125f, getPosition().y + sprite.height * 0.8f  * (counter - MAX_POINTS/4) + (sprite.height * 0.8f) / 2)
              }
              ROTATE_BOTTOM ->{
                  a.updatePosition(getPosition().x + sprite.width * 0.8f * (counter - MAX_POINTS/4) + (sprite.width * 0.8f) / 2 , getPosition().y + sprite.height * 0.8125f )
                  b.updatePosition(getPosition().x + sprite.width * 0.8f * (counter - MAX_POINTS/4) + (sprite.width * 0.8f) / 2, getPosition().y - sprite.height * 0.8125f  )
              }
              ROTATE_TOP ->{
                  a.updatePosition(getPosition().x + sprite.width * 0.8f * (counter - MAX_POINTS/4) + (sprite.width * 0.8f) / 2 , getPosition().y - sprite.height * 0.8125f )
                  b.updatePosition(getPosition().x + sprite.width * 0.8f * (counter - MAX_POINTS/4) + (sprite.width * 0.8f) / 2, getPosition().y + sprite.height * 0.8125f  )
              }
          }

          //  lines[counter].updatePosition(a.getPosition().x,a.getPosition().y, b.getPosition().x, b.getPosition().y)
        }*/

        if (selected) {
            updateColor(CDefaults.GATE_SELECTED_COLOR)
        }

        data.forEach {
            it.update()
        }
    }

    override fun contains(entity: CNode): CNode? {
        val parentCollides = super.contains(entity)
        if (parentCollides != null) {
            return parentCollides
        }
        data.forEach {
            if (it is CNode) {
                val childCollides = it.contains(entity)
                if (childCollides != null) {
                    return childCollides
                }
            }
        }
        return null
    }

    override fun contains(rect: Rectangle): CNode? {
        val parentCollides = super.contains(rect)
        if (parentCollides != null) {
            return parentCollides
        }
        data.forEach {
            if (it is CNode) {
                val childCollides = it.contains(rect)
                if (childCollides != null) {
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
        return CFanOutBus(
            getPosition().x,
            getPosition().y,
            inputSize,
            segments,
            rotationDirection,
            scene
        )
    }

}

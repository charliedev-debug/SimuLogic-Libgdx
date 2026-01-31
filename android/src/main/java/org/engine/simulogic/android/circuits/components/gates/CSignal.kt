package org.engine.simulogic.android.circuits.components.gates

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.CTypes
import org.engine.simulogic.android.scene.Entity
import org.engine.simulogic.android.scene.PlayGroundScene

open class CSignal(x: Float, y: Float,  type: CTypes, val signalIndex: Int, private val scene: PlayGroundScene) :
    CNode() {
    var parent: Entity? = null
    init {
        val textureAtlas = scene.assetManager.get("component.atlas", TextureAtlas::class.java)
        val spriteRegion = when (type) {
            CTypes.SIGNAL_IN -> textureAtlas.findRegion("INPUT")
            CTypes.SIGNAL_OUT -> textureAtlas.findRegion("OUTPUT")
            CTypes.E_SIGNAL_IN -> textureAtlas.findRegion("E-ENABLE")
            CTypes.D_SIGNAL_IN -> textureAtlas.findRegion("D-INPUT")
            CTypes.Q_SIGNAL_OUT -> textureAtlas.findRegion("Q-OUTPUT")
            CTypes.SIGNAL_RANGE_POINT->textureAtlas.findRegion("MOVE-INPUT")
            else -> textureAtlas.findRegion("INPUT")
        }
        sprite = Sprite(spriteRegion).apply {
            setOrigin(x, y)
            setSize(CDefaults.signalIconRadius, CDefaults.signalIconRadius)
            setOriginCenter()
            setPosition(x - CDefaults.signalIconRadius / 2f, y - CDefaults.signalIconRadius / 2f)
            rotation = 0f
        }
        this.type = type
    }

    override fun setSize(width: Float, height: Float) {
        sprite.apply {
            setOrigin(getPosition().x, getPosition().y)
            setSize(width, height)
            setOriginCenter()
            setPosition(getPosition().x - width/ 2f, getPosition().y - height / 2f)
            rotation = 0f
        }
    }

    override fun setHeight(value: Float) {
        setSize(sprite.width,value)
    }

    override fun setWidth(value: Float) {
       setSize(value, sprite.height)
    }

    override fun update() {
        updateColor(if(selected) CDefaults.INPUT_SELECTED_COLOR else CDefaults.INPUT_UNSELECTED_COLOR)

    }

    override fun clone(): CSignal {
        return CSignal(getPosition().x, getPosition().y, type, signalIndex, scene)
    }
}

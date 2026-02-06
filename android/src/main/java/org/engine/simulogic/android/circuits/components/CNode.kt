package org.engine.simulogic.android.circuits.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.interfaces.ICollidable
import org.engine.simulogic.android.circuits.components.interfaces.IExecutable
import org.engine.simulogic.android.scene.Entity

open class CNode : Entity(), ICollidable,IExecutable{
    protected lateinit var sprite : Sprite
    companion object{
        val INPUT_SIGNAL_INDEX = 0
        val OUTPUT_SIGNAL_A = 1
        val OUTPUT_SIGNAL_B = 2
        val OUTPUT_SIGNAL_C = 3
        val SIGNAL_ACTIVE = 1
        val SIGNAL_INACTIVE = 0
    }
    private val centerPosition = Vector2()
    override fun updatePosition(x: Float, y: Float) {
        sprite.apply {
            setOrigin(x, y)
            setSize(sprite.width, sprite.height)
            setOriginCenter()
            setPosition(x - sprite.width / 2f, y - sprite.height / 2f)
        }
    }

    override fun updatePosition(position: Vector2) {
        updatePosition(position.x,position.y)
    }

    override fun updatePosition(position: Vector3) {
        updatePosition(position.x,position.y)
    }

    override fun draw(spriteBatch: SpriteBatch) {
        data.forEach {
            it.draw(spriteBatch)
        }

        sprite.draw(spriteBatch)
    }

    override fun updateColor(color: Color) {
        sprite.color = color
    }

    override fun getPosition(): Vector2 {
        centerPosition.set(sprite.x + sprite.width / 2f, sprite.y + sprite.height / 2f)
        return centerPosition
    }

    override fun getCenter(): Vector2 {
        return  centerPosition.set(sprite.x, sprite.y)
    }

    override fun contains(x: Float, y: Float): CNode? {
         if(sprite.boundingRectangle.contains(x, y)){
             return this
         }
        return null
    }

    override fun contains(entity: CNode): CNode? {
        if(sprite.boundingRectangle.overlaps(entity.sprite.boundingRectangle)){
            return this
        }
        return null
    }

    override fun contains(rect: Rectangle): CNode? {
        if(sprite.boundingRectangle.overlaps(rect)){
            return this
        }
        return null
    }

    open fun toggleAction(){

    }

    fun getWidth():Float{
        return sprite.width
    }

    fun getHeight():Float{
        return sprite.height
    }

    override fun execute() {}

}

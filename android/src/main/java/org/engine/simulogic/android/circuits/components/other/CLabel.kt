package org.engine.simulogic.android.circuits.components.other

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import org.engine.simulogic.android.scene.Entity
import org.engine.simulogic.android.scene.LayerEnums
import org.engine.simulogic.android.scene.PlayGroundScene

open class CLabel(private val font:BitmapFont, var text:String, x:Float, y:Float, scene: PlayGroundScene):Entity() {
    private val position = Vector2(x,y)
    var color = Color.WHITE
    init {
        scene.getLayerById(LayerEnums.GRID_LAYER_LABELS.name).also {  layer->
            layer.attachChild(this)
        }
    }

    override fun draw(spriteBatch: SpriteBatch) {
        font.color = color
        font.draw(spriteBatch, text, position.x, position.y)
    }


    override fun updatePosition(x: Float, y: Float) {
        position.set(x,y)
    }
    override fun getPosition(): Vector2 {
        return position
    }

}

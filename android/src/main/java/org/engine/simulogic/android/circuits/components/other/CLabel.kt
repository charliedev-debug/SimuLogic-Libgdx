package org.engine.simulogic.android.circuits.components.other

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.CTypes
import org.engine.simulogic.android.scene.Entity
import org.engine.simulogic.android.scene.LayerEnums
import org.engine.simulogic.android.scene.PlayGroundScene

open class CLabel(private val font:BitmapFont, var text:String, x:Float, y:Float, private val scene: PlayGroundScene, private  val layerId: LayerEnums = LayerEnums.GRID_LAYER_LABELS):CNode() {
    private val position = Vector2(x,y)
    private val layout = GlyphLayout()
    var color = Color.WHITE
    init {
        val textureAtlas = scene.assetManager.get("component.atlas", TextureAtlas::class.java)
        val spriteRegion = textureAtlas.findRegion("TRANSPARENT")
        layout.setText(font,text)
        type = CTypes.LABEL
        sprite = Sprite(spriteRegion)
        scene.getLayerById(layerId.name).also {  layer->
            layer.attachChild(this)
        }
    }

    override fun draw(spriteBatch: SpriteBatch) {
        font.color = color
        font.draw(spriteBatch, text, position.x, position.y)
    }

    override fun update() {
        // this helps when resolving for collisions
        sprite.setOrigin(position.x , position.y)
        sprite.setSize(layout.width, layout.height)
        sprite.setOriginCenter()
        sprite.setPosition(position.x,position.y)
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

    override fun updatePosition(x: Float, y: Float) {
        position.set(x,y)
    }
    override fun getPosition(): Vector2 {
        return position
    }

    override fun clone(): CLabel{
        return CLabel(font,text,position.x, position.y, scene, layerId)
    }

}

package org.engine.simulogic.android.circuits.components.other

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.scene.PlayGroundScene

class CRect (x:Float, y:Float, width:Float, height:Float, color: Color, private val scene: PlayGroundScene) : CNode(){
    init {

        val textureAtlas = scene.assetManager.get("component.atlas", TextureAtlas::class.java)
        val spriteRegion = textureAtlas.findRegion("TRANSPARENT")
        sprite = Sprite(spriteRegion).apply {
            setOrigin(x, y)
            setSize(width, height)
            setOriginCenter()
            rotation = 0f
            setPosition(x - width / 2f, y - height / 2f)
        }

        sprite.color = color

    }

}

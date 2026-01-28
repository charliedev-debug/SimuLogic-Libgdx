package org.engine.simulogic.android.circuits.components.other

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.scene.LayerEnums
import org.engine.simulogic.android.scene.PlayGroundScene

class CPointer (x:Float, y:Float, scene: PlayGroundScene) : CNode() {

    init {

        val textureAtlas = scene.assetManager.get("component.atlas", TextureAtlas::class.java)
        val spriteRegion = textureAtlas.findRegion("POINTER")
        val width = 30f
        val height = 30f
        sprite = Sprite(spriteRegion).apply {
            setOrigin(x, y)
            setSize(width, height)
            setOriginCenter()
            rotation = 0f
            setPosition(x - width / 2f, y - height / 2f)
        }

        scene.getLayerById(LayerEnums.SCREEN_LAYER.name).also { layer ->
            layer.attachChild(this)
        }
    }
}

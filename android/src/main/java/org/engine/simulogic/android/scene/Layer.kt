package org.engine.simulogic.android.scene

import com.badlogic.gdx.graphics.g2d.SpriteBatch

open class Layer(val layerId:String) : Entity(){
    override fun draw(spriteBatch: SpriteBatch) {
        synchronized(data) {
            data.forEach { entity ->
                if (entity.isVisible) {
                    entity.draw(spriteBatch)
                }
            }
        }
    }

    override fun update() {
        autoDetachChildren()
    }

}

package org.engine.simulogic.android.scene

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.engine.simulogic.android.circuits.components.CNode

open class Layer(val layerId:String) : Entity(){
    override fun draw(spriteBatch: SpriteBatch, camera: OrthographicCamera) {
        val factor = (1f/ camera.zoom)
        val viewPortWidth = (camera.viewportWidth / factor)
        val viewPortHeight = (camera.viewportHeight / factor)
        val topLeft = camera.position.x - viewPortWidth / 2
        val topRight = camera.position.x + viewPortWidth / 2
        val bottomLeft = camera.position.y - viewPortHeight / 2
        val bottomRight = camera.position.y + viewPortHeight / 2
        synchronized(data) {
            data.forEach { entity ->
                if (entity.isVisible) {
                    if(entity is CNode){
                        val pos = entity.getPosition()
                        val width = entity.getWidth()
                        val height = entity.getHeight()
                        if(((pos.x + width * 2)>= topLeft && (pos.x - width * 2) <= topRight &&
                            (pos.y + height * 2) >= bottomLeft && (pos.y - height) <= bottomRight) || !entity.cameraClippingEnabled) {
                            entity.draw(spriteBatch)
                        }
                    }else{
                        entity.draw(spriteBatch)
                    }
                }
            }
        }
    }

    override fun update() {
        autoDetachChildren()
    }

}

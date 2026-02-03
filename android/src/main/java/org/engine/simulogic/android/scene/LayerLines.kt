package org.engine.simulogic.android.scene
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

class LayerLines(layerId:String) : Layer(layerId) {
    override fun draw(shapeRenderer: ShapeRenderer) {
        synchronized(data) {
            data.forEach { entity ->
                if (entity.isVisible) {
                    entity.draw(shapeRenderer)
                }
            }
        }
    }

}

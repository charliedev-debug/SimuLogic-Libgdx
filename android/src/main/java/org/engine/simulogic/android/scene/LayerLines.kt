package org.engine.simulogic.android.scene
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import org.engine.simulogic.android.circuits.components.lines.CLine

class LayerLines(layerId:String) : Layer(layerId) {
    override fun draw(shapeRenderer: ShapeRenderer, camera: OrthographicCamera) {
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
                    if(entity is CLine){
                        val x1 = entity.x1
                        val x2 = entity.x2
                        val y1 = entity.y1
                        val y2 = entity.y2
                        if(((x1+ 10f) >= topLeft && (x1-10f) <= topRight)||
                            ((x2+ 10f) >= topLeft && (x2-10f) <= topRight)||
                            ((y1 + 10f) >= bottomLeft && (y1 - 10f) <= bottomRight)||
                            ((y2 + 10f) >= bottomLeft && (y2 - 10f) <= bottomRight)){
                            entity.draw(shapeRenderer)
                        }
                    }else {
                        entity.draw(shapeRenderer)
                    }
                }
            }
        }
    }

}

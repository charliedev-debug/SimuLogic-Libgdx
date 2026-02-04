package org.engine.simulogic.android.scene

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer


class PlayGroundScene (private val spriteBatch: SpriteBatch,
                       private val camera: OrthographicCamera, val assetManager:AssetManager) : Layer(LayerEnums.SCENE.name){

    private val shapeRenderer = ShapeRenderer(10000)

    init {
        addLayer(LayerLines(LayerEnums.GRID_LAYER.name))
        addLayer(Layer(LayerEnums.GRID_LAYER_LABELS.name))
        addLayer(LayerLines(LayerEnums.CONNECTION_LAYER.name))
        addLayer(Layer(LayerEnums.CONNECTION_LAYER_INPUTS.name))
        addLayer(Layer(LayerEnums.GATE_LAYER.name))
        addLayer(Layer(LayerEnums.SCREEN_LAYER.name))
    }
   private fun addLayer(layer: Layer):Layer{
       data.add(layer)
       return layer
    }

    fun getLayerById(layerId: String):Layer{
        return data.find { (it as Layer).layerId == layerId } as Layer
    }

    override fun update() {
        synchronized(data) {
            data.forEach {
                it.update()
            }
        }
    }
    override fun draw() {
        camera.update()
        spriteBatch.projectionMatrix = camera.combined
        spriteBatch.begin()
        synchronized(data) {
            data.forEachIndexed { index, entity ->
                if (entity is LayerLines) {
                    spriteBatch.end()
                    shapeRenderer.projectionMatrix = camera.combined
                    shapeRenderer.setAutoShapeType(true)
                    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
                }
                if (entity.isVisible) {
                    if (entity is LayerLines) {
                        entity.draw(shapeRenderer,camera)
                    } else
                        entity.draw(spriteBatch,camera)
                }
                //TODO("look ahead to see if there is a line layer before flushing")
                if (entity is LayerLines) {
                    shapeRenderer.end()
                    if ((index + 1) < data.size) {
                        spriteBatch.projectionMatrix = camera.combined
                        spriteBatch.begin()
                    }
                }
            }
        }
        spriteBatch.end()
    }

}

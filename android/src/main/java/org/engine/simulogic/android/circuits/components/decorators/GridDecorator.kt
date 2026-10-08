package org.engine.simulogic.android.circuits.components.decorators

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import org.engine.simulogic.android.SimulationLoop
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.interfaces.IUpdate
import org.engine.simulogic.android.circuits.components.lines.CLine
import org.engine.simulogic.android.circuits.components.other.CLabel
import org.engine.simulogic.android.circuits.theme.EnvironmentTheme
import org.engine.simulogic.android.events.MotionGestureListener
import org.engine.simulogic.android.scene.LayerEnums
import org.engine.simulogic.android.scene.PlayGroundScene
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

class GridDecorator(private val font:BitmapFont,private val scene:PlayGroundScene, private val gestureListener: MotionGestureListener, private val camera: OrthographicCamera):IUpdate {
    private val linesAxisX = mutableListOf<CLine>()
    private val linesAxisY = mutableListOf<CLine>()
    private val labelsX = mutableListOf<GridLabel>()
    private val labelsY = mutableListOf<GridLabel>()
    private var lastZoom = camera.zoom
    private var lineLayer = scene.getLayerById(LayerEnums.GRID_LAYER.name)
    private val lineColor = Color(72f/255f, 72f/255f, 80f/255f, 1f)
    private val labelColor = Color(200f/255f, 200f/255f, 200f/255f, 1f)
    private val labelHeaderColor = Color(120f/255f, 120f/255f, 120f/255f, 1f)
    private val lastCameraPosition = Vector2()
    private var labelsVisible = true
    private var gridVisible = true
    private var labelHeaderVisible = true
    private var positionGridLine = false
    var refresh = false
    private var inititialized = false
    class GridLabel(font: BitmapFont, text:String,x:Float, y:Float, scene: PlayGroundScene): CLabel(font,25f, text, x, y, scene)

    fun toggleLabels(value:Boolean){
        labelsVisible = value
    }

    fun toggleGrid(value: Boolean){
        gridVisible = value
    }

    fun showLabelHeader(){
        labelHeaderVisible = true
    }

    fun hideLabelHeader(){
        labelHeaderVisible = false
    }

    fun hidePositionGridLine(){
        positionGridLine = false
    }

    fun showPositionGridLine(){
        positionGridLine = true
    }

    override fun update() {
        val factor = 1f/ camera.zoom
        val viewPortWidth = (camera.viewportWidth / factor)
        val viewPortHeight = (camera.viewportHeight / factor)
        val rawSpacing = viewPortHeight / 10f
        val spacingX =  ceil(rawSpacing / CDefaults.GRID_WIDTH) * CDefaults.GRID_WIDTH
        val spacingY =  ceil(rawSpacing / CDefaults.GRID_WIDTH) * CDefaults.GRID_WIDTH
        val labelOffsetY = 0f
        val labelOffsetXTop = -(SimulationLoop.offsetTop * viewPortHeight / Gdx.graphics.height)
        val labelFontSizeMax = 200f
        val labelFontSize =  min(15f * camera.zoom, labelFontSizeMax)
        val lineCountX = round(viewPortWidth / spacingX).toInt() + 1
        val lineCountY = round(viewPortHeight / spacingY).toInt() + 1
        val originX = camera.position.x - viewPortWidth / 2f
        val originY = camera.position.y - viewPortHeight / 2f
        // prevents flickering
        val lineWidth = 1.1f
        val endX = originX + viewPortWidth
        val endY = originY + viewPortHeight
        if(!inititialized){
            lastCameraPosition.set(camera.position.x, camera.position.y)
            inititialized = true
            linesAxisX.forEach { it.detachSelf() }
            linesAxisX.clear()
            linesAxisY.forEach { it.detachSelf() }
            linesAxisY.clear()
            labelsX.forEach { it.detachSelf() }
            labelsX.clear()
            labelsY.forEach { it.detachSelf() }
            labelsY.clear()
            for(i in 0 until lineCountX){
                val x = originX + i * spacingX
                CLine(x,originY,x,originY+ viewPortHeight, lineWidth).also { line->
                    line.color = EnvironmentTheme.colorOutline2//lineColor
                    linesAxisX.add(line)
                    lineLayer.attachChild(line)
                    line.isVisible =  gridVisible
                    if(i % 2 == 0){
                        line.color = EnvironmentTheme.colorOutline
                    }else
                        line.color = EnvironmentTheme.colorOutline2
                }

                labelsX.add(GridLabel(font, "$i", x, originY + viewPortHeight + labelOffsetXTop, scene).apply {
                    color = EnvironmentTheme.colorOnBackground
                    fontSize = labelFontSize
                    isVisible = labelsVisible
                })
            }

            for(i in 0 .. lineCountY){
                val y = originY + i * spacingY
                CLine(originX, y, originX + viewPortWidth,y, lineWidth).also { line->
                    line.color = EnvironmentTheme.colorOutline2
                    linesAxisY.add(line)
                    lineLayer.attachChild(line)
                    line.isVisible = gridVisible
                    if(i % 2 == 0){
                        line.color = EnvironmentTheme.colorOutline
                    }else
                        line.color = EnvironmentTheme.colorOutline2
                }
                val approximateY = floor(y / CDefaults.GRID_WIDTH) * CDefaults.GRID_WIDTH
                labelsY.add(GridLabel(font, "${approximateY.toInt()}", originX + labelOffsetY, y, scene).apply {
                    color = EnvironmentTheme.colorOnBackground
                    fontSize = labelFontSize
                    isVisible = labelsVisible
                })
            }

        }
        if(camera.zoom != lastZoom && !gestureListener.isZooming){
            linesAxisX.onEachIndexed { index, line ->
                val x = originX + index * spacingX
                line.updatePosition(x, originY, x, originY + viewPortHeight)
            }
            linesAxisY.onEachIndexed { index, line ->
                val y = originY + index * spacingY
                line.updatePosition(originX, y, originX + viewPortWidth, y)
            }
            lastZoom = camera.zoom
        }
        val cameraOffsetX = lastCameraPosition.x - camera.position.x
        val cameraOffsetY = lastCameraPosition.y - camera.position.y
        linesAxisX.onEachIndexed { index, line->
            line.updatePosition(line.x1 + cameraOffsetX, originY,line.x2 + cameraOffsetX, endY)
            labelsX[index].also {label ->
                val approximateX = (ceil(line.x1 / CDefaults.GRID_WIDTH) * CDefaults.GRID_WIDTH) / 10
                label.text = "${approximateX.toInt()}"
                label.updatePosition(line.x1, endY + labelOffsetXTop)
                label.fontSize = labelFontSize
                label.isVisible = labelsVisible
            }
            line.isVisible = gridVisible
        }
        labelsX.onEach{ label->
            label.updatePosition(label.getPosition().x + cameraOffsetX, label.getPosition().y)
        }
        linesAxisY.onEachIndexed { index, line->
            line.updatePosition(originX, line.y1 + cameraOffsetY, endX, line.y2 + cameraOffsetY)
            labelsY[index].also {label ->
                val approximateY = (ceil(line.y1 / CDefaults.GRID_WIDTH) * CDefaults.GRID_WIDTH) / 10
                label.text = "${approximateY.toInt()}"
                label.updatePosition(originX + label.fontSize * 2f, line.y1)
                label.fontSize = labelFontSize
                label.isVisible = labelsVisible
            }
            line.isVisible = gridVisible
        }

        linesAxisX.first().also { line->
            if(line.x1 < originX - spacingX){
                val last = linesAxisX.last()
                line.updatePosition(last.x1 + spacingX, line.y1,last.x1 + spacingX, line.y2)
                linesAxisX.remove(line)
                linesAxisX.add(line)
            }
        }

        linesAxisX.last().also {
            if(it.x1 > endX + spacingX){
                val first = linesAxisX.first()
                it.updatePosition(first.x1 - spacingX, it.y1,first.x1 - spacingX, it.y2)
                linesAxisX.remove(it)
                linesAxisX.add(0,it)
            }
        }

        linesAxisY.first().also {
            if(it.y1 < originY - spacingY){
                val last = linesAxisY.last()
                it.updatePosition(it.x1, last.y1 + spacingY,it.x1, last.y2 + spacingY)
                linesAxisY.remove(it)
                linesAxisY.add(it)
            }
        }

        linesAxisY.last().also {
            if(it.y1 > endY + spacingY){
                val first = linesAxisY.first()
                it.updatePosition(it.x1, first.y1 - spacingY,it.x1, first.y2 - spacingY)
                linesAxisY.remove(it)
                linesAxisY.add(0,it)
            }
        }

        lastCameraPosition.set(camera.position.x, camera.position.y)
    }
}

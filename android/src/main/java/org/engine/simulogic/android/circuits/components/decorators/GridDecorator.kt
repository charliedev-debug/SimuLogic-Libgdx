package org.engine.simulogic.android.circuits.components.decorators

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.interfaces.IUpdate
import org.engine.simulogic.android.circuits.components.lines.CLine
import org.engine.simulogic.android.circuits.components.other.CLabel
import org.engine.simulogic.android.circuits.components.other.CRangeSelect
import org.engine.simulogic.android.scene.LayerEnums
import org.engine.simulogic.android.scene.PlayGroundScene
import kotlin.math.abs
import kotlin.math.round

class GridDecorator(private val font:BitmapFont,private val scene:PlayGroundScene, private val camera: OrthographicCamera):IUpdate {
    private val linesAxisX = mutableListOf<CLine>()
    private val linesAxisY = mutableListOf<CLine>()
    private val labelsX = mutableListOf<GridLabel>()
    private val labelsY = mutableListOf<GridLabel>()
    private var lastZoom = camera.zoom
    private var lineLayer = scene.getLayerById(LayerEnums.GRID_LAYER.name)
    private val lineColor = Color(72f/255f, 72f/255f, 80f/255f, 1f)
    private val labelColor = Color(200f/255f, 200f/255f, 200f/255f, 1f)
    private val labelHeaderColor = Color(120f/255f, 120f/255f, 120f/255f, 1f)
    private var labelsVisible = true
    private var gridVisible = true
    private var labelHeaderVisible = true
    var refresh = false
    class GridLabel(font: BitmapFont, text:String,x:Float, y:Float, scene: PlayGroundScene): CLabel(font, text, x, y, scene){
           var lineHeader = CLine(0f,0f, 0f, 0f,1f)
        override fun detachSelf() {
            super.detachSelf()
            lineHeader.detachSelf()
        }
    }

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

    override fun update() {
        val factor = (1f/ camera.zoom)
        val viewPortWidth = (camera.viewportWidth / factor)
        val viewPortHeight = (camera.viewportHeight / factor)
        val spacingX = CDefaults.GRID_WIDTH * 2
        val spacingY = CDefaults.GRID_HEIGHT * 2
        val labelSpacingX = (spacingX * 4).toInt()
        val labelSpacingY = (spacingY * 4).toInt()
        val labelOffsetY = 0f
        val labelOffsetX = 15f
        val lineCountX = round(viewPortWidth / spacingX).toInt() + 2
        val lineCountY = round(viewPortHeight / spacingY).toInt() + 2
        val originX = camera.position.x - viewPortWidth / 2f
        val originY = camera.position.y - viewPortHeight / 2f
        // prevents flickering
        val lineWidth = 1f
        val endX = originX + viewPortWidth
        val endY = originY + viewPortHeight

        if(lastZoom != camera.zoom|| linesAxisX.isEmpty() || linesAxisY.isEmpty()|| refresh){
            linesAxisX.forEach { it.detachSelf() }
            linesAxisX.clear()
            linesAxisY.forEach { it.detachSelf() }
            linesAxisY.clear()
            labelsX.forEach { it.detachSelf() }
            labelsX.clear()
            labelsY.forEach { it.detachSelf() }
            labelsY.clear()
            for(i in 0  .. lineCountX ){
                val x = originX + i * spacingX
                CLine(x,originY,x,originY+ viewPortHeight, lineWidth).also { line->
                    line.color = lineColor
                    linesAxisX.add(line)
                    lineLayer.attachChild(line)
                    line.isVisible = gridVisible
                }
            }

            val limitLabelX = (viewPortWidth / labelSpacingX ).toInt() / 2
            for(i in -limitLabelX .. limitLabelX){
                val x = originX + i * labelSpacingX
                val lx = round(x / labelSpacingX).toInt() * labelSpacingX
                labelsX.add(GridLabel(font, "$lx", x, originY + viewPortHeight - labelOffsetY, scene).apply {
                        color = labelColor
                        lineHeader.updatePosition(x, getPosition().y, x, 0f )
                        lineHeader.color = labelHeaderColor
                        lineHeader.lineWidth = lineWidth
                        lineLayer.attachChild(lineHeader)
                        lineHeader.isVisible = labelHeaderVisible && gridVisible
                        isVisible = labelsVisible
                })
            }

            for(i in 0 .. lineCountY){
                val y = originY + i * spacingY
                CLine(originX, y, originX + viewPortWidth,y, lineWidth).also { line->
                    line.color = lineColor
                    linesAxisY.add(line)
                    lineLayer.attachChild(line)
                    line.isVisible = gridVisible
                }
            }

            val limitLabelY = (viewPortHeight / labelSpacingY).toInt() / 2
            for(i in - limitLabelY .. limitLabelY){
                val y = originY + i * labelSpacingY
                val ly = round(y / labelSpacingY).toInt() * labelSpacingY
                labelsY.add(GridLabel(font, "$ly", originX + labelOffsetY, y, scene).apply {
                    color = labelColor
                    lineHeader.updatePosition(getPosition().x, y, viewPortWidth, y )
                    lineHeader.color = labelHeaderColor
                    lineHeader.lineWidth = lineWidth
                    lineLayer.attachChild(lineHeader)
                    lineHeader.isVisible = labelHeaderVisible && gridVisible
                    isVisible = labelsVisible
                })
            }
            lastZoom = camera.zoom
            refresh = false
        }

        linesAxisX.sortBy { it.x1 }
        val lastX = linesAxisX[linesAxisX.size - 1]
        val firstX = linesAxisX[0]
        linesAxisX.forEach {
            it.updatePosition(it.x1,originY, it.x2, originY + viewPortHeight)
            it.isVisible = gridVisible
        }
        if(firstX.x1 > (originX+ spacingX)){
            val offsetX = firstX.x1 - spacingX
            val y = originY + viewPortHeight
            lastX.updatePosition(offsetX,originY, offsetX, y )
        }else if(lastX.x1 < (endX - spacingX)){
            val offsetX = lastX.x1 + spacingX
            val y = originY + viewPortHeight
            firstX.updatePosition(offsetX, originY, offsetX, y)
        }

        labelsX.sortBy { it.getPosition().x }
        labelsX.forEach {
            it.updatePosition(it.getPosition().x, originY + viewPortHeight - labelOffsetY)
            it.lineHeader.updatePosition(it.getPosition().x, it.getPosition().y, it.getPosition().x, originY)
            it.lineHeader.isVisible = labelHeaderVisible && gridVisible
            it.isVisible = labelsVisible
        }
        val lastLabelX = labelsX[labelsX.size - 1]
        val firstLabelX = labelsX[0]
        if(firstLabelX.getPosition().x > (originX + labelSpacingX)){
            val offsetX = firstLabelX.getPosition().x - labelSpacingX
            lastLabelX.updatePosition(offsetX,firstLabelX.getPosition().y)
            lastLabelX.lineHeader.updatePosition(offsetX, firstLabelX.getPosition().y, offsetX, originY )
            lastLabelX.text = "${firstLabelX.text.toInt() - labelSpacingX}"
        }else if(lastLabelX.getPosition().x <= (endX - labelSpacingX)){
            val offsetX = lastLabelX.getPosition().x + labelSpacingX
            firstLabelX.updatePosition(offsetX, firstLabelX.getPosition().y)
            firstLabelX.lineHeader.updatePosition(offsetX, firstLabelX.getPosition().y, offsetX, originY)
            firstLabelX.text = "${lastLabelX.text.toInt()  + labelSpacingX}"
        }


        linesAxisY.sortBy { it.y1 }
        val lastY = linesAxisY[linesAxisY.size - 1]
        val firstY = linesAxisY[0]
        linesAxisY.forEach {
            it.updatePosition(originX, it.y1, originX + viewPortWidth, it.y2)
            it.isVisible = gridVisible
        }
        if(firstY.y1 > (originY + spacingY)){
            val offsetY = firstY.y1 - spacingY
            val x = originX + viewPortWidth
            lastY.updatePosition(originX,offsetY, x, offsetY)
        }else if(lastY.y1 <= (endY - spacingY)){
            val offsetY = lastY.y1 + spacingY
            val x = originX + viewPortWidth
            firstY.updatePosition(originX, offsetY, x, offsetY)
        }

        labelsY.sortBy { it.getPosition().y }
        labelsY.forEach {
            it.updatePosition(originX + labelOffsetY + it.getWidth() / 4f , it.getPosition().y)
            it.lineHeader.updatePosition(it.getPosition().x, it.getPosition().y, endX, it.getPosition().y)
            it.lineHeader.isVisible = labelHeaderVisible && gridVisible
            it.isVisible = labelsVisible
        }
        val lastLabelY = labelsY[labelsY.size - 1]
        val firstLabelY = labelsY[0]

        if(firstLabelY.getPosition().y > (originY + labelSpacingY)){
            val offsetY = firstLabelY.getPosition().y - labelSpacingY
            lastLabelY.updatePosition(lastLabelY.getPosition().x, offsetY)
            lastLabelY.lineHeader.updatePosition(lastLabelY.getPosition().x,offsetY, endX, offsetY)
            lastLabelY.text = "${firstLabelY.text.toInt() - labelSpacingY}"
        }else if(lastLabelY.getPosition().y < (endY - labelSpacingY)){
            val offsetY = lastLabelY.getPosition().y + labelSpacingY
            firstLabelY.updatePosition(lastLabelY.getPosition().x, offsetY)
            lastLabelY.lineHeader.updatePosition(lastLabelY.getPosition().x,offsetY, endX, offsetY)
            firstLabelY.text = "${lastLabelY.text.toInt() + labelSpacingY}"
        }
    }
}

package org.engine.simulogic.android.circuits.components.combinational

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Rectangle
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.CTypes
import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.lines.CLine
import org.engine.simulogic.android.circuits.components.other.CAnchor
import org.engine.simulogic.android.circuits.components.other.CLabel
import org.engine.simulogic.android.circuits.components.other.CSprite
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.circuits.theme.EnvironmentTheme
import org.engine.simulogic.android.scene.LayerEnums
import org.engine.simulogic.android.scene.PlayGroundScene

class CDeMultiplexer(x:Float, y:Float,rotationDirection:Int, private val title:String, private val font: BitmapFont, private val connection: Connection, private val scene: PlayGroundScene) :CNode(){

    private val lines = mutableListOf<CLine>()
    private val signalLineMarkers = mutableListOf<CLine>()
    private val signalSignalLabelMarker = mutableListOf<ListNode>()
    private var inputDataCount = 16
    private var inputSelectorCount = 2
    private var outputDataCount = 2
    private var textTitleLabel: ListNode?= null
    private var centerLabelBanner : CSprite? = null
    private var centerLabelTexture = "1-2"
    constructor(x:Float, y:Float, title:String,font: BitmapFont,connection: Connection, scene: PlayGroundScene):this(x, y, ROTATE_RIGHT, title, font,connection, scene)
    init {
        val textureAtlas = scene.assetManager.get("${EnvironmentTheme.name}.atlas", TextureAtlas::class.java)
        val spriteRegion = textureAtlas.findRegion("TRANSPARENT")
        val lineWidth = 2f
        type = CTypes.DEMULTIPLEXER
        when(title){
            "DEMUXER 1-16"->{
                inputDataCount = 1
                inputSelectorCount = 4
                outputDataCount = 16
                centerLabelTexture = "1-16"
                type = CTypes.DEMULTIPLEXER
            }

            "DEMUXER 1-8"->{
                inputDataCount = 1
                inputSelectorCount = 3
                outputDataCount = 8
                centerLabelTexture = "1-8"
                type = CTypes.DEMULTIPLEXER
            }

            "DEMUXER 1-4"->{
                inputDataCount = 1
                inputSelectorCount = 2
                outputDataCount = 4
                centerLabelTexture = "1-4"
                type = CTypes.DEMULTIPLEXER
            }

            "DEMUXER 1-2"->{
                inputDataCount = 1
                inputSelectorCount = 1
                outputDataCount = 2
                centerLabelTexture = "1-2"
                type = CTypes.DEMULTIPLEXER
            }
        }
        val maxWidth = outputDataCount * CDefaults.muxerSignalSpacing
        val maxInputWidth = inputDataCount * CDefaults.muxerSignalSpacing
        val inputDataWidth = maxWidth
        val selectorHeight = inputSelectorCount * CDefaults.muxerSignalSpacing
        this.rotationDirection = rotationDirection
        sprite = Sprite(spriteRegion).apply {
            setOrigin(x , y)
            setSize(inputDataWidth, selectorHeight)
            setOriginCenter()
            when(rotationDirection){
                ROTATE_BOTTOM->{
                    rotation = 270f
                }
                ROTATE_TOP->{
                    rotation = 90f
                }
                ROTATE_LEFT->{
                    rotation = 180f
                }
                ROTATE_RIGHT->{
                    rotation = 0f
                }
            }
            setPosition(x - inputDataWidth / 2f,y - selectorHeight / 2f)
        }


        val offsetLeft = (sprite.width - sprite.width * 0.5f / inputDataCount ) / inputDataCount + maxInputWidth / 2f
        val offsetTop = (sprite.height - sprite.height / inputSelectorCount) / inputSelectorCount
        val offsetCenterX = sprite.width * 0.5f / outputDataCount
        // create input data
        for( i in 0 until inputDataCount){
            CSignal(getCenter().x + i * sprite.width / inputDataCount + offsetLeft / 2f,
                getCenter().y + selectorHeight + CDefaults.muxerSignalSpacing, CTypes.SIGNAL_IN,i,scene).also { signal ->
                signals.add(signal)
                ListNode(CLabel(font, 25f, "${i+1}", x, y, scene).also {
                    it.color = EnvironmentTheme.colorOnBackground
                    it.anchor = CAnchor(signal, CAnchor.ALIGN_BOTTOM_RIGHT)
                }).also { node ->
                    node.value.collidable = false
                    signalSignalLabelMarker.add(node)
                }
            }

        }

        // create input selector
        for( i in 0 until inputSelectorCount){
            CSignal(getCenter().x  - CDefaults.muxerSignalSpacing,getCenter().y + i * sprite.height / inputSelectorCount + offsetTop / 2f,
                CTypes.SIGNAL_IN,signals.size + i,scene).also { signal->
                signals.add(signal)
                ListNode(CLabel(font, 25f, "S${i+1}", x, y, scene).also {
                    it.color = EnvironmentTheme.colorOnBackground
                    it.anchor = CAnchor(signal, CAnchor.ALIGN_TOP_RIGHT)
                }).also { node ->
                    node.value.collidable = false
                    signalSignalLabelMarker.add(node)
                }
            }

        }

        // create output data
        for( i in 0 until outputDataCount){
            CSignal(getCenter().x + i * CDefaults.muxerSignalSpacing + offsetCenterX,
                getCenter().y - CDefaults.muxerSignalSpacing, CTypes.SIGNAL_OUT,i,scene).also { signal ->
                signals.add(signal)
                ListNode(CLabel(font, 25f, "${i+1}", x, y, scene).also {
                    it.color = EnvironmentTheme.colorOnBackground
                    it.anchor = CAnchor(signal, CAnchor.ALIGN_TOP_RIGHT)
                }).also { node ->
                    node.value.collidable = false
                    signalSignalLabelMarker.add(node)
                }
            }
        }


        signals.forEach {
            attachChild(it)
        }

        scene.getLayerById(LayerEnums.GATE_LAYER.name).also { layer ->
            layer.attachChild(this)
        }
        scene.getLayerById(LayerEnums.GATE_LAYER.name).also { layer ->
            centerLabelBanner = CSprite(x, y, 100f, 50f,centerLabelTexture, EnvironmentTheme.colorOnBackground,scene).also {
                layer.attachChild(it)
            }
        }

        scene.getLayerById(LayerEnums.CONNECTION_LAYER.name).also { layer->
            // bottom line
            lines.add(CLine(getCenter().x, getCenter().y, getCenter().x + inputDataWidth,getCenter().y,lineWidth * 2f))
            // top line
            lines.add(CLine(getCenter().x,getCenter().y + selectorHeight,getCenter().x + inputDataWidth,getCenter().y + selectorHeight,lineWidth * 2f))
            // left line
            lines.add(CLine(getCenter().x ,getCenter().y,getCenter().x , getCenter().y + selectorHeight,lineWidth * 2f))
            // right line
            lines.add(CLine(getCenter().x + inputDataWidth,getCenter().y,getCenter().x + inputDataWidth, getCenter().y + selectorHeight,lineWidth * 2f))

            for(i in 0 until inputDataCount){
                val signal = signals[i]
                CLine(signal.getPosition().x, signal.getPosition().y,signal.getPosition().x, signal.getPosition().y - CDefaults.muxerSignalSpacing * 2f, lineWidth).also { line->
                    signalLineMarkers.add(line)
                    lines.add(line)
                }
            }

            for( i in inputDataCount until (inputDataCount+inputSelectorCount)){
                val signal = signals[i]
                CLine(signal.getPosition().x, signal.getPosition().y,signal.getPosition().x, signal.getPosition().y - CDefaults.muxerSignalSpacing * 2f, lineWidth).also { line->
                    signalLineMarkers.add(line)
                    lines.add(line)
                }
            }
            for(i in inputDataCount + inputSelectorCount until inputDataCount + inputSelectorCount + outputDataCount){
                val signal = signals[i]
                CLine(signal.getPosition().x, signal.getPosition().y,signal.getPosition().x, signal.getPosition().y + CDefaults.muxerSignalSpacing * 2f, lineWidth).also { line->
                    signalLineMarkers.add(line)
                    lines.add(line)
                }
            }
            lines.forEach {
                layer.attachChild(it)
            }
        }
        ListNode(CLabel(font, 50f, "DEMUXER", x, y, scene).also {
            it.color = EnvironmentTheme.colorOnBackground
            it.anchor = CAnchor(this, CAnchor.ALIGN_TOP_LEFT)
        }).also { node ->
            node.value.collidable = false
            textTitleLabel = node
        }
    }

    override fun execute() {
        //unused
    }


    override fun attachSelf() {
        super.attachSelf()
        scene.getLayerById(LayerEnums.CONNECTION_LAYER.name).also { layer ->
            lines.forEach {
                it.isRemoved = false
                layer.attachChild(it)
            }
        }
        scene.getLayerById(LayerEnums.GATE_LAYER.name).also { layer ->
            signals.forEach {
                it.isRemoved = false
                layer.attachChild(it)
            }
            layer.attachChild(this)
            centerLabelBanner?.also {
                layer.attachChild(it)
            }
        }
        textTitleLabel?.attachSelf()
        signalSignalLabelMarker.forEach {
            it.attachSelf()
        }
    }

    override fun detachSelf() {
        super.detachSelf()
        lines.forEach { it.detachSelf() }
        signals.forEach { it.detachSelf() }
        centerLabelBanner?.detachSelf()
        textTitleLabel?.detachSelf()
        signalSignalLabelMarker.forEach {
            it.detachSelf()
        }
    }

    override fun update() {
        if(selected){
            updateColor(CDefaults.GATE_SELECTED_COLOR)
        }else{
            updateColor(if(signals[0].value == SIGNAL_ACTIVE) CDefaults.SIGNAL_ACTIVE_COLOR else  CDefaults.LED_INACTIVE_COLOR)
        }
        val maxWidth = outputDataCount * CDefaults.muxerSignalSpacing
        val selectorHeight = inputSelectorCount * CDefaults.muxerSignalSpacing
        val zoomOffset = lines[0].zoomFactor * lines[0].lineWidth / 2f
        when(rotationDirection){
            ROTATE_RIGHT->{

                val offsetLeft = (sprite.width + zoomOffset - sprite.width * 0.5f / inputDataCount ) / inputDataCount
                val offsetTop = (sprite.height - sprite.height / inputSelectorCount) / inputSelectorCount
                val offsetCenterX = sprite.width * 0.5f / outputDataCount
                // update input data input
                for( i in 0 until inputDataCount){
                    val signal = signals[i]
                    signal.updatePosition(getCenter().x + i * CDefaults.muxerSignalSpacing + offsetLeft,getCenter().y + selectorHeight + CDefaults.muxerSignalSpacing)
                    signalLineMarkers[i].updatePosition(signal.getPosition().x, signal.getPosition().y,signal.getPosition().x, signal.getPosition().y - CDefaults.muxerSignalSpacing * 2f)
                }
                // update input selectors
                for((counter, i) in (inputDataCount until inputDataCount + inputSelectorCount).withIndex()){
                    val signal = signals[i]
                    signal.updatePosition(getCenter().x  - CDefaults.muxerSignalSpacing,getCenter().y + counter * sprite.height / inputSelectorCount + offsetTop / 2f)
                    signalLineMarkers[i].updatePosition(signal.getPosition().x, signal.getPosition().y,signal.getPosition().x + CDefaults.muxerSignalSpacing * 2f, signal.getPosition().y )
                }
                // update data output
                for( (counter, i) in (inputDataCount + inputSelectorCount  until inputDataCount + inputSelectorCount + outputDataCount).withIndex()){
                    val signal = signals[i]
                    signal.updatePosition(getCenter().x + counter * CDefaults.muxerSignalSpacing + offsetCenterX,getCenter().y - CDefaults.muxerSignalSpacing)
                    signalLineMarkers[i].updatePosition(signal.getPosition().x, signal.getPosition().y,signal.getPosition().x, signal.getPosition().y + CDefaults.muxerSignalSpacing * 2f)
                }

            }

            ROTATE_LEFT->{


            }

            ROTATE_TOP->{


            }

            ROTATE_BOTTOM->{


            }
        }
        // bottom line
        lines[0].updatePosition(getCenter().x, getCenter().y, getCenter().x + maxWidth,getCenter().y)
        // top line
        lines[1].updatePosition(getCenter().x,getCenter().y + selectorHeight,
            getCenter().x + maxWidth,getCenter().y + selectorHeight)
        // left line
        lines[2].updatePosition(getCenter().x ,lines[0].y1 - zoomOffset,getCenter().x , lines[1].y2  + zoomOffset)
        // right line
        lines[3].updatePosition(getCenter().x + maxWidth,lines[0].y1 - zoomOffset,
            getCenter().x + maxWidth, lines[1].y2  + zoomOffset)

        data.forEach {
            it.update()
        }
        textTitleLabel?.update()
        centerLabelBanner?.updatePosition(getPosition())
        signalSignalLabelMarker.onEach {
            it.update()
        }
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

    override fun clone(): CNode {
        return CDeMultiplexer(getPosition().x, getPosition().y,rotationDirection, title, font, connection,scene)
    }
}

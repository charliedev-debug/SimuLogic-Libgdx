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
import kotlin.math.max
import kotlin.math.pow

class CMultiplexer(x:Float, y:Float,rotationDirection:Int, private val title:String, private val font: BitmapFont, private val connection: Connection, private val scene: PlayGroundScene) :CNode(){

    private val lines = mutableListOf<CLine>()
    private val signalLineMarkers = mutableListOf<CLine>()
    private val signalSignalLabelMarker = mutableListOf<ListNode>()
    private var inputDataCount = 16
    private var inputSelectorCount = 2
    private var outputDataCount = 2
    private var textTitleLabel: ListNode?= null
    private var centerLabelBanner : CSprite? = null
    private var centerLabelTexture = "2-1"
    constructor(x:Float, y:Float, title:String,font: BitmapFont,connection: Connection, scene: PlayGroundScene):this(x, y, ROTATE_RIGHT, title, font,connection, scene)
    init {
        val textureAtlas = scene.assetManager.get("${EnvironmentTheme.name}.atlas", TextureAtlas::class.java)
        val spriteRegion = textureAtlas.findRegion("TRANSPARENT")
        val lineWidth = 2f
        type = CTypes.MULTIPLEXER_2_1
        when(title){
            "MUXER 16-1"->{
                inputDataCount = 16
                inputSelectorCount = 4
                outputDataCount = 1
                centerLabelTexture = "16-1"
                type = CTypes.MULTIPLEXER_16_1
            }

            "MUXER 8-1"->{
                inputDataCount = 8
                inputSelectorCount = 3
                outputDataCount = 1
                centerLabelTexture = "8-1"
                type = CTypes.MULTIPLEXER_8_1
            }

            "MUXER 4-1"->{
                inputDataCount = 4
                inputSelectorCount = 2
                outputDataCount = 1
                centerLabelTexture = "4-1"
                type = CTypes.MULTIPLEXER_4_1
            }

            "MUXER 2-1"->{
                inputDataCount = 2
                inputSelectorCount = 1
                outputDataCount = 1
                centerLabelTexture = "2-1"
                type = CTypes.MULTIPLEXER_2_1
            }
        }
        var maxWidth = 0f
        var maxHeight = 0f
        this.rotationDirection = rotationDirection
        when(rotationDirection){
            ROTATE_RIGHT->{
                maxWidth = inputDataCount * CDefaults.muxerSignalSpacing
                maxHeight = inputSelectorCount * CDefaults.muxerSignalSpacing
            }
            ROTATE_LEFT->{
                maxWidth = inputDataCount * CDefaults.muxerSignalSpacing
                maxHeight = inputSelectorCount * CDefaults.muxerSignalSpacing
            }
            ROTATE_TOP->{
                maxWidth = inputSelectorCount * CDefaults.muxerSignalSpacing
                maxHeight =inputDataCount * CDefaults.muxerSignalSpacing
            }
            ROTATE_BOTTOM->{
                maxWidth = inputSelectorCount * CDefaults.muxerSignalSpacing
                maxHeight =inputDataCount * CDefaults.muxerSignalSpacing
            }
        }
        // change the rotation direction for later processing but don't transform the sprite
        this.enableRotation = false
        sprite = Sprite(spriteRegion).apply {
            setOrigin(x , y)
            setSize(maxWidth, maxHeight)
            setOriginCenter()
            if(enableRotation) {
                when (rotationDirection) {
                    ROTATE_BOTTOM -> {
                        rotation = 270f
                    }

                    ROTATE_TOP -> {
                        rotation = 90f
                    }

                    ROTATE_LEFT -> {
                        rotation = 180f
                    }

                    ROTATE_RIGHT -> {
                        rotation = 0f
                    }
                }
            }
            setPosition(x - maxWidth / 2f,y - maxHeight / 2f)
        }


        val offsetLeft = (sprite.width - sprite.width * 0.5f / inputDataCount ) / inputDataCount
        val offsetTop = (sprite.height - sprite.height / inputSelectorCount) / inputSelectorCount
        val offsetCenterX = sprite.width * 0.5f / outputDataCount
        // create input data
        for( i in 0 until inputDataCount){
            CSignal(getCenter().x + i * sprite.width / inputDataCount + offsetLeft / 2f,
                getCenter().y + maxHeight + CDefaults.muxerSignalSpacing, CTypes.SIGNAL_IN,i,scene).also { signal ->
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
                CTypes.SIGNAL_IN,inputDataCount + i,scene).also { signal->
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
                getCenter().y - CDefaults.muxerSignalSpacing, CTypes.SIGNAL_OUT,inputDataCount + inputSelectorCount + i,scene).also { signal ->
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
            centerLabelBanner = CSprite(x, y, CDefaults.muxerSignalSpacing * 0.9f, 50f,centerLabelTexture, EnvironmentTheme.colorOnBackground,scene).also {
                layer.attachChild(it)
            }
        }

        scene.getLayerById(LayerEnums.CONNECTION_LAYER.name).also { layer->
            // bottom line
            lines.add(CLine(getCenter().x, getCenter().y, getCenter().x + maxWidth,getCenter().y,lineWidth * 2f))
            // top line
            lines.add(CLine(getCenter().x,getCenter().y + maxHeight,getCenter().x + maxWidth,getCenter().y + maxHeight,lineWidth * 2f))
            // left line
            lines.add(CLine(getCenter().x ,getCenter().y,getCenter().x , getCenter().y + maxHeight,lineWidth * 2f))
            // right line
            lines.add(CLine(getCenter().x + maxWidth,getCenter().y,getCenter().x + maxWidth, getCenter().y + maxHeight,lineWidth * 2f))

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
        ListNode(CLabel(font, 50f, "MUXER", x, y, scene).also {
            it.color = EnvironmentTheme.colorOnBackground
            it.anchor = CAnchor(this, CAnchor.ALIGN_TOP_LEFT)
        }).also { node ->
            node.value.collidable = false
            textTitleLabel = node
        }
    }


    infix fun Int.pow(exponent: Int): Int {
        return this.toDouble().pow(exponent.toDouble()).toInt()
    }

    private fun getActiveIndex():Int{
        var activeIndex = 0
        for((counter,i) in (inputDataCount until inputDataCount + inputSelectorCount).withIndex() ){
            signals[i].also{value->
                activeIndex +=  2.pow(counter) * value.value

            }
        }
        return activeIndex
    }

    override fun execute() {
        val output = signals[inputDataCount + inputSelectorCount]
        getActiveIndex().also { activeIndex->
            output.value = signals[activeIndex].value
        }
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
            updateColor(CDefaults.LED_INACTIVE_COLOR)
        }
        var maxWidth = 0f
        var maxHeight = 0f
        val zoomOffset = lines[0].zoomFactor * lines[0].lineWidth / 2f
        when(rotationDirection){
            ROTATE_RIGHT->{
                maxWidth = inputDataCount * CDefaults.muxerSignalSpacing
                maxHeight = inputSelectorCount * CDefaults.muxerSignalSpacing
                resizeLayout(maxWidth,maxHeight)
                // update input data input
                arrangeIOTop(signalLineMarkers, 0, inputDataCount)
                // update input selectors
                arrangeIOLeft(signalLineMarkers, inputDataCount, inputDataCount + inputSelectorCount)
                // update data output
                arrangeIOBottom(signalLineMarkers,inputDataCount + inputSelectorCount,inputDataCount + inputSelectorCount + outputDataCount)
            }

            ROTATE_LEFT->{
                maxWidth = inputDataCount * CDefaults.muxerSignalSpacing
                maxHeight = inputSelectorCount * CDefaults.muxerSignalSpacing
                resizeLayout(maxWidth,maxHeight)
                // update input data input
                arrangeIOBottom(signalLineMarkers, 0, inputDataCount)
                // update input selectors
                arrangeIORight(signalLineMarkers, inputDataCount, inputDataCount + inputSelectorCount)
                // update data output
                arrangeIOTop(signalLineMarkers,inputDataCount + inputSelectorCount,inputDataCount + inputSelectorCount + outputDataCount)
            }

            ROTATE_TOP->{
                maxWidth = inputSelectorCount * CDefaults.muxerSignalSpacing
                maxHeight =inputDataCount * CDefaults.muxerSignalSpacing
                resizeLayout(maxWidth,maxHeight)
                // update input data input
                arrangeIORight(signalLineMarkers, 0, inputDataCount)
                // update input selectors
                arrangeIOTop(signalLineMarkers, inputDataCount, inputDataCount + inputSelectorCount)
                // update data output
                arrangeIOLeft(signalLineMarkers,inputDataCount + inputSelectorCount,inputDataCount + inputSelectorCount + outputDataCount)

            }

            ROTATE_BOTTOM->{
                maxWidth = inputSelectorCount * CDefaults.muxerSignalSpacing
                maxHeight =inputDataCount * CDefaults.muxerSignalSpacing
                resizeLayout(maxWidth,maxHeight)
                // update input data input
                arrangeIOLeft(signalLineMarkers, 0, inputDataCount)
                // update input selectors
                arrangeIOBottom(signalLineMarkers, inputDataCount, inputDataCount + inputSelectorCount)
                // update data output
                arrangeIORight(signalLineMarkers,inputDataCount + inputSelectorCount,inputDataCount + inputSelectorCount + outputDataCount)


            }
        }
        // bottom line
        lines[0].updatePosition(getCenter().x, getCenter().y, getCenter().x + maxWidth,getCenter().y)
        // top line
        lines[1].updatePosition(getCenter().x,getCenter().y + maxHeight,
            getCenter().x + maxWidth,getCenter().y + maxHeight)
        // left line
        lines[2].updatePosition(getCenter().x ,lines[0].y1 - zoomOffset,getCenter().x , lines[1].y2  + zoomOffset)
        // right line
        lines[3].updatePosition(getCenter().x + maxWidth,lines[0].y1 - zoomOffset,
            getCenter().x + maxWidth, lines[1].y2  + zoomOffset)

        data.forEach {
            it.update()
        }
        centerLabelBanner?.updatePosition(getPosition())
        textTitleLabel?.update()
        signalSignalLabelMarker.onEach {
            it.update()
        }
    }

    fun arrangeIOTop(dataMarkers: MutableList<CLine>,start:Int, end:Int){
        val range = end - start
        val offsetLeft = (sprite.width / range) / 2f
        // update input data input
        for( (counter, i) in (start until end).withIndex()){
            val signal = signals[i]
            signal.updatePosition(getCenter().x + counter * CDefaults.muxerSignalSpacing + offsetLeft,getCenter().y + sprite.height  + CDefaults.muxerSignalSpacing)
            dataMarkers[i].updatePosition(signal.getPosition().x, signal.getPosition().y,signal.getPosition().x, signal.getPosition().y - CDefaults.muxerSignalSpacing * 2f)
        }
    }

    fun arrangeIOLeft(dataMarkers: MutableList<CLine>,start:Int, end:Int){
        val range = end - start
        val offsetTop =  (sprite.height / range ) / 2f
        for((counter, i) in (start until end).withIndex()){
            val signal = signals[i]
            signal.updatePosition(getCenter().x  - CDefaults.muxerSignalSpacing,getCenter().y + counter * CDefaults.muxerSignalSpacing + offsetTop )
            dataMarkers[i].updatePosition(signal.getPosition().x, signal.getPosition().y,signal.getPosition().x + CDefaults.muxerSignalSpacing * 2f, signal.getPosition().y )
        }
    }

    fun arrangeIORight(dataMarkers: MutableList<CLine>,start:Int, end:Int){
        val range = end - start
        val offsetTop =  (sprite.height / range ) / 2f
        for((counter, i) in (start until end).withIndex()){
            val signal = signals[i]
            signal.updatePosition(getCenter().x + sprite.width + CDefaults.muxerSignalSpacing,getCenter().y + counter * CDefaults.muxerSignalSpacing + offsetTop)
            dataMarkers[i].updatePosition(signal.getPosition().x, signal.getPosition().y,signal.getPosition().x - CDefaults.muxerSignalSpacing * 2f, signal.getPosition().y )
        }
    }

    fun arrangeIOBottom(dataMarkers: MutableList<CLine>,start:Int, end:Int){
        val range = end - start
        val offsetCenterX = (sprite.width / range) / 2f
        for( (counter, i) in (start  until end).withIndex()){
            val signal = signals[i]
            signal.updatePosition(getCenter().x + counter * CDefaults.muxerSignalSpacing + offsetCenterX,getCenter().y - CDefaults.muxerSignalSpacing)
            dataMarkers[i].updatePosition(signal.getPosition().x, signal.getPosition().y,signal.getPosition().x, signal.getPosition().y + CDefaults.muxerSignalSpacing * 2f)
        }
    }

    fun resizeLayout(layoutWidth: Float, layoutHeight: Float){
        setSize(layoutWidth,layoutHeight)
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
        return CMultiplexer(getPosition().x, getPosition().y,rotationDirection, title, font, connection,scene)
    }
}

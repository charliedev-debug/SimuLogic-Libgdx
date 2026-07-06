package org.engine.simulogic.android.circuits.components.lines

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import org.engine.simulogic.android.circuits.algorithms.WirePathRouter
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.CDefaults.Companion.LINE_MARKER_ACTIVE
import org.engine.simulogic.android.circuits.components.CDefaults.Companion.LINE_MARKER_INACTIVE
import org.engine.simulogic.android.circuits.components.CDefaults.Companion.SIGNAL_ACTIVE_COLOR
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.CTypes
import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.interfaces.ICollidable
import org.engine.simulogic.android.circuits.components.interfaces.IUpdate
import org.engine.simulogic.android.circuits.components.other.CRangeLine
import org.engine.simulogic.android.circuits.components.other.CRect
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.circuits.theme.EnvironmentTheme
import org.engine.simulogic.android.scene.Entity
import org.engine.simulogic.android.scene.LayerEnums
import org.engine.simulogic.android.scene.PlayGroundScene
import kotlin.math.abs
import kotlin.math.sign

class LineMarker(
    val scene: PlayGroundScene,
    val from: ListNode,
    val to: ListNode,
    var signalFrom: Int,
    val signalTo: Int,
    var index: Int = 0,
    val linePointCountX: Int = CDefaults.linePointCountX,
    val linePointCountY: Int = CDefaults.linePointCountY
) : Entity(), ICollidable,
    IUpdate {
    private val lines = mutableListOf<CLine>()
    private val lineRect = mutableListOf<CRect>()
    var markerActive = false
    var hasChildren = false
    companion object{
        const val FROM_SIGNAL = 0
        const val FROM_COMPONENT = 1
        fun getNodeOriginFromStatic(node: ListNode): ListNode? {
            if (node.value !is CSignal) {
                return node
            } else {
                var parent = node.value as CSignal
                while (parent.parent != null && parent.parent is LineMarker) {
                    val origin = (parent.parent as LineMarker)
                    if (origin.from.value is CSignal) {
                        parent = origin.from.value as CSignal
                    } else {
                        return origin.from
                    }
                }
                return null
            }
        }
    }
    /*Connections can be made in 2 ways:-
    * 1. Through components with inputs and outputs.
    * 2. Through already established connection nodes. Acting like an extension.
    * */
    fun initialize(scene: PlayGroundScene) {

        val signalFrom = if(isSourceSignal()) from.value else from.value.signals[signalFrom]
        val signalTo = if(isDestinationSignal()) to.value else to.value.signals[signalTo]
        val pFrom = signalFrom.getPosition()
        val pTo = signalTo.getPosition()
        val distanceX = pTo.x - pFrom.x
        val distanceY = pTo.y - pFrom.y
        val maxDistanceBetweenX = distanceX / (linePointCountX + 1)
        val maxDistanceBetweenY = distanceY / (linePointCountY + 1)
        var signalIndex = 0
        val fromSignY = sign(pTo.y - pFrom.y)

        signals.add(
            CSignal(
                pFrom.x,
                pFrom.y,
                CTypes.SIGNAL_RANGE_POINT,
                signalIndex++,
                scene
            ).apply {
                parent = this@LineMarker
            })

        for (i in 1..linePointCountX) {
            val x = pFrom.x + maxDistanceBetweenX * (i - 1)
            val y = pFrom.y
            signals.add(
                CSignal(
                    x,
                    y + from.value.getHeight() * fromSignY,
                    CTypes.SIGNAL_RANGE_POINT,
                    signalIndex++,
                    scene
                ).apply {
                    parent = this@LineMarker
                })
        }

        for (i in 0..linePointCountY) {
            val y = pFrom.y + maxDistanceBetweenY * i
            val x = pTo.x
            signals.add(
                CSignal(
                    x,
                    y + from.value.getHeight() * fromSignY,
                    CTypes.SIGNAL_RANGE_POINT,
                    signalIndex++,
                    scene
                ).apply {
                    parent = this@LineMarker
                })
        }

        createMarker(scene)
    }

    fun initialize(scene: PlayGroundScene, connection: Connection) {
        val signalFrom = if(isSourceSignal()) from.value else from.value.signals[signalFrom]
        val signalTo = if(isDestinationSignal()) to.value else to.value.signals[signalTo]
        val wirePathRouter = WirePathRouter(connection, scene)
        wirePathRouter.route(signalFrom, signalTo).also { pathQueue ->
            var countPoint = linePointCountX + linePointCountY
            val startNode = pathQueue.removeFirst()
            val endNode = pathQueue.removeLast()
            var signalIndex = 0
            val directionX = sign(startNode.x - endNode.x)
            val directionY = sign(startNode.y - endNode.y)
            signals.add(
                CSignal(
                    startNode.x,
                    startNode.y,
                    CTypes.SIGNAL_RANGE_POINT,
                    signalIndex,
                    scene
                ).apply {
                    parent = this@LineMarker
                })

            signalIndex++
            while (countPoint > 0) {
                if (pathQueue.size == 1) {
                    val node = pathQueue[0]
                     signals.add(
                        CSignal(
                            node.x,
                            node.y,
                            CTypes.SIGNAL_RANGE_POINT,
                            signalIndex,
                            scene
                        ).apply {
                            parent = this@LineMarker
                        })
                } else {
                    val node = pathQueue.removeFirst()
                    val x =  node.x
                    val y =  node.y

                    signals.add(
                        CSignal(
                            x,
                            y,
                            CTypes.SIGNAL_RANGE_POINT,
                            signalIndex,
                            scene
                        ).apply {
                            parent = this@LineMarker
                        })
                }
                signalIndex++
                countPoint--
            }

            signals.add(
                CSignal(
                    endNode.x ,
                    endNode.y ,
                    CTypes.SIGNAL_RANGE_POINT,
                    signalIndex,
                    scene
                ).apply {
                    parent = this@LineMarker
                })

            /* enables user to access the wire nodes easily without moving the components they are connected to
            * this is because sometimes the nodes overlaps with the components. So we make sure the nodes are not
            * overlapping the components by moving in by a small offset just enough for the user to move the nodes independently*/
            for(i in 1 until signals.size - 2){
                val current = signals[i]
                val next = signals[i + 1]
                val prev = signals[i - 1]
                val offsetX = 60f
                val offsetY = 60f
                if(current.getPosition().x == next.getPosition().x &&
                    current.getPosition().y == next.getPosition().y &&
                    endNode.x == current.getPosition().x && endNode.y == current.getPosition().y){
                    val directionX = sign(prev.getPosition().x - current.getPosition().x)
                    val directionY = sign(prev.getPosition().y - current.getPosition().y)
                    val x = current.getPosition().x + offsetX * directionX
                    val y = current.getPosition().y + offsetY * directionY
                    current.updatePosition(x, y)
                }
            }

        }


        //initialize(scene)
        createMarker(scene)
    }

    fun getNodeOriginFrom(node: ListNode): LineMarker {
        if (node.value !is CSignal) {
            return this
        } else {
            var parent = node.value as CSignal
            var origin = this
            while (parent.parent != null && parent.parent is LineMarker) {
                origin = (parent.parent as LineMarker)
                if (origin.from.value is CSignal) {
                    parent = origin.from.value as CSignal
                } else {
                    break
                }
            }
            return origin
        }
    }

    fun getNodeOriginDepth():Int{
        val node = from
        var depth = 0
        if (node.value !is CSignal) {
            return depth
        } else {
            var parent = node.value as CSignal
            var origin: Entity
            while (parent.parent != null && parent.parent is LineMarker) {
                origin = (parent.parent as LineMarker)
                if (origin.from.value is CSignal) {
                    parent = origin.from.value as CSignal
                } else {
                    break
                }
                depth++
            }
            return depth
        }
    }

    override fun detachSelf() {
        super.detachSelf()
        lines.forEach {
            it.detachSelf()
        }
        signals.forEach {
            it.detachSelf()
        }
        lineRect.forEach {
            it.detachSelf()
        }
    }

    // removes marker for the parent node
    fun removeSelf() {
        detachSelf()
        getNodeOriginFrom(from).from.removeMarker(this)
    }

    override fun attachSelf() {
        super.attachSelf()
        scene.getLayerById(LayerEnums.CONNECTION_LAYER.name).also { layer ->
            lines.forEach {
                it.isRemoved = false
                layer.attachChild(it)
            }
        }

        lineRect.onEach {
            it.isVisible = false
            it.isRemoved = false
            scene.getLayerById(LayerEnums.GATE_LAYER.name).also{ layer ->
                layer.attachChild(it)
            }
        }

        scene.getLayerById(LayerEnums.CONNECTION_LAYER_INPUTS.name).also { layer ->
            signals.forEach {
                it.isRemoved = false
                layer.attachChild(it)
            }
        }

        getNodeOriginFrom(from).from.insertChildUnmarked(to, this)
    }

    private fun createMarker(scene: PlayGroundScene) {
        scene.getLayerById(LayerEnums.CONNECTION_LAYER.name).also { layer ->
            for (i in 0 until signals.size - 1) {
                val prev = signals[i].getPosition()
                val next = signals[i + 1].getPosition()
                lines.add(
                    CLine(
                        prev.x,
                        prev.y,
                        next.x,
                        next.y,
                        CDefaults.lineWeight
                    ).also { line ->
                        layer.attachChild(line)
                    })
            }
        }
        val colorRect = Color(EnvironmentTheme.colorPrimary).apply {
            a = 0.5f
        }
        for (i in 1 until signals.size - 2){
            lineRect.add(CRangeLine(signals[i], signals[i + 1],colorRect, scene))
        }

        lineRect.onEach {
            it.isVisible = false
            scene.getLayerById(LayerEnums.GATE_LAYER.name).also{ layer ->
                layer.attachChild(it)
            }
        }
        scene.getLayerById(LayerEnums.CONNECTION_LAYER_INPUTS.name).also { layer ->
            // to prevent collisions during touch don't attach the first and the last point
            for (i in 1 until signals.size - 1) {
                layer.attachChild(signals[i])
            }
        }

    }

    private fun snapAlignOriginPoints() {
        // snap align start and end points
        val startFrom = signals[0]
        val startSnapFrom = signals[1]
        val endTo = signals[signals.size - 1]
        val endSnapTo = signals[signals.size - 2]
        val offsetFromX = abs(startFrom.getPosition().x - startSnapFrom.getPosition().x)
        val offsetFromY = abs(startFrom.getPosition().y - startSnapFrom.getPosition().y)
        val offsetToX = abs(endTo.getPosition().x - endSnapTo.getPosition().x)
        val offsetToY = abs(endTo.getPosition().y - endSnapTo.getPosition().y)
        val snapFromOrigin = from.value.snapAlignOriginPoints || startSnapFrom.snapAlignOriginPoints
        val snapToOrigin = to.value.snapAlignOriginPoints || endSnapTo.snapAlignOriginPoints

        if (snapFromOrigin) {
            if (offsetFromX <= CDefaults.GRID_WIDTH) {
                startSnapFrom.updatePosition(
                    startFrom.getPosition().x,
                    startSnapFrom.getPosition().y
                )
            } else
                if (offsetFromY <= CDefaults.GRID_HEIGHT) {
                    startSnapFrom.updatePosition(
                        startSnapFrom.getPosition().x,
                        startFrom.getPosition().y
                    )
                }
        }

        if (snapToOrigin) {
            if (offsetToX <= CDefaults.GRID_WIDTH) {
                endSnapTo.updatePosition(endTo.getPosition().x, endSnapTo.getPosition().y)
            } else
                if (offsetToY <= CDefaults.GRID_HEIGHT) {
                    endSnapTo.updatePosition(endSnapTo.getPosition().x, endTo.getPosition().y)
                }
        }
    }

    override fun update() {
        val signalFrom = if(isSourceSignal()) from.value else from.value.signals[signalFrom]
        val signalTo =  if (isDestinationSignal()) to.value else to.value.signals[signalTo]
        val pFrom = signalFrom.getPosition()
        val pTo = signalTo.getPosition()
        // the first and last marker come from the origin node
        if (signals.isNotEmpty()) {
            signals[0].updatePosition(pFrom.x, pFrom.y)
            signals[signals.size - 1].updatePosition(pTo.x, pTo.y)
        }
        for (i in 1 until signals.size - 1) {
            signals[i].update()
        }

        snapAlignOriginPoints()

        //snap align body
        var index = 1
        while (index < signals.size - 2) {
            val prevSignal = signals[index]
            val nextSignal = signals[index + 1]
            val prev = prevSignal.getPosition()
            val next = nextSignal.getPosition()
            val offsetX = prev.x - next.x
            val offsetY = prev.y - next.y
            val distanceFromPrevX = abs(pFrom.x - prev.x)
            val distanceFromPrevY = abs(pFrom.y - prev.y)
            val distanceToPrevX = abs(pTo.x - prev.x)
            val distanceToPrevY = abs(pTo.y - prev.y)
            val snapAlignOriginPoints =
                prevSignal.snapAlignOriginPoints || nextSignal.snapAlignOriginPoints
            // ignore the first and the last elements since we can't modify them directly since it's the source
            if (snapAlignOriginPoints && (distanceFromPrevX < distanceToPrevX || index == 0)) {
                if (abs(offsetX) <= CDefaults.GRID_WIDTH) {
                    nextSignal.updatePosition(prev.x, next.y)
                }
            } else if (snapAlignOriginPoints && (distanceFromPrevX > distanceToPrevX)) {
                if (abs(offsetX) <= CDefaults.GRID_WIDTH) {
                    prevSignal.updatePosition(next.x, prev.y)
                }
            }

            if (snapAlignOriginPoints && (distanceFromPrevY < distanceToPrevY || (index + 1) == signals.size - 1)) {
                if (abs(offsetY) <= CDefaults.GRID_HEIGHT) {
                    nextSignal.updatePosition(next.x, prev.y)
                }
            } else if (snapAlignOriginPoints && (distanceFromPrevY > distanceToPrevY)) {
                if (abs(offsetY) <= CDefaults.GRID_HEIGHT) {
                    prevSignal.updatePosition(prev.x, next.y)
                }
            }
            index++
        }

        signals.forEach {
            it.snapAlignOriginPoints = false
        }

        from.value.snapAlignOriginPoints = false
        to.value.snapAlignOriginPoints = false

        // mark, highlight lines and set coordinates
        var origin:LineMarker
        var parentMarker:LineMarker
        var parentNodeMarker: CNode
        // in case it's a nested connection we highlight it based on the source origin node
        if(isSourceSignal()){
            parentMarker = getNodeOriginFrom(from)
            origin = ((from.value as CSignal).parent as LineMarker)
            parentNodeMarker = parentMarker.from.value.signals[parentMarker.signalFrom]
        }else{
            origin = this
            parentMarker = this
            parentNodeMarker = from.value.signals[this.signalFrom]
        }
        markerActive = false
        for (i in 0 until signals.size - 1) {
            val prevSignal = signals[i]
            val nextSignal = signals[i + 1]
            val prev = prevSignal.getPosition()
            val next = nextSignal.getPosition()
            lines[i].also { line ->
                line.color =
                    if (parentNodeMarker.value == CNode.SIGNAL_ACTIVE) SIGNAL_ACTIVE_COLOR else LINE_MARKER_INACTIVE
                line.updatePosition(prev.x, prev.y, next.x, next.y)
            }
            markerActive = markerActive || nextSignal.selected || prevSignal.selected || origin.markerActive || parentMarker.markerActive
        }
        if (markerActive) {
            updateColor(LINE_MARKER_ACTIVE)
        }

        lineRect.onEach {
            it.update()
        }

    }

    fun isSourceSignal(): Boolean{
        return from.value is CSignal
    }
    fun isDestinationSignal():Boolean{
        return to.value is CSignal
    }
    override fun updateColor(color: Color) {
        lines.forEach {
            it.color = color
        }
    }

    override fun contains(x: Float, y: Float): CNode? {
        return null
    }

    override fun contains(entity: CNode): CNode? {

        for (i in 1 until signals.size - 1) {
            val obj = signals[i].contains(entity)
            if (obj != null) {
                return obj
            }
        }
        lineRect.onEach {
            it.isVisible = false
        }
        for(i in 0 until lineRect.size){
            val obj = lineRect[i].contains(entity)
            if(obj != null){
                obj.isVisible = true
                return obj
            }
        }
        return null
    }

    override fun contains(rect: Rectangle): CNode? {

        for (i in 1 until signals.size - 1) {
            val obj = signals[i].contains(rect)
            if (obj != null) {
                return obj
            }
        }
        lineRect.onEach {
            it.isVisible = false
        }
        for(i in 0 until lineRect.size){
            val obj = lineRect[i].contains(rect)
            if(obj != null){
                obj.isVisible = true
                return obj
            }
        }
        return null
    }

    fun clone(
        from: ListNode,
        to: ListNode,
        signalFrom: Int,
        signalTo: Int,
        scene: PlayGroundScene
    ): LineMarker {
        return LineMarker(
            scene,
            from,
            to,
            signalFrom,
            signalTo,
            index,
            linePointCountX,
            linePointCountY
        ).also { it.initialize(scene) }
    }

}

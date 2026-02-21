package org.engine.simulogic.android.circuits.components.other

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.CTypes
import org.engine.simulogic.android.circuits.components.lines.CLine
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.SnapAlign
import org.engine.simulogic.android.circuits.tools.CommandHistory
import org.engine.simulogic.android.circuits.tools.CopyTool
import org.engine.simulogic.android.circuits.tools.DataContainer
import org.engine.simulogic.android.events.CollisionDetector
import org.engine.simulogic.android.events.MotionGestureListener
import org.engine.simulogic.android.scene.Entity
import org.engine.simulogic.android.scene.LayerEnums
import org.engine.simulogic.android.scene.PlayGroundScene
import kotlin.math.abs

class CGroup(
    private val initialX: Float,
    private val initialY: Float,
    private val initialWidth: Float,
    private val initialHeight: Float,
    connection: Connection,
    private val scene: PlayGroundScene
) : CRangeSelect(initialX, initialY, connection, scene, LayerEnums.GATE_LAYER.name) {
    val dataContainer = DataContainer()
    private var previousPosition = Vector2(initialX, initialY)
    private var previousSnapPosition = Vector2()
    private val lines = mutableListOf<CLine>()
    var collidableChildren = true
    private val snapAlign = SnapAlign()
    val componentGroupIds = mutableListOf<Int>()
    var gestureListener: MotionGestureListener? = null

    init {
        type = CTypes.GROUP
        sprite.color = CDefaults.GROUP_SELECTED_COLOR
        previousPosition.set(getPosition().x, getPosition().y)

        scene.getLayerById(LayerEnums.CONNECTION_LAYER.name).also { layer ->
            for (i in 0 until 4) {
                CLine(0f, 0f, 0f, 0f, 1f).also { line ->
                    line.color = CDefaults.RANGED_LINE_COLOR
                    layer.attachChildAt(0, line)
                    lines.add(line)
                }

            }
        }

        scene.getLayerById(LayerEnums.GATE_LAYER.name).also { layer ->
            layer.attachChildAt(0, this)
        }

        isVisible = true
        enableDragMotion = true
        adjustView()
    }

    // insert range
    fun insert(inputContainer: DataContainer, range: CRangeSelect) {
        inputContainer.insertTo(dataContainer)
        dataContainer.forEach { node ->
            node.value.collidable = false
        }
        setSize(range.getWidth(), range.getHeight())
        updatePosition(range.getPosition().x, range.getPosition().y)
        adjustView()
    }

    // insert selection
    fun insert(inputContainer: DataContainer) {
        inputContainer.insertTo(dataContainer)
        dataContainer.forEach { node ->
            node.value.collidable = false
        }
        dataContainer.sortX()
        val firstX = dataContainer.first()
        val lastX = dataContainer.last()
        dataContainer.sortY()
        val firstY = dataContainer.first()
        val lastY = dataContainer.last()

        val rangeWidth =
            abs((firstX.value.getPosition().x - firstX.value.getWidth()) - (lastX.value.getPosition().x + lastX.value.getWidth()))
        val rangeHeight =
            abs((firstY.value.getPosition().y - firstY.value.getHeight()) - (lastY.value.getPosition().y + lastY.value.getHeight()))

        setSize(rangeWidth, rangeHeight)
        updatePosition(
            firstX.value.getPosition().x + (rangeWidth / 2f - firstX.value.getWidth() / 2f - lastX.value.getWidth() / 2f),
            firstY.value.getPosition().y + (rangeHeight / 2f - firstY.value.getHeight() / 2f - lastY.value.getHeight() / 2f)
        )
        adjustView()
    }

    fun loadFromIds(connection: Connection) {
        componentGroupIds.forEach { index ->
            dataContainer.insert(connection[index].also { node ->
                node.value.collidable = false
            })
        }
        previousPosition.setZero()
        setSize(initialWidth, initialHeight)
        updatePosition(initialX, initialY)
        adjustView()
    }

    override fun translate(offsetX: Float, offsetY: Float) {
        updatePosition(
            getPosition().x - previousSnapPosition.x,
            getPosition().y - previousSnapPosition.y
        )
        updateGroup(-previousSnapPosition.x, -previousSnapPosition.y)
        previousPosition.add(offsetX, offsetY)
        snapAlign.getSnapCoordinates(previousPosition.x, previousPosition.y).also { position ->
            updatePosition(getPosition().x + position.x, getPosition().y + position.y)
            previousSnapPosition.set(position)
            updateGroup(position.x, position.y)
        }
        adjustView()
    }

    private fun updateGroup(offsetX: Float, offsetY: Float) {
        if (dataContainer.isNotEmpty()) {
            for (i in 0 until dataContainer.size()) {
                val p = dataContainer[i].value
                val ix = p.getPosition().x + offsetX
                val iy = p.getPosition().y + offsetY
                // if it's a group translate it indirectly to apply effect to children
                if (p is CGroup) {
                    /* reset buffers to prevent unexpected glitches,
                    we don't need to remember the last position the parent does it for us
                     */
                    p.resetPositionBuffers()
                    p.translate(offsetX, offsetY)
                    p.resetPositionBuffers()
                } else {
                    p.updatePosition(ix, iy)
                }
            }
            dataContainer.forEach { data ->
                data.getLineMarkerChildren().forEach { marker ->
                    marker.signals.forEach { point ->
                        val nx = point.getPosition().x + offsetX
                        val ny = point.getPosition().y + offsetY
                        point.updatePosition(nx, ny)
                    }
                    marker.update()
                }
            }
            previousPosition.set(0f, 0f)
        }
    }

    override fun detachSelf() {
        super.detachSelf()
        dataContainer.forEach { node ->
            node.value.collidable = true
        }
        lines.forEach {
            it.isRemoved = true
            it.detachSelf()
        }
    }

    override fun attachSelf() {
        super.attachSelf()
        signals.forEach {
            it.isRemoved = false
            attachChild(it)
        }
        scene.getLayerById(layerId).also { layer ->
            layer.attachChild(this)
        }
        dataContainer.forEach { node ->
            node.value.collidable = false
        }
        scene.getLayerById(LayerEnums.CONNECTION_LAYER.name).also { layer ->
            lines.forEach {
                it.isRemoved = false
                layer.attachChild(it)
            }
        }
    }

    fun resetPositionBuffers() {
        previousSnapPosition.setZero()
        previousPosition.setZero()
    }

    override fun update() {
        val signalTopLeft = signals[0] as CRangePoint
        val signalTopRight = signals[1] as CRangePoint
        val signalBottomLeft = signals[2] as CRangePoint
        val signalBottomRight = signals[3] as CRangePoint
        val signalWidth = signalTopLeft.getWidth()
        val signalHeight = signalTopLeft.getHeight()
        var updated = false
        signals.forEach {
            gestureListener?.collisionDetector?.also { collisionDetector ->
                it.isVisible = collisionDetector.mode != MotionGestureListener.INTERACT_MODE
            }
            (it as CRangePoint).apply {
                updated = isUpdated || updated
                if (isUpdated) {
                    childX?.also { child ->
                        child.updatePosition(child.getPosition().x, getPosition().y)
                        child.isUpdated = false
                    }
                    childY?.also { child ->
                        child.updatePosition(getPosition().x, child.getPosition().y)
                        child.isUpdated = false
                    }
                    isUpdated = false
                }

            }
            it.update()
        }

        lines[0].also {
            gestureListener?.collisionDetector?.also { collisionDetector ->
                it.isVisible = collisionDetector.mode != MotionGestureListener.INTERACT_MODE
            }
            it.updatePosition(
                signalTopLeft.getPosition().x, signalTopLeft.getPosition().y,
                signalTopRight.getPosition().x, signalTopRight.getPosition().y
            )
        }
        lines[1].also {
            gestureListener?.collisionDetector?.also { collisionDetector ->
                it.isVisible = collisionDetector.mode != MotionGestureListener.INTERACT_MODE
            }
            it.updatePosition(
                signalTopLeft.getPosition().x, signalTopLeft.getPosition().y,
                signalBottomLeft.getPosition().x, signalBottomLeft.getPosition().y
            )
        }
        lines[2].also {
            gestureListener?.collisionDetector?.also { collisionDetector ->
                it.isVisible = collisionDetector.mode != MotionGestureListener.INTERACT_MODE
            }
            it.updatePosition(
                signalTopRight.getPosition().x, signalTopRight.getPosition().y,
                signalBottomRight.getPosition().x, signalBottomRight.getPosition().y
            )
        }
        lines[3].also {
            gestureListener?.collisionDetector?.also { collisionDetector ->
                it.isVisible = collisionDetector.mode != MotionGestureListener.INTERACT_MODE
            }
            it.updatePosition(
                signalBottomLeft.getPosition().x, signalBottomLeft.getPosition().y,
                signalBottomRight.getPosition().x, signalBottomRight.getPosition().y
            )
        }

        if (updated) {
            // update the range background size and position
            val width = abs(signalTopLeft.getPosition().x - signalTopRight.getPosition().x)
            val height = abs(signalTopLeft.getPosition().y - signalBottomLeft.getPosition().y)
            sprite.setSize(width, height)
            updatePosition(
                signalTopLeft.getPosition().x + width / 2f,
                signalTopLeft.getPosition().y - height / 2f
            )
        }
        updateColor(if (selected) CDefaults.GROUP_SELECTED_COLOR else CDefaults.GROUP_UNSELECTED_COLOR)
    }

    override fun contains(entity: CNode): CNode? {
        if (collidableChildren) {
            dataContainer.forEach {
                it.value.collidable = true
                val childCollides = it.contains(entity)
                it.value.collidable = false
                if (childCollides != null) {
                    return childCollides
                }
            }
        }
        val parentCollides = super.contains(entity)
        if (parentCollides != null) {
            return parentCollides
        }

        return null
    }

    override fun contains(rect: Rectangle): CNode? {
        if (collidableChildren) {
            dataContainer.forEach {
                it.value.collidable = true
                val childCollides = it.contains(rect)
                it.value.collidable = false
                if (childCollides != null) {
                    return childCollides
                }
            }
        }
        val parentCollides = super.contains(rect)
        if (parentCollides != null) {
            return parentCollides
        }

        return null
    }


    override fun clone(): CGroup {
        val position = getPosition()
        return CGroup(
            position.x,
            position.y,
            getWidth(),
            getHeight(),
            connection,
            scene
        ).also { clone -> clone.gestureListener = gestureListener }
    }

}

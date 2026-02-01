package org.engine.simulogic.android.events

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import org.engine.simulogic.android.SimulationLoop
import org.engine.simulogic.android.circuits.components.decorators.GridDecorator
import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.interfaces.IUpdate
import org.engine.simulogic.android.circuits.components.lines.LineMarker
import org.engine.simulogic.android.circuits.components.other.CPointer
import org.engine.simulogic.android.circuits.components.other.CRangeSelect
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.circuits.tools.CommandHistory
import org.engine.simulogic.android.circuits.tools.CopyTool
import org.engine.simulogic.android.circuits.tools.CutTool
import org.engine.simulogic.android.circuits.tools.DataContainer
import org.engine.simulogic.android.circuits.tools.DeleteLineTool
import org.engine.simulogic.android.circuits.tools.MoveCommand
import org.engine.simulogic.android.scene.PlayGroundScene


class MotionGestureListener(private val camera:OrthographicCamera, private  val connection: Connection, private val collisionDetector: CollisionDetector,private val scene: PlayGroundScene): GestureDetector.GestureListener, IUpdate{

    private var initialZoom = 1f
    private val rangeSelect = CRangeSelect(camera.position.x, camera.position.y,Connection(),scene)
    val rectPointer = CPointer(SimulationLoop.CAMERA_WIDTH / 2f,SimulationLoop.CAMERA_HEIGHT / 2f,scene)
    private var touch = Vector3(0f, 0f, 0f)
    private val commandHistory = CommandHistory()
    private val dataContainer = DataContainer()
    private val cutTool = CutTool(dataContainer, commandHistory)
    private val copyTool = CopyTool(dataContainer, connection, commandHistory)
    private val deleteLineTool = DeleteLineTool(dataContainer, connection, scene, commandHistory)
    private val modeBuffer = mutableListOf<Int>()
    var gridDecorator:GridDecorator? = null
    companion object {
         const val MIN_ZOOM_FACTOR = 0.6f
         const val MAX_ZOOM_FACTOR = 4.5f
        //move objects around and perform single element selection
        const val TOUCH_MODE = 1

        // no motion just interaction
        const val INTERACT_MODE = 2

        // multi-select and single element selection
        const val SELECTION_MODE = 3

        // wiring mode
        const val CONNECTION_MODE = 4

        //select a range of items
        const val RANGED_SELECTION_MODE = 5
    }

    fun setMode(mode:Int){
        collisionDetector.mode = mode
        collisionDetector.reset()
        if(mode == RANGED_SELECTION_MODE){
            rangeSelect.updatePosition(rectPointer.getPosition())
            rangeSelect.adjustView()
            rangeSelect.isVisible = true
        }else{
            rangeSelect.isVisible = false
        }
    }

    fun origin(){
        camera.position.set(SimulationLoop.CAMERA_WIDTH / 2f, SimulationLoop.CAMERA_HEIGHT / 2f, 0f)
        camera.update()
        gridDecorator?.refresh = true
    }

    fun undo(){
        commandHistory.undo()
    }

    fun redo(){
        commandHistory.redo()
    }

    fun cut(){
        dataContainer.mode = DataContainer.CUT
        if(collisionDetector.mode == RANGED_SELECTION_MODE){
            sendRangeItemsToDataContainer()
        }else {
            collisionDetector.selectedItems.forEach { item ->
                dataContainer.insert(item.caller)
            }
        }
    }

    fun copy(){
        dataContainer.mode = DataContainer.COPY
        if(collisionDetector.mode == RANGED_SELECTION_MODE){
            sendRangeItemsToDataContainer()
        }else {
            collisionDetector.selectedItems.forEach { item ->
                dataContainer.insert(item.caller)
            }
        }
    }

    fun delete(){
        if(collisionDetector.mode == RANGED_SELECTION_MODE){
            sendRangeItemsToDataContainer()
        }else {
            collisionDetector.selectedItems.forEach { item ->
                // it must be a line
                if(item.subject is CSignal && item.subject.parent is LineMarker){
                    dataContainer.insert(ListNode(item.subject))
                }
                // deletes the whole component
                else {
                    dataContainer.insert(item.caller)
                }
            }
        }
        //deleteTool.execute()
        deleteLineTool.execute()
        collisionDetector.reset()
    }

    fun paste(){
        if(dataContainer.mode == DataContainer.CUT) {
            cutTool.execute(rectPointer.getPosition().x, rectPointer.getPosition().y)
        }else if(dataContainer.mode == DataContainer.COPY){
            copyTool.execute(rectPointer.getPosition().x, rectPointer.getPosition().y,scene)
        }
        dataContainer.clear()
        collisionDetector.reset()
      //  println("Data Size: ${connection.size()}")
    }


    override fun update() {
        rangeSelect.connection.update()
    }

    private fun sendRangeItemsToDataContainer(){
        rangeSelect.rangeItems.forEach {
            dataContainer.insert(it.caller)
        }
    }

    override fun pinch(
        initialPointer1: Vector2,
        initialPointer2: Vector2,
        pointer1: Vector2,
        pointer2: Vector2
    ): Boolean {
         val initialDistance = initialPointer1.dst(initialPointer2)
         val currentDistance = pointer1.dst(pointer2)
         val ratio = initialDistance / currentDistance
            camera.zoom  = initialZoom * ratio
            camera.zoom = MathUtils.clamp(camera.zoom, MIN_ZOOM_FACTOR, MAX_ZOOM_FACTOR)
        return true
    }

    override fun touchDown(x: Float, y: Float, pointer: Int, button: Int): Boolean {
        initialZoom = camera.zoom
        touch.set(x,y, 0f)
        camera.unproject(touch)
        rectPointer.updatePosition(touch.x, touch.y)
        if(collisionDetector.mode == INTERACT_MODE){
            collisionDetector.contains(rectPointer)?.also { collisionItem ->
                collisionItem.subject.toggleAction()
            }
        }else {
            collisionDetector.contains(rectPointer)?.also { collisionItem ->
                collisionItem.subject.selected = collisionItem.subject.selected.not()
            }

            if (collisionDetector.mode == RANGED_SELECTION_MODE) {
                rangeSelect.collisionDetector.contains(rectPointer)
            }
        }

        return false
    }

    override fun pinchStop() {
       initialZoom = camera.zoom
    }

    override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean {
        touch.set(x,y, 0f)
        camera.unproject(touch)
        rectPointer.updatePosition(touch.x, touch.y)
        if(collisionDetector.mode == TOUCH_MODE) {
            if (collisionDetector.isNotEmpty()) {
                collisionDetector.selectedItems.forEach {
                    it.subject.also { subject ->
                        commandHistory.execute(MoveCommand().apply {
                            node = ListNode(subject)
                            newPosition.set(touch.x, touch.y)
                            oldPosition.set(subject.getPosition())
                        })
                        subject.updatePosition(touch.x, touch.y)
                    }
                }
            } else {
                camera.position.add(-deltaX * camera.zoom, deltaY * camera.zoom, 0f)
            }
        }else if(collisionDetector.mode == RANGED_SELECTION_MODE){
            if (rangeSelect.collisionDetector.isNotEmpty()) {
                rangeSelect.collisionDetector.selectedItems.forEach {
                    it.subject.also { subject ->
                        commandHistory.execute(MoveCommand().apply {
                            node = ListNode(subject)
                            newPosition.set(touch.x, touch.y)
                            oldPosition.set(subject.getPosition())
                        })
                        subject.updatePosition(touch.x, touch.y)
                        rangeSelect.update()
                    }
                }
                collisionDetector.containsRanged(rangeSelect)

            } else {
                camera.position.add(-deltaX * camera.zoom, deltaY * camera.zoom, 0f)
            }
        } else{
            camera.position.add(-deltaX*camera.zoom, deltaY*camera.zoom, 0f)
        }
        return  true
    }

    override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean {
        return false
    }

    override fun longPress(x: Float, y: Float): Boolean {
        return false
    }

    override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean {
        //camera.position.add(velocityX, velocityY, 0f)
        return false
    }

    override fun panStop(x: Float, y: Float, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun zoom(initialDistance: Float, distance: Float): Boolean {
        return false
    }



}

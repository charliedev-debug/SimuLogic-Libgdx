package org.engine.simulogic.android.events

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import org.engine.simulogic.android.SimulationLoop
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.decorators.GridDecorator
import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.interfaces.IUpdate
import org.engine.simulogic.android.circuits.components.lines.LineMarker
import org.engine.simulogic.android.circuits.components.other.CGroup
import org.engine.simulogic.android.circuits.components.other.CPointer
import org.engine.simulogic.android.circuits.components.other.CRangeSelect
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ConnectionManager
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.circuits.logic.SnapAlign
import org.engine.simulogic.android.circuits.storage.AutoSave
import org.engine.simulogic.android.circuits.tools.CommandHistory
import org.engine.simulogic.android.circuits.tools.CopyTool
import org.engine.simulogic.android.circuits.tools.CutTool
import org.engine.simulogic.android.circuits.tools.DataContainer
import org.engine.simulogic.android.circuits.tools.DeleteTool
import org.engine.simulogic.android.circuits.tools.MoveCommand
import org.engine.simulogic.android.circuits.tools.RotateCommand
import org.engine.simulogic.android.scene.PlayGroundScene
import kotlin.math.round


class MotionGestureListener(val camera:OrthographicCamera, private val connection: Connection, val collisionDetector: CollisionDetector,private val scene: PlayGroundScene): GestureDetector.GestureListener, IUpdate{

    private var initialZoom = 1f
    private val rangeSelect = CRangeSelect(camera.position.x, camera.position.y,Connection(),scene).apply { this@apply.connection.insertNode(ListNode(this@apply)) }
    val rectPointer = CPointer(SimulationLoop.CAMERA_WIDTH / 2f,SimulationLoop.CAMERA_HEIGHT / 2f,scene)
    private var touch = Vector3(0f, 0f, 0f)
    private val commandHistory = CommandHistory()
    private val dataContainer = DataContainer()
    private val cutTool = CutTool(dataContainer, commandHistory)
    private val copyTool = CopyTool(dataContainer, connection, commandHistory)
    private val deleteTool = DeleteTool(dataContainer, connection, scene, commandHistory)
    private val connectionManager = ConnectionManager(connection, collisionDetector,commandHistory,scene)
    private var moveCommand = MoveCommand()
    private val snapAlign = SnapAlign()
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
            rangeSelect.collisionDetector.reset()
            rangeSelect.updatePosition(rectPointer.getPosition())
            collisionDetector.containsRanged(rangeSelect)
            rangeSelect.adjustView()
            rangeSelect.isVisible = true
        }else{
            collisionDetector.reset()
            rangeSelect.reset()
            rangeSelect.isVisible = false
        }
    }

    fun zoomValue():Float{
        return camera.zoom
    }

    fun setCameraPosition(x:Float, y:Float){
        camera.position.set(x, y,0f)
    }

    fun setCameraZoom(zoom:Float){
        camera.zoom = zoom
    }

    fun origin(){
        camera.position.set(SimulationLoop.CAMERA_WIDTH / 2f, SimulationLoop.CAMERA_HEIGHT / 2f, 0f)
        camera.update()
        gridDecorator?.refresh = true
    }

    fun rotateRight(){
        val rotateCommand = RotateCommand()
        collisionDetector.selectedItems.forEach { item->
            val pRotation = item.caller.value.rotationDirection
            item.caller.value.rotateRight()
            val nRotation = item.caller.value.rotationDirection
            RotateCommand.RotateItem(pRotation,nRotation,item.caller.value).also {item->
                rotateCommand.insert(item)
            }
        }
        commandHistory.execute(rotateCommand)
        AutoSave.dataChanged = true
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
        deleteTool.execute()
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

    fun insertGroup(){
       sendRangeItemsToDataContainer()
        if(dataContainer.isNotEmpty()) {
            connection.insertNode(
                ListNode(
                    CGroup(
                        rectPointer.getPosition().x,
                        rectPointer.getPosition().y,
                        200f,
                        200f,
                        connection,
                        scene
                    ).also { group->
                        group.insert(dataContainer,rangeSelect)
                        group.gestureListener = this
                    }
                )
            )

        }
        setMode(TOUCH_MODE)
        dataContainer.clear()
    }

    fun removeGroup(){
        collisionDetector.selectedItems.forEach { item ->
            if (item.subject is CGroup){
                item.caller.detachSelf()
                connection.removeNode(item.caller)
            }
        }
        collisionDetector.reset()
    }

    override fun update() {
       // rangeSelect.connection.update()
        connectionManager.resolveConnection()
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
                // this might be moved in the future
                snapAlign.getSnapCoordinates(touch).also { coord->
                    moveCommand.oldPosition.set(coord.x, coord.y)
                }

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
                val snapCoordinates = snapAlign.getSnapCoordinates(touch)
                collisionDetector.selectedItems.forEach {
                    it.subject.also { subject ->
                        moveCommand.apply {
                            node = ListNode(subject)
                            if(subject is CGroup){
                                snapAlign.getSnapCoordinates(touch.x - moveCommand.oldPosition.x, touch.y - moveCommand.oldPosition.y)
                                 subject.translate( snapCoordinates.x, snapCoordinates.y)
                                 newPosition.set(subject.getPosition())
                            }else if(subject is CSignal){
                                newPosition.set(snapCoordinates.x, snapCoordinates.y)
                                subject.updatePosition(snapCoordinates.x, snapCoordinates.y)
                            }else {
                                newPosition.set(snapCoordinates.x, snapCoordinates.y)
                                subject.updatePosition(snapCoordinates.x, snapCoordinates.y)
                            }
                        }

                    }
                }
            } else {
                camera.position.add(-deltaX * camera.zoom, deltaY * camera.zoom, 0f)
            }
        }else if(collisionDetector.mode == RANGED_SELECTION_MODE){
            if (rangeSelect.collisionDetector.isNotEmpty()) {
                val snapCoordinates = snapAlign.getSnapCoordinates(touch)
                rangeSelect.collisionDetector.selectedItems.forEach {
                    it.subject.also { subject ->
                        commandHistory.execute(MoveCommand().apply {
                            node = ListNode(subject)
                            newPosition.set(snapCoordinates.x, snapCoordinates.y)
                            oldPosition.set(subject.getPosition())
                        })
                        subject.updatePosition(snapCoordinates.x, snapCoordinates.y)
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
        //double tap to enable and disable groups
        collisionDetector.selectedItems.forEach {
            if(it.subject is CGroup){
                it.subject.collidableChildren = !it.subject.collidableChildren
            }
        }
        return true
    }

    override fun longPress(x: Float, y: Float): Boolean {
        return false
    }

    override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean {

        return false
    }

    override fun panStop(x: Float, y: Float, pointer: Int, button: Int): Boolean {
        if(collisionDetector.mode == TOUCH_MODE){
            commandHistory.execute(moveCommand)
            collisionDetector.selectedItems.forEach {
                it.subject.also { subject ->
                    if (subject is CGroup) {
                        subject.resetPositionBuffers()
                    }
                }
            }
            // replace the old command to facilitate a new component or point
            moveCommand = MoveCommand()
        }
        AutoSave.dataChanged = true
        return false
    }

    override fun zoom(initialDistance: Float, distance: Float): Boolean {
        return false
    }



}

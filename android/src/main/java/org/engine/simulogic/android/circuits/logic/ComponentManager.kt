package org.engine.simulogic.android.circuits.logic

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector
import com.badlogic.gdx.math.Vector2
import org.engine.simulogic.android.circuits.components.buses.CDataBus
import org.engine.simulogic.android.circuits.components.buses.CFanOutBus
import org.engine.simulogic.android.circuits.components.buttons.CPower
import org.engine.simulogic.android.circuits.components.flipflops.CDFlipFlop
import org.engine.simulogic.android.circuits.components.gates.CAnd
import org.engine.simulogic.android.circuits.components.gates.CNand
import org.engine.simulogic.android.circuits.components.gates.CNor
import org.engine.simulogic.android.circuits.components.gates.CNot
import org.engine.simulogic.android.circuits.components.gates.COr
import org.engine.simulogic.android.circuits.components.gates.CXnor
import org.engine.simulogic.android.circuits.components.gates.CXor
import org.engine.simulogic.android.circuits.components.latches.CLatch
import org.engine.simulogic.android.circuits.components.visuals.CLed
import org.engine.simulogic.android.circuits.components.generators.CClock
import org.engine.simulogic.android.circuits.components.generators.CRandom
import org.engine.simulogic.android.circuits.components.other.CLabel
import org.engine.simulogic.android.circuits.components.visuals.CBCDDisplay
import org.engine.simulogic.android.circuits.components.visuals.CSevenSegmentDisplay
import org.engine.simulogic.android.circuits.components.wireless.CChannel
import org.engine.simulogic.android.circuits.components.wireless.ChannelBuffer
import org.engine.simulogic.android.circuits.logic.events.EventAndCommand
import org.engine.simulogic.android.circuits.logic.events.EventAndThreeInputCommand
import org.engine.simulogic.android.circuits.logic.events.EventBcdDisplayCommand
import org.engine.simulogic.android.circuits.logic.events.EventBridge
import org.engine.simulogic.android.circuits.logic.events.EventChannelCommand
import org.engine.simulogic.android.circuits.logic.events.EventClockCommand
import org.engine.simulogic.android.circuits.logic.events.EventCopyCommand
import org.engine.simulogic.android.circuits.logic.events.EventCutCommand
import org.engine.simulogic.android.circuits.logic.events.EventDataBusCommand
import org.engine.simulogic.android.circuits.logic.events.EventDeleteCommand
import org.engine.simulogic.android.circuits.logic.events.EventEditTextCommand
import org.engine.simulogic.android.circuits.logic.events.EventFipFlopCommand
import org.engine.simulogic.android.circuits.logic.events.EventFullAdderCommand
import org.engine.simulogic.android.circuits.logic.events.EventHalfAdderCommand
import org.engine.simulogic.android.circuits.logic.events.EventInsertGroupCommand
import org.engine.simulogic.android.circuits.logic.events.EventJKFlipFlopCommand
import org.engine.simulogic.android.circuits.logic.events.EventLabelAnchorCommand
import org.engine.simulogic.android.circuits.logic.events.EventLabelCommand
import org.engine.simulogic.android.circuits.logic.events.EventLatchCommand
import org.engine.simulogic.android.circuits.logic.events.EventLedCommand
import org.engine.simulogic.android.circuits.logic.events.EventModeCommand
import org.engine.simulogic.android.circuits.logic.events.EventNandCommand
import org.engine.simulogic.android.circuits.logic.events.EventNandThreeInputCommand
import org.engine.simulogic.android.circuits.logic.events.EventNorCommand
import org.engine.simulogic.android.circuits.logic.events.EventNorThreeInputCommand
import org.engine.simulogic.android.circuits.logic.events.EventNotCommand
import org.engine.simulogic.android.circuits.logic.events.EventOrCommand
import org.engine.simulogic.android.circuits.logic.events.EventOrThreeInputCommand
import org.engine.simulogic.android.circuits.logic.events.EventPasteCommand
import org.engine.simulogic.android.circuits.logic.events.EventPointCommand
import org.engine.simulogic.android.circuits.logic.events.EventPowerCommand
import org.engine.simulogic.android.circuits.logic.events.EventPulseButtonCommand
import org.engine.simulogic.android.circuits.logic.events.EventRandomCommand
import org.engine.simulogic.android.circuits.logic.events.EventRedoCommand
import org.engine.simulogic.android.circuits.logic.events.EventRemoveGroupCommand
import org.engine.simulogic.android.circuits.logic.events.EventRotateCommand
import org.engine.simulogic.android.circuits.logic.events.EventSRLatchCommand
import org.engine.simulogic.android.circuits.logic.events.EventSSDisplayCommand
import org.engine.simulogic.android.circuits.logic.events.EventTFlipFlopCommand
import org.engine.simulogic.android.circuits.logic.events.EventUndoCommand
import org.engine.simulogic.android.circuits.logic.events.EventXnorCommand
import org.engine.simulogic.android.circuits.logic.events.EventXorCommand
import org.engine.simulogic.android.circuits.storage.AutoSave
import org.engine.simulogic.android.circuits.storage.DataTransferObject
import org.engine.simulogic.android.circuits.storage.ProjectOptions
import org.engine.simulogic.android.circuits.tools.CommandHistory
import org.engine.simulogic.android.circuits.tools.InsertCommand
import org.engine.simulogic.android.events.MotionGestureListener
import org.engine.simulogic.android.scene.Entity
import org.engine.simulogic.android.scene.PlayGroundScene

class ComponentManager(private val projectOptions: ProjectOptions,private val font: BitmapFont, private val connection:Connection, private  val scene: PlayGroundScene, private val gestureListener: MotionGestureListener) {

    private val snapAlign = SnapAlign()
    val eventBridge = EventBridge()
    fun loadProject(){
        when(projectOptions.mode){
            ProjectOptions.CREATE->{
                createProject()
            }
            ProjectOptions.OPEN->{
                readProject()
            }
        }
    }

    fun size():Int{
        return connection.size()
    }

    fun connectionSize():Int{
        var counter = 0
        connection.forEach {
            counter += it.getLineMarkerChildren().size
        }
        return counter
    }

    fun setMode(mode:Int){
        eventBridge.insertCommand(EventModeCommand(mode, gestureListener))
    }

    fun rotateRight(){
        eventBridge.insertCommand(EventRotateCommand(gestureListener))
    }

    fun cut(){
        eventBridge.insertCommand(EventCutCommand(gestureListener))
    }

    fun copy(){
        eventBridge.insertCommand(EventCopyCommand(gestureListener))
    }

    fun delete(){
        eventBridge.insertCommand(EventDeleteCommand(gestureListener))
    }

    fun paste(){
        eventBridge.insertCommand(EventPasteCommand(gestureListener, scene))
    }

    fun undo(){
        eventBridge.insertCommand(EventUndoCommand(gestureListener))
    }

    fun redo(){
        eventBridge.insertCommand(EventRedoCommand(gestureListener))
    }

    fun insertAND(){
       gestureListener.rectPointer.getPosition().also {position->
            snapAlign.getSnapCoordinates(position).also { coordinates->
                eventBridge.insertCommand(EventAndCommand(Vector2(coordinates),connection,gestureListener.commandHistory,scene))
            }
        }
    }

    fun insertANDThreeInput(){
        gestureListener.rectPointer.getPosition().also {position->
            snapAlign.getSnapCoordinates(position).also { coordinates->
                eventBridge.insertCommand(EventAndThreeInputCommand(Vector2(coordinates),connection,gestureListener.commandHistory,scene))
            }
        }
    }

    fun insertOrThreeInput(){
        gestureListener.rectPointer.getPosition().also {position->
            snapAlign.getSnapCoordinates(position).also { coordinates->
                eventBridge.insertCommand(EventOrThreeInputCommand(Vector2(coordinates),connection,gestureListener.commandHistory,scene))
            }
        }
    }

    fun insertNorThreeInput(){
        gestureListener.rectPointer.getPosition().also {position->
            snapAlign.getSnapCoordinates(position).also { coordinates->
                eventBridge.insertCommand(EventNorThreeInputCommand(Vector2(coordinates),connection,gestureListener.commandHistory,scene))
            }
        }
    }

    fun insertNandThreeInput(){
        gestureListener.rectPointer.getPosition().also {position->
            snapAlign.getSnapCoordinates(position).also { coordinates->
                eventBridge.insertCommand(EventNandThreeInputCommand(Vector2(coordinates),connection,gestureListener.commandHistory,scene))
            }
        }
    }

    fun insertOR(){
        gestureListener.rectPointer.getPosition().also {position->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                eventBridge.insertCommand(EventOrCommand(Vector2(coordinates),connection,gestureListener.commandHistory,scene))
            }
        }
    }

    fun insertXOR(){
        gestureListener.rectPointer.getPosition().also {position->
            snapAlign.getSnapCoordinates(position).also { coordinates->
            eventBridge.insertCommand(EventXorCommand(Vector2(coordinates),connection,gestureListener.commandHistory,scene))
        }
    }}

    fun insertNOR() {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
            eventBridge.insertCommand(EventNorCommand(Vector2(coordinates),connection,gestureListener.commandHistory,scene))
            }
        }
    }

    fun insertNOT() {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
            eventBridge.insertCommand(EventNotCommand(Vector2(coordinates),connection,gestureListener.commandHistory,scene))
            }
        }
    }
    fun insertNAND() {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
            eventBridge.insertCommand(EventNandCommand(Vector2(coordinates),connection,gestureListener.commandHistory,scene))
            }
        }
    }
    fun insertXNOR() {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
            eventBridge.insertCommand(EventXnorCommand(Vector2(coordinates),connection,gestureListener.commandHistory,scene))
            }
        }
    }
    fun insertCClock(freq:Float) {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
             eventBridge.insertCommand(EventClockCommand(Vector2(coordinates),freq, connection, gestureListener.commandHistory, scene))
            }
        }
    }
    fun insertCLatch() {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
              eventBridge.insertCommand(EventLatchCommand(Vector2(coordinates),connection, gestureListener.commandHistory, scene))
            }
        }
    }

    fun insertCSRLatch() {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                eventBridge.insertCommand(EventSRLatchCommand(Vector2(coordinates),connection, gestureListener.commandHistory, scene))
            }
        }
    }
    fun insertCDFlipFlop(){
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
               eventBridge.insertCommand(EventFipFlopCommand(Vector2(coordinates),connection, gestureListener.commandHistory, scene))
            }
        }
    }
    fun insertCTFlipFlop(){
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                eventBridge.insertCommand(EventTFlipFlopCommand(Vector2(coordinates),connection, gestureListener.commandHistory, scene))
            }
        }
    }
    fun insertCJKFlipFlop(){
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                eventBridge.insertCommand(EventJKFlipFlopCommand(Vector2(coordinates),connection, gestureListener.commandHistory, scene))
            }
        }
    }
    fun insertPoint() {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                eventBridge.insertCommand(EventPointCommand(Vector2(coordinates),connection,gestureListener.commandHistory,scene))
            }
        }
    }
    fun insertCLed() {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
               eventBridge.insertCommand(EventLedCommand(Vector2(coordinates),connection, gestureListener.commandHistory, scene))
            }
        }
    }
    fun insertCPower(signalValue:Int) {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
              eventBridge.insertCommand(EventPowerCommand(Vector2(coordinates),signalValue, connection, gestureListener.commandHistory, scene))
            }
        }
    }
    fun insertPulseButton(){
        gestureListener.rectPointer.getPosition().also { position->
            snapAlign.getSnapCoordinates(position).also { coordinates->
                eventBridge.insertCommand(EventPulseButtonCommand(Vector2(coordinates),connection, gestureListener.commandHistory,scene))
            }
        }
    }
    fun insertCRandom(){
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
               eventBridge.insertCommand(EventRandomCommand(Vector2(coordinates),connection, gestureListener.commandHistory, scene))
            }
        }
    }
    fun insertSevenSegmentDisplay() {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
               eventBridge.insertCommand(EventSSDisplayCommand(Vector2(coordinates),connection,gestureListener.commandHistory, scene))
            }
        }
    }

    fun insertBCDDisplay(){
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                eventBridge.insertCommand(EventBcdDisplayCommand(Vector2(coordinates),connection,gestureListener.commandHistory,scene))
            }
        }
    }

    fun editCLabel(text:String,fontSize: Int){
        eventBridge.insertCommand(EventEditTextCommand(text, fontSize.toFloat(), gestureListener))
    }

    fun insertCLabel(text:String, fontSize:Int) {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                eventBridge.insertCommand(EventLabelCommand(Vector2(coordinates),text, fontSize.toFloat(), font, connection, gestureListener.commandHistory, scene))
            }
        }
    }

    fun insertCAnchorLabel(text:String, fontSize:Int,alignment:Int) {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                eventBridge.insertCommand(EventLabelAnchorCommand(Vector2(coordinates),text, fontSize.toFloat(), font, alignment, connection, gestureListener,scene))
            }
        }
    }

    fun insertCDataBus(size:Int) {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                eventBridge.insertCommand(EventDataBusCommand(Vector2(coordinates),size, connection, gestureListener.commandHistory,scene))
            }
        }
    }
    fun insertCFanOutBus(inputSize:Int, segments:Int) {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                ListNode(
                    CFanOutBus(
                        coordinates.x,
                        coordinates.y,
                        inputSize,
                        segments,
                        scene
                    )
                ).also{node->
                    connection.insertNode(node)
                    gestureListener.commandHistory.execute(InsertCommand(node, connection))
                }

            }
        }
    }

    fun insertGroup(){
        eventBridge.insertCommand(EventInsertGroupCommand(gestureListener))
    }

    fun removeGroup(){
        eventBridge.insertCommand(EventRemoveGroupCommand(gestureListener))
    }

    fun insertChannel(id:String, type:Int){
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
             eventBridge.insertCommand(EventChannelCommand(Vector2(coordinates),id,type,connection, gestureListener.commandHistory, scene))
            }
        }
    }
    fun insertCFullAdder(){
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                eventBridge.insertCommand(
                    EventFullAdderCommand(
                        Vector2(coordinates),
                        connection,
                        gestureListener.commandHistory,
                        scene
                    )
                )
            }
        }
    }
    fun insertCHalfAdder(){
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                eventBridge.insertCommand(
                    EventHalfAdderCommand(
                        Vector2(coordinates),
                        connection,
                        gestureListener.commandHistory,
                        scene
                    )
                )
            }
        }
    }
    fun setStyleA(){
        gestureListener.gridDecorator?.showLabelHeader()
        gestureListener.gridDecorator?.hidePositionGridLine()
    }

    fun setStyleB(){
        gestureListener.gridDecorator?.showPositionGridLine()
        gestureListener.gridDecorator?.showLabelHeader()
    }

    fun setStyleC(){
        gestureListener.gridDecorator?.hideLabelHeader()
        gestureListener.gridDecorator?.showPositionGridLine()
    }

    fun setStyleNone(){
        gestureListener.gridDecorator?.hidePositionGridLine()
        gestureListener.gridDecorator?.hideLabelHeader()
    }

    fun saveProject(){
        DataTransferObject().writeData(projectOptions, gestureListener, connection)
    }

    private fun readProject(){
        DataTransferObject().readData(projectOptions,gestureListener,connection,font, scene)
    }

    private fun createProject(){
        DataTransferObject().createData(projectOptions)
    }
}

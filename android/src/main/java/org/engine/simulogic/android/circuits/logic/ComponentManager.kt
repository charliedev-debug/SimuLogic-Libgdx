package org.engine.simulogic.android.circuits.logic

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.BitmapFont
import org.engine.simulogic.android.circuits.components.buses.CDataBus
import org.engine.simulogic.android.circuits.components.buses.CFanOutBus
import org.engine.simulogic.android.circuits.components.buttons.CPower
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
import org.engine.simulogic.android.circuits.components.visuals.CSevenSegmentDisplay
import org.engine.simulogic.android.circuits.components.wireless.CChannel
import org.engine.simulogic.android.circuits.components.wireless.ChannelBuffer
import org.engine.simulogic.android.circuits.storage.AutoSave
import org.engine.simulogic.android.circuits.storage.DataTransferObject
import org.engine.simulogic.android.circuits.storage.ProjectOptions
import org.engine.simulogic.android.events.MotionGestureListener
import org.engine.simulogic.android.scene.Entity
import org.engine.simulogic.android.scene.PlayGroundScene

class ComponentManager(private val projectOptions: ProjectOptions,private val executor: Executor,private val font: BitmapFont, private val connection:Connection, private  val scene: PlayGroundScene, private val gestureListener: MotionGestureListener) {

    private val snapAlign = SnapAlign()
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

    fun insertAND(){
       gestureListener.rectPointer.getPosition().also {position->
            snapAlign.getSnapCoordinates(position).also { coordinates->
                connection.insertNode(ListNode(CAnd( coordinates.x,coordinates.y,scene)))
            }

        }
    }

    fun insertOR(){
        gestureListener.rectPointer.getPosition().also {position->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                connection.insertNode(ListNode(COr(coordinates.x, coordinates.y, scene)))
            }
        }
    }

    fun insertXOR(){
        gestureListener.rectPointer.getPosition().also {position->
            snapAlign.getSnapCoordinates(position).also { coordinates->
            connection.insertNode(ListNode(CXor(coordinates.x,coordinates.y,scene)))
        }
    }}

    fun insertNOR() {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                connection.insertNode(ListNode(CNor(coordinates.x, coordinates.y, scene)))
            }
        }
    }

    fun insertNOT() {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                connection.insertNode(ListNode(CNot(coordinates.x, coordinates.y, scene)))
            }
        }
    }
    fun insertNAND() {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                connection.insertNode(ListNode(CNand(coordinates.x, coordinates.y, scene)))
            }
        }
    }
    fun insertXNOR() {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                connection.insertNode(ListNode(CXnor(coordinates.x, coordinates.y, scene)))
            }
        }
    }
    fun insertCClock(freq:Float) {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                connection.insertExecutionPoint(
                    ListNode(
                        CClock(
                            coordinates.x,
                            coordinates.y,
                            freq,
                            scene
                        )
                    )
                )
            }
        }
    }
    fun insertCLatch() {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                connection.insertNode(ListNode(CLatch(coordinates.x, coordinates.y, scene)))
            }
        }
    }
    fun insertCLed() {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                connection.insertNode(ListNode(CLed(coordinates.x, coordinates.y, scene)))
            }
        }
    }
    fun insertCPower(signalValue:Int) {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                connection.insertExecutionPoint(
                    ListNode(
                        CPower(
                            signalValue,
                            coordinates.x,
                            coordinates.y,
                            scene
                        )
                    )
                )
            }
        }
    }
    fun insertCRandom(){
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                connection.insertNode(ListNode(CRandom(coordinates.x, coordinates.y, scene)))
            }
        }
    }
    fun insertSevenSegmentDisplay() {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                connection.insertNode(ListNode(CSevenSegmentDisplay(coordinates.x, coordinates.y, scene)))
            }
        }
    }
    fun insertCLabel(text:String) {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                connection.insertNode(ListNode(CLabel(font, text, coordinates.x, coordinates.y, scene)))
            }
        }
    }
    fun insertCDataBus(size:Int) {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                connection.insertNode(ListNode(CDataBus(coordinates.x, coordinates.y, size, scene)))
            }
        }
    }
    fun insertCFanOutBus(inputSize:Int, segments:Int) {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                connection.insertNode(
                    ListNode(
                        CFanOutBus(
                            coordinates.x,
                            coordinates.y,
                            inputSize,
                            segments,
                            scene
                        )
                    )
                )
            }
        }
    }

    fun insertGroup(){
        gestureListener.insertGroup()
    }

    fun removeGroup(){
        gestureListener.removeGroup()
    }

    fun insertChannel(id:String, type:Int){
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                connection.insertExecutionPoint(ListNode(CChannel(coordinates.x, coordinates.y, id,type, Entity.ROTATE_RIGHT, scene)))
            }
        }
    }

    fun setStyleA(){
        gestureListener.gridDecorator?.showLabelHeader()
    }

    fun setStyleB(){
        gestureListener.gridDecorator?.hideLabelHeader()
    }

    fun saveProject(){
        DataTransferObject().writeData(projectOptions,connection)
    }

    private fun readProject(){
        DataTransferObject().readData(projectOptions,connection,font, scene)
    }

    private fun createProject(){
        DataTransferObject().createData(projectOptions)
    }
}

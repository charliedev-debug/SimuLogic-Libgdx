package org.engine.simulogic.android.circuits.logic

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.BitmapFont
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
                ListNode(CAnd( coordinates.x,coordinates.y,scene)).also{node->
                    connection.insertNode(node)
                    gestureListener.commandHistory.execute(InsertCommand(node, connection))
                }
            }

        }
    }

    fun insertOR(){
        gestureListener.rectPointer.getPosition().also {position->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                ListNode(COr(coordinates.x, coordinates.y, scene)).also{node->
                    connection.insertNode(node)
                    gestureListener.commandHistory.execute(InsertCommand(node, connection))
                }

            }
        }
    }

    fun insertXOR(){
        gestureListener.rectPointer.getPosition().also {position->
            snapAlign.getSnapCoordinates(position).also { coordinates->
                ListNode(CXor(coordinates.x,coordinates.y,scene)).also{node->
                    connection.insertNode(node)
                    gestureListener.commandHistory.execute(InsertCommand(node, connection))
                }
        }
    }}

    fun insertNOR() {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                ListNode(CNor(coordinates.x, coordinates.y, scene)).also{node->
                    connection.insertNode(node)
                    gestureListener.commandHistory.execute(InsertCommand(node, connection))
                }

            }
        }
    }

    fun insertNOT() {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                ListNode(CNot(coordinates.x, coordinates.y, scene)).also{node->
                    connection.insertNode(node)
                    gestureListener.commandHistory.execute(InsertCommand(node, connection))
                }
            }
        }
    }
    fun insertNAND() {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                ListNode(CNand(coordinates.x, coordinates.y, scene)).also{node->
                    connection.insertNode(node)
                    gestureListener.commandHistory.execute(InsertCommand(node, connection))
                }
            }
        }
    }
    fun insertXNOR() {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                ListNode(CXnor(coordinates.x, coordinates.y, scene)).also{node->
                    connection.insertNode(node)
                    gestureListener.commandHistory.execute(InsertCommand(node, connection))
                }

            }
        }
    }
    fun insertCClock(freq:Float) {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                ListNode(CClock(coordinates.x, coordinates.y, freq, scene)).also { node->
                    connection.insertExecutionPoint(node)
                    gestureListener.commandHistory.execute(InsertCommand(node, connection))
                }

            }
        }
    }
    fun insertCLatch() {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                ListNode(CLatch(coordinates.x, coordinates.y, scene)).also{node->
                    connection.insertNode(node)
                    gestureListener.commandHistory.execute(InsertCommand(node, connection))
                }

            }
        }
    }
    fun insertCDFlipFlop(){
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                ListNode(CDFlipFlop(coordinates.x, coordinates.y, scene)).also{node->
                    connection.insertNode(node)
                    gestureListener.commandHistory.execute(InsertCommand(node, connection))
                }

            }
        }
    }
    fun insertCLed() {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                ListNode(CLed(coordinates.x, coordinates.y, scene)).also{node->
                    connection.insertNode(node)
                    gestureListener.commandHistory.execute(InsertCommand(node, connection))
                }

            }
        }
    }
    fun insertCPower(signalValue:Int) {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                ListNode(CPower(
                        signalValue,
                        coordinates.x,
                        coordinates.y,
                        rotationDirection = Entity.ROTATE_RIGHT,
                        scene
                    )
                ).also { node->
                    connection.insertExecutionPoint(node)
                    gestureListener.commandHistory.execute(InsertCommand(node, connection))
                }
            }
        }
    }
    fun insertCRandom(){
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                ListNode(CRandom(coordinates.x, coordinates.y, scene)).also{node->
                    connection.insertNode(node)
                    gestureListener.commandHistory.execute(InsertCommand(node, connection))
                }
            }
        }
    }
    fun insertSevenSegmentDisplay() {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                ListNode(CSevenSegmentDisplay(coordinates.x, coordinates.y, scene)).also{node->
                    connection.insertNode(node)
                    gestureListener.commandHistory.execute(InsertCommand(node, connection))
                }

            }
        }
    }

    fun insertBCDDisplay(){
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                ListNode(CBCDDisplay(coordinates.x, coordinates.y, scene)).also{node->
                    connection.insertNode(node)
                    gestureListener.commandHistory.execute(InsertCommand(node, connection))
                }
            }
        }
    }
    fun insertCLabel(text:String, fontSize:Int) {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                ListNode(CLabel(font, fontSize.toFloat(), text, coordinates.x, coordinates.y, scene)).also{node->
                    connection.insertNode(node)
                    gestureListener.commandHistory.execute(InsertCommand(node, connection))
                }
            }
        }
    }
    fun insertCDataBus(size:Int) {
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                ListNode(CDataBus(coordinates.x, coordinates.y, size, scene)).also{node->
                    connection.insertNode(node)
                    gestureListener.commandHistory.execute(InsertCommand(node, connection))
                }
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
        gestureListener.insertGroup()
    }

    fun removeGroup(){
        gestureListener.removeGroup()
    }

    fun insertChannel(id:String, type:Int){
        gestureListener.rectPointer.getPosition().also { position ->
            snapAlign.getSnapCoordinates(position).also { coordinates ->
                ListNode(
                    CChannel(
                        coordinates.x,
                        coordinates.y,
                        id,
                        type,
                        Entity.ROTATE_RIGHT,
                        scene
                    )
                ).also{node->
                    if(type == ChannelBuffer.CHANNEL_OUTPUT) {
                        connection.insertExecutionPoint(node)
                    }else{
                        connection.insertNode(node)
                    }
                    gestureListener.commandHistory.execute(InsertCommand(node, connection))
                }

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
        DataTransferObject().writeData(projectOptions, gestureListener, connection)
    }

    private fun readProject(){
        DataTransferObject().readData(projectOptions,gestureListener,connection,font, scene)
    }

    private fun createProject(){
        DataTransferObject().createData(projectOptions)
    }
}

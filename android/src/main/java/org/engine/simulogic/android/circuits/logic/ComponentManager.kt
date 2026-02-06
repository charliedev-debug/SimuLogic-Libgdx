package org.engine.simulogic.android.circuits.logic

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.BitmapFont
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
import org.engine.simulogic.android.circuits.storage.AutoSave
import org.engine.simulogic.android.circuits.storage.DataTransferObject
import org.engine.simulogic.android.circuits.storage.ProjectOptions
import org.engine.simulogic.android.events.MotionGestureListener
import org.engine.simulogic.android.scene.PlayGroundScene

class ComponentManager(private val projectOptions: ProjectOptions,private val executor: Executor,private val font: BitmapFont, private val connection:Connection, private  val scene: PlayGroundScene, private val gestureListener: MotionGestureListener) {

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
            connection.insertNode(ListNode(CAnd(position.x,position.y,scene)))
        }
    }
    fun insertOR(){
        gestureListener.rectPointer.getPosition().also {position->
            connection.insertNode(ListNode(COr(position.x,position.y,scene)))
        }
    }
    fun insertXOR(){
        gestureListener.rectPointer.getPosition().also {position->
            connection.insertNode(ListNode(CXor(position.x,position.y,scene)))
        }
    }
    fun insertNOR(){
        gestureListener.rectPointer.getPosition().also {position->
            connection.insertNode(ListNode(CNor(position.x,position.y,scene)))
        }
    }
    fun insertNOT(){
        gestureListener.rectPointer.getPosition().also {position->
            connection.insertNode(ListNode(CNot(position.x,position.y,scene)))
        }
    }
    fun insertNAND(){
        gestureListener.rectPointer.getPosition().also {position->
            connection.insertNode(ListNode(CNand(position.x,position.y,scene)))
        }
    }
    fun insertXNOR(){
        gestureListener.rectPointer.getPosition().also {position->
            connection.insertNode(ListNode(CXnor(position.x,position.y,scene)))
        }
    }
    fun insertCClock(freq:Float){
        gestureListener.rectPointer.getPosition().also {position->
            connection.insertExecutionPoint(ListNode(CClock(position.x,position.y,freq,scene)))
        }
    }
    fun insertCLatch(){
        gestureListener.rectPointer.getPosition().also {position->
            connection.insertNode(ListNode(CLatch(position.x,position.y,scene)))
        }
    }
    fun insertCLed(){
        gestureListener.rectPointer.getPosition().also {position->
            connection.insertNode(ListNode(CLed(position.x,position.y,scene)))
        }
    }
    fun insertCPower(signalValue:Int){
        gestureListener.rectPointer.getPosition().also {position->
            connection.insertExecutionPoint(ListNode(CPower(signalValue,position.x,position.y,scene)))
        }
    }
    fun insertCRandom(){
        gestureListener.rectPointer.getPosition().also {position->
            connection.insertNode(ListNode(CRandom(position.x,position.y,scene)))
        }
    }
    fun insertSevenSegmentDisplay(){
        gestureListener.rectPointer.getPosition().also {position->
            connection.insertNode(ListNode(CSevenSegmentDisplay(position.x,position.y,scene)))
        }
    }

    fun insertCLabel(text:String){
        gestureListener.rectPointer.getPosition().also { position->
            connection.insertNode(ListNode(CLabel(font,text,position.x, position.y, scene )))
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

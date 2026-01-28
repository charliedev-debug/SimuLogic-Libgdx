package org.engine.simulogic.android.circuits.storage

import android.content.Context
import android.os.Environment
import com.badlogic.gdx.Gdx
import org.engine.simulogic.android.circuits.components.CTypes
import org.engine.simulogic.android.circuits.components.gates.CAnd
import org.engine.simulogic.android.circuits.components.gates.CNand
import org.engine.simulogic.android.circuits.components.gates.CNor
import org.engine.simulogic.android.circuits.components.gates.CNot
import org.engine.simulogic.android.circuits.components.gates.COr
import org.engine.simulogic.android.circuits.components.gates.CXnor
import org.engine.simulogic.android.circuits.components.gates.CXor
import org.engine.simulogic.android.circuits.components.generators.CClock
import org.engine.simulogic.android.circuits.components.generators.CRandom
import org.engine.simulogic.android.circuits.components.latches.CLatch
import org.engine.simulogic.android.circuits.components.lines.LineMarker
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.scene.PlayGroundScene
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.EOFException
import java.io.FileOutputStream
import java.io.IOException

class DataTransferObject {

    private val IDENTIFIER = 0xC145FF
    private val VERSION = 1
    fun writeData(connection: Connection){
        val file = Gdx.files.local("projects/circuit.bin")
       // println("Saving file.... ${file.file()?.path} : ${file.file().exists()}")
        val stream = DataOutputStream(file.write(false))
        stream.writeInt(IDENTIFIER)
        stream.writeInt(VERSION)
        // component size
        stream.writeInt(connection.size())
        // save the components first for easier reading in the future
        connection.forEachIndexed { index, listNode ->
            // the id will change after every save
            val component = listNode.value.apply { id = index }
            stream.writeInt(component.type.name.length)
            stream.write(component.type.name.toByteArray(Charsets.UTF_8))
            stream.writeInt(index)
            stream.writeFloat(component.getPosition().x)
            stream.writeFloat(component.getPosition().y)
            stream.writeInt(component.rotation)
        }
        connection.forEach{ listNode ->
            val component = listNode.value
            //from id
            stream.writeInt(component.id)
            stream.writeInt(listNode.getLineMarkerChildren().size)
            listNode.getLineMarkerChildren().forEach { marker->
                stream.writeInt(marker.index)
                // to id
                stream.writeInt(marker.to.value.id)
                stream.writeInt(marker.signalFrom)
                stream.writeInt(marker.signalTo)
                stream.writeInt(marker.signals.size)
                marker.signals.forEach { signal->
                    stream.writeInt(signal.signalIndex)
                    stream.writeFloat(signal.getPosition().x)
                    stream.writeFloat(signal.getPosition().y)
                }
            }
        }
        stream.flush()
        stream.close()
      //  println("File saved ${file.file()?.path} : ${file.file().exists()}")
    }

    fun readData(connection: Connection, scene: PlayGroundScene){
        val file = Gdx.files.local("projects/circuit.bin")
        val stream = DataInputStream(BufferedInputStream(file.read()))
        val identifier = stream.readInt()
        if (identifier != IDENTIFIER) throw IOException("Corrupt or Not a circuit file")
        val version = stream.readInt()
        val componentSize = stream.readInt()
        for(i in 0 until  componentSize){
            val typeLength = stream.readInt()
            val typeBytes = ByteArray(typeLength)
            stream.readFully(typeBytes)
            val type = CTypes.valueOf(String(typeBytes,Charsets.UTF_8))
            val index = stream.readInt()
            val x = stream.readFloat()
            val y = stream.readFloat()
            val rotation = stream.readInt()
            when(type){
                CTypes.AND->{
                    connection.insertNode(ListNode(CAnd(x, y, scene)))
                }
                CTypes.NAND->{
                    connection.insertNode(ListNode(CNand(x, y, scene)))
                }
                CTypes.NOR->{
                    connection.insertNode(ListNode(CNor(x, y, scene)))
                }
                CTypes.NOT->{
                    connection.insertNode(ListNode(CNot(x, y, scene)))
                }
                CTypes.OR->{
                    connection.insertNode(ListNode(COr(x, y, scene)))
                }
                CTypes.XNOR->{
                    connection.insertNode(ListNode((CXnor(x, y, scene))))
                }
                CTypes.XOR->{
                    connection.insertNode(ListNode(CXor(x, y, scene)))
                }
                CTypes.LATCH->{
                    connection.insertNode(ListNode(CLatch(x, y, scene)))
                }
                CTypes.CLOCK->{
                    connection.insertNode(ListNode(CClock(x, y, scene)))
                }
                CTypes.RANDOM->{
                    connection.insertNode(ListNode(CRandom(x, y, scene)))
                }
                else -> { throw  IOException("Unknown component exception $type")}
            }
        }

        try {
            while(true){
                val fromId = stream.readInt()
                val markerSizeFrom = stream.readInt()
                for(i in 0 until markerSizeFrom){
                    val index = stream.readInt()
                    val toId = stream.readInt()
                    val signalFromIndex = stream.readInt()
                    val signalToIndex = stream.readInt()
                    val signalSize = stream.readInt()
                    LineMarker(connection[fromId],connection[toId],signalFromIndex, signalToIndex, index).also { marker->
                        marker.initialize(scene)
                        for(j in 0 until  signalSize){
                            val signalIndex = stream.readInt()
                            val x = stream.readFloat()
                            val y = stream.readFloat()
                            marker.signals[signalIndex].also { signal->
                                signal.updatePosition(x, y)
                            }
                        }
                        connection[fromId].insertChildUnmarked(connection[toId],marker)
                    }
                }
            }
        }catch (eof:EOFException){
            eof.printStackTrace()
        }
    }
}

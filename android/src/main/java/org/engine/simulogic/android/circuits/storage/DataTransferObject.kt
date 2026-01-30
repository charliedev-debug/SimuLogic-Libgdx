package org.engine.simulogic.android.circuits.storage

import android.content.Context
import android.os.Environment
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
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
import org.engine.simulogic.android.circuits.components.other.CLabel
import org.engine.simulogic.android.circuits.components.visuals.CLed
import org.engine.simulogic.android.circuits.components.visuals.CSevenSegmentDisplay
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.scene.PlayGroundScene
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.EOFException
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class DataTransferObject {

    private val IDENTIFIER = 0xC145FF
    private val VERSION = 1
    fun writeData(projectOptions: ProjectOptions, connection: Connection) {
        val title = projectOptions.title
        val description = projectOptions.description
        val file = Gdx.files.external("projects/$title")
        val temp = Gdx.files.external("projects/$title.temp")
        // println("Saving file.... ${file.file()?.path} : ${file.file().exists()}")
        val stream = DataOutputStream(temp.write(false))
        stream.writeInt(IDENTIFIER)
        stream.writeInt(VERSION)
        stream.writeInt(title.length)
        stream.write(title.toByteArray(Charsets.UTF_8))
        stream.writeInt(description.length)
        stream.write(description.toByteArray(Charsets.UTF_8))
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
            // save label data
            if (component is CLabel) {
                stream.writeInt(component.text.length)
                stream.write(component.text.toByteArray(Charsets.UTF_8))
            } else if (component is CClock) {
                stream.writeFloat(component.freq)
            }
        }
        connection.forEach { listNode ->
            val component = listNode.value
            //from id
            stream.writeInt(component.id)
            stream.writeInt(listNode.getLineMarkerChildren().size)
            listNode.getLineMarkerChildren().forEach { marker ->
                stream.writeInt(marker.index)
                // to id
                stream.writeInt(marker.to.value.id)
                stream.writeInt(marker.signalFrom)
                stream.writeInt(marker.signalTo)
                stream.writeInt(marker.signals.size)
                marker.signals.forEach { signal ->
                    stream.writeInt(signal.signalIndex)
                    stream.writeFloat(signal.getPosition().x)
                    stream.writeFloat(signal.getPosition().y)
                }
            }
        }
        stream.flush()
        stream.close()
        file.delete()
        temp.moveTo(file)
        //  println("File saved ${file.file()?.path} : ${file.file().exists()}")
    }

    fun createData(projectOptions: ProjectOptions) {
        val title = "${projectOptions.title}.bin"
        val description = projectOptions.description
        val file = Gdx.files.external("projects/$title")
        // println("Saving file.... ${file.file()?.path} : ${file.file().exists()}")
        val stream = DataOutputStream(file.write(false))
        stream.writeInt(IDENTIFIER)
        stream.writeInt(VERSION)
        stream.writeInt(title.length)
        stream.write(title.toByteArray(Charsets.UTF_8))
        stream.writeInt(description.length)
        stream.write(description.toByteArray(Charsets.UTF_8))
        stream.flush()
        stream.close()
    }

    fun readData(
        projectOptions: ProjectOptions,
        connection: Connection,
        font: BitmapFont,
        scene: PlayGroundScene
    ) {

        val file = Gdx.files.external("projects/${projectOptions.title}")
       // println("${projectOptions.title} = ${file.file().path}")
        val stream = DataInputStream(BufferedInputStream(file.read()))
        try {
            val identifier = stream.readInt()
            if (identifier != IDENTIFIER) throw IOException("Corrupt or Not a circuit file")
            val version = stream.readInt()
            val titleLen = stream.readInt()
            val title = stream.readFully(ByteArray(titleLen))
            val descrLen = stream.readInt()
            val description = stream.readFully(ByteArray(descrLen))
            val componentSize = stream.readInt()
            for (i in 0 until componentSize) {
                val typeLength = stream.readInt()
                val typeBytes = ByteArray(typeLength)
                stream.readFully(typeBytes)
                val type = CTypes.valueOf(String(typeBytes, Charsets.UTF_8))
                val index = stream.readInt()
                val x = stream.readFloat()
                val y = stream.readFloat()
                val rotation = stream.readInt()
                // clock specific options
                val freq = if (type == CTypes.CLOCK) stream.readFloat() else 0f
                // label specific options
                val labelTextLength = if (type == CTypes.LABEL) stream.readInt() else 0
                val labelText = if (type == CTypes.LABEL) ByteArray(labelTextLength) else null
                labelText?.let {
                    stream.readFully(labelText)
                }
                when (type) {
                    CTypes.AND -> {
                        connection.insertNode(ListNode(CAnd(x, y, scene)))
                    }

                    CTypes.NAND -> {
                        connection.insertNode(ListNode(CNand(x, y, scene)))
                    }

                    CTypes.NOR -> {
                        connection.insertNode(ListNode(CNor(x, y, scene)))
                    }

                    CTypes.NOT -> {
                        connection.insertNode(ListNode(CNot(x, y, scene)))
                    }

                    CTypes.OR -> {
                        connection.insertNode(ListNode(COr(x, y, scene)))
                    }

                    CTypes.XNOR -> {
                        connection.insertNode(ListNode((CXnor(x, y, scene))))
                    }

                    CTypes.XOR -> {
                        connection.insertNode(ListNode(CXor(x, y, scene)))
                    }

                    CTypes.LATCH -> {
                        connection.insertNode(ListNode(CLatch(x, y, scene)))
                    }

                    CTypes.CLOCK -> {
                        connection.insertNode(ListNode(CClock(x, y, freq, scene)))
                    }

                    CTypes.RANDOM -> {
                        connection.insertNode(ListNode(CRandom(x, y, scene)))
                    }

                    CTypes.LED -> {
                        connection.insertNode(ListNode(CLed(x, y, scene)))
                    }

                    CTypes.SEVEN_SEGMENT_DISPLAY -> {
                        connection.insertNode(ListNode(CSevenSegmentDisplay(x, y, scene)))
                    }

                    CTypes.LABEL -> {
                        connection.insertNode(
                            ListNode(
                                CLabel(
                                    font,
                                    "${labelText?.toString(Charsets.UTF_8)}",
                                    x,
                                    y,
                                    scene
                                )
                            )
                        )
                    }

                    else -> {
                        throw IOException("Unknown component exception $type")
                    }
                }
            }
            while (true) {
                val fromId = stream.readInt()
                val markerSizeFrom = stream.readInt()
                for (i in 0 until markerSizeFrom) {
                    val index = stream.readInt()
                    val toId = stream.readInt()
                    val signalFromIndex = stream.readInt()
                    val signalToIndex = stream.readInt()
                    val signalSize = stream.readInt()
                    LineMarker(
                        connection[fromId],
                        connection[toId],
                        signalFromIndex,
                        signalToIndex,
                        index
                    ).also { marker ->
                        marker.initialize(scene)
                        for (j in 0 until signalSize) {
                            val signalIndex = stream.readInt()
                            val x = stream.readFloat()
                            val y = stream.readFloat()
                            marker.signals[signalIndex].also { signal ->
                                signal.updatePosition(x, y)
                            }
                        }
                        connection[fromId].insertChildUnmarked(connection[toId], marker)
                    }
                }
            }
        } catch (eof: EOFException) {
            eof.printStackTrace()
        }
    }

    fun existsNoExtension(title: String): Boolean {
        val file = Gdx.files.external("projects/$${title}.bin")
        return file.exists()
    }

    fun exists(title: String): Boolean {
        val file = Gdx.files.external("projects/$${title}")
        return file.exists()
    }

    fun readFileHeader(path: File): ProjectOptions {
        val inputStream = FileInputStream(path)
        val stream = DataInputStream(BufferedInputStream(inputStream))
        try {
            val identifier = stream.readInt()
            if (identifier != IDENTIFIER) throw IOException("Corrupt or Not a circuit file")
            val version = stream.readInt()
            val titleLen = stream.readInt()
            val title = ByteArray(titleLen)
            stream.readFully(title)
            val descrLen = stream.readInt()
            val description = ByteArray(descrLen)
            stream.readFully(description)
            inputStream.close()
            stream.close()
            return ProjectOptions(
                title.toString(Charsets.UTF_8),
                description.toString(Charsets.UTF_8),
                path.lastModified(),
                ProjectOptions.OPEN
            )
        } catch (eof: EOFException) {
            eof.printStackTrace()
        }
        inputStream.close()
        stream.close()
        return ProjectOptions("none", "none", 0L, ProjectOptions.OPEN)
    }

    fun listProjects(context: Context): List<ProjectOptions> {
        val files = mutableListOf<ProjectOptions>()
        val data = File(context.getExternalFilesDir(""), "projects").listFiles()
        data?.forEach {
            //println(it.path)
            files.add(readFileHeader(it))
        }
        files.sortByDescending { it.lastModified }
        return files
    }
}

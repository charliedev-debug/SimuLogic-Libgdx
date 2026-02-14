package org.engine.simulogic.android.circuits.storage

import android.content.Context
import android.net.Uri
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import org.engine.simulogic.android.circuits.components.CTypes
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
import org.engine.simulogic.android.circuits.components.generators.CClock
import org.engine.simulogic.android.circuits.components.generators.CRandom
import org.engine.simulogic.android.circuits.components.latches.CLatch
import org.engine.simulogic.android.circuits.components.lines.LineMarker
import org.engine.simulogic.android.circuits.components.other.CGroup
import org.engine.simulogic.android.circuits.components.other.CLabel
import org.engine.simulogic.android.circuits.components.visuals.CLed
import org.engine.simulogic.android.circuits.components.visuals.CSevenSegmentDisplay
import org.engine.simulogic.android.circuits.components.wireless.CChannel
import org.engine.simulogic.android.circuits.components.wireless.ChannelBuffer
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.events.MotionGestureListener
import org.engine.simulogic.android.scene.PlayGroundScene
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.EOFException
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.security.MessageDigest
import java.util.UUID

class DataTransferObject {

    private val IDENTIFIER = 0xC145FF
    private val VERSION = 1

    companion object {
        fun deleteFile(context: Context, title: String) {
            File(context.getExternalFilesDir(""), "projects/$title").delete()
        }

        fun randomFileName(extension: String = "bin"): String {
            val randomString = UUID.randomUUID().toString()
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(randomString.toByteArray())
            val hexString = hashBytes.joinToString("") { "%02x".format(it) }
            return "$hexString.$extension"
        }
    }


    fun writeData(projectOptions: ProjectOptions,gestureListener:MotionGestureListener, connection: Connection) {
        val title = projectOptions.title
        val description = projectOptions.description
        val file = Gdx.files.external("projects/${projectOptions.fileName}")
        val temp = Gdx.files.external("projects/${projectOptions.fileName}.temp")
        // println("Saving file.... ${file.file()?.path} : ${file.file().exists()}")
        val stream = DataOutputStream(temp.write(false))
        stream.writeInt(IDENTIFIER)
        stream.writeInt(VERSION)
        stream.writeInt(title.length)
        stream.write(title.toByteArray(Charsets.UTF_8))
        stream.writeInt(description.length)
        stream.write(description.toByteArray(Charsets.UTF_8))
        // save camera state
        stream.writeFloat(gestureListener.rectPointer.getPosition().x)
        stream.writeFloat(gestureListener.rectPointer.getPosition().y)
        stream.writeFloat(gestureListener.zoomValue())
        // component size
        stream.writeInt(connection.size())
        // assign indices first for later processing
        connection.forEachIndexed { index, listNode ->
            listNode.value.id = index
        }
        // save the components first for easier reading in the future
        connection.forEachIndexed { index, listNode ->
            // the id will change after every save
            val component = listNode.value
            stream.writeInt(component.type.name.length)
            stream.write(component.type.name.toByteArray(Charsets.UTF_8))
            stream.writeInt(index)
            stream.writeFloat(component.getPosition().x)
            stream.writeFloat(component.getPosition().y)
            stream.writeInt(component.rotationDirection)
            // save label data
            if (component is CLabel) {
                stream.writeInt(component.text.length)
                stream.write(component.text.toByteArray(Charsets.UTF_8))
            } else if (component is CClock) {
                stream.writeFloat(component.freq)
            } else if (component is CPower) {
                stream.writeInt(component.value)
            } else if (component is CDataBus) {
                stream.writeInt(component.size)
            } else if (component is CFanOutBus) {
                stream.writeInt(component.inputSize)
                stream.writeInt(component.segments)
            } else if (component is CGroup) {
                stream.writeFloat(component.getWidth())
                stream.writeFloat(component.getHeight())
                stream.writeInt(component.dataContainer.size())
                component.dataContainer.forEach { item ->
                    stream.writeInt(item.value.id)
                }
            } else if (component is CChannel) {
                stream.writeInt(component.channelId.length)
                stream.write(component.channelId.toByteArray(Charsets.UTF_8))
                stream.writeInt(component.channelType)
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
                stream.writeInt(marker.linePointCountX)
                stream.writeInt(marker.linePointCountY)
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
        val title = projectOptions.title
        val description = projectOptions.description
        val file = Gdx.files.external("projects/${projectOptions.fileName}")
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
        gestureListener: MotionGestureListener,
        connection: Connection,
        font: BitmapFont,
        scene: PlayGroundScene
    ) {
        val file = Gdx.files.external("projects/${projectOptions.fileName}")
        // println("${projectOptions.title} = ${file.file().path}")
        val stream = DataInputStream(BufferedInputStream(file.read()))
        val groups = mutableListOf<CGroup>()
        try {
            val identifier = stream.readInt()
            if (identifier != IDENTIFIER) throw IOException("Corrupt or Not a circuit file")
            val version = stream.readInt()
            val titleLen = stream.readInt()
            val title = stream.readFully(ByteArray(titleLen))
            val descrLen = stream.readInt()
            val description = stream.readFully(ByteArray(descrLen))
            // load camera state
            val cameraX = stream.readFloat()
            val cameraY = stream.readFloat()
            val cameraZoom = stream.readFloat()
            gestureListener.setCameraPosition(cameraX, cameraY)
            gestureListener.setCameraZoom(cameraZoom)
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
                // power generator signal value
                val powerValue = if (type == CTypes.POWER) stream.readInt() else 0
                // data bus size value
                val bus_size = if (type == CTypes.DATA_BUS) stream.readInt() else 0
                // data bus fan out
                val bus_fan_out_input_size =
                    if (type == CTypes.DATA_BUS_FAN_OUT) stream.readInt() else 0
                val bus_fan_out_segments =
                    if (type == CTypes.DATA_BUS_FAN_OUT) stream.readInt() else 0
                // load group width, height and all group members
                val groupWidth = if (type == CTypes.GROUP) stream.readFloat() else 0f
                val groupHeight = if (type == CTypes.GROUP) stream.readFloat() else 0f
                val groupMemberIds = if (type == CTypes.GROUP) {
                    mutableListOf<Int>().also { list ->
                        var dataContainerSize = stream.readInt()
                        while (dataContainerSize > 0) {
                            list.add(stream.readInt())
                            dataContainerSize--
                        }
                    }
                } else {
                    mutableListOf()
                }

                // load channel data
                val channelIdLength = if (type == CTypes.CHANNEL) stream.readInt() else 0
                val channelIdText = if (type == CTypes.CHANNEL) ByteArray(channelIdLength) else null
                channelIdText?.let {
                    stream.readFully(channelIdText)
                }
                val channelType = if (type == CTypes.CHANNEL) stream.readInt() else 0
                when (type) {
                    CTypes.AND -> {
                        connection.insertNode(ListNode(CAnd(x, y, rotation, scene)))
                    }

                    CTypes.NAND -> {
                        connection.insertNode(ListNode(CNand(x, y, rotation, scene)))
                    }

                    CTypes.NOR -> {
                        connection.insertNode(ListNode(CNor(x, y, rotation, scene)))
                    }

                    CTypes.NOT -> {
                        connection.insertNode(ListNode(CNot(x, y, rotation, scene)))
                    }

                    CTypes.OR -> {
                        connection.insertNode(ListNode(COr(x, y, rotation, scene)))
                    }

                    CTypes.XNOR -> {
                        connection.insertNode(ListNode((CXnor(x, y, rotation, scene))))
                    }

                    CTypes.XOR -> {
                        connection.insertNode(ListNode(CXor(x, y, rotation, scene)))
                    }

                    CTypes.LATCH -> {
                        connection.insertNode(ListNode(CLatch(x, y, rotation, scene)))
                    }

                    CTypes.CLOCK -> {
                        connection.insertExecutionPoint(
                            ListNode(
                                CClock(
                                    x,
                                    y,
                                    freq,
                                    rotation,
                                    scene
                                )
                            )
                        )
                    }

                    CTypes.RANDOM -> {
                        connection.insertNode(ListNode(CRandom(x, y, rotation, scene)))
                    }

                    CTypes.LED -> {
                        connection.insertNode(ListNode(CLed(x, y, rotation, scene)))
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

                    CTypes.POWER -> {
                        connection.insertExecutionPoint(ListNode(CPower(powerValue, x, y, scene)))
                    }

                    CTypes.DATA_BUS -> {
                        connection.insertNode(ListNode(CDataBus(x, y, bus_size, rotation, scene)))
                    }

                    CTypes.DATA_BUS_FAN_OUT -> {
                        connection.insertNode(
                            ListNode(
                                CFanOutBus(
                                    x,
                                    y,
                                    bus_fan_out_input_size,
                                    bus_fan_out_segments,
                                    rotation,
                                    scene
                                )
                            )
                        )
                    }

                    CTypes.GROUP -> {
                        connection.insertNode(ListNode(CGroup(x, y,groupWidth, groupHeight, connection, scene).also { group ->
                            group.setSize(groupWidth, groupHeight)
                            group.componentGroupIds.addAll(groupMemberIds)
                            group.gestureListener = gestureListener
                            groups.add(group)
                        }))
                    }

                    CTypes.CHANNEL -> {
                        if (channelType == ChannelBuffer.CHANNEL_OUTPUT) {
                            connection.insertExecutionPoint(
                                ListNode(
                                    CChannel(
                                        x,
                                        y,
                                        "${channelIdText?.toString(Charsets.UTF_8)}",
                                        channelType,
                                        rotation,
                                        scene
                                    )
                                )
                            )
                        } else {
                            connection.insertNode(
                                ListNode(
                                    CChannel(
                                        x,
                                        y,
                                        "${channelIdText?.toString(Charsets.UTF_8)}",
                                        channelType,
                                        rotation,
                                        scene
                                    )
                                )
                            )
                        }
                    }

                    else -> {
                        throw IOException("Unknown component exception $type")
                    }
                }
            }

            groups.forEach {
                it.loadFromIds(connection)
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
                    val linePointCountX = stream.readInt()
                    val linePointCountY = stream.readInt()
                    LineMarker(
                        scene,
                        connection[fromId],
                        connection[toId],
                        signalFromIndex,
                        signalToIndex,
                        index,
                        linePointCountX,
                        linePointCountY
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

    fun exists(context: Context, title: String): Boolean {
        val file = File(context.getExternalFilesDir(""), "projects/${title}")
        return file.exists()
    }

    fun importProject(context: Context, uri: Uri): ProjectOptions? {
        val file = File(context.getExternalFilesDir(""), "projects/${randomFileName()}")
        val outputStream = FileOutputStream(file)
        context.contentResolver?.openInputStream(uri).use { inputStream ->
            inputStream?.copyTo(outputStream)
        }
        outputStream.close()
        try {
            return readFileHeader(file)
        } catch (io: IOException) {
            file.delete()
        }
        return null
    }

    private fun readFileHeader(path: File): ProjectOptions {
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
                path.name,
                title.toString(Charsets.UTF_8),
                description.toString(Charsets.UTF_8),
                path.path,
                path.lastModified(),
                ProjectOptions.OPEN
            )
        } catch (eof: EOFException) {
            eof.printStackTrace()
        }
        inputStream.close()
        stream.close()
        return ProjectOptions("none", "none", "none", path.path, 0L, ProjectOptions.OPEN)
    }


    fun listProjects(context: Context): List<ProjectOptions> {
        val files = mutableListOf<ProjectOptions>()
        val data = File(context.getExternalFilesDir(""), "projects").listFiles()
        data?.forEach {
            //println(it.path)
            try {
                readFileHeader(it).also { options ->
                    files.add(options)
                }
            } catch (io: Exception) {
                //ignore the file and continue
            }
        }
        files.sortByDescending { it.lastModified }
        return files
    }
}

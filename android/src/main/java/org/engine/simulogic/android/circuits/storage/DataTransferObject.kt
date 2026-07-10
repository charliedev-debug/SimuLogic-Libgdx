package org.engine.simulogic.android.circuits.storage

import android.content.Context
import android.content.res.AssetManager
import android.net.Uri
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.CTypes
import org.engine.simulogic.android.circuits.components.buses.CDataBus
import org.engine.simulogic.android.circuits.components.buses.CFanOutBus
import org.engine.simulogic.android.circuits.components.buttons.CPower
import org.engine.simulogic.android.circuits.components.buttons.CPulseButton
import org.engine.simulogic.android.circuits.components.flipflops.CDFlipFlop
import org.engine.simulogic.android.circuits.components.flipflops.CJKFlipFlop
import org.engine.simulogic.android.circuits.components.flipflops.CTFlipFlop
import org.engine.simulogic.android.circuits.components.gates.CAnd
import org.engine.simulogic.android.circuits.components.gates.CAndThreeInput
import org.engine.simulogic.android.circuits.components.gates.CNand
import org.engine.simulogic.android.circuits.components.gates.CNandThreeInput
import org.engine.simulogic.android.circuits.components.gates.CNor
import org.engine.simulogic.android.circuits.components.gates.CNorThreeInput
import org.engine.simulogic.android.circuits.components.gates.CNot
import org.engine.simulogic.android.circuits.components.gates.COr
import org.engine.simulogic.android.circuits.components.gates.COrThreeInput
import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.gates.CXnor
import org.engine.simulogic.android.circuits.components.gates.CXor
import org.engine.simulogic.android.circuits.components.generators.CClock
import org.engine.simulogic.android.circuits.components.generators.CRandom
import org.engine.simulogic.android.circuits.components.latches.CLatch
import org.engine.simulogic.android.circuits.components.latches.CSRLatch
import org.engine.simulogic.android.circuits.components.lines.LineMarker
import org.engine.simulogic.android.circuits.components.other.CAnchor
import org.engine.simulogic.android.circuits.components.other.CGroup
import org.engine.simulogic.android.circuits.components.other.CLabel
import org.engine.simulogic.android.circuits.components.other.CPoint
import org.engine.simulogic.android.circuits.components.visuals.CBCDDisplay
import org.engine.simulogic.android.circuits.components.visuals.CLed
import org.engine.simulogic.android.circuits.components.visuals.CSevenSegmentDisplay
import org.engine.simulogic.android.circuits.components.wireless.CChannel
import org.engine.simulogic.android.circuits.components.wireless.ChannelBuffer
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.circuits.theme.EnvironmentTheme
import org.engine.simulogic.android.events.MotionGestureListener
import org.engine.simulogic.android.helpers.ErrorLogs
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
import kotlin.math.min

class DataTransferObject {

    private val IDENTIFIER = 0xC145FF
    private val VERSION_1 = 1
    private val VERSION_2 = 2
    private val VERSION_3 = 3
    private val DESCRIPTION_LENGTH_MAX_CHARACTERS = 512
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

    data class NestedLineMarkerHelper(
        val fromId: Int,
        val markerSizeFrom: Int,
        val index: Int,
        val toId: Int,
        val signalFromIndex: Int,
        val signalToIndex: Int,
        val signalSize: Int,
        val linePointCountX: Int,
        val linePointCountY: Int,
        val originDepth: Int,
        val fromSourceNode: Int,
        val fromLineMarker: Int,
        val sourceSignalIndex: Int,
        val signals: MutableList<NestedCSignalHelper> = mutableListOf()
    )

    data class NestedCSignalHelper(
        val signalIndex: Int,
        val x: Float,
        val y: Float
    )

    data class LabelAnchorHelper(val label: CLabel, val anchorEntityId:Int, val alignment:Int)

    fun writeData(
        projectOptions: ProjectOptions,
        gestureListener: MotionGestureListener,
        connection: Connection
    ) {
        val title = projectOptions.title
        val description = projectOptions.description
        val file = Gdx.files.external("projects/${projectOptions.fileName}")
        val temp = Gdx.files.external("projects/${projectOptions.fileName}.temp")
        // println("Saving file.... ${file.file()?.path} : ${file.file().exists()}")
        val stream = DataOutputStream(temp.write(false))
        val descriptionLength = min(description.length,DESCRIPTION_LENGTH_MAX_CHARACTERS)
        stream.writeInt(IDENTIFIER)
        stream.writeInt(VERSION_3)
        stream.writeInt(title.length)
        stream.write(title.toByteArray(Charsets.UTF_8))
        stream.writeInt(descriptionLength)
        stream.write(description.substring(0, descriptionLength).toByteArray(Charsets.UTF_8))
        // save camera state
        stream.writeFloat(gestureListener.camera.position.x)
        stream.writeFloat(gestureListener.camera.position.y)
        stream.writeFloat(gestureListener.zoomValue())
        // component size
        stream.writeInt(connection.size())
        synchronized(connection) {
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
                when (component) {
                    is CLabel -> {
                        stream.writeInt(component.text.length)
                        stream.write(component.text.toByteArray(Charsets.UTF_8))
                        stream.writeFloat(component.fontSize)
                       if(component.anchor != null){
                           stream.writeInt(component.anchor!!.alignment)
                           stream.writeInt(component.anchor!!.anchorEntity.id)
                       }else{
                           stream.writeInt(-1)
                           stream.writeInt(-1)
                       }
                    }

                    is CClock -> {
                        stream.writeFloat(component.freq)
                    }

                    is CPower -> {
                        stream.writeInt(component.value)
                    }

                    is CDataBus -> {
                        stream.writeInt(component.DATA_SIZE)
                    }

                    is CFanOutBus -> {
                        stream.writeInt(component.inputSize)
                        stream.writeInt(component.segments)
                    }

                    is CGroup -> {
                        stream.writeFloat(component.getWidth())
                        stream.writeFloat(component.getHeight())
                        stream.writeInt(component.dataContainer.size())
                        component.dataContainer.forEach { item ->
                            stream.writeInt(item.value.id)
                        }
                    }

                    is CChannel -> {
                        stream.writeInt(component.channelId.length)
                        stream.write(component.channelId.toByteArray(Charsets.UTF_8))
                        stream.writeInt(component.channelType)
                    }
                }
            }

            /* Sort all the lines in order, this will make loading the data easier
            * Parent -> Children since every child will have a higher origin depth.*/
            connection.forEach { listNode ->
                listNode.getLineMarkerChildren().sortedBy { it.getNodeOriginDepth() }
                listNode.getLineMarkerChildren().forEachIndexed { index, marker ->
                    marker.index = index
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
                    stream.writeInt(marker.getNodeOriginDepth())
                    // in case the connection is connected to another connection joint
                    if (marker.isSourceSignal()) {
                        stream.writeInt(LineMarker.FROM_SIGNAL)
                        // where this node is picking up a signal from
                        stream.writeInt(marker.getNodeOriginFrom(marker.from).from.value.id)
                        //which lineMarker it is connected to
                        stream.writeInt(((marker.from.value as CSignal).parent as LineMarker).index)
                        // which signal is it connected to
                        stream.writeInt((marker.from.value as CSignal).signalIndex)
                    } else {
                        stream.writeInt(LineMarker.FROM_COMPONENT)
                    }
                    marker.signals.forEach { signal ->
                        stream.writeInt(signal.signalIndex)
                        stream.writeFloat(signal.getPosition().x)
                        stream.writeFloat(signal.getPosition().y)
                    }
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
        stream.writeInt(VERSION_3)
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
        val textAnchors = mutableListOf<LabelAnchorHelper>()
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
                val labelFontSize = if (type == CTypes.LABEL) stream.readFloat() else 0f
                val labelAnchorAlignment = if(type == CTypes.LABEL && version >= VERSION_3) stream.readInt() else -1
                val labelAnchorID = if(type == CTypes.LABEL && version >= VERSION_3) stream.readInt() else -1
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

                    CTypes.AND_THREE_INPUT -> {
                        connection.insertNode(ListNode(CAndThreeInput(x, y, rotation, scene)))
                    }

                    CTypes.NAND_THREE_INPUT -> {
                        connection.insertNode(ListNode(CNandThreeInput(x, y, rotation, scene)))
                    }

                    CTypes.OR_THREE_INPUT -> {
                        connection.insertNode(ListNode(COrThreeInput(x, y, rotation, scene)))
                    }

                    CTypes.NOR_THREE_INPUT -> {
                        connection.insertNode(ListNode(CNorThreeInput(x, y, rotation, scene)))
                    }

                    CTypes.LATCH -> {
                        connection.insertNode(ListNode(CLatch(x, y, rotation, scene)))
                    }

                    CTypes.SR_LATCH -> {
                        connection.insertNode(ListNode(CSRLatch(x, y, rotation, scene)))
                    }

                    CTypes.FLIP_FLOP -> {
                        connection.insertNode(ListNode(CDFlipFlop(x, y, rotation, scene)))
                    }

                    CTypes.JK_FLIP_FLOP -> {
                        connection.insertNode(ListNode(CJKFlipFlop(x, y, rotation, scene)))
                    }

                    CTypes.T_FLIP_FLOP -> {
                        connection.insertNode(ListNode(CTFlipFlop(x, y, rotation, scene)))
                    }

                    CTypes.POINT -> {
                        connection.insertNode(ListNode(CPoint(x, y, rotation, scene)))
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

                    CTypes.BCD_SEVEN_SEGMENT_DISPLAY -> {
                        connection.insertNode(ListNode(CBCDDisplay(x, y, scene)))
                    }

                    CTypes.LABEL -> {
                        connection.insertNode(
                            ListNode(
                                CLabel(
                                    font,
                                    labelFontSize,
                                    "${labelText?.toString(Charsets.UTF_8)}",
                                    x,
                                    y,
                                    scene
                                ).also {
                                    it.color = EnvironmentTheme.colorOnBackground
                                    if(labelAnchorID != -1){
                                        textAnchors.add(LabelAnchorHelper(it,labelAnchorID,labelAnchorAlignment))
                                    }
                                }
                            )
                        )
                    }

                    CTypes.POWER -> {
                        connection.insertExecutionPoint(
                            ListNode(
                                CPower(
                                    powerValue,
                                    x,
                                    y,
                                    rotation,
                                    scene
                                )
                            )
                        )
                    }

                    CTypes.PULSE_BUTTON -> {
                        connection.insertExecutionPoint(
                            ListNode(
                                CPulseButton(
                                    x,
                                    y,
                                    rotation,
                                    scene
                                )
                            )
                        )
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
                        connection.insertNode(
                            ListNode(
                                CGroup(
                                    x,
                                    y,
                                    groupWidth,
                                    groupHeight,
                                    connection,
                                    scene
                                ).also { group ->
                                    group.setSize(groupWidth, groupHeight)
                                    group.componentGroupIds.addAll(groupMemberIds)
                                    group.gestureListener = gestureListener
                                    groups.add(group)
                                })
                        )
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
            textAnchors.onEach {data->
                data.label.anchor = CAnchor(connection[data.anchorEntityId].value,data.alignment)
            }
            val nestedLineMarkerList = mutableListOf<NestedLineMarkerHelper>()
            try {
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
                        if (version >= VERSION_2) {
                            val originDepth = stream.readInt()
                            val sourceID = stream.readInt()
                            if (sourceID == LineMarker.FROM_SIGNAL) {
                                val fromSourceNode = stream.readInt()
                                val fromLineMarker = stream.readInt()
                                val sourceSignalIndex = stream.readInt()
                          try{
                              NestedLineMarkerHelper(
                                  fromId = fromId,
                                  markerSizeFrom = markerSizeFrom,
                                  index = index,
                                  toId = toId,
                                  signalFromIndex = signalFromIndex,
                                  signalToIndex = signalToIndex,
                                  signalSize = signalSize,
                                  linePointCountX = linePointCountX,
                                  linePointCountY = linePointCountY,
                                  originDepth = originDepth,
                                  fromSourceNode = fromSourceNode,
                                  fromLineMarker = fromLineMarker,
                                  sourceSignalIndex = sourceSignalIndex
                              ).also {
                                  nestedLineMarkerList.add(it)
                              }.signals.also {
                                  for (j in 0 until signalSize) {
                                      val signalIndex = stream.readInt()
                                      val x = stream.readFloat()
                                      val y = stream.readFloat()
                                      it.add(
                                          NestedCSignalHelper(
                                              signalIndex = signalIndex,
                                              x = x,
                                              y = y
                                          )
                                      )
                                  }
                              }
                          }catch(e:Exception){
                              ErrorLogs.add(e.stackTraceToString())
                              for (j in 0 until signalSize) {
                                  stream.readInt()
                                  stream.readFloat()
                                  stream.readFloat()
                              }
                          }

                         } else {

                            try{
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
                                    connection[fromId].insertChildUnmarkedEnd(connection[toId], marker)
                                }
                            }catch(e:Exception){
                                ErrorLogs.add(e.stackTraceToString())
                                for (j in 0 until signalSize) {
                                    stream.readInt()
                                    stream.readFloat()
                                    stream.readFloat()
                                }
                            }

                          }
                        } else {

                            try{
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
                                    connection[fromId].insertChildUnmarkedEnd(connection[toId], marker)
                                }
                            }catch(e:Exception){
                                ErrorLogs.add(e.stackTraceToString())
                                for (j in 0 until signalSize) {
                                     stream.readInt()
                                     stream.readFloat()
                                     stream.readFloat()
                                }
                            }

                        }
                    }
                }
            } catch (eof: EOFException) {
                //process nestedLines with depth
              //  nestedLineMarkerList.sortBy { it.originDepth }
               /* nestedLineMarkerList.onEach {
                     println("Line Index ${it.originDepth}")
                }*/

                nestedLineMarkerList.onEach {
                    CDefaults.linePointCountX = it.linePointCountX
                    CDefaults.linePointCountY = it.linePointCountY
                    //  println("connection size ${connection[it.fromSourceNode].getLineMarkerChildren().size}")
                    //  println("connection requested ${it.fromLineMarker}")
                    try{
                        connection.insertConnection(
                            parent = connection[it.fromSourceNode],
                            from = ListNode(
                                connection[it.fromSourceNode].getLineMarkerChildren()[it.fromLineMarker]
                                    .signals[it.sourceSignalIndex]
                            ),
                            to = connection[it.toId],
                            signalFrom = it.sourceSignalIndex,
                            signalTo = it.signalToIndex,
                            scene = scene
                        ).also { marker ->
                            for (j in 0 until it.signalSize) {
                                val signalHelper = it.signals[j]
                                val signalIndex = signalHelper.signalIndex
                                val x = signalHelper.x
                                val y = signalHelper.y
                                marker.signals[signalIndex].also { signal ->
                                    signal.updatePosition(x, y)
                                }
                            }
                        }
                    }catch(e:Exception){
                        ErrorLogs.add(e.stackTraceToString())
                    }

                }
                throw eof
            }


        } catch (eof: EOFException) {
            eof.printStackTrace()
            stream.close()
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
        try {
        context.contentResolver?.openInputStream(uri).use { inputStream ->
            inputStream?.copyTo(outputStream)
        }
        outputStream.close()
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

    private fun readFileHeaderFromAsset(path: File, assetManager: AssetManager): ProjectOptions {
        val inputStream = assetManager.open(path.path)
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
            val fileSize = inputStream.available().toLong()
            stream.readFully(description)
            inputStream.close()
            stream.close()
            return ProjectOptions(
                path.name,
                title.toString(Charsets.UTF_8),
                description.toString(Charsets.UTF_8),
                path.path,
                fileSize,
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

    fun listSampleProjects(context: Context): List<ProjectOptions> {
        val files = mutableListOf<ProjectOptions>()
        val parent = "sample_projects"
        val assetManager = context.assets
        assetManager.list("sample_projects")?.onEach { name ->
            try {
                files.add(readFileHeaderFromAsset(File(parent, name), assetManager))
            } catch (io: java.lang.Exception) {
                //ignore the file and continue
            }

        }
        return files
    }

    fun getSampleProjectDetails(context: Context, path: String): ProjectOptions {
        return readFileHeaderFromAsset(File(path), context.assets)
    }

    fun fetchSampleProject(context: Context, projectOptions: ProjectOptions): ProjectOptions {
        val parentFolder = File(context.getExternalFilesDir(""), "projects")
        while (!parentFolder.exists()) parentFolder.mkdirs()
        val destinationFolder = File(parentFolder, File(projectOptions.path).name)
        if (destinationFolder.exists() && destinationFolder.length() != 0L) {
            return readFileHeader(destinationFolder)
        }
        val assetManager = context.assets
        val sourceStream = assetManager.open(projectOptions.path)
        val fileOutputStream = FileOutputStream(destinationFolder)

        try {
            val buffer = ByteArray(8192)
            var length: Int
            while (sourceStream.read(buffer).also { length = it } > 0) {
                fileOutputStream.write(buffer, 0, length)
            }
        } catch (io: IOException) {
            io.printStackTrace()
            sourceStream.close()
            fileOutputStream.close()
            return projectOptions
        }
        sourceStream.close()
        fileOutputStream.close()
        return readFileHeader(destinationFolder)
    }
}

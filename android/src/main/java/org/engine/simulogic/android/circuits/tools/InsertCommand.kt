package org.engine.simulogic.android.circuits.tools
import org.engine.simulogic.android.circuits.components.buttons.CPower
import org.engine.simulogic.android.circuits.components.generators.CClock
import org.engine.simulogic.android.circuits.components.wireless.CChannel
import org.engine.simulogic.android.circuits.components.wireless.ChannelBuffer
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ListNode

class InsertCommand(private val node:ListNode,private val connection: Connection) : Command() {


    override fun undo() {
         node.value.detachSelf()
         connection.removeNode(node)
    }

    override fun redo() {
        node.value.attachSelf()
        when(node.value){
            is CClock, is CPower -> connection.insertExecutionPoint(node)
            is CChannel ->{
                if(node.value.channelType == ChannelBuffer.CHANNEL_OUTPUT){
                    connection.insertExecutionPoint(node)
                }else{
                    connection.insertNode(node)
                }
            }
            else -> connection.insertNode(node)
        }
    }
}

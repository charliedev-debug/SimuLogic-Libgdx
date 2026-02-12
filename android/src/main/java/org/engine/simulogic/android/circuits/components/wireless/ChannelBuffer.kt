package org.engine.simulogic.android.circuits.components.wireless

class ChannelBuffer {
    companion object{
        const val CHANNEL_INPUT = 0
        const val CHANNEL_OUTPUT = 1
        private val inputBufferMap = mutableMapOf<String,CChannel>()
        fun insertInput(channel:CChannel){
             inputBufferMap[channel.channelId] = channel
        }
        fun getInput(channelId: String):CChannel?{
            return inputBufferMap[channelId]
        }
        fun removeInput(channel:CChannel){
            inputBufferMap.remove(channel.channelId)
        }
        fun isAvailable(channelId:String):Boolean{
            return inputBufferMap[channelId] != null
        }
        fun clear(){
            inputBufferMap.clear()
        }
        fun size():Int{
            return inputBufferMap.size
        }
    }
}

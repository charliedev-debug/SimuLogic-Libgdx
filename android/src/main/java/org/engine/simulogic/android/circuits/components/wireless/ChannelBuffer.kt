package org.engine.simulogic.android.circuits.components.wireless

class ChannelBuffer {
    companion object{
        private val inputBufferMap = mutableMapOf<String,CChannel>()
        fun insertInput(channel:CChannel){
             inputBufferMap[channel.channelId] = channel
        }
        fun getInput(channelId: String):CChannel?{
            return inputBufferMap[channelId]
        }
        fun isAvailable(channelId:String):Boolean{
            return inputBufferMap[channelId] != null
        }
        fun clear(){
            inputBufferMap.clear()
        }
    }
}

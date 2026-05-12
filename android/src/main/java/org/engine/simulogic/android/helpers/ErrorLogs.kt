package org.engine.simulogic.android.helpers

class ErrorLogs {

    companion object{
        private val messages = mutableListOf<String>()

        fun add(message: String){
            messages.add(message)
        }

        operator  fun get(index:Int):String{
            return messages[0]
        }

        fun reset(){
            messages.clear()
        }

        fun size():Int{
            return messages.size
        }

        fun last():String{
            return messages[size() - 1]
        }

        fun isNotEmpty(): Boolean{
            return messages.isNotEmpty()
        }

        fun isEmpty():Boolean{
            return messages.isEmpty()
        }
    }
}

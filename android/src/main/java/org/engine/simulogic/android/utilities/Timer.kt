package org.engine.simulogic.android.utilities

class Timer(var limit:Float, private val listener: ITimerListener) {
    private var elapsedTime = 0f
    var dt = 0f
    fun update(hasReset: Boolean = false){
        elapsedTime+= dt
        if (elapsedTime >= limit) {
            listener.onTick(hasReset)
            elapsedTime = 0f
        }
    }

    fun reset(){
        elapsedTime = 0f
    }

    fun getTime():Float{
        return elapsedTime
    }

    interface ITimerListener{
        fun onTick(hasReset:Boolean)
    }

}

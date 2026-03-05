package org.engine.simulogic.android.utilities

class TimerManager {
    private val timers = mutableListOf<Timer>()
    private var resetTick = false
    fun insert(timer:Timer){
        timers.add(timer)
        timers.onEach {
            it.reset()
        }
        resetTick = true
    }

    fun remove(timer:Timer){
        timers.remove(timer)
    }
    fun update(){
        timers.onEach {
            it.update(resetTick)
        }
        resetTick = false
    }
    companion object{
        private val instanceObject = TimerManager()
        fun getInstance():TimerManager{
            return instanceObject
        }
        fun reset(){
            instanceObject.timers.clear()
        }
    }
}

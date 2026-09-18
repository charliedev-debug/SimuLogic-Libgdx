package org.engine.simulogic.android.utilities

import org.engine.simulogic.android.scene.Entity
import java.util.Collections
import kotlin.math.max

class TimerManager {
    private val timers = Collections.synchronizedList(mutableListOf<Timer>())
    private var resetTick = false
    var STEP = 0.001f
    var highestLimit = 1000f
    fun insert(timer:Timer){
        synchronized(timers) {
            timers.add(timer)
            timers.onEach {
                highestLimit = max(1f / timer.limit,highestLimit)
                it.reset()
            }
        }
        resetTick = true
    }

    fun remove(timer:Timer){
        synchronized(timers) {
            timers.remove(timer)
        }
    }
    fun update(){
        synchronized(timers) {
            timers.onEach {
                it.update(resetTick)
            }
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

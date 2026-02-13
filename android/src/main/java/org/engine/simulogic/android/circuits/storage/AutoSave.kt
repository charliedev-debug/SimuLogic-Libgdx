package org.engine.simulogic.android.circuits.storage

import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.events.MotionGestureListener
import org.engine.simulogic.android.utilities.Timer

class AutoSave private constructor(private val projectOptions: ProjectOptions, private val gestureListener: MotionGestureListener, private val connection: Connection) {

   private val dto = DataTransferObject()
    var enabled = true
    companion object {
        var dataChanged = false

        lateinit var instance:AutoSave
        fun initialize(projectOptions: ProjectOptions, gestureListener: MotionGestureListener, connection: Connection){
            instance = AutoSave(projectOptions, gestureListener, connection)
        }
    }

    fun forceSave(){
        if(dataChanged){
            dto.writeData(projectOptions, gestureListener, connection)
            dataChanged = false
        }
    }

   // save the file at 1sec intervals
   private val timer = Timer(10f, listener = object : Timer.ITimerListener{
       override fun onTick() {
           if(dataChanged && enabled) {
               dto.writeData(projectOptions,gestureListener, connection)
               dataChanged = false
           }
       }
   })

    fun run(){
        timer.update()
    }
}

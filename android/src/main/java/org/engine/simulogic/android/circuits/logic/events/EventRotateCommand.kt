package org.engine.simulogic.android.circuits.logic.events

import org.engine.simulogic.android.circuits.storage.AutoSave
import org.engine.simulogic.android.circuits.tools.Command
import org.engine.simulogic.android.circuits.tools.RotateCommand
import org.engine.simulogic.android.events.MotionGestureListener

class EventRotateCommand(private val gestureListener: MotionGestureListener) : Command() {

    override fun execute() {
        val rotateCommand = RotateCommand()
        gestureListener.collisionDetector.selectedItems.forEach { item->
            val pRotation = item.caller.value.rotationDirection
            item.caller.value.rotateRight()
            val nRotation = item.caller.value.rotationDirection
            RotateCommand.RotateItem(pRotation,nRotation,item.caller.value).also {item->
                rotateCommand.insert(item)
            }
        }
        gestureListener.commandHistory.execute(rotateCommand)
        AutoSave.dataChanged = true
    }
}

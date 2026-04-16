package org.engine.simulogic.android.circuits.logic.events

import org.engine.simulogic.android.circuits.tools.Command
import org.engine.simulogic.android.events.MotionGestureListener
import org.engine.simulogic.android.scene.PlayGroundScene

class EventPasteCommand(private val gestureListener: MotionGestureListener, private val scene: PlayGroundScene): Command() {

    override fun execute() {
        gestureListener.paste()
    }
}

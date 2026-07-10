package org.engine.simulogic.android.circuits.logic.events

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2
import org.engine.simulogic.android.circuits.components.other.CAnchor
import org.engine.simulogic.android.circuits.components.other.CLabel
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.circuits.theme.EnvironmentTheme
import org.engine.simulogic.android.circuits.tools.Command
import org.engine.simulogic.android.circuits.tools.CommandHistory
import org.engine.simulogic.android.circuits.tools.InsertCommand
import org.engine.simulogic.android.events.MotionGestureListener
import org.engine.simulogic.android.scene.PlayGroundScene

class EventLabelAnchorCommand (private val position: Vector2,
                         private val text: String,
                         private val fontSize: Float,
                         private val font: BitmapFont,
                         private val alignment:Int,
                         private val connection: Connection,
                         private val gestureListener: MotionGestureListener,
                         private val scene: PlayGroundScene) : Command(){

    override fun execute() {
        gestureListener.createAnchors(alignment).also { anchors ->
            for( anchor in anchors) {
                ListNode(CLabel(font, fontSize, text, position.x, position.y, scene).also {
                    it.color = EnvironmentTheme.colorOnBackground
                    it.anchor = anchor
                }).also { node ->
                    connection.insertNode(node)
                    gestureListener.commandHistory.execute(InsertCommand(node, connection))
                }
            }
        }

    }
}

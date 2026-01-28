package org.engine.simulogic.android.circuits.components.other

import com.badlogic.gdx.graphics.Color
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.CTypes
import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.scene.PlayGroundScene

class CRangePoint(x: Float, y: Float,  type: CTypes,  signalIndex: Int, private val scene: PlayGroundScene) : CSignal(x, y ,type, signalIndex, scene) {
    var childX:CRangePoint? = null
    var childY:CRangePoint? = null
    var isUpdated = false

    override fun updatePosition(x: Float, y: Float) {
        super.updatePosition(x, y)
        isUpdated = true
    }
}

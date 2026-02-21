package org.engine.simulogic.android.circuits.logic

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.events.CollisionDetector
import kotlin.math.round

class SnapAlign(private val offsetX:Float = CDefaults.GRID_WIDTH, private val offsetY:Float = CDefaults.GRID_HEIGHT) {
    private val position = Vector2()
    fun getSnapCoordinates(coordinates:Vector2):Vector2{
        return getSnapCoordinates(coordinates.x, coordinates.y)
    }
    fun getSnapCoordinates(coordinates:Vector3):Vector2{
        return getSnapCoordinates(coordinates.x, coordinates.y)
    }
    fun getSnapCoordinates(x:Float, y:Float):Vector2{
        val snapX = round(x / offsetX) * offsetX
        val snapY = round(y / offsetY) * offsetY
        return position.set(snapX, snapY)
    }

}

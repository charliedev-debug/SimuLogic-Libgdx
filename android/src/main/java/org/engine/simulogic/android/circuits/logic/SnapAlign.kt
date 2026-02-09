package org.engine.simulogic.android.circuits.logic

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.events.CollisionDetector
import kotlin.math.round

class SnapAlign {
    private val position = Vector2()
    fun getSnapCoordinates(coordinates:Vector2):Vector2{
        val snapX = round(coordinates.x / CDefaults.GRID_WIDTH) * CDefaults.GRID_WIDTH
        val snapY = round(coordinates.y / CDefaults.GRID_HEIGHT) * CDefaults.GRID_HEIGHT
        return position.set(snapX, snapY)
    }
    fun getSnapCoordinates(coordinates:Vector3):Vector2{
        val snapX = round(coordinates.x / CDefaults.GRID_WIDTH) * CDefaults.GRID_WIDTH
        val snapY = round(coordinates.y / CDefaults.GRID_HEIGHT) * CDefaults.GRID_HEIGHT
        return position.set(snapX, snapY)
    }
}

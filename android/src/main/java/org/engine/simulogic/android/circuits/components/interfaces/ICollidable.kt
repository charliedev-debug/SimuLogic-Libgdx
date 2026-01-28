package org.engine.simulogic.android.circuits.components.interfaces

import com.badlogic.gdx.math.Rectangle
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.scene.Entity

interface ICollidable {

    fun contains(x:Float, y:Float):CNode?
    fun contains(entity: CNode): CNode?
    fun contains(rect: Rectangle): CNode?

}

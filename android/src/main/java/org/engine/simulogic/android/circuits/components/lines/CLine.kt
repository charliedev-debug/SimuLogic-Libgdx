package org.engine.simulogic.android.circuits.components.lines

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.scene.Entity

class CLine(var x1:Float,var y1:Float,var x2:Float,var y2:Float, var lineWidth:Float) : Entity(){

    var color = Color.WHITE
    override fun draw(shapeRenderer: ShapeRenderer) {
        shapeRenderer.color = color
        //shapeRenderer.line(x1,y1,x2,y2,color,color)
        shapeRenderer.rectLine(x1,y1,x2,y2,lineWidth, color, color)
    }

    override fun updatePosition(x1: Float, y1: Float, x2: Float, y2: Float) {
        this.x1 = x1
        this.y1 = y1
        this.x2 = x2
        this.y2 = y2
    }
}

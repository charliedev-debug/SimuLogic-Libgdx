package org.engine.simulogic.android.circuits.components.other
import com.badlogic.gdx.graphics.Color
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.lines.LineMarker
import org.engine.simulogic.android.scene.Entity
import org.engine.simulogic.android.scene.PlayGroundScene
import kotlin.math.abs

class CRangeLine(val start: CNode, val end: CNode, val parentLine: LineMarker,  color: Color, private val scene: PlayGroundScene)
    : CRect(0f,0f, CDefaults.signalIconRadius, CDefaults.signalIconRadius,color,scene) {
    var direction = 0

    fun setVisibility(visible: Boolean){
        isVisible = visible
        start.selected = visible
        end.selected = visible
    }

    override fun update() {
        super.update()
        direction = if(start.getPosition().x == end.getPosition().x) 0 else if ( start.getPosition().y == end.getPosition().y) 1 else -1
        when (direction) {
            0 -> {
                setSize(CDefaults.signalIconRadius, abs(start.getPosition().y - end.getPosition().y))
            }
            1 -> {
                setSize(abs(start.getPosition().x - end.getPosition().x), CDefaults.signalIconRadius)
            }
            else -> isVisible = false

        }
        if(!selected) {
            updatePosition(
                start.getPosition().x + (end.getPosition().x - start.getPosition().x) / 2f,
                start.getPosition().y + (end.getPosition().y - start.getPosition().y ) / 2f
            )
        }
    }

    override fun updatePosition(x: Float, y: Float) {
        if(selected){
            when(direction){
                0 ->{
                    super.updatePosition(x, getPosition().y)
                    start.updatePosition(x, start.getPosition().y)
                    end.updatePosition(x, end.getPosition().y)
                }

                1 ->{
                    super.updatePosition(getPosition().x, y)
                    start.updatePosition(start.getPosition().x, y)
                    end.updatePosition(end.getPosition().x, y)
                }

            }
        }else{
            super.updatePosition(x, y)
        }
       /* when(direction){
            0 ->{
                super.updatePosition(getPosition().x, y)
            }
            1 ->{
                super.updatePosition(x, getPosition().y)
            }
            else-> super.updatePosition(x, y)
        }*/
    }
}

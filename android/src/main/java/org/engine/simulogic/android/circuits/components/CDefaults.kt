package org.engine.simulogic.android.circuits.components

import com.badlogic.gdx.graphics.Color

class CDefaults {
    companion object{
        val signalIconRadius = 15f
        val gateWidth = 80f
        val gateHeight = 50f
        val clockWidth = 60f
        val clockHeight = 60f
        val latchWidth = 60f
        val latchHeight = 60f
        val ledHeight = 60f
        val ledWidth = 60f
        val randomWidth = 60f
        val randomHeight = 60f
        val segmentDisplayWidth = 14f
        val segmentDisplayHeight = 24f
        var linePointCountX = 3
        var linePointCountY = 2
        val lineWeight = 3f
        val GATE_SELECTED_COLOR = Color.RED
        val GATE_UNSELECTED_COLOR = Color.WHITE
        val INPUT_SELECTED_COLOR = Color.RED
        val INPUT_UNSELECTED_COLOR = Color.WHITE
        val GROUP_SELECTED_COLOR = Color(1f, 1f, 1f , 0.2f)
        val GROUP_UNSELECTED_COLOR = Color(1f, 1f, 1f , 0f)
        val LINE_MARKER_ACTIVE = Color(136f/255f, 218f/255f, 248f/255f,1f)
        val LINE_MARKER_INACTIVE = Color(1f,1f,1f,1f)
        val SIGNAL_ACTIVE_COLOR = Color(76f/255f, 175f/255f, 80f/255f,1f)
        val LED_INACTIVE_COLOR = Color(73f/255f, 73f/255f, 73f/255f, 1f)
        val LABEL_SELECTED_COLOR = Color(165f/255f, 66f/255f, 66f/255f,0.75f)
        val GRID_WIDTH = 50f
        val GRID_HEIGHT = 50f
        val GRID_LINE_COLOR_B = Color(0.411f,0.411f,0.411f ,1f)
        val GRID_LINE_COLOR_A = Color(0.711f,0.711f,0.711f ,1f)
        val RANGED_ICON_RADIUS = 30f
        val RANGED_LINE_COLOR = Color.RED

        fun setLinePointCount(x:Int, y:Int){
            linePointCountX = x
            linePointCountY = y
        }
    }
}

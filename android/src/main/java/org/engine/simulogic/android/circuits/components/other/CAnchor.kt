package org.engine.simulogic.android.circuits.components.other

import org.engine.simulogic.android.scene.Entity

class CAnchor( val anchorEntity: Entity , var alignment: Int) {

    companion object{
        const val ALIGN_TOP_LEFT = 0
        const val ALIGN_BOTTOM_LEFT = 1
        const val ALIGN_TOP_RIGHT = 2
        const val ALIGN_BOTTOM_RIGHT = 3
        const val ALIGN_TOP_CENTER = 4
        const val ALIGN_BOTTOM_CENTER = 5
        const val ALIGN_LEFT_CENTER = 6
        const val ALIGN_RIGHT_CENTER = 7
    }

    fun apply(to: Entity){
        val anchorPosition = anchorEntity.getCenter()!!
        val anchorWidth = anchorEntity.getWidth()
        val anchorHeight = anchorEntity.getHeight()
        val toWidth = to.getWidth()
        val toHeight = to.getHeight()
        var x = 0f
        var y = 0f
        when(alignment){
            // DIRECTIONS
            ALIGN_TOP_LEFT->{
                x = anchorPosition.x - toWidth /2
                y = anchorPosition.y + anchorHeight + toHeight
            }
            ALIGN_TOP_RIGHT->{
                x = anchorPosition.x + toWidth /2 + anchorWidth
                y = anchorPosition.y + anchorHeight + toHeight
            }
            ALIGN_BOTTOM_LEFT->{
                x = anchorPosition.x - toWidth / 2
                y = anchorPosition.y
            }
            ALIGN_BOTTOM_RIGHT->{
                x = anchorPosition.x + toWidth /2 + anchorWidth
                y = anchorPosition.y
            }
            ALIGN_TOP_CENTER ->{
                x = anchorPosition.x + anchorWidth / 2
                y = anchorPosition.y + anchorHeight + toHeight
            }
            ALIGN_BOTTOM_CENTER->{
                x = anchorPosition.x + anchorWidth / 2
                y = anchorPosition.y
            }
            ALIGN_LEFT_CENTER->{
                x = anchorPosition.x - anchorWidth
                y = anchorPosition.y + anchorHeight / 2 + toHeight / 2
            }
            ALIGN_RIGHT_CENTER->{
                x = anchorPosition.x + anchorWidth/2 + toWidth
                y = anchorPosition.y + anchorHeight / 2 + toHeight / 2
            }

        }
        to.updatePosition(x, y)
    }
}

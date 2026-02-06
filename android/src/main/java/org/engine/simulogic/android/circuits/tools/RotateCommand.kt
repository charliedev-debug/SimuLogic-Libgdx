package org.engine.simulogic.android.circuits.tools

import org.engine.simulogic.android.circuits.components.CNode

class RotateCommand:Command()  {

    data class RotateItem(val pRotation:Int, val nRotation:Int, val entity:CNode)

    private val dataList = mutableListOf<RotateItem>()

    fun insert(rotateItem: RotateItem){
        dataList.add(rotateItem)
    }

    override fun undo() {
        dataList.forEach {
            it.entity.rotationDirection = it.pRotation
            it.entity.adjustRotation()
        }
    }

    override fun redo() {
        dataList.forEach {
            it.entity.rotationDirection = it.nRotation
            it.entity.adjustRotation()
        }
    }
}

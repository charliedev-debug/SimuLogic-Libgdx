package org.engine.simulogic.android.circuits.storage

import java.io.Serializable

data class ProjectOptions(var title:String,var description:String = "", var path:String, var lastModified:Long = 0L,val mode:Int = OPEN):Serializable{
    companion object{
        const val CREATE = 0
        const val OPEN = 1
        const val RENAME = 2
    }
}

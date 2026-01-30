package org.engine.simulogic.android.circuits.storage

import java.io.Serializable

data class ProjectOptions(var title:String,var description:String = "", var lastModified:Long = 0L,val mode:Int):Serializable{
    companion object{
        const val CREATE = 0
        const val OPEN = 1
        const val RENAME = 2
    }
}

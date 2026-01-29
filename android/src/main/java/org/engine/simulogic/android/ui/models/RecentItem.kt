package org.engine.simulogic.android.ui.models

data class RecentItem(val title:String, val path:String,val description:String,val type:Int, val ispremium:Boolean, val enableDelete:Boolean = false){
    companion object{
        const val VIEW_ITEM = 0
        const val VIEW_HEADER = 1
    }
}

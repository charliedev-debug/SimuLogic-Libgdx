package org.engine.simulogic.android.views.interfaces

interface IDialogLabelAnchorListener {
    fun onCompleted(text:String, fontSize:Int, alignment: Int)
    fun onCancelled()
}

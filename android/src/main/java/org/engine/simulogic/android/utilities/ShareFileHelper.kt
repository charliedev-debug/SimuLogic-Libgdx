package org.engine.simulogic.android.utilities

import android.content.Context
import android.content.Intent

class ShareFileHelper {


    companion object{
        fun share(uri:String, title:String, context:Context){
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "application/octet-stream"
            }

            context.startActivity(Intent.createChooser(shareIntent, title, null))
        }
    }
}

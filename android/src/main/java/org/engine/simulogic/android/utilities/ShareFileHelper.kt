package org.engine.simulogic.android.utilities

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class ShareFileHelper {


    companion object{
        fun share(file: File, title:String, context:Context){
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM,  FileProvider.getUriForFile(context, "org.engine.simulogic.android.fileprovider",file,title))
                type = "application/octet-stream"
            }


            context.startActivity(Intent.createChooser(shareIntent, title, null))
        }
    }
}

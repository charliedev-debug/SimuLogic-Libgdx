package org.engine.simulogic.android.views.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.engine.simulogic.R


class LoadingDialog (context: Context, private val title:String, private val listener: IDialogLoadingListener) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = layoutInflater.inflate(R.layout.loading_dialog_layout,null)
        view.findViewById<TextView>(R.id.title).apply { text = title }
        // in case the user disabled auto-save or the application did not save user data
        CoroutineScope(Dispatchers.Default).launch {
            launch(Dispatchers.IO) {
                delay(3000)
               listener.onLoad()
                launch(Dispatchers.Main){
                    dismiss()
                }
            }

        }
        this.setContentView(view)
        this.setCancelable(false)
    }

    override fun onStart() {
        super.onStart()
        val width: Int = context.resources.getDimensionPixelSize(R.dimen.popup_width)
        window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawableResource(R.color.transparent)
        //  window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    interface IDialogLoadingListener{

        fun onLoad()
        fun onCancelled()
    }
}


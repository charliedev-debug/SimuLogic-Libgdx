package org.engine.simulogic.android.views.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.engine.simulogic.android.views.adapters.MenuItem

class MenuViewModel : ViewModel() {
    private val _message = MutableLiveData<MenuItem>()
    val message:LiveData<MenuItem> = _message
    fun onModeChanged(item: MenuItem){
        _message.postValue(item)
    }
}

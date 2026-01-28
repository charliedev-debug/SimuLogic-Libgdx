package org.engine.simulogic.android.views.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.engine.simulogic.android.views.adapters.MenuAdapterItem

class MenuViewModel : ViewModel() {
    private val _message = MutableLiveData<MenuAdapterItem>()
    val message:LiveData<MenuAdapterItem> = _message
    fun onModeChanged(item: MenuAdapterItem){
        _message.postValue(item)
    }
}

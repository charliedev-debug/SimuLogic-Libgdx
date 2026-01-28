package org.engine.simulogic.android.views.models
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.engine.simulogic.android.views.adapters.ComponentItem

class BottomSheetViewModel:ViewModel() {
    private val _message = MutableLiveData<ComponentItem>()
    val message: LiveData<ComponentItem> = _message
    fun onComponentTriggered(id: ComponentItem){
        _message.postValue(id)
    }
}

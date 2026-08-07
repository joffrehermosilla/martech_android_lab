package com.adobe.marketing.mobile.messagingsample.ui.identity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adobe.marketing.mobile.messagingsample.repository.IdentityRepository

class IdentityViewModel : ViewModel() {

    private val repository = IdentityRepository()

    private val _state = MutableLiveData(IdentityState())
    val state: LiveData<IdentityState> = _state

    fun load() {
        repository.loadIdentity {
            _state.postValue(it)
        }
    }

    /**
     * Actualiza el Customer ID usando el namespace homologado.
     */
    fun updateCustomerId(customerId: String, namespace: String = "CUSTOMER") {
        repository.updateCustomerId(customerId, namespace) {
            val current = _state.value ?: IdentityState()
            _state.postValue(
                current.copy(
                    customerId = customerId,
                    namespace = namespace,
                    connected = true
                )
            )
        }
    }

    fun resetIdentities() {
        repository.resetIdentities()
        _state.postValue(IdentityState())
    }
}

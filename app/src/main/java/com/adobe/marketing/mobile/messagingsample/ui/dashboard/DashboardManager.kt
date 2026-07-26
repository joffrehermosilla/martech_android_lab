package com.adobe.marketing.mobile.messagingsample.ui.dashboard

import androidx.lifecycle.MutableLiveData

/**
 * Gestor de estado reactivo para el Dashboard Enterprise.
 * Centraliza los indicadores de conexión del SDK de Adobe.
 */
object DashboardManager {

    val sdkConnected = MutableLiveData(false)
    val edgeConnected = MutableLiveData(false)
    val assuranceConnected = MutableLiveData(false)
    val placesConnected = MutableLiveData(false)
    val pushConnected = MutableLiveData(false)
    val customerAuthenticated = MutableLiveData(false)
    val identityLoaded = MutableLiveData(false)

    val lastMessage = MutableLiveData("Sistema Operativo Listo")

    fun updateMessage(message: String) {
        lastMessage.postValue(message)
    }
}

package com.adobe.marketing.mobile.messagingsample.adobe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adobe.marketing.mobile.MobileCore
import com.adobe.marketing.mobile.messagingsample.logger.AdobeLogger
import com.adobe.marketing.mobile.messagingsample.ui.dashboard.DashboardManager

/**
 * Gestiona el registro de tokens de Firebase (FCM) y el procesamiento de notificaciones Push.
 * Soporta identificadores de canal (adb_channel_id) y metadatos de AJO.
 */
object AdobePushManager {

    private val _pushToken = MutableLiveData<String>("")
    val pushToken: LiveData<String> get() = _pushToken

    private val _lastNotificationPayload = MutableLiveData<Map<String, String>>()
    val lastNotificationPayload: LiveData<Map<String, String>> get() = _lastNotificationPayload

    /**
     * Sincroniza el token de FCM con el SDK de Adobe para habilitar el envío desde AJO.
     */
    fun updateToken(token: String) {
        if (token.isBlank()) return
        
        MobileCore.setPushIdentifier(token)
        _pushToken.postValue(token)
        
        // NOTIFICACIÓN AL DASHBOARD
        DashboardManager.pushConnected.postValue(true)

        AdobeLogger.add("Push", "Token FCM registrado: ${token.take(8)}...", "SUCCESS")
    }

    /**
     * Procesa y registra el payload de una notificación entrante.
     * Captura el adb_channel_id si está presente para trazabilidad en CDP.
     */
    fun handlePayload(data: Map<String, String>) {
        _lastNotificationPayload.postValue(data)
        
        val title = data["adb_title"] ?: "Notificación"
        val body = data["adb_body"] ?: ""
        val channelId = data["adb_channel_id"] ?: "Default"
        
        AdobeLogger.add("Push", "Llegada (Foreground) - Canal: $channelId | $title: $body", "SUCCESS")
    }
}

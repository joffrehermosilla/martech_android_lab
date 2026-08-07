package com.adobe.marketing.mobile.messagingsample

import android.app.Application
import com.adobe.marketing.mobile.messagingsample.adobe.AdobeSDKManager

/**
 * Punto de entrada de la aplicación.
 * Delegamos la inicialización del SDK a AdobeSDKManager para seguir la arquitectura Clean.
 */
class MessagingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Inicialización centralizada del SDK de Adobe
        AdobeSDKManager.init(this)
    }
}

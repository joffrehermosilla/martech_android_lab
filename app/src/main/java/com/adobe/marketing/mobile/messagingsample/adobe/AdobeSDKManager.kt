package com.adobe.marketing.mobile.messagingsample.adobe

import android.app.Application
import com.adobe.marketing.mobile.Assurance
import com.adobe.marketing.mobile.Edge
import com.adobe.marketing.mobile.Lifecycle
import com.adobe.marketing.mobile.LoggingMode
import com.adobe.marketing.mobile.Messaging
import com.adobe.marketing.mobile.MobileCore
import com.adobe.marketing.mobile.Places
import com.adobe.marketing.mobile.edge.identity.Identity
import com.adobe.marketing.mobile.messagingsample.BuildConfig
import com.adobe.marketing.mobile.messagingsample.logger.AdobeLogger

/**
 * Orquestador central del Adobe Experience Platform SDK.
 * Gestiona el ciclo de vida y la inicialización de todas las extensiones corporativas.
 */
object AdobeSDKManager {

    private var isInitialized = false

    fun init(application: Application) {
        if (isInitialized) return

        MobileCore.setApplication(application)
        MobileCore.setLogLevel(LoggingMode.VERBOSE)

        val extensions = listOf(
            Messaging.EXTENSION,
            Identity.EXTENSION,
            Edge.EXTENSION,
            Lifecycle.EXTENSION,
            Places.EXTENSION,
            Assurance.EXTENSION
        )

        MobileCore.registerExtensions(extensions) {
            isInitialized = true
            AdobeLogger.add("SDK", "Adobe SDK Initialized with ${extensions.size} extensions", "SUCCESS")
            
            // Configuración desde .env.local via BuildConfig
            if (BuildConfig.ADOBE_APP_ID.isNotBlank()) {
                MobileCore.configureWithAppID(BuildConfig.ADOBE_APP_ID)
            } else {
                AdobeLogger.add("SDK", "CRITICAL: ADOBE_APP_ID not found", "ERROR")
            }

            // Assurance Session para debugging en tiempo real
            if (BuildConfig.ADOBE_ASSURANCE_SESSION_ID.isNotBlank()) {
                Assurance.startSession(BuildConfig.ADOBE_ASSURANCE_SESSION_ID)
            }
        }
    }

    fun isSdkReady(): Boolean = isInitialized
}

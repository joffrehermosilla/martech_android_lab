package com.adobe.marketing.mobile.messagingsample.adobe

import com.adobe.marketing.mobile.Edge
import com.adobe.marketing.mobile.ExperienceEvent
import com.adobe.marketing.mobile.MobileCore
import com.adobe.marketing.mobile.messagingsample.logger.AdobeLogger
import com.adobe.marketing.mobile.messagingsample.model.XdmBcpSchema
import com.adobe.marketing.mobile.messagingsample.ui.dashboard.DashboardManager

/**
 * Gestiona el envío de eventos hacia Adobe Edge Network.
 * Alineado con el Mapper corporativo del banco para evitar error MAPPER-3700-199.
 */
object AdobeJourneyManager {

    fun sendInteractionEvent(
        customerId: String,
        label: String,
        group: String = "LocationServices",
        category: String = "AdobePlaces.POI",
        component: String = "loanButton",
        onComplete: (Boolean) -> Unit = {}
    ) {
        // 1. Esquema XDM (_bcp)
        val xdmData = XdmBcpSchema.createMobileInteractionXdm(
            customerId = customerId,
            label = label,
            group = group,
            category = category,
            component = component
        )

        // 2. Data Contextual (__adobe) - CRÍTICO para el Datastream del banco
        // El servidor busca el ID en analytics.contextData para el mapeo bmo.user.profileid
        val dataMap = mapOf(
            "__adobe" to mapOf(
                "analytics" to mapOf(
                    "contextData" to mapOf(
                        "bmo.user.profileid" to customerId
                    )
                )
            )
        )

        val event = ExperienceEvent.Builder()
            .setXdmSchema(xdmData)
            .setData(dataMap) // Soluciona "Source data does not contain field __adobe"
            .build()

        Edge.sendEvent(event) { handles ->
            val success = handles != null
            if (success) {
                AdobeLogger.add("Journey", "Evento enviado con cicid: $customerId", "SUCCESS")
                DashboardManager.edgeConnected.postValue(true)
            } else {
                AdobeLogger.add("Journey", "Error en Edge: Verifique configuración en AEP", "ERROR")
                DashboardManager.edgeConnected.postValue(false)
            }
            onComplete(success)
        }
    }

    fun triggerInApp(actionName: String, contextData: Map<String, String>? = null) {
        MobileCore.trackAction(actionName, contextData)
        AdobeLogger.add("AJO", "Trigger In-App: $actionName", "INFO")
        DashboardManager.messagingConnected.postValue(true)
    }
}

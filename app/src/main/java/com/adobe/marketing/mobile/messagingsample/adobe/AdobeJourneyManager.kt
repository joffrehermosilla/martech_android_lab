package com.adobe.marketing.mobile.messagingsample.adobe

import com.adobe.marketing.mobile.Edge
import com.adobe.marketing.mobile.ExperienceEvent
import com.adobe.marketing.mobile.MobileCore
import com.adobe.marketing.mobile.messagingsample.logger.AdobeLogger
import com.adobe.marketing.mobile.messagingsample.model.XdmBcpSchema

/**
 * Gestiona el envío de eventos de experiencia (XDM) hacia Adobe Edge Network.
 * Permite la orquestación de Journeys en AJO y segmentación en CDP.
 */
object AdobeJourneyManager {

    /**
     * Envía un evento de interacción móvil utilizando el esquema corporativo _bcp.
     */
    fun sendInteractionEvent(
        customerId: String,
        label: String,
        group: String = "ProductListLayout",
        category: String = "BCPProductList.Loans",
        component: String = "loanButton",
        onComplete: (Boolean) -> Unit = {}
    ) {
        val xdmData = XdmBcpSchema.createMobileInteractionXdm(
            customerId = customerId,
            label = label,
            group = group,
            category = category,
            component = component
        )

        val event = ExperienceEvent.Builder()
            .setXdmSchema(xdmData)
            .build()

        Edge.sendEvent(event) { handles ->
            val success = handles != null
            if (success) {
                AdobeLogger.add("Journey", "Evento XDM _bcp enviado: $label", "SUCCESS")
            } else {
                AdobeLogger.add("Journey", "Error al enviar evento XDM a Edge", "ERROR")
            }
            onComplete(success)
        }
    }

    /**
     * Dispara una acción local para gatillar mensajes In-App de AJO.
     * @param actionName Nombre del evento/acción configurado como trigger en AJO.
     * @param contextData Datos de contexto adicionales (opcional).
     */
    fun triggerInApp(actionName: String, contextData: Map<String, String>? = null) {
        MobileCore.trackAction(actionName, contextData)
        AdobeLogger.add("AJO", "Trigger In-App disparado: $actionName", "INFO")
    }
}

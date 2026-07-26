package com.adobe.marketing.mobile.messagingsample.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Representa el esquema XDM corporativo _bcp para Adobe Experience Platform.
 * Homologado para Real-Time CDP y Adobe Journey Optimizer.
 */
object XdmBcpSchema {

    fun createMobileInteractionXdm(
        customerId: String,
        eventName: String = "mobile.action.interaction",
        group: String = "ProductListLayout",
        category: String = "BCPProductList.Loans",
        actionName: String = "Tap",
        label: String = "Descubrir más préstamos",
        element: String = "Button",
        component: String = "loanButton"
    ): Map<String, Any> {
        
        // _bcp.mobile.action
        val actionMap = mapOf(
            "group" to group,
            "category" to category,
            "name" to actionName,
            "label" to label,
            "element" to element,
            "component" to component
        )

        // _bcp.mobile
        val mobileMap = mapOf(
            "action" to actionMap
            // "screen" y "error" pueden añadirse aquí según necesidad
        )

        // _bcp.identity
        val identityMap = mapOf(
            "customerId" to customerId
        )

        // _bcp.event
        val eventMap = mapOf(
            "name" to eventName
        )

        // Objeto Raíz _bcp
        val bcpMap = mapOf(
            "identity" to identityMap,
            "event" to eventMap,
            "mobile" to mobileMap
        )

        // XDM Wrapper
        return mapOf(
            "_bcp" to bcpMap,
            "eventType" to eventName,
            "timestamp" to getIso8601Timestamp()
        )
    }

    private fun getIso8601Timestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }
}

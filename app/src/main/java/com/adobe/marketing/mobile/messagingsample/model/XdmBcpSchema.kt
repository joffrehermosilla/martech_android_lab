package com.adobe.marketing.mobile.messagingsample.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Representa el esquema XDM corporativo _bcp para Adobe Experience Platform.
 * Alineado con el esquema corporativo del banco.
 */
object XdmBcpSchema {

    fun createMobileInteractionXdm(
        customerId: String,
        eventName: String = "mobile.action.interaction",
        group: String = "LocationServices",
        category: String = "AdobePlaces.POI",
        actionName: String = "Tap",
        label: String = "POI Entry",
        element: String = "Button",
        component: String = "POI_ID"
    ): Map<String, Any> {
        
        val actionMap = mapOf(
            "group" to group,
            "category" to category,
            "name" to actionName,
            "label" to label,
            "element" to element,
            "component" to component
        )

        val mobileMap = mapOf(
            "action" to actionMap
        )

        val identityMap = mapOf(
            "customerId" to customerId
        )

        val eventMap = mapOf(
            "name" to eventName
        )

        // El objeto raíz _bcp solo debe contener XDM.
        // El Mapper del banco busca la data fuera de aquí.
        val bcpMap = mapOf(
            "identity" to identityMap,
            "event" to eventMap,
            "mobile" to mobileMap
        )

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

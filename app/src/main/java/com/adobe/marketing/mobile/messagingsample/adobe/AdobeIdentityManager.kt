package com.adobe.marketing.mobile.messagingsample.adobe

import com.adobe.marketing.mobile.MobileCore
import com.adobe.marketing.mobile.edge.identity.AuthenticatedState
import com.adobe.marketing.mobile.edge.identity.Identity
import com.adobe.marketing.mobile.edge.identity.IdentityItem
import com.adobe.marketing.mobile.edge.identity.IdentityMap
import com.adobe.marketing.mobile.messagingsample.logger.AdobeLogger

/**
 * Gestiona la identidad del usuario en Adobe Experience Platform.
 * Soporta Identity Stitching para Real-Time CDP.
 */
object AdobeIdentityManager {

    // Almacenamos el CustomerID actual en memoria para que todos los eventos lo usen automáticamente
    var currentCustomerId: String = ""
        private set

    fun getECID(callback: (String) -> Unit) {
        Identity.getExperienceCloudId { ecid ->
            val finalEcid = ecid ?: ""
            AdobeLogger.add("Identity", "ECID obtenido: $finalEcid", "INFO")
            callback(finalEcid)
        }
    }

    /**
     * Establece el CustomerID (DNI, Email, CRMID) y lo persiste para eventos futuros.
     */
    fun setCustomerID(
        customerId: String,
        namespace: String = "CRM",
        onComplete: (() -> Unit)? = null
    ) {
        if (customerId.isBlank()) {
            AdobeLogger.add("Identity", "CustomerID vacío", "WARN")
            return
        }

        currentCustomerId = customerId
        val item = IdentityItem(customerId, AuthenticatedState.AUTHENTICATED, true)
        val identityMap = IdentityMap()
        identityMap.addItem(item, namespace)

        Identity.updateIdentities(identityMap)
        AdobeLogger.add("Identity", "CustomerID vinculado: $customerId [$namespace]", "SUCCESS")
        onComplete?.invoke()
    }

    /**
     * Retorna el ID actual o el ECID si no hay un CustomerID seteado.
     * Esto asegura que el esquema _bcp siempre tenga un identificador.
     */
    fun getActiveIdentifier(callback: (String) -> Unit) {
        if (currentCustomerId.isNotBlank()) {
            callback(currentCustomerId)
        } else {
            getECID(callback)
        }
    }

    fun resetIdentities() {
        currentCustomerId = ""
        MobileCore.resetIdentities()
        AdobeLogger.add("Identity", "SDK Reset: Identidades limpiadas", "WARN")
    }
}

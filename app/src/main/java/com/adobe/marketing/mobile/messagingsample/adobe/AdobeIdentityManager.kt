package com.adobe.marketing.mobile.messagingsample.adobe

import com.adobe.marketing.mobile.MobileCore
import com.adobe.marketing.mobile.edge.identity.AuthenticatedState
import com.adobe.marketing.mobile.edge.identity.Identity
import com.adobe.marketing.mobile.edge.identity.IdentityItem
import com.adobe.marketing.mobile.edge.identity.IdentityMap
import com.adobe.marketing.mobile.messagingsample.logger.AdobeLogger
import com.adobe.marketing.mobile.messagingsample.ui.dashboard.DashboardManager

/**
 * Gestiona la identidad del usuario en Adobe Experience Platform.
 * Alineado con el namespace técnico 'cicid' del banco.
 */
object AdobeIdentityManager {

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
     * Establece el CustomerID usando el namespace 'cicid' (homologado en CDP).
     */
    fun setCustomerID(
        customerId: String,
        namespace: String = "cicid", // ALINEACIÓN: Namespace cambiado a 'cicid'
        onComplete: (() -> Unit)? = null
    ) {
        if (customerId.isBlank()) return

        currentCustomerId = customerId
        
        // Limpiamos namespaces genéricos previos para evitar ruidos
        Identity.removeIdentity(IdentityItem(customerId), "CRM")
        Identity.removeIdentity(IdentityItem(customerId), "CUSTOMER")

        // Registro con cicid (Primary Identity en CDP)
        val item = IdentityItem(customerId, AuthenticatedState.AUTHENTICATED, true)
        val identityMap = IdentityMap()
        identityMap.addItem(item, namespace)

        Identity.updateIdentities(identityMap)
        
        DashboardManager.customerAuthenticated.postValue(true)
        
        AdobeLogger.add("Identity", "Identidad vinculada en CDP como '$namespace': $customerId", "SUCCESS")
        onComplete?.invoke()
    }

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
        DashboardManager.customerAuthenticated.postValue(false)
        AdobeLogger.add("Identity", "SDK Reset: Identidades limpiadas", "WARN")
    }
}

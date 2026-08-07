package com.adobe.marketing.mobile.messagingsample.repository

import com.adobe.marketing.mobile.MobileCore
import com.adobe.marketing.mobile.messagingsample.adobe.AdobeIdentityManager
import com.adobe.marketing.mobile.messagingsample.adobe.AdobePushManager
import com.adobe.marketing.mobile.messagingsample.ui.identity.IdentityState

class IdentityRepository {

    fun loadIdentity(callback: (IdentityState) -> Unit) {
        AdobeIdentityManager.getECID { ecid ->
            // Recuperamos el token actual de Push
            val pushToken = AdobePushManager.pushToken.value ?: "-"
            
            callback(
                IdentityState(
                    ecid = if (ecid.isNotBlank()) ecid else "-",
                    customerId = AdobeIdentityManager.currentCustomerId.ifBlank { "-" },
                    pushToken = pushToken,
                    sdkVersion = MobileCore.extensionVersion(),
                    connected = ecid.isNotBlank()
                )
            )
        }
    }

    /**
     * Actualiza el identificador del usuario.
     * ALINEACIÓN: El namespace por defecto ahora es 'cicid'.
     */
    fun updateCustomerId(customerId: String, namespace: String = "cicid", callback: () -> Unit) {
        AdobeIdentityManager.setCustomerID(customerId, namespace) {
            callback()
        }
    }

    fun resetIdentities() {
        AdobeIdentityManager.resetIdentities()
    }
}
package com.adobe.marketing.mobile.messagingsample.repository

import com.adobe.marketing.mobile.MobileCore
import com.adobe.marketing.mobile.messagingsample.adobe.AdobeIdentityManager
import com.adobe.marketing.mobile.messagingsample.ui.identity.IdentityState

class IdentityRepository {

    fun loadIdentity(callback: (IdentityState) -> Unit) {
        AdobeIdentityManager.getECID { ecid ->
            callback(
                IdentityState(
                    ecid = if (ecid.isNotBlank()) ecid else "-",
                    sdkVersion = MobileCore.extensionVersion(),
                    connected = ecid.isNotBlank()
                )
            )
        }
    }

    fun updateCustomerId(customerId: String, namespace: String = "CRM", callback: () -> Unit) {
        AdobeIdentityManager.setCustomerID(customerId, namespace) {
            callback()
        }
    }

    fun resetIdentities() {
        AdobeIdentityManager.resetIdentities()
    }
}